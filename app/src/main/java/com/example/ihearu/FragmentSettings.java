package com.example.ihearu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class FragmentSettings extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    Preference editText;
    Preference switchPref;

    public FragmentSettings() {}

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        switchPref = findPreference("reset_switch");
        editText = findPreference("user_msg");

        // read the value from the SharedPreference
        String sosMsg = sharedPreferences.getString("user_msg", getString(R.string.default_msg));
        editText.setSummary(sosMsg);
        editText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editText.setSummary(newValue.toString());
                return true;
            }
        });


        switchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if ((boolean) newValue) {
                    // Write back the default preference values
                    SharedPreferences.Editor edit = sharedPreferences.edit();

                    edit.putString("user_msg", getString(R.string.default_msg));
                    edit.apply();
                    //show the new value in summary
                    editText.setSummary(getString(R.string.default_msg));
                    Toast.makeText(getContext(), "Default Text Is Set", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }
}