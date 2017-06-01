package edu.uw.lbaker7.localtravelapp.activitites;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_RADIUS = "pref_searchRadius";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Fragment fragment = SettingsFragment.newInstance();

        getFragmentManager().beginTransaction().replace(R.id.settingsContent, fragment).commit();
    }
}
