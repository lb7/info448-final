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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONObject;

import java.util.ArrayList;

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

    private ArrayList<String> placeIds;
    private ArrayList<PlaceItem> places;
    private ArrayAdapter<PlaceItem> adapter;


    public ItineraryDetailFragment() {
        // Required empty public constructor
    }

    public interface OnCreateMapButtonSelectedListener {
        void onCreateMapButtonSelected(ArrayList<PlaceItem> places);
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
            String itineraryName = getArguments().getString(ITINERARY_NAME_KEY);
            TextView name = (TextView) rootView.findViewById(R.id.itineraryName);
            name.setText(itineraryName);

            places = new ArrayList<PlaceItem>();

            ArrayList<PlaceItem> arrayOfPlaceItems = new ArrayList<PlaceItem>();

            placeIds = getPlaceIds();

            adapter = new PlaceItemAdapter(getActivity(), arrayOfPlaceItems);

            ListView listView = (ListView) rootView.findViewById(R.id.placeItems);
            listView.setAdapter(adapter);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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
        }
        setHasOptionsMenu(true);

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
                args.putString(ShareItineraryDialog.ARG_ITINERARY_ID, itineraryKey);

                shareDialog.setArguments(args);
                shareDialog.show(getFragmentManager(), "shareItineraryDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                            places.remove(placeToMove);
                            adapter.insert(placeToMove, position + 1);
                            places.add(position + 1, placeToMove);
                        }
                    }
                });

                moveUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position > 0) {
                            PlaceItem placeToMove = adapter.getItem(position);
                            adapter.remove(placeToMove);
                            places.remove(placeToMove);
                            adapter.insert(placeToMove, position - 1);
                            places.add(position - 1, placeToMove);
                        }
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlaceItem placeToDelete = adapter.getItem(position);
                        adapter.remove(placeToDelete);
                        deleteButton.setVisibility(View.INVISIBLE);

                        firebaseController.deletePlaceFromItinerary(placeIds.get(position), itineraryKey);

                        placeIds.remove(position);
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

    private void getPlaceFromId(String placeId) {

        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        String urlString = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
        urlString += placeId;
        urlString += "&key=" + getString(R.string.google_place_key);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    PlaceItem place = PlaceItem.parseObjectFromJson(response);
                    adapter.add(place);
                    places.add(place);
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.v(TAG, "A volley error occurred: "+ error);
                }
            });
            queue.add(jsonRequest);
    }

    private void onCreateMap(ArrayList<PlaceItem> places) {

    }

    private ArrayList<String> getPlaceIds() {

        final ArrayList<String> placeIdsToReturn = new ArrayList<String>();

        firebaseController.getPlacesFromItinerary(itineraryKey, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getPlaceFromId(dataSnapshot.getKey());
                placeIdsToReturn.add(dataSnapshot.getKey());
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
        return placeIdsToReturn;
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseController = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseController = FirebaseController.getInstance();
    }

}
