package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.ItineraryListItem;
import edu.uw.lbaker7.localtravelapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItineraryListFragment extends Fragment {
    
    private static final String TAG = "ItineraryListFragemnt";

    private OnItinerarySelectedListener itinerarySelectedCallback;

    private List<ItineraryListItem> data;
    private ItineraryAdapter adapter;

    public interface OnItinerarySelectedListener {
        void onItinerarySelected(ItineraryListItem item);
    }


    public ItineraryListFragment() {
        // Required empty public constructor
    }

    public static ItineraryListFragment newInstance() {
        
        Bundle args = new Bundle();
        ItineraryListFragment fragment = new ItineraryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            itinerarySelectedCallback = (OnItinerarySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnItinerarySelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG, "On create view");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_itinerary_list, container, false);
        ListView itineraryListView = (ListView)rootView.findViewById(R.id.itinerary_list);
        data = new ArrayList<>();

        //dummy data for testing
        ItineraryListItem item1 = new ItineraryListItem("Seattle", "02 May 2017");
        ItineraryListItem item2 = new ItineraryListItem("Tacoma", "05 May 2017");
        ItineraryListItem item3 = new ItineraryListItem("Bellevue", "23 May 2017");
        ItineraryListItem item4 = new ItineraryListItem("Olympia", "22 May 2017");
        Log.v(TAG, item1.itineraryName);
        Log.v(TAG, item1.dateCreated);
        data.add(item1);
        data.add(item2);
        data.add(item3);
        data.add(item4);

        adapter = new ItineraryAdapter(getActivity(), data);
        itineraryListView.setAdapter(adapter);

        itineraryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final ItineraryListItem listItem = (ItineraryListItem)parent.getItemAtPosition(position);
                final ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btn_delete_itinerary);
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.remove(listItem);
                        adapter.notifyDataSetChanged();
                        btnDelete.setVisibility(View.INVISIBLE);

                    }
                });
                return true;
            }
        });

        itineraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItineraryListItem listItem = (ItineraryListItem)parent.getItemAtPosition(position);
                itinerarySelectedCallback.onItinerarySelected(listItem);
            }
        });

        return rootView;

    }

    public class ItineraryAdapter extends ArrayAdapter<ItineraryListItem> {

        //placeholder for TextViews
        private class ViewHolder {
            TextView itineraryName;
            TextView date;
        }

        public ItineraryAdapter(Context context, List<ItineraryListItem> itineraryItem) {
            super(context, R.layout.itinerary_list_item, itineraryItem);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ItineraryListItem item = getItem(position);

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.itinerary_list_item, parent, false);
                holder.itineraryName = (TextView)convertView.findViewById(R.id.itinerary_name);
                holder.date = (TextView)convertView.findViewById(R.id.itinerary_date_created);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            //set the text of itinerary name
            holder.itineraryName.setText(item.itineraryName);

            //set text of date
            holder.date.setText("Date created: " + item.dateCreated);

            return convertView;
        }
    }

}
