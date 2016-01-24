package com.bramblellc.myapplication.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.data.Globals;
import com.bramblellc.myapplication.sensor.GuardDogSensorListener;
import com.bramblellc.myapplication.services.ActionConstants;
import com.bramblellc.myapplication.services.AddContactService;
import com.bramblellc.myapplication.services.AnalyzeService;
import com.bramblellc.myapplication.services.DataService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class Landing extends Activity {

    private TextView usersText;
    private TextView dogsText;
    private GuardDogSensorListener guardDogSensorListener;
    private BatchBroadcastReceiver batchBroadcastReceiver;
    private AnalyzeBroadcastReceiver analyzeBroadcastReceiver;

    private IntentFilter addContactFilter;

    private AddContactReceiver addContactReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_layout);
        usersText = (TextView) findViewById(R.id.users_body);
        dogsText = (TextView) findViewById(R.id.dogs_body);
        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
        guardDogSensorListener = new GuardDogSensorListener(this, prefs.getString("username", "hodor"));
        batchBroadcastReceiver = new BatchBroadcastReceiver();
        analyzeBroadcastReceiver = new AnalyzeBroadcastReceiver();

        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            String un = prefs.getString("username", null);
            SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
            editor.putString("old_username",un);
            editor.putString("username", null);
            editor.putString("password", null);
            editor.apply();
            Intent intent = new Intent(this, AccountPortal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
            startActivity(intent);
            finish();
            return;
        }
        init();
    }

    public void init() {
        animateTextView(0,Integer.parseInt(Globals.userCounter),usersText);
        animateTextView(0,Integer.parseInt(Globals.contactsCounter),dogsText);
        boolean setup = getIntent().getBooleanExtra("setup", false);
        if (setup) {
            new MaterialDialog.Builder(this)
                    .title("WELCOME")
                    .content("Let's start out by adding some Guard Dogs to your account! A Guard Dog" +
                            " is someone that will receive text alerts if you are ever determined to be in danger")
                    .positiveText("Next")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            addDogsChain();
                        }
                    })
                    .show();
        }
        else {
            startListening();
        }
    }

    public void animateTextView(int initialValue, int finalValue, final TextView textview) {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(0.8f);
        int start = Math.min(initialValue, finalValue);
        int end = Math.max(initialValue, finalValue);
        int difference = Math.abs(finalValue - initialValue);
        Handler handler = new Handler();
        for (int count = start; count <= end; count++) {
            int time = Math.round(decelerateInterpolator.getInterpolation((((float) count) / difference)) * 10) * count;
            final int finalCount = ((initialValue > finalValue) ? initialValue - count : count);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textview.setText(finalCount + "");
                }
            }, time);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Guard-Dog", "Stopped. Lmaoooooo.");
        stopListening();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d("Guard-Dog", "Resumed. Lmaoooooo.");
        startListening();
    }

    public void addDogs(View view) {
        addDogsChain();
    }

    public void addDogsChain() {
        new MaterialDialog.Builder(this)
                .title("ADD A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to add.")
                .positiveText("Add")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Intent addIntent = new Intent(Landing.this, AddContactService.class);
                        addIntent.putExtra("phone_number", input.toString());
                        addIntent.putExtra("token", Globals.getToken());
                        addContactFilter = new IntentFilter(ActionConstants.ADD_CONTACT_ACTION);
                        addContactReceiver = new AddContactReceiver();
                        LocalBroadcastManager.getInstance(Landing.this).registerReceiver(addContactReceiver, addContactFilter);
                        startService(addIntent);
                    }
                }).show();
    }

    public void addAnotherDog() {
        new MaterialDialog.Builder(this)
                .content("Would you like to add more Guard Dogs?")
                .positiveText("Add another")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addDogsChain();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        emergencyContact();
                    }
                })
                .show();
    }

    public void emergencyContact() {
        new MaterialDialog.Builder(this)
                .title("EMERGENCY CONTACT")
                .content("Your emergency contact is the only contact that will be called in the event" +
                        "of an incident. Would you like to add an emergency contact?" )
                .positiveText("Yes")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addEmergencyContact();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        congrats();
                    }
                })
                .show();
    }

    public void congrats() {
        new MaterialDialog.Builder(this)
                .title("WELCOME TO THE PACK!")
                .content("Thanks for downloading Guard Dog.")
                .positiveText("Get started")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                    }
                })
                .show();
        startListening();
    }

    public void addEmergencyContact() {
        new MaterialDialog.Builder(this)
                .title("ADD AN EMERGENCY CONTACT")
                .content("Enter the phone number of emergency contact you would like to add.")
                .positiveText("Add")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        String contact = input.toString();
                        editor.putString("phone", contact);
                        editor.apply();
                        congrats();
                    }
                }).show();
    }

    // LOGIN PATH
    // when the login button is pressed
    public void settingsPressed(View v){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void promptForResponse(String content) {
        try {
            final JSONObject jsonObject = new JSONObject(content);
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("ARE YOU OK?")
                    .positiveText("YES")
                    .negativeText("NO")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            try {
                                jsonObject.put("real", false);
                                Intent localIntent = new Intent(Landing.this, DataService.class);
                                localIntent.putExtra("content", jsonObject.toString());
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                });
                                startService(localIntent);
                                startListening();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            try {
                                jsonObject.put("real", true);
                                Intent localIntent = new Intent(Landing.this, DataService.class);
                                localIntent.putExtra("content", jsonObject.toString());
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                });
                                startService(localIntent);
                                startListening();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .build();
            dialog.show();
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            };
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    try {
                        handler.removeCallbacks(runnable);
                        jsonObject.put("real", true);
                        Intent localIntent = new Intent(Landing.this, DataService.class);
                        localIntent.putExtra("content", jsonObject.toString());
                        startService(localIntent);
                        startListening();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            handler.postDelayed(runnable, 10000);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        IntentFilter filter = new IntentFilter(ActionConstants.SENSOR_ACTION);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(batchBroadcastReceiver);
        batchBroadcastReceiver = new BatchBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(batchBroadcastReceiver, filter);
        guardDogSensorListener.startListening();
        Log.d("Guard-Dog", "Listening for frames now. Ayy lmao.");
    }

    public void stopListening() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(batchBroadcastReceiver);
        guardDogSensorListener.stopListening();
        Log.d("Guard-Dog", "No longer listening for frames. Ayy lmao.");
    }

    private class BatchBroadcastReceiver extends BroadcastReceiver {

        private BatchBroadcastReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            Intent localIntent = new Intent(Landing.this, AnalyzeService.class);
            localIntent.putExtra("content", content);
            IntentFilter filter = new IntentFilter(ActionConstants.ANALYZE_ACTION);
            LocalBroadcastManager.getInstance(Landing.this).unregisterReceiver(analyzeBroadcastReceiver);
            analyzeBroadcastReceiver = new AnalyzeBroadcastReceiver();
            LocalBroadcastManager.getInstance(Landing.this).registerReceiver(analyzeBroadcastReceiver, filter);
            startService(localIntent);
            stopListening();
        }

    }

    private class AnalyzeBroadcastReceiver extends BroadcastReceiver {

        private AnalyzeBroadcastReceiver() {

        }


        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(Landing.this).unregisterReceiver(analyzeBroadcastReceiver);
            String content = intent.getStringExtra("content");
            boolean guess = intent.getBooleanExtra("guess", false);
            if (guess) {
                Landing.this.promptForResponse(content);
            }
            else {
                startListening();
            }
        }

    }

    private class AddContactReceiver extends BroadcastReceiver {

        private AddContactReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(Landing.this).unregisterReceiver(this);
            boolean successful = intent.getBooleanExtra("successful", false);
            if (!successful) {
                Toast.makeText(Landing.this, "An error occurred.", Toast.LENGTH_LONG).show();
            }
            else {
                addAnotherDog();
            }
        }

    }

}
