package com.bramblellc.myapplication.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.layouts.CustomActionbar;
import com.bramblellc.myapplication.layouts.FullWidthButton;
import com.bramblellc.myapplication.services.LogoutService;

import java.util.HashSet;
import java.util.Set;

public class Settings extends Activity {

    private CustomActionbar settingsCustomActionbar;
    private FullWidthButton myDogsFullWidthButton;
    private FullWidthButton phoneServicesFullWidthButton;
    private FullWidthButton logoutFullWidthButton;
    private FullWidthButton testFullWidthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        settingsCustomActionbar = (CustomActionbar) findViewById(R.id.settings_custom_actionbar);
        myDogsFullWidthButton = (FullWidthButton) findViewById(R.id.my_dogs_full_width_button);
        phoneServicesFullWidthButton = (FullWidthButton) findViewById(R.id.phone_aid_input_full_width_button);
        logoutFullWidthButton = (FullWidthButton) findViewById(R.id.logout_full_width_button);
        //testFullWidthButton = (FullWidthButton) findViewById(R.id.test_full_width_button);

        settingsCustomActionbar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myDogsFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDogsPressed(v);
            }
        });

        phoneServicesFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneServicesPressed(v);
            }
        });

        logoutFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPressed(v);
            }
        });

        /*
        testFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFullWidthPressed(v);
            }
        });
        */
    }

    public void testFullWidthPressed(View view) {
        Intent intent = new Intent(Settings.this, TestEnvironment.class);
        startActivity(intent);
    }

    public void myDogsPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("ADD OR REMOVE A GUARD DOG")
                .positiveText("ADD")
                .negativeText("REMOVE")
                .neutralText("CANCEL")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addDog();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        removeDog();
                    }
                })
                .show();
    }

    public void addDog() {
        new MaterialDialog.Builder(this)
                .title("ADD A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to add to your kennel.")
                .positiveText("Add")
                .negativeText("Cancel")
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        Set<String> dogSet = prefs.getStringSet("dogs", null);
                        if (dogSet == null) {
                            dogSet = new HashSet<>();
                        }
                        dogSet.add(input.toString());
                        editor.putStringSet("dogs", dogSet);
                        editor.apply();
                    }
                }).show();
    }

    public void removeDog() {
        new MaterialDialog.Builder(this)
                .title("REMOVE A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to remove from your kennel.")
                .positiveText("remove")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        Set<String> dogSet = prefs.getStringSet("dogs", null);
                        if (dogSet.contains(input)) {
                            dogSet.remove(input.toString());
                            editor.putStringSet("dogs", dogSet);
                            editor.apply();
                        } else {
                            Toast.makeText(Settings.this, "There was no Guard Dog with the inputted phone number in your kennel.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();

    }

    public void phoneServicesPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("ADD OR REMOVE CONTACT FOR PHONE SERVICES")
                .positiveText("ADD")
                .negativeText("REMOVE")
                .neutralText("CANCEL")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addPhoneServiceContact();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
                        String contact = prefs.getString("phone", null);
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        editor.putString("phone", null);
                        editor.apply();
                        if (contact != null)
                            Toast.makeText(Settings.this, "Your phone service provider has been removed as your contact.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Settings.this, "There was no phone service provider to remove.", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    public void addPhoneServiceContact() {
        new MaterialDialog.Builder(this)
                .title("ADD A PHONE SERVICE CONTACT")
                .content("Enter the phone number of the phone service provider you would like to add or replace as your contact.")
                .positiveText("Add")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        String contact = input.toString();
                        editor.putString("phone", contact);
                        editor.apply();
                    }
                }).show();
    }

    public void logoutPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("LOG OUT")
                .content("Are you sure you would like to log out?")
                .positiveText("Yes")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        logout();
                    }
                })
                .show();
    }

    public void logout() {
        Intent deauthenticateIntent = new Intent(Settings.this, LogoutService.class);
        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
        deauthenticateIntent.putExtra("token", prefs.getString("token", ""));
        editor.remove("token");
        editor.apply();
        Intent startIntent = new Intent(Settings.this, Landing.class);
        startIntent.putExtra("finish", true);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startService(deauthenticateIntent);
        startActivity(startIntent);
        finish();
    }
}
