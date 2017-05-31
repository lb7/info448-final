package edu.uw.lbaker7.localtravelapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christa Joy Jaeger on 5/26/2017.
 */

public class AddPlaceDialog extends DialogFragment {
    private PlaceItem placeItem;

    private static final String TAG = "ItineraryDialog";

    private List<ItineraryListItem> data;
    private ItineraryAdapter adapter;
    private static FirebaseController firebaseController;
    private String itineraryKey;


    public static AddPlaceDialog newInstance(PlaceItem place) {
        Bundle args = new Bundle();
        AddPlaceDialog fragment = new AddPlaceDialog();
        Log.v(TAG, place.placeName);
        args.putParcelable("Place", place);
        fragment.setArguments(args);
        return fragment;
    }





    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog builderSent;
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        placeItem = getArguments().getParcelable("Place");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_itinerary_list, null);

        final ListView itineraryListView = (ListView)rootView.findViewById(R.id.itinerary_list);
        data = new ArrayList<>();
        adapter = new ItineraryAdapter(getActivity(), data);
        itineraryListView.setAdapter(adapter);

        firebaseController = FirebaseController.getInstance();

        //get the itineraries from Firebase
        firebaseController.getItineraries(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                itineraryKey = dataSnapshot.getKey();
                String itineraryName = dataSnapshot.child("itineraryName").getValue().toString();
                String dateCreated = dataSnapshot.child("dateCreated").getValue().toString();
                ItineraryListItem item = new ItineraryListItem(itineraryName, dateCreated);
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

        itineraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                firebaseController.addPlaceToItinerary(placeItem.id,  itineraryKey);
                Toast.makeText(getContext(),"Place added to Itinerary", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });

        //create a new itinerary, prompts user for new itinerary name
        Button btnCreate = (Button)rootView.findViewById(R.id.btn_new_itinerary);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = NewItineraryDialog.newInstance(placeItem.id);
                dialog.show(getFragmentManager(), "new itinerary dialog");
            }
        });
        builder.setView(rootView).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //LoginDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();

    }

    // Custom adapter to convert ItineraryListItem to ListViews
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

    public static class NewItineraryDialog extends DialogFragment {
        private String placeKey;
        public static NewItineraryDialog newInstance() {

            Bundle args = new Bundle();

            NewItineraryDialog fragment = new NewItineraryDialog();
            fragment.setArguments(args);
            return fragment;
        }
        public static NewItineraryDialog newInstance(String key) {

            Bundle args = new Bundle();

            NewItineraryDialog fragment = new NewItineraryDialog();
            args.putString("PlaceId", key);
            fragment.setArguments(args);
            return fragment;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            placeKey = getArguments().getString("PlaceId");

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
                            ItineraryListItem newItem = new ItineraryListItem(newItineraryName, dateString);
                            String ItID = firebaseController.addItinerary(newItem);
                            firebaseController.addPlaceToItinerary(placeKey,  ItID);
                            Toast.makeText(getContext(),"Place added to Itinerary", Toast.LENGTH_LONG).show();

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
