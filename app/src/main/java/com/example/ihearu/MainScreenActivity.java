package com.example.ihearu;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

public class MainScreenActivity extends AppCompatActivity {

    public static final String TAG = "iHearU_App";

    private static final int MY_PERMISSION_REQUEST_CODE = 122;
    private static final int MY_LOCATION_REQUEST_CODE = 19;

    private static final String PREF_FILE_NAME = "com.example.ihearu.my_pref";
    private static final String KEY_CONTACTS_NO = "no_of_contacts";

    private int emergencyContacts = 0;

    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        sh = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        // ACCESS_LOCATION and SEND_SMS
        askPermission();

        startService(new Intent(this, MyPowerButtonService.class));

        (findViewById(R.id.my_fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGPS();
            }
        });

        View backgroundImage = findViewById(R.id.my_layout);
        Drawable background = backgroundImage.getBackground();
        //background.setAlpha(50);

    }

    @Override
    protected void onStart() {
        super.onStart();

        emergencyContacts = sh.getInt(KEY_CONTACTS_NO, 0);

        if (emergencyContacts == 0) {
            Snackbar.make(findViewById(R.id.my_layout), "Please Add Sharing Contacts First", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.emg_ph_menu:
                Intent i1 = new Intent(this, EmergencyContactsActivity.class);
                startActivity(i1);
                break;

            case R.id.msg_menu:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.about_menu:
                Intent i2 = new Intent(this, InfoActivity.class);
                startActivity(i2);
        }

        return true;
    }

    private void checkGPS() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        // Get a reference to device's setting
        SettingsClient client = LocationServices.getSettingsClient(this);
        client.checkLocationSettings(builder.build()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {

                    // Location settings are not satisfied
                    // show the user a dialog
                    try {
                        ((ResolvableApiException) e).startResolutionForResult(MainScreenActivity.this, MY_LOCATION_REQUEST_CODE);

                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        })

                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        triggerSOSService();
                    }
                });
    }


    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // ask for the permission
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS }, MY_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                showSnackBar("Turn on Location and SMS permission");
            }
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showSnackBar("Turn on Location permission");
            }
            else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                showSnackBar("Turn on SMS permission");
            }
        }
    }


    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_layout), msg, Snackbar.LENGTH_SHORT);

        snackbar.setAction("Please Go To Settings", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open settings activity
                Intent intent = new Intent(Settings.ACTION_SETTINGS);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        snackbar.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                triggerSOSService();
            }
            else if (resultCode == RESULT_CANCELED) {
                triggerSOSService();
            }
        }
    }

    private void triggerSOSService() {

        Intent i = new Intent(MainScreenActivity.this, MyPowerButtonService.class);
        i.putExtra(MyReceiver.SOS_ACTIVATED, true);
        Log.d(TAG, emergencyContacts + "");

        if (emergencyContacts >= 2) {
            startService(i);
            Snackbar.make(findViewById(R.id.my_layout), "Song Sending Service On", Snackbar.LENGTH_SHORT).show();
        }
        else {
            startService(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // start the service again
        Log.d(TAG, MainActivity.class.getSimpleName() + " destroyed");
    }
}