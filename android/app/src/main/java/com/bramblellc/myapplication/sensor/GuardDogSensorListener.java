package com.bramblellc.myapplication.sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bramblellc.myapplication.services.ActionConstants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.UUID;

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

    private long last_phone_record;

    private boolean trial_in_progress;

    private final UUID PEBBLE_APP_UUID = UUID.fromString("5bcd06fe-f8ba-4e9d-8168-7852a3e49dd3");


    private LinkedList<Frame> phone_frames;

    public GuardDogSensorListener(Context ctx, String username) {
        this.ctx = ctx;
        this.username = username;
        initialized = false;
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        phone_frames = new LinkedList<Frame>();
        last_phone_record = 0;
        PebbleKit.startAppOnPebble(ctx, PEBBLE_APP_UUID);
        PebbleKit.registerReceivedDataHandler(ctx, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                Log.d("Hoober", data.getBytes(0) + "");
            }
        });
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
//        else {
//            mLastX = x;
//            mLastY = y;
//            mLastZ = z;
//            long current = System.currentTimeMillis();
//            last_time = current;
//
//            if (trial_in_progress)
//            {
//                long current_time = System.currentTimeMillis();
//                // Push the data to the list
//                if (index < phone_frames.length) {
//                    if (current_time - last_phone_record >= 200) {
//                        Frame c_frame = new Frame();
//                        c_frame.accel_x = x;
//                        c_frame.accel_y = y;
//                        c_frame.accel_z = z;
//                        c_frame.batch_order = index;
//                        phone_frames.add(c_frame);
//                        index++;
//                        last_phone_record = current_time;
//                        if (index >= 25) {
//                            stopListening();
//                            broadcastFrames();
//                        }
//                    }
//                } else {
//                    trial_in_progress = false;
//                }
//
//            }
//        }
    }

//    public void broadcastFrames() {
//        try {
//            Batch b = createBatch(true);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("username", username);
//            JSONArray array = new JSONArray();
//            for (Object f : b.frames) {
//                JSONObject frameObject = new JSONObject();
//                frameObject.put("accel_x", f.accel_x);
//                frameObject.put("accel_y", f.accel_y);
//                frameObject.put("accel_z", f.accel_z);
//                frameObject.put("batch_order", f.batch_order);
//                array.put(frameObject);
//            }
//            jsonObject.put("frames", array);
//            Intent localIntent = new Intent(ActionConstants.SENSOR_ACTION);
//            localIntent.putExtra("content", jsonObject.toString());
//            LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Batch createBatch(boolean real) {
//        try{
//            Batch b = new Batch();
//            b.real = real;
//            b.frames = phone_frames;
//            System.out.println(b.toString());
//            return b;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

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

}
