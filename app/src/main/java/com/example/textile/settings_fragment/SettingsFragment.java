package com.example.textile.settings_fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragment;

import com.example.textile.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
    }
}
