package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.uw.lbaker7.localtravelapp.R;

public class PlaceListFragment extends Fragment {
    
    private static final String TAG = "PlaceListFragment";


    public PlaceListFragment() {
        // Required empty public constructor
    }

    public static PlaceListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PlaceListFragment fragment = new PlaceListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_list, container, false);

        return rootView;
    }

    public class PlacesAdapter extends ArrayAdapter<String> {

        private class ViewHolder {
            ImageView placeImage;
            TextView placeName;
            TextView placeDescr;
            TextView rating;
        }

        // List<String> for now
        public PlacesAdapter(Context context, List<String> places) {
            super(context, R.layout.place_list_item, places);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            return super.getView(position, convertView, parent);
        }
    }

}
