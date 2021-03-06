package live.flume.wireless.debugger;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Reads Logcat Logs from the buffer, gathers device metrics from System Monitor and sends them
 * to a server using the WebSocketMessenger.
 * Implements Runnable so it can be run its own thread.
 */
class LogReader implements Runnable {

    private static final String TAG = "WirelessDebugger";
    private final ArrayList<String> mLogs = new ArrayList<>();
    private Boolean mHostAppRunning = true;
    private Boolean mThreadRunning = true;
    private final WebSocketMessenger mWebSocketMessenger;
    private final SystemMonitor systemMonitor;
    private final int totalSystemMemory;
    private final int mUpdateTimeInterval;
    private long mLastSendTime = 0;

    /**
     * Creates LogReader instance if none exists.
     * Creates a new WebSocketMessenger also.
     * @param hostname Server's IP/Host address
     * @param apiKey User's API Key
     * @param appName Name of the application being debugged.
     * @param timeInterval Time interval between log sends
     */
    LogReader(String hostname, String apiKey, String appName, int timeInterval) {
        mWebSocketMessenger = WebSocketMessenger.buildNewConnection(hostname, apiKey, appName);
        if (mWebSocketMessenger == null) {
            Log.e(TAG, "Failed to create WebSocketMessenger Object");
        }
        mUpdateTimeInterval = timeInterval;
        systemMonitor = new SystemMonitor();
        totalSystemMemory = systemMonitor.getTotalMemory();
    }

    /**
     * Inherited from Runnable. Run on a separate thread.
     * Reads and sends logs and device metrics to the WebSocketMessenger.
     */
    @Override
    public void run() {
        if (mWebSocketMessenger == null) {
            Log.e(TAG, "No WebSocketMessengerObject, exiting.");
            mThreadRunning = false;
            return;
        }

        try {
            // Clear logcat buffer of any previous data and exit
            Runtime.getRuntime().exec("logcat -c");

            // Start the logcat process
            Process logcatProcess = Runtime.getRuntime().exec("logcat -v threadtime");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(logcatProcess.getInputStream()));

            String logLine = "";

            while (mHostAppRunning && mWebSocketMessenger.isRunning()) {
                sendLogsIfReady();

                /* If bufferReader is ready to be read from, read the line then proceed with the
                   remainder of the loop.
                   If not, sleep the thread for 10ms and continue the loop.
                 */
                if (bufferedReader.ready()) {
                    logLine = bufferedReader.readLine();
                } else {
                    try {
                        /* This is mostly a test.  With high accelerometer logging this value
                           the difference between mLogs is about 20 ms, so hopefully a
                           sleep time of 10ms is enough to not miss any mLogs.
                         */
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                mWebSocketMessenger.enqueueLog(logLine);
                mLogs.add(logLine);
            }

            bufferedReader.close();
            mWebSocketMessenger.sendEndSessionMessage();
        } catch (IOException ioe) {
            Log.e(TAG, "IO Exception Occurred in run() thread " + ioe.toString());
        }

        // Signals to owning service that thread operations are complete
        mThreadRunning = false;
    }

    /**
     * Checks if enough time has passed to send logs through the WebSocketManager.
     */
    private void sendLogsIfReady() {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - mLastSendTime;
        if (timeDifference > mUpdateTimeInterval && mWebSocketMessenger.isOpen()) {

            mWebSocketMessenger.sendSystemMetrics(
                    systemMonitor.getMemoryUsage(),
                    totalSystemMemory,
                    systemMonitor.getCpuUsage(),
                    systemMonitor.getSentBytesPerSecond(),
                    systemMonitor.getReceivedBytesPerSecond(),
                    currentTime);

            mWebSocketMessenger.sendLogDump();
            mLastSendTime = currentTime;
        }
    }

    /**
     * Notifies LogReader that the application is no longer running, logging is not longer required.
     */
    void setAppTerminated() {
        mHostAppRunning = false;
    }

    /**
     * Tells the caller if LogReader is still working (reading mLogs or sending them).
     * @return True if it is still running.  False otherwise.
     */
    boolean isThreadRunning() {
        return mThreadRunning;
    }
}
