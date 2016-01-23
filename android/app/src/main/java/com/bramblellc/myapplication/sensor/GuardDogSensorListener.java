package com.bramblellc.myapplication.sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;

import com.bramblellc.myapplication.services.ActionConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuardDogSensorListener implements SensorEventListener {

    private Context ctx;

    private String username;

    private float mLastX, mLastY, mLastZ;
    private boolean initialized;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long last_time;

    private long start_time;
    private long end_time;

    private long last_record;

    private boolean trial_in_progress;

    private Frame[] frame_data;
    private int index;

    public GuardDogSensorListener(Context ctx, String username) {
        this.ctx = ctx;
        this.username = username;
        initialized = false;
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        frame_data = new Frame[25];
        last_record = 0;
    }

    public void startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        trial_in_progress = true;
        start_time = System.currentTimeMillis();
        end_time = start_time + (1000*5); // 5 seconds of data

        index = 0;
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
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            long current = System.currentTimeMillis();
            last_time = current;

            if (trial_in_progress)
            {
                long current_time = System.currentTimeMillis();
                // Push the data to the list
                if (index < frame_data.length) {
                    if (current_time - last_record >= 200) {
                        Frame c_frame = new Frame();
                        c_frame.accel_x = x;
                        c_frame.accel_y = y;
                        c_frame.accel_z = z;
                        c_frame.batch_order = index;
                        frame_data[index] = c_frame;
                        index++;
                        last_record = current_time;
                        if (index >= 25) {
                            stopListening();
                            broadcastFrames();
                        }
                    }
                } else {
                    trial_in_progress = false;
                }

            }
        }
    }

    public void broadcastFrames() {
        try {
            Batch b = createBatch(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            JSONArray array = new JSONArray();
            for (Frame f : b.frames) {
                JSONObject frameObject = new JSONObject();
                frameObject.put("accel_x", f.accel_x);
                frameObject.put("accel_y", f.accel_y);
                frameObject.put("accel_z", f.accel_z);
                frameObject.put("batch_order", f.batch_order);
                array.put(frameObject);
            }
            jsonObject.put("frames", array);
            Intent localIntent = new Intent(ActionConstants.SENSOR_ACTION);
            localIntent.putExtra("content", jsonObject.toString());
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Batch createBatch(boolean real) {
        try{
            Batch b = new Batch();
            b.real = real;
            b.frames = frame_data;
            System.out.println(b.toString());
            return b;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // implying
    }


    public class Batch {
        public boolean real;
        public Frame[] frames;

        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append(real);
            sb.append("\n");
            for(Frame f : frames) {
                sb.append(f.toString()).append("\n");
            }
            return sb.toString();
        }
    }


    private class Frame  {
        public float accel_x;
        public float accel_y;
        public float accel_z;
        public int batch_order;

        public String toString() {
            return batch_order + " " + accel_x + " " + accel_y + " " + accel_z;
        }
    }

}
