package com.bramblellc.myapplication.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bramblellc.myapplication.R;

public class Propoganda extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.propoganda_layout);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AccountPortal.class);
        startActivity(intent);
        finish();
    }

    public void continuePressed(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }

    public void cancelPressed(View view) {
        onBackPressed();
    }
}
