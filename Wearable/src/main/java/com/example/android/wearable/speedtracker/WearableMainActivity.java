/*
 * Copyright (C) 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wearable.speedtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.wear.ambient.AmbientModeSupport;

import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * The main activity for the wearable app. User can pick a speed limit, and after this activity
 * obtains a fix on the GPS, it starts reporting the speed. In addition to showing the current
 * speed, if user's speed gets close to the selected speed limit, the color of speed turns yellow
 * and if the user exceeds the speed limit, it will turn red. In order to show the user that GPS
 * location data is coming in, a small green dot keeps on blinking while GPS data is available.
 */
public class WearableMainActivity extends FragmentActivity implements
        AmbientModeSupport.AmbientCallbackProvider,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "WearableActivity";

    private static final long UPDATE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(5);
    private static final long FASTEST_INTERVAL_MS = TimeUnit.SECONDS.toMillis(5);

    private static final float MPH_IN_METERS_PER_SECOND = 2.23694f;

    private static final int SPEED_LIMIT_DEFAULT_MPH = 45;

    private static final long INDICATOR_DOT_FADE_AWAY_MS = 500L;

    // Request codes for changing speed limit and location permissions.
    private static final int REQUEST_PICK_SPEED_LIMIT = 0;

    // Id to identify Location permission request.
    private static final int REQUEST_GPS_PERMISSION = 1;

    // Shared Preferences for saving speed limit and location permission between app launches.
    private static final String PREFS_SPEED_LIMIT_KEY = "SpeedLimit";;

    private TextView timeView;
    private View tracking;
    private Button start;

    private Boolean blink;

    private Boolean is_tracking;

    private int mSpeedLimit;

    private String time = "0:00";

    private Calendar trackedTime;

    private Timer timer;


    /**
     * Ambient mode controller attached to this display. Used by the Activity to see if it is in
     * ambient mode.
     */
    private AmbientModeSupport.AmbientController mAmbientController;

    private GoogleApiClient mGoogleApiClient;

    private Handler mHandler = new Handler();

    private enum SpeedState {
        BELOW(R.color.speed_below), CLOSE(R.color.speed_close), ABOVE(R.color.speed_above);

        private int mColor;

        SpeedState(int color) {
            mColor = color;
        }

        int getColor() {
            return mColor;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");


        setContentView(R.layout.main_activity);

        /*
         * Enables Always-on, so our app doesn't shut down when the watch goes into ambient mode.
         * Best practice is to override onEnterAmbient(), onUpdateAmbient(), and onExitAmbient() to
         * optimize the display for ambient mode. However, for brevity, we aren't doing that here
         * to focus on learning location and permissions. For more information on best practices
         * in ambient mode, check this page:
         * https://developer.android.com/training/wearables/apps/always-on.html
         */
        // Enables Ambient mode.
        mAmbientController = AmbientModeSupport.attach(this);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        /*
         * If this hardware doesn't support GPS, we warn the user. Note that when such device is
         * connected to a phone with GPS capabilities, the framework automatically routes the
         * location requests from the phone. However, if the phone becomes disconnected and the
         * wearable doesn't support GPS, no location is recorded until the phone is reconnected.
         */
        if (!hasGps()) {
            Log.w(TAG, "This hardware doesn't have GPS, so we warn user.");
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.gps_not_available))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

        trackedTime = Calendar.getInstance();
        reset();
        blink = false;
        is_tracking = false;

        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!is_tracking){
                    // start tracking
                    startTracking();
                }
                else {
                    // stop tracking
                    reset();
                    // save time spent
                    // reset the timer

                }
            }
        });

        setupViews();
    }

    private void reset(){

        // reset the display time
        trackedTime.set(Calendar.HOUR, 0);
        trackedTime.set(Calendar.MINUTE, 0);
        trackedTime.set(Calendar.SECOND, 0);

        // stop blinking
        blink = false;
        is_tracking = false;

        // remove the timer
        timer.cancel();
        timer.purge();
    }

    private void startTracking() {

        is_tracking = true;

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                trackedTime.add(Calendar.SECOND, 1);
                final Date next = trackedTime.getTime();
                System.out.println(next);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                final String next_time = formatter.format(next);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeView.setText(next_time);
                        if (blink){
                            tracking.setVisibility(View.VISIBLE);
                            blink = false;
                        }
                        else{
                            tracking.setVisibility(View.INVISIBLE);
                            blink = true;
                        }
                    }
                });
            }

        },0,1000);//Update text every second
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                start.setText("Stop");
            }
        });
    }

    private void setupViews() {
        timeView = findViewById(R.id.current_time);
        tracking = findViewById(R.id.dot);

    }

    public void onSpeedLimitClick(View view) {
        Intent speedIntent = new Intent(WearableMainActivity.this,
                SpeedPickerActivity.class);
        startActivityForResult(speedIntent, REQUEST_PICK_SPEED_LIMIT);
    }

    private void updateSpeedInViews() {
        timeView.setText(time);
    }

    /**
     * Handles user choices for both speed limit and location permissions (GPS tracking).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PICK_SPEED_LIMIT) {
            if (resultCode == RESULT_OK) {
                // The user updated the speed limit.
                int newSpeedLimit =
                        data.getIntExtra(SpeedPickerActivity.EXTRA_NEW_SPEED_LIMIT, mSpeedLimit);

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(WearableMainActivity.PREFS_SPEED_LIMIT_KEY, newSpeedLimit);
                editor.apply();

                updateSpeedInViews();
            }
        }
    }


    /**
     * Returns {@code true} if this device has the GPS capabilities.
     */
    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        /** Prepares the UI for ambient mode. */
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);

            Log.d(TAG, "onEnterAmbient() " + ambientDetails);

            // Changes views to grey scale.
            timeView.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

        /** Restores the UI to active (non-ambient) mode. */
        @Override
        public void onExitAmbient() {
            super.onExitAmbient();

            Log.d(TAG, "onExitAmbient()");

            // Changes views to color.
            timeView.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
    }
}
