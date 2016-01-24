package com.bramblellc.myapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bramblellc.myapplication.R;

public class Splashscreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);

        this.pause();
    }

    public void pause() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent accountPortalIntent = new Intent(Splashscreen.this, AccountPortal.class);
                startActivity(accountPortalIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}

