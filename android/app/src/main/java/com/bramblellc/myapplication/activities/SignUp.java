package com.bramblellc.myapplication.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.fragments.LoadingBar;
import com.bramblellc.myapplication.services.ActionConstants;
import com.bramblellc.myapplication.services.SignUpService;


public class SignUp extends Activity {

    private EditText desiredNameEditText;
    private EditText desiredPasswordEditText;
    private EditText phoneNumberEditText;
    private Button continueButton;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private LoadingBar loadingBar;

    private String username;
    private String password;
    private String phoneNumber;

    private SignUpReceiver signUpReceiver;

    private CharSequence[] countryCodeArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        fm = getFragmentManager();
        loadingBar = new LoadingBar();

        desiredNameEditText = (EditText) findViewById(R.id.editTextDesiredUsername);
        desiredPasswordEditText = (EditText) findViewById(R.id.editTextDesiredPassword);
        phoneNumberEditText = (EditText) findViewById(R.id.editTextPhoneNumber);
        continueButton = (Button) findViewById(R.id.buttonSignUp);

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {

            private boolean isFormatting;
            private boolean deletingHyphen;
            private int hyphenStart;
            private boolean deletingBackward;

            @Override
            public void afterTextChanged(Editable text) {
                if (isFormatting)
                    return;

                isFormatting = true;

                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length()) {
                            text.delete(hyphenStart - 1, hyphenStart);
                        }
                    } else if (hyphenStart < text.length()) {
                        text.delete(hyphenStart, hyphenStart + 1);
                    }
                }
                if (text.length() == 3 || text.length() == 7) {
                    text.append('-');
                }

                isFormatting = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting)
                    return;

                // Make sure user is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(s);
                final int selEnd = Selection.getSelectionEnd(s);
                if (s.length() > 1 // Can delete another character
                        && count == 1 // Deleting only one character
                        && after == 0 // Deleting
                        && s.charAt(start) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    deletingHyphen = true;
                    hyphenStart = start;
                    // Check if the user is deleting forward or backward
                    if (selStart == start + 1) {
                        deletingBackward = true;
                    } else {
                        deletingBackward = false;
                    }
                } else {
                    deletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    signUp();
                }
                return false;
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        signUpReceiver = new SignUpReceiver();
        IntentFilter filter = new IntentFilter(ActionConstants.REGISTER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(signUpReceiver, filter);
    }

    public void disableButtons() {
        continueButton.setEnabled(false);
        desiredNameEditText.setEnabled(false);
        desiredPasswordEditText.setEnabled(false);
        phoneNumberEditText.setEnabled(false);
    }

    public void enableButtons() {
        continueButton.setEnabled(true);
        desiredNameEditText.setEnabled(true);
        desiredPasswordEditText.setEnabled(true);
        phoneNumberEditText.setEnabled(true);
    }

    // when the continue button is pressed (sign up)
    public void continueSignUpPressed(View v) {
        signUp();
    }

    public void signUp() {
        username = desiredNameEditText.getText().toString();
        password = desiredPasswordEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString().replace("-", "");
        // native checks on inputs gathered
        if (username.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_username_error), Toast.LENGTH_SHORT).show();
        }
        else if (password.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_password_error), Toast.LENGTH_SHORT).show();
        }
        else if(phoneNumber.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_phone_number_error), Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 6 || password.length() > 20){
            Toast.makeText(this,  getResources().getString(R.string.sign_up_credentials_invalid_password_length_error), Toast.LENGTH_SHORT).show();
        }
        else {
            disableButtons();
            ft = fm.beginTransaction();
            ft.add(R.id.loading_frame, loadingBar);
            ft.commit();
            continueButton.setText("SIGNING UP");
            Intent intent = new Intent(this, SignUpService.class);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("phone_number", phoneNumber);
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AccountPortal.class);
        startActivity(intent);
        finish();
    }

    public String getDesiredUsernameString() {
        return this.desiredNameEditText.getText().toString();
    }

    public String getDesiredPasswordString() {
        return this.desiredPasswordEditText.getText().toString();
    }

    public String getPhoneNumberString() {
        return this.phoneNumberEditText.getText().toString();
    }

    public void setDesiredUsernameString(String desiredUsername) {
        this.desiredNameEditText.setText(desiredUsername);
    }

    public void setDesiredPasswordString(String desiredPassword) {
        this.desiredPasswordEditText.setText(desiredPassword);
    }

    public void setPhoneNumberString(String phoneNumber) {
        this.phoneNumberEditText.setText(phoneNumber);
    }

    private class SignUpReceiver extends BroadcastReceiver {

        private SignUpReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);
            if (!successful) {
                ft = fm.beginTransaction();
                ft.remove(loadingBar);
                ft.commit();
                Toast.makeText(SignUp.this, intent.getStringExtra("message"), Toast.LENGTH_LONG).show();
                SignUp.this.enableButtons();
            }
            else {
                SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();
                LocalBroadcastManager.getInstance(SignUp.this).unregisterReceiver(signUpReceiver);
                SignUp.this.enableButtons();
                Intent startIntent = new Intent(SignUp.this, Landing.class);
                startIntent.putExtra("setup", true);
                startActivity(startIntent);
                finish();
            }
        }
    }
}

