package edu.uw.lbaker7.localtravelapp.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.FirebaseController;
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
    private static FirebaseController firebaseController;

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

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_itinerary_list, container, false);
        final ListView itineraryListView = (ListView)rootView.findViewById(R.id.itinerary_list);
        data = new ArrayList<>();
        adapter = new ItineraryAdapter(getActivity(), data);
        itineraryListView.setAdapter(adapter);

        firebaseController = FirebaseController.getInstance();

        //get the itineraries from Firebase
        firebaseController.getItineraries(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String itineraryKey = dataSnapshot.getKey();
                String itineraryName = dataSnapshot.child("itineraryName").getValue().toString();
                String dateCreated = dataSnapshot.child("dateCreated").getValue().toString();

                ItineraryListItem item = new ItineraryListItem(itineraryName, dateCreated);
                item.setKey(itineraryKey);

                Object sharedBy = dataSnapshot.child("ownerName").getValue();

                if (!dataSnapshot.child("owner").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                        sharedBy != null) {
                    item.sharedBy = sharedBy.toString();
                }

                data.add(item);
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


        itineraryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.v(TAG, "long click");

                final ItineraryListItem listItem = (ItineraryListItem)parent.getItemAtPosition(position);
                final ImageButton btnDelete = (ImageButton)view.findViewById(R.id.btn_delete_itinerary);
                if (btnDelete.getVisibility() == View.INVISIBLE) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            data.remove(position);
                            firebaseController.deleteItinerary(listItem.getKey());
                            adapter.notifyDataSetChanged();
                            btnDelete.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    btnDelete.setVisibility(View.INVISIBLE);
                }

                return true;
            }
        });


        itineraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "on item click");
                ItineraryListItem listItem = (ItineraryListItem)parent.getItemAtPosition(position);
                itinerarySelectedCallback.onItinerarySelected(listItem);
            }
        });

        //create a new itinerary, prompts user for new itinerary name
        Button btnCreate = (Button)rootView.findViewById(R.id.btn_new_itinerary);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = NewItineraryDialog.newInstance();
                dialog.show(getFragmentManager(), "new itinerary dialog");
            }
        });

        return rootView;

    }

    // Custom adapter to convert ItineraryListItem to ListViews
    public class ItineraryAdapter extends ArrayAdapter<ItineraryListItem> {

        //placeholder for TextViews
        private class ViewHolder {
            TextView itineraryName;
            TextView date;
            TextView sharedBy;
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
                holder.sharedBy = (TextView) convertView.findViewById(R.id.itinerary_shared_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            //set the text of itinerary name
            holder.itineraryName.setText(item.itineraryName);

            //set text of date
            holder.date.setText("Date created: " + item.dateCreated);

            if (item.sharedBy != null) {
                holder.sharedBy.setText("| Shared by " + item.sharedBy);
            } else {
                holder.sharedBy.setText("");
            }

            return convertView;
        }
    }

    //Dialog for creating a new itinerary
    public static class NewItineraryDialog extends DialogFragment {

        public static NewItineraryDialog newInstance() {

            Bundle args = new Bundle();

            NewItineraryDialog fragment = new NewItineraryDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final EditText itineraryNameInput = new EditText(getContext());
            itineraryNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
            itineraryNameInput.setHint("Itinerary Name");

            return new AlertDialog.Builder(getActivity())
                    .setTitle("Create Itinerary")
                    .setView(itineraryNameInput)
                    .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            String dateString = sdf.format(date);
                            String newItineraryName = itineraryNameInput.getText().toString();
                            if (!newItineraryName.isEmpty()) {
                                ItineraryListItem newItem = new ItineraryListItem(newItineraryName, dateString);
                                firebaseController.addItinerary(newItem);
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
        }
    }

}
