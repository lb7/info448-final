package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.PlaceItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.VolleySingleton;
import edu.uw.lbaker7.localtravelapp.fragments.dialogs.ShareItineraryDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItineraryDetailFragment extends Fragment {

    private static final String TAG = "ItineraryDetailFragment";

    public static final String ITINERARY_NAME_KEY = "itineraryName";

    private OnCreateMapButtonSelectedListener createMapCallback;

    private ArrayList<PlaceItem> places;
    private ArrayAdapter<PlaceItem> adapter;


    public ItineraryDetailFragment() {
        // Required empty public constructor
    }

    public interface OnCreateMapButtonSelectedListener {
        void onCreateMapButtonSelected(List<PlaceItem> places);
    }

    public static ItineraryDetailFragment newInstance(String itineraryName) {
        ItineraryDetailFragment fragment = new ItineraryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ITINERARY_NAME_KEY, itineraryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itinerary_detail, container, false);

        if (getArguments() != null) {
            String itineraryName = getArguments().getString(ITINERARY_NAME_KEY);
            TextView name = (TextView) rootView.findViewById(R.id.itineraryName);
            name.setText(itineraryName);
            ArrayList<String> placeIds = getPlaceIds();
            if (placeIds != null) {
                places = getPlacesFromId(placeIds);

                ArrayList<PlaceItem> placesArrayList = new ArrayList<PlaceItem>();

                adapter = new PlaceItemAdapter(getActivity(), placesArrayList);

                getDummyObjects(); //dummy data

                ListView listView = (ListView) rootView.findViewById(R.id.placeItems);
                listView.setAdapter(adapter);

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        PlaceItem place = (PlaceItem) parent.getItemAtPosition(position);
                        Log.v(TAG, "deleting item");
                        Toast.makeText(getActivity(), "Would you like to delete Dialog", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            } else {
                Log.v(TAG, "placeIds was null");
            }

            setHasOptionsMenu(true);
        }

        Button shareButton = (Button) rootView.findViewById(R.id.shareItineraryButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show share itinerary dialog
            }
        });

        Button createMapButton = (Button) rootView.findViewById(R.id.createMap);
        createMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMapCallback.onCreateMapButtonSelected(places);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            createMapCallback = (OnCreateMapButtonSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCreateMapButtonSelectedListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionShare:
                DialogFragment shareDialog = new ShareItineraryDialog();

                Bundle args = new Bundle(1);
                // TODO: 5/30/17 replace dummy itinerary id with actual value
                args.putString(ShareItineraryDialog.ARG_ITINERARY_ID, "-KlQJ4U-cH5HAqwOVC6p");

                shareDialog.setArguments(args);
                shareDialog.show(getFragmentManager(), "shareItineraryDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getDummyObjects() {

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
            if (data != null) {
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
            }
            return convertView;
        }
    }

    private void fetchPlaceIcon(String url, ImageView placeIconView){
        ImageLoader loader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        loader.get(url, ImageLoader.getImageListener(placeIconView, 0, 0));
    }

    private ArrayList<PlaceItem> getPlacesFromId(ArrayList<String> placeIds) {

        final ArrayList<PlaceItem> placeList = new ArrayList<PlaceItem>();

        //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyB8Ui2WT4bSCv5JLwFx2FAkR1wUrdUlgtM

        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        for (String currentPlaceId : placeIds) {
            String urlString = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
            urlString += currentPlaceId;
            urlString += "&key=AIzaSyB8Ui2WT4bSCv5JLwFx2FAkR1wUrdUlgtM";

            Log.v(TAG, "current url: " + urlString);

            //RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    PlaceItem place = PlaceItem.parseObjectFromJson(response);
                    adapter.add(place);
                    placeList.add(place);
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.v(TAG, "Error occured.");
                }
            });
            queue.add(jsonRequest);
        }
        return placeList;
    }

    private void onCreateMap(ArrayList<PlaceItem> places) {

    }

    private ArrayList<String> getPlaceIds() {
        /*ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseController.getPlacesFromItinerary(itineraryId, listener);*/

        ArrayList<String> returnArray = new ArrayList<String>();
        returnArray.add("ChIJN1t_tDeuEmsRUsoyG83frY4");
        return returnArray;
    }

}
