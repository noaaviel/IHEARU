package com.example.ihearu.LoginAndMain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ihearu.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContactsActivity extends AppCompatActivity {
    //codes:
    private static final int MY_CONTACT_REQUEST_CODE = 61;

    private static final String TAG = "CONTACT_APP";
    private static final String PREF_FILE_NAME = "com.example.ihearu.my_pref";
    SharedPreferences sharedPreferences;
    private static final String DELIMITER = "/";

    Map<String, String> myContacts = new HashMap<>(3);
    ImageButton addContactButton;
    int emergencyContacts;
    private static final String KEY_CONTACT1 = "con1";
    private static final String KEY_CONTACT2 = "con2";
    private static final String KEY_CONTACT3 = "con3";
    private static final String KEY_CONTACTS_NO = "no_of_contacts";

    ImageView[] icons = new ImageView[3];
    TextView[] people = new TextView[3];
    TextView textView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        icons[0] = findViewById(R.id.imageView1);
        icons[1] = findViewById(R.id.imageView2);
        icons[2] = findViewById(R.id.imageView3);

        people[0] = findViewById(R.id.textView1);
        people[1] = findViewById(R.id.textView2);
        people[2] = findViewById(R.id.textView3);


        addContactButton = findViewById(R.id.add_button);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user picks a phone number from contacts
                Intent intent = new Intent(Intent.ACTION_PICK);
                // or set MIME TYPE
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, MY_CONTACT_REQUEST_CODE);
                }
            }
        });

        sharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        emergencyContacts = sharedPreferences.getInt(KEY_CONTACTS_NO, 0);        // No. of emergency contacts

        myContacts.put(KEY_CONTACT1, sharedPreferences.getString(KEY_CONTACT1, ""));
        myContacts.put(KEY_CONTACT2, sharedPreferences.getString(KEY_CONTACT2, ""));
        myContacts.put(KEY_CONTACT3, sharedPreferences.getString(KEY_CONTACT3, ""));

        countContacts();
    }

    private void countContacts() {

        if (emergencyContacts == 0) {
            Snackbar.make(findViewById(R.id.my_constLayout), "Add emergency contacts first", Snackbar.LENGTH_LONG).show();
        }
        else if (emergencyContacts == 1) {

            Snackbar.make(findViewById(R.id.my_constLayout), "Add one more emergency contact to send SOS", Snackbar.LENGTH_LONG).show();
        }
        else if (emergencyContacts == 2) {

            Snackbar.make(findViewById(R.id.my_constLayout), "Add one more emergency contact for your better safety", Snackbar.LENGTH_LONG).show();
        }

        showContacts();
    }



    private void showContacts() {
        int curEmptyView = 0;
        // clear the list first
        for (int i = 0; i < 3; i++) {
            icons[i].setImageResource(0);
            people[i].setText("");
            unregisterForContextMenu(people[i]);
        }
        for (String key : myContacts.keySet()) {
            String value = myContacts.get(key);

            if (!value.isEmpty()) {
                icons[curEmptyView].setImageResource(R.drawable.ic_user);

                String[] data = value.split(DELIMITER);

                people[curEmptyView].setText(String.format("%s\n%s", data[0], data[1]));

                // register for the context menu
                registerForContextMenu(people[curEmptyView]);

                curEmptyView++;
            }
        }

        if (emergencyContacts == 3) {
            // hide the button
            addContactButton.setVisibility(View.GONE);
        }
        else {
            // show the button
            addContactButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);

        // To know which textView has been pressed
        textView = findViewById(v.getId());
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (textView != null) {

            String value = textView.getText().toString().replace("\n", DELIMITER);

            // need the key associate with the value
            for (String key : myContacts.keySet()) {

                if (value.equals(myContacts.get(key))) {

                    // update the map
                    myContacts.put(key, "");

                    // update the shared reference
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(key);
                    editor.putInt(KEY_CONTACTS_NO, --emergencyContacts);
                    editor.apply();

                    break;
                }
            }


            Log.d(TAG, myContacts.toString() + " : " + emergencyContacts);


            // update the View to display new Contacts
            countContacts();
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_CONTACT_REQUEST_CODE && resultCode == RESULT_OK) {

            //  Get the contact URI for the selected contact and query the content provider for the phone number
            Uri uri = data.getData();

            String[] cols = new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            try (Cursor cursor = getContentResolver().query(uri, cols, null, null, null)) {

                if (cursor != null && cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String ph = cursor.getString(1);


                    String contactInfo = name + DELIMITER + ph;

                    // check if we already have that emergency contact

                    if (myContacts.containsValue(contactInfo)) {

                        Snackbar.make(findViewById(R.id.my_constLayout), "Already included in the list", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        // Where to place the new contact in SharedPreference??

                        for (String key : myContacts.keySet()) {

                            String value = myContacts.get(key);

                            if (value.isEmpty()) {

                                // write the new contact here
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(key, contactInfo);
                                editor.putInt(KEY_CONTACTS_NO, ++emergencyContacts);
                                editor.apply();


                                // also update the map
                                myContacts.put(key, contactInfo);

                                break;
                            }
                        }

                        Log.d(TAG, myContacts.toString() + " : " + emergencyContacts);

                        // update the View to display new Contacts
                        countContacts();
                    }
                }
            }
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}