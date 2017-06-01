package edu.uw.lbaker7.localtravelapp.activitites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import edu.uw.lbaker7.localtravelapp.ItineraryListItem;
import edu.uw.lbaker7.localtravelapp.PlaceItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.ItineraryDetailFragment;
import edu.uw.lbaker7.localtravelapp.fragments.ItineraryListFragment;

public class ItineraryActivity extends AppCompatActivity implements ItineraryListFragment.OnItinerarySelectedListener, ItineraryDetailFragment.OnCreateMapButtonSelectedListener {
    public static final String ACTION_DRAW = "edu.uw.lbaker7.localtravelapp.ActionDraw";
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
    public void onCreateMapButtonSelected(ArrayList<PlaceItem> places) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putParcelableArrayListExtra("places",places );
        intent.setAction(ACTION_DRAW);
        startActivity(intent);
    }
}
