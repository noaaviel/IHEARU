package com.example.ihearu.LoginAndMain;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ihearu.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Activity_Login extends AppCompatActivity {

    private final int RC_SIGN_IN = 123456;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private HashMap<String, Song> songsMap = new HashMap<>();
    private enum GENRE {
        ROCK,
        POP,
        RAP,
        GENERAL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //all products must change to songs
        DatabaseReference myRef = database.getReference("songs");

        updateSongs(myRef);
        if (user != null) {
            openApp();
        } else {
            startLoginMethod();
        }


    }

    private void generateProducts() {
        songsMap.put("1", new Song()
                .setKey("S0001")
                .setName("Killpop").setGenre(GENRE.ROCK.name()));
        songsMap.put("2", new Song()
                .setKey("S0002")
                .setName("American Pie").setGenre(GENRE.ROCK.GENERAL.name()));
        songsMap.put("3", new Song()
                .setKey("S0003")
                .setName("Baby Got Back").setGenre(GENRE.RAP.name()));
        songsMap.put("4",new Song()
                .setKey("S0004").setName("Rap God").setGenre(GENRE.RAP.name()));
        songsMap.put("5",new Song()
                .setKey("S0005").setName("Mockingbird").setGenre(GENRE.RAP.name()));
        songsMap.put("6",new Song()
                .setKey("S0006").setName("It's My Life").setGenre(GENRE.ROCK.name()));
        songsMap.put("7",new Song()
                .setKey("S0007").setName("Keep Your Head Up").setGenre(GENRE.RAP.name()));
        songsMap.put("8",new Song()
                .setKey("S0008").setName("Living On A Prayer").setGenre(GENRE.ROCK.name()));
        songsMap.put("9",new Song()
                .setKey("S0009").setName("Heaven").setGenre(GENRE.POP.name()));
        songsMap.put("10",new Song()
                .setKey("S0010").setName("Toxic").setGenre(GENRE.POP.name()));
        songsMap.put("11",new Song()
                .setKey("S0011").setName("Stadium Arcadium").setGenre(GENRE.ROCK.name()));

        //@!.&#
    }


    private void updateSongs(DatabaseReference myRef) {
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //myRef = database.getReference(MY_REF);
        generateProducts();
        for (Map.Entry<String, Song> entry : songsMap.entrySet()) {
            Song song = entry.getValue();
            myRef.child(song.getKey()).setValue(song);
        }
    }

    private void startLoginMethod() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.AnonymousBuilder().build()
                        ))
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .setTheme(R.style.AppThemeFirebaseAuth)
                        .build(),
                RC_SIGN_IN);
    }

    //change to open ContactsSelectActivity
    private void openApp() {

        Intent myIntent = new Intent(this, MainScreenActivity.class);
        startActivity(myIntent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                openApp();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                showSnackbar(R.string.unknown_error);

            }
        }
    }

    private void showSnackbar(int id) {
        Toast.makeText(this, getText(id), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}