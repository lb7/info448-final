package edu.uw.lbaker7.localtravelapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import edu.uw.lbaker7.localtravelapp.R;

/**
 * Created by Ryan Magee on 5/31/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
