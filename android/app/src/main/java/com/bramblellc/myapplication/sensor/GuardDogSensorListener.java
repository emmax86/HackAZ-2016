package com.bramblellc.myapplication.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bramblellc.myapplication.services.ActionConstants;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.LinkedList;

public class GuardDogSensorListener implements SensorEventListener {

    private Context ctx;

    private String token;

    private float mLastX, mLastY, mLastZ;
    private boolean initialized;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long last_time;

    private long start_time;
    private long end_time;

    private long last_phone_record;

    private boolean trial_in_progress;

    private BatchReceiver batchReceiver;

    private LinkedList<Batch> batch_queue;

    private LinkedList<Frame> phone_frames;

    public GuardDogSensorListener(Context ctx, String token) {
        this.ctx = ctx;
        this.token = token;
        initialized = false;
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        phone_frames = new LinkedList<Frame>();
        batch_queue = new LinkedList<Batch>();
        last_phone_record = 0;
        batchReceiver = new BatchReceiver();
        LocalBroadcastManager.getInstance(ctx).registerReceiver(batchReceiver, new IntentFilter(ActionConstants.WOL));
    }

    public void startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        trial_in_progress = true;
        start_time = System.currentTimeMillis();
        end_time = start_time + (1000 * 5); // 5 seconds of data
    }

    public void stopListening() {
        sensorManager.unregisterListener(this, accelerometer);
        trial_in_progress = false;
        initialized = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!initialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            last_time = System.currentTimeMillis();
            initialized = true;
        }
        else {
            long current_time = System.currentTimeMillis();
            if (current_time - last_phone_record >= 100) {
                Frame frame = new Frame();
                frame.accel_x = x;
                frame.accel_y = y;
                frame.accel_z = z;
                frame.timestamp = current_time;
                phone_frames.add(frame);
            }
        }

        if (phone_frames.size() == 50) {
            Batch batch = new Batch();
            int i = 0;
            for (Frame frame : phone_frames) {
                batch.frames[i] = frame;
                i++;
            }
            batch_queue.add(batch);

            for (i = 0; i < 10; i++) {
                phone_frames.pop();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // implying
    }


    public class Batch {
        public Frame[] frames;

        public Batch() {
            frames = new Frame[50];
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < frames.length; i++) {
                Frame frame = frames[i];
                sb.append("[").append(frame.accel_x).append(", ").append(frame.accel_y).append(", ").append(frame.accel_z).append("]");
                if (i != frames.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }


    private class Frame  {
        public float accel_x;
        public float accel_y;
        public float accel_z;
        public long timestamp;

        public String toString() {
            return timestamp + " " + accel_x + " " + accel_y + " " + accel_z;
        }
    }

    private class BatchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String batch = intent.getStringExtra("batch");
            Intent localIntent = new Intent(ActionConstants.SENSOR_ACTION);
            localIntent.putExtra("batch-phone", batch_queue.pop().toString());
            localIntent.putExtra("batch-watch", batch);
            localIntent.putExtra("token", token);
            LocalBroadcastManager.getInstance(GuardDogSensorListener.this.ctx).sendBroadcast(localIntent);
        }

    }

}
