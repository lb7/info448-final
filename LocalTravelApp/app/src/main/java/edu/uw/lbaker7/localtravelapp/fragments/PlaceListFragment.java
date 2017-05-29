package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import edu.uw.lbaker7.localtravelapp.PlacesRequestQueue;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.activitites.MapsActivity;

public class PlaceListFragment extends Fragment {
    
    private static final String TAG = "PlaceListFragment";

    private List<MapsActivity.PlaceItem> data;
    private PlacesAdapter adapter;
    private ImageLoader imageLoader;

    private OnMapButtonClickedListener mapButtonClickedCallback;

    public interface OnMapButtonClickedListener {
        void onMapButtonClicked();
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mapButtonClickedCallback = (OnMapButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMapButtonClickedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_list, container, false);
        ListView placesListView = (ListView)rootView.findViewById(R.id.place_list);
        placesListView.setBackgroundColor(Color.WHITE);
        data = ((MapsActivity)getActivity()).getPlaceList();
        imageLoader = PlacesRequestQueue.getInstance(getContext()).getImageLoader();

        adapter = new PlacesAdapter(getActivity(), data);
        placesListView.setAdapter(adapter);

        Button mapButton = (Button)rootView.findViewById(R.id.btn_to_map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapButtonClickedCallback.onMapButtonClicked();
            }
        });

        return rootView;
    }

    public class PlacesAdapter extends ArrayAdapter<MapsActivity.PlaceItem> {

        private class ViewHolder {
            NetworkImageView placeImage;
            TextView placeName;
            TextView placeAddress;
            TextView rating;
        }

        public PlacesAdapter(Context context, List<MapsActivity.PlaceItem> places) {
            super(context, R.layout.place_list_item, places);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            MapsActivity.PlaceItem placeItem = getItem(position);

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.place_list_item, parent, false);

                holder.placeImage = (NetworkImageView)convertView.findViewById(R.id.img_place);
                holder.placeName = (TextView)convertView.findViewById(R.id.txt_place_name);
                holder.placeAddress = (TextView)convertView.findViewById(R.id.txt_place_address);
                holder.rating = (TextView)convertView.findViewById(R.id.txt_ratings);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            //set image
            holder.placeImage.setImageUrl(placeItem.icon, imageLoader);

            //set name
            holder.placeName.setText(placeItem.placeName);

            //set address
            holder.placeAddress.setText(placeItem.address);

            //set ratings
            holder.rating.setText("(" + placeItem.rating + "/5.0)");

            return convertView;
        }
    }
}
