package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.FirebaseController;
import edu.uw.lbaker7.localtravelapp.PlaceItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.VolleySingleton;
import edu.uw.lbaker7.localtravelapp.fragments.dialogs.ShareItineraryDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItineraryDetailFragment extends Fragment {

    private static final String TAG = "ItineraryDetailFragment";

    public static final String ITINERARY_ID_KEY = "itineraryKey";
    public static final String ITINERARY_NAME_KEY = "itineraryName";

    private static FirebaseController firebaseController;

    private OnCreateMapButtonSelectedListener createMapCallback;

    private String itineraryKey;

    private ArrayList<PlaceItem> places;
    private ArrayAdapter<PlaceItem> adapter;


    public ItineraryDetailFragment() {
        // Required empty public constructor
    }

    public interface OnCreateMapButtonSelectedListener {
        void onCreateMapButtonSelected(List<PlaceItem> places);
    }

    public static ItineraryDetailFragment newInstance(String itineraryName, String itineraryKey) {
        ItineraryDetailFragment fragment = new ItineraryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ITINERARY_NAME_KEY, itineraryName);
        args.putString(ITINERARY_ID_KEY, itineraryKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itinerary_detail, container, false);
        firebaseController = FirebaseController.getInstance();

        if (getArguments() != null) {
            itineraryKey = getArguments().getString(ITINERARY_ID_KEY);
            Log.v(TAG, itineraryKey);
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
                        Log.v(TAG, "showing delete button");

                        //show red x button
                        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.deletePlaceButton);
                        if (deleteButton.getVisibility() == View.INVISIBLE) {
                            deleteButton.setVisibility(View.VISIBLE);
                        } else {
                            deleteButton.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    }
                });
            } else {
                Log.v(TAG, "placeIds was null");
            }
        }

        Button shareButton = (Button) rootView.findViewById(R.id.shareItineraryButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareItineraryDialog shareDialog = new ShareItineraryDialog();
                shareDialog.show(getFragmentManager(), "dialog");
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
                final ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deletePlaceButton);

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

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlaceItem placeToDelete = adapter.getItem(position);
                        adapter.remove(placeToDelete);
                        deleteButton.setVisibility(View.INVISIBLE);

                        //TODO - update places arraylist maybe

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

        final ArrayList<String> placeIds = new ArrayList<String>();

        firebaseController.getPlacesFromItinerary(itineraryKey, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    Log.v(TAG, "dataSnapshot has children");
                    Iterable<DataSnapshot> placesIterable = dataSnapshot.getChildren();
                    for (DataSnapshot currentChild : placesIterable) {
                        placeIds.add(currentChild.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                adapter.notifyDataSetChanged();
            }
        });

        placeIds.add("ChIJN1t_tDeuEmsRUsoyG83frY4");
        return placeIds;
    }

}
