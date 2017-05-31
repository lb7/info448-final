package edu.uw.lbaker7.localtravelapp.activitites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import edu.uw.lbaker7.localtravelapp.ItineraryListItem;
import edu.uw.lbaker7.localtravelapp.PlaceItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.ItineraryDetailFragment;
import edu.uw.lbaker7.localtravelapp.fragments.ItineraryListFragment;

public class ItineraryActivity extends BaseActivity implements ItineraryListFragment.OnItinerarySelectedListener, ItineraryDetailFragment.OnCreateMapButtonSelectedListener {

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
        String itineraryKey = item.itineraryKey;
        ItineraryDetailFragment itineraryDetailFragment = ItineraryDetailFragment.newInstance(itineraryName, itineraryKey);
        FragmentTransaction ft  = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.itinerary_list_container, itineraryDetailFragment, "ItineraryDetailFragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onCreateMapButtonSelected(List<PlaceItem> places) {
        startActivity(new Intent(this, MapsActivity.class));
    }
}
