package edu.uw.lbaker7.localtravelapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.lbaker7.localtravelapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItineraryDetailFragment extends Fragment {


    public ItineraryDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_itinerary_detail, container, false);
    }

}
