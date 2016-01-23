package com.bramblellc.myapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.bramblellc.myapplication.R;

public class AccountPortal extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_portal_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // LOGIN PATH
    // when the login button is pressed
    public void toSignInPressed(View v){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    // SIGN UP PATH
    // when the sign up button is pressed
    public void toSignUpPressed(View v){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }
}

