package com.a64adam.kks;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class StepService extends Service implements SensorEventListener {
    public static final String SS_TAG  = "StepService";
    public static final Intent intent   = new Intent(SS_TAG);

    private SensorManager mSensorManager;
    private Sensor mStep;

    private static int steps = 0;

    private class StepProcessor extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... v) {
            String s = Integer.toString((++steps));
            Log.d(SS_TAG, s);

            intent.putExtra("stepCount", s);
            sendBroadcast(intent);
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(SS_TAG, "SSC started, received start id " + startId + ": " + intent);

        mSensorManager  = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mStep  = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStep, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                new StepProcessor().execute();
                break;
            default:
                Log.d(SS_TAG, "Received unregistered sensor data");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }

}