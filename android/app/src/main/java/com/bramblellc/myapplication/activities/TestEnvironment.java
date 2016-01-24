package com.bramblellc.myapplication.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.services.DataService;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class TestEnvironment extends Activity implements SensorEventListener {

    private float mLastX, mLastY, mLastZ;
    private boolean initialized;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long last_time;

    private long start_time;
    private long end_time;

    private long last_record;

    private TextView time_tv;
    private Button start;
    private Button event;
    private Button no_event;

    private boolean trial_in_progress;

    private Frame[] frame_data;
    private int index;

    private LinkedList<Frame> phone_frames;

    private static String batch1;
    private static String batch2;

    public class Batch {
        public Frame[] frames;

        public String toString() {

            StringBuilder sb = new StringBuilder();
            int i = 0;
            sb.append("[");
            for(Frame f : frames) {
                sb.append(f.toString());
                if (i < frames.length - 1) {
                    sb.append(",");
                }
                i++;
            }
            sb.append("]");
            return sb.toString();
        }
    }


    private class Frame  {
        public float accel_x;
        public float accel_y;
        public float accel_z;
        public int batch_order;

        public String toString() {
            return "[" + accel_x + ", " + accel_y + ", " + accel_z + "]";
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_environment_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initialized = false;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        start = (Button)findViewById(R.id.start_b);
        event = (Button)findViewById(R.id.true_b);
        no_event = (Button)findViewById(R.id.false_b);

        phone_frames = new LinkedList<>();

        time_tv = (TextView)findViewById(R.id.time_tv);

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                trial_in_progress = true;
                time_tv.setText("0");
                start_time = System.currentTimeMillis();
                end_time = start_time + (1000*5); // 5 seconds of data

                frame_data = new Frame[50];
                index = 0;
            }
        });

        last_record = 0;

        event.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("batch-phone", batch1);
                    jsonObject.put("batch-watch", batch2);
                    jsonObject.put("answer", 1);

                    Intent localIntent = new Intent(TestEnvironment.this, DataService.class);
                    localIntent.putExtra("content", jsonObject.toString());
                    startService(localIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        no_event.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("batch-phone", batch1);
                    jsonObject.put("batch-watch", batch2);
                    jsonObject.put("answer", 0);
                    Intent localIntent = new Intent(TestEnvironment.this, DataService.class);
                    localIntent.putExtra("content", jsonObject.toString());
                    startService(localIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        // sensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Implying
    }


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
        } else {
            long current_time = System.currentTimeMillis();
            if (current_time - last_record >= 100) {
                Frame frame = new Frame();
                frame.accel_x = x;
                frame.accel_y = y;
                frame.accel_z = z;
                phone_frames.add(frame);
                time_tv.setText(current_time - start_time + "");
            }
        }
        if (phone_frames.size() == 50) {
            Batch batch = new Batch();
            batch.frames = new Frame[50];
            int i = 0;
            for (Frame frame : phone_frames) {
                batch.frames[i] = frame;
                i++;
            }

            for (i = 0; i < 10; i++) {
                phone_frames.pop();
            }
            batch1 = batch.toString();
        }
    }

    public class ListenerService extends WearableListenerService {

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageEvent.getPath().equals("/message_path")) {
                final String message = new String(messageEvent.getData());
                TestEnvironment.batch2 = message;

                Log.v("myTag", "Message received on watch is: " + message);
            }
            else {
                super.onMessageReceived(messageEvent);
            }
        }

    }

}
