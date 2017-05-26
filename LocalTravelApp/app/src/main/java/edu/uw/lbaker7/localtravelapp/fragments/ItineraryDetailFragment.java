package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.PlaceItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.VolleySingleton;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItineraryDetailFragment extends Fragment {

    private static final String TAG = "ItineraryDetailFragment";

    public static final String ITINERARY_NAME_KEY = "itineraryName";
    public static final String PLACE_IDS_ARRAY_KEY = "placesArray";

    private ArrayList<PlaceItem> places;
    private ArrayAdapter<PlaceItem> adapter;


    public ItineraryDetailFragment() {
        // Required empty public constructor
    }

    public interface OnCreateMapButtonSelectedListener {
        void onCreateMapButtonSelected(List<PlaceItem> places);
    }

    public static ItineraryDetailFragment newInstance(String itineraryName, ArrayList<String> placeIds) {
        ItineraryDetailFragment fragment = new ItineraryDetailFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(PLACE_IDS_ARRAY_KEY, placeIds);
        args.putString(ITINERARY_NAME_KEY, itineraryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itinerary_detail, container, false);

        if(getArguments() != null) {
            String itineraryName = getArguments().getString(ITINERARY_NAME_KEY);
            TextView name = (TextView) rootView.findViewById(R.id.itineraryName);
            name.setText(itineraryName);
            ArrayList<String> placeIds = getArguments().getStringArrayList(PLACE_IDS_ARRAY_KEY);
            if (placeIds != null) {
                places = getPlacesFromId(placeIds);

                ArrayList<PlaceItem> placesArrayList = new ArrayList<PlaceItem>();

                adapter = new PlaceItemAdapter(getActivity(), placesArrayList);

                getPlaceObjectsFromId();

                ListView listView = (ListView) rootView.findViewById(R.id.placeItems);
                listView.setAdapter(adapter);
            } else {
                Log.v(TAG, "placeIds was null");
            }
        }

        return rootView;
    }

    public void getPlaceObjectsFromId() {

        //load dummy data until firebase fetching, api requesting, and json parsing are all fully implemented

        PlaceItem itemOne = new PlaceItem("Famous House", new LatLng(22.71409, -147.2209), "https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png", "22 Baker Street");
        PlaceItem itemTwo = new PlaceItem("Jakob's Bar", new LatLng(22.74098, -150.2211), "https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png", "51 Main Street");
        PlaceItem itemThree = new PlaceItem("Honeycomb Factory", new LatLng(21.0987, -151.2288), "https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png", "509 Eaton Lane");

        adapter.add(itemOne);
        places.add(itemOne);
        adapter.add(itemTwo);
        places.add(itemTwo);
        adapter.add(itemThree);
        places.add(itemThree);
    }

    public class PlaceItemAdapter extends ArrayAdapter<PlaceItem> {

        public PlaceItemAdapter(@NonNull Context context, @NonNull ArrayList<PlaceItem> placesList) {
            super(context, 0, placesList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            PlaceItem data = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_place, parent, false);
            }

            TextView positionNumberView = (TextView) convertView.findViewById(R.id.positionNumber);
            ImageView placeIconView = (ImageView) convertView.findViewById(R.id.placeIcon);
            TextView placeNameView = (TextView) convertView.findViewById(R.id.placeName);
            TextView placeAddressView = (TextView) convertView.findViewById(R.id.placeAddress);

            ImageButton moveDownButton = (ImageButton) convertView.findViewById(R.id.moveDownButton);
            ImageButton moveUpButton = (ImageButton) convertView.findViewById(R.id.moveUpButton);

            moveDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < (adapter.getCount() - 1)) {
                        PlaceItem placeToMove = adapter.getItem(position);
                        adapter.remove(placeToMove);
                        adapter.insert(placeToMove, position + 1);
                    }

                    //TODO - Update firebase here

                }
            });

            moveUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position > 0) {
                        PlaceItem placeToMove = adapter.getItem(position);
                        adapter.remove(placeToMove);
                        adapter.insert(placeToMove, position - 1);
                    }

                    //TODO - Update firebase here

                }
            });

            fetchPlaceIcon(data.icon, placeIconView);

            positionNumberView.setText(Integer.toString(position + 1));
            placeNameView.setText(data.placeName);
            placeAddressView.setText(data.address);

            return convertView;
        }
    }

    private void fetchPlaceIcon(String url, ImageView placeIconView){
        ImageLoader loader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        loader.get(url, ImageLoader.getImageListener(placeIconView, 0, 0));
    }

    private ArrayList<PlaceItem> getPlacesFromId(ArrayList<String> placeIds) {
        //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyB8Ui2WT4bSCv5JLwFx2FAkR1wUrdUlgtM
        ArrayList<PlaceItem> placeList = new ArrayList<PlaceItem>();

        return placeList;
    }

    private void onCreateMap(ArrayList<PlaceItem> places) {

    }

}
