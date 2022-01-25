package com.example.ihearu;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Activity_Panel extends AppCompatActivity {

    private MaterialButton panel_BTN_iHearU;
    private MaterialButton panel_BTN_updateItems;
    private TextView panel_LBL_name;
    private TextView t1,t2;
    private HashMap<String, Song> songsMap = new HashMap<>();

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        findViews();
        initViews();
        generateProducts();
        user = new User().setName("SysUser");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //all products must change to songs
        DatabaseReference myRef = database.getReference("products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String value = dataSnapshot.getValue(String.class);
                //Log.d("pttt", "Value is: " + value);

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Song song = postSnapshot.getValue(Song.class);
                    Log.d("pttt", "Song: " + song.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pttt", "Failed to read value.", error.toException());
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    User value = dataSnapshot.getValue(User.class);
                    user = value;
                    panel_LBL_name.setText("Hi " + user.getName());
                   // updateFavoriteProduct();
                    Log.d("pttt", "Value is: " + value);
                } catch (Exception ex) { }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pttt", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String phoneNumber = "";
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                //http://stackoverflow.com/questions/866769/how-to-call-android-contacts-list   our upvoted answer
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone.equalsIgnoreCase("1"))
                    hasPhone = "true";
                else
                    hasPhone = "false";

                if (Boolean.parseBoolean(hasPhone)) {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phoneNumber = phoneNumber.replace(" ","");
                        phoneNumber = phoneNumber.replace("-","");
                        phoneNumber = phoneNumber.replace("(","");
                        phoneNumber = phoneNumber.replace(")","");
                        if (!String.valueOf(phoneNumber.charAt(0)).equals("+")) {
                            phoneNumber = "+972" + phoneNumber;
                        }

                    }
                    phones.close();
                }
                t1.setText("Name: " + name);
                t2.setText("Phone: " + phoneNumber);
                Log.d("curs", name + " num" + phoneNumber);
            }
            c.close();
        }
    }

    //get random song from db
    private void updateFavoriteProduct() {
      /*  FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");
        ValueEventListener vel = myRef.child(user.getFavoriteProduct()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Song song = dataSnapshot.getValue(Song.class);
                if (song == null) {
                    return;
                }
                panel_LBL_name.setText("Hi " + user.getName() + "\n" + song.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

*/
    }

    private void generateProducts() {
        songsMap.put("S0001", new Song()
                .setKey("S0001")
                .setName("Killpop"));
        songsMap.put("S0002", new Song()
                .setKey("S0002")
                .setName("American Pie")
                );
        songsMap.put("S0003", new Song()
                .setKey("S0003")
                .setName("Baby Got Back")
                );
        songsMap.put("S0004",new Song()
        .setKey("S0004").setName("Rap God"));
        songsMap.put("S0005",new Song()
                .setKey("S0005").setName("Mockingbird"));
        songsMap.put("S0006",new Song()
                .setKey("S0006").setName("It's My Life"));
        songsMap.put("S0007",new Song()
                .setKey("S0007").setName("Keep Your Head Up"));
        songsMap.put("S0008",new Song()
                .setKey("S0008").setName("Living On A Prayer"));
        songsMap.put("S0009",new Song()
                .setKey("S0009").setName("It's My Life"));
        //@!.&#
    }


    private void updateItems() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");

        for (Map.Entry<String, Song> entry : songsMap.entrySet()) {
            Song product = entry.getValue();
            myRef.child(product.getKey()).setValue(product);
        }
    }

    private void updateUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String uid = firebaseUser.getUid();
        String phone = firebaseUser.getPhoneNumber();

        Log.d("pttt", "uid=" + uid);
        Log.d("pttt", "phone=" + phone);

        User user = new User()
                .setName("Noa Aviel")
                .setPhone(phone)
                .setUid(uid);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(user.getUid()).setValue(user);
    }
    String str = "";
    private void initViews() {
        panel_BTN_iHearU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = t2.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + str));

                intent.putExtra("sms_body", "Listen To This Song With Me + GPS");
               // startActivity(intent);
                if(isReadContactPermissionGranted())
                    startActivityForResult(intent, 1);
                else
                    requestContactPermission();
                startActivityForResult(intent, 1);
            }
        });


        //open contacts
        panel_BTN_updateItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContacts();
            }
        });
    }

    private void getContacts(){
        Intent intent = new Intent();
        intent.setClass(this, EmergencyContactsActivity.class);
    }

    private void requestContactPermission() {
        final String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    private boolean isReadContactPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }



    private void findViews() {
        panel_BTN_iHearU = findViewById(R.id.panel_BTN_iHearU);
        panel_LBL_name = findViewById(R.id.panel_LBL_name);
        panel_BTN_updateItems = findViewById(R.id.share_with);
        t2 = findViewById(R.id.t2);
        t1 = findViewById(R.id.t1);
    }
}