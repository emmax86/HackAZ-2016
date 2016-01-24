package com.bramblellc.myapplication.services;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("ASDFASDF", "ASDFAASDF");
        if (messageEvent.getPath().equals("/message_path")) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message received on watch is: " + message);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

}