package edu.uw.lbaker7.localtravelapp.activitites;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import edu.uw.lbaker7.localtravelapp.ItineraryListItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.ItineraryListFragment;

public class ItineraryActivity extends BaseActivity implements ItineraryListFragment.OnItinerarySelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itineraries);

        ItineraryListFragment itineraryList = ItineraryListFragment.newInstance();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.itinerary_list_container, itineraryList);
        ft.commit();
    }

    @Override
    public void onItinerarySelected(ItineraryListItem item) {
        String itineraryName = item.itineraryName;
        FragmentTransaction ft  = getSupportFragmentManager().beginTransaction();

    }
}
