package com.google.wireless.debugging;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MainFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "Wireless Debugging";

    // Buttons and Text
    private Button mLogButton;
    private EditText mLogText;
    private Button mAccelerometerToggleButton;

    // Accelerometer Logging
    private boolean mLogAccelerometerData = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widb_fragment, container, false);

        createButtons(view);

        return view;
    }

    /**
     * Creates Logging control buttons within the view
     * @param v View container for buttons
     */
    private void createButtons(View v) {

        mLogText = (EditText) v.findViewById(R.id.log_message_text);

        mLogButton = (Button) v.findViewById(R.id.send_log_button);
        mLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLogText.getText().toString().equals("")) {
                    Log.i(TAG, mLogText.getText().toString());
                }
            }
        });

        v.findViewById(R.id.crash_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("Forced Crash");
            }
        });

        v.findViewById(R.id.exception_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    throw new NullPointerException("Forced Null Pointer Exception");
                } catch (NullPointerException nPE){
                    Log.e(TAG, nPE.toString());
                }
            }
        });

        mAccelerometerToggleButton = (Button) v.findViewById(R.id.accel_toggle_button);
        mAccelerometerToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogAccelerometerData = !mLogAccelerometerData;
                if (mLogAccelerometerData){
                    mAccelerometerToggleButton.setText(R.string.stop_accel_data);
                } else {
                    mAccelerometerToggleButton.setText(R.string.start_accel_data);
                }

            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mLogAccelerometerData) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float aX = event.values[0];
                float aY = event.values[1];
                Log.i(TAG, "aX: " + aX + " aY: " + aY);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not Used
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
