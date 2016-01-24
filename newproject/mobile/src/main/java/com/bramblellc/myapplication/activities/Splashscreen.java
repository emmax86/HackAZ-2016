package com.bramblellc.myapplication.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.data.Globals;
import com.bramblellc.myapplication.services.ActionConstants;
import com.bramblellc.myapplication.services.StatsRetrievalService;

public class Splashscreen extends Activity {

    private StatsReceiver statsReceiver;

    private static int SPLASH_TIME_OUT = 3000;
    private static boolean finished;
    private static boolean other_finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);
        Intent intent = new Intent(this, StatsRetrievalService.class);
        statsReceiver = new StatsReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(statsReceiver, new IntentFilter(ActionConstants.STATS_ACTION));
        startService(intent);
        other_finished = false;
        finished = false;
        this.pause();
    }

    public void pause() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finished) {
                    Intent accountPortalIntent = new Intent(Splashscreen.this, AccountPortal.class);
                    startActivity(accountPortalIntent);
                    finish();
                } else {
                    other_finished = true;
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private class StatsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Globals.userCounter = intent.getIntExtra("users-count", 0);
            Globals.contactsCounter = intent.getIntExtra("contacts-count", 0);
            LocalBroadcastManager.getInstance(Splashscreen.this).unregisterReceiver(statsReceiver);
            if (other_finished) {
                Intent accountPortalIntent = new Intent(Splashscreen.this, AccountPortal.class);
                startActivity(accountPortalIntent);
                finish();
            }
            else {
                finished = true;
            }
        }

    }
}
