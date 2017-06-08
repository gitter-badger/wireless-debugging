package com.google.wireless.debugger;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class SystemMonitor {

    private static final String TAG = "System Monitor";
    private static final String PROC_ROOT = "/proc/";
    private static final String PROC_STAT = PROC_ROOT + "stat";
    private static final String PROC_MEMINFO = PROC_ROOT + "meminfo";
    private static final String PROC_NET_DEV = PROC_ROOT + "net/dev";
    private static final String REGEX_WHITESPACE = "\\s+";

    private int[] mPreviousCpuStats = new int[2];
    private long mLastBytesSentTime;
    private long mLastBytesReceivedTime;
    private int mPreviousBytesSent;
    private int mPreviousBytesReceived;

    public SystemMonitor() {
        ArrayList<String> cpuStatLines = getFileLines(PROC_STAT);
        if (!cpuStatLines.isEmpty()) {
            String firstLine = cpuStatLines.get(0);
            mPreviousCpuStats = parseCpuLine(firstLine);
        }
        mPreviousBytesSent = getSentBytes();
        mLastBytesSentTime = System.currentTimeMillis();
        mPreviousBytesReceived = getReceivedBytes();
        mLastBytesReceivedTime = System.currentTimeMillis();
    }

    private ArrayList<String> getFileLines(String path) {
        ArrayList<String> fileLines = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            String line;
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return fileLines;
    }

    public double getCpuUsage() {

        ArrayList<String> cpuStatLines = getFileLines(PROC_STAT);

        if (cpuStatLines.isEmpty()) {
            Log.d(TAG, "cpuStatLines is Empty");
            return 0.0;
        }

        int[] currentCpuStats = parseCpuLine(cpuStatLines.get(0));

        int systemUsage = currentCpuStats[0] - mPreviousCpuStats[0];
        int totalUsage = currentCpuStats[1] - mPreviousCpuStats[1];
        // Clamped value taken because sometimes the the file is read from the past (what!)
        double cpuUsagePercent = Math.max(0, Math.min(1, (systemUsage / (double) totalUsage)));

        /*
        Log.d(TAG, "SysUsage: " + systemUsage);
        Log.d(TAG, "CurrentTot " + currentCpuStats[1]);
        Log.d(TAG, "PrevTot " + mPreviousCpuStats[1]);
        Log.d(TAG, "TotUsage: " + totalUsage);
        Log.e(TAG, "CPU% " + cpuUsagePercent);
        */
        mPreviousCpuStats = currentCpuStats;

        return cpuUsagePercent;
    }

    public int getMemoryUsage() {
        return getMemoryUsageStatFromLine(5);
    }

    public int getTotalMemory() {
        return getMemoryUsageStatFromLine(0);
    }

    public double getSentBytesPerSecond() {
        int currentBytesSent = getSentBytes();
        long currentTime = System.currentTimeMillis();
        long elapsedMilliseconds = System.currentTimeMillis() - mLastBytesSentTime;
        mLastBytesSentTime = currentTime;
        double bytesPerMillisecond  = (currentBytesSent - mPreviousBytesSent) /
                (double) elapsedMilliseconds;

        mPreviousBytesSent = currentBytesSent;

        return bytesPerMillisecond * (1000 / elapsedMilliseconds );
    }

    public double getReceivedBytesPerSecond() {
        int currentBytesReceived = getReceivedBytes();
        long currentTime = System.currentTimeMillis();
        long elapsedMilliseconds = System.currentTimeMillis() - mLastBytesReceivedTime;
        mLastBytesReceivedTime = currentTime;
        double bytesPerMillisecond  = (currentBytesReceived - mPreviousBytesReceived) /
                (double) elapsedMilliseconds;

        mPreviousBytesReceived = currentBytesReceived;

        return bytesPerMillisecond * (1000 / elapsedMilliseconds );
    }

    private int getSentBytes() {
        return sumNetworkUsageColumn(9);
    }

    private int getReceivedBytes() {
        return sumNetworkUsageColumn(1);
    }

    private int[] parseCpuLine(String line) {
        String[] lineParts = line.split(REGEX_WHITESPACE);
        int[] times = new int[2];
        //Log.e(TAG, "RawLine: " + line);

        int timeUser = Integer.parseInt(lineParts[1]);
        int timeNice = Integer.parseInt(lineParts[2]);
        int timeSystem = Integer.parseInt(lineParts[3]);
        int timeIdle = Integer.parseInt(lineParts[4]);
        int timeIoWait = Integer.parseInt(lineParts[5]);
        int timeIrq = Integer.parseInt(lineParts[6]);
        int timeSoftIrq = Integer.parseInt(lineParts[7]);
        int timeSteal = Integer.parseInt(lineParts[8]);

        int totalIdleTime = timeIdle + timeIoWait;
        int totalTime = totalIdleTime + timeIrq + timeSoftIrq + timeSystem + timeSteal + timeUser
                + timeNice;

        times[0] = totalTime - totalIdleTime;
        times[1] = totalTime;

        return times;
    }

    private int getMemoryUsageStatFromLine(int line) {
        ArrayList<String> memInfoLines = getFileLines(PROC_MEMINFO);

        if (memInfoLines.isEmpty()){
            return 0;
        }

        String[] firstLineParts = memInfoLines.get(line).split(REGEX_WHITESPACE);
        return Integer.parseInt(firstLineParts[1]);
    }

    private int sumNetworkUsageColumn(int column){
        ArrayList<String> networkFileLines = getFileLines(PROC_NET_DEV);
        int totalBytes = 0;

        // Remove first two lines from the /proc/net/dev file
        networkFileLines.remove(0);
        networkFileLines.remove(0);

        for (String networkLine : networkFileLines) {
            networkLine = networkLine.substring(networkLine.indexOf(":"));
            String[] networkStats = networkLine.split(REGEX_WHITESPACE);
            totalBytes += Integer.parseInt(networkStats[column]);
        }

        return totalBytes;
    }
}
