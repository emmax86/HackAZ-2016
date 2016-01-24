package com.bramblellc.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private Button button;
    private GuardDogSensorListener listener;
    private boolean listening;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.start_button);
        listener = new GuardDogSensorListener(this);
        listening = false;
        setAmbientEnabled();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (null != googleApiClient && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.white));

        } else {
            mContainerView.setBackground(null);
        }
    }

    public void toggleListening(View view) {
        if (listening) {
            listener.stopListening();
            button.setText("Start Listening");
            listening = false;
        } else {
            listener.startListening();
            button.setText("Stop listening");
            listening = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

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


        private LinkedList<Frame> phone_frames;

        public GuardDogSensorListener(Context ctx) {
            this.ctx = ctx;
            initialized = false;
            sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            phone_frames = new LinkedList<Frame>();
            last_phone_record = 0;
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
            } else {
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
                new SendToDataLayerThread("/message_path", batch.toString()).start();
                for (i = 0; i < 10; i++) {
                    phone_frames.pop();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

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


        private class Frame {
            public float accel_x;
            public float accel_y;
            public float accel_z;
            public long timestamp;

            public String toString() {
                return timestamp + " " + accel_x + " " + accel_y + " " + accel_z;
            }
        }
    }


    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
