package edu.uw.lbaker7.localtravelapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Christa Joy Jaeger on 5/26/2017.
 */

public class PlacesDialog extends DialogFragment {
    private PlaceItem placeItem;
    private OnItineraryChooseListener itineraryCallback;

    public static PlacesDialog newInstance(PlaceItem place) {
        Bundle args = new Bundle();
        PlacesDialog fragment = new PlacesDialog();
        args.putParcelable("Place", place);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            itineraryCallback = (OnItineraryChooseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnItineraryChooseListener");
        }
    }
    public interface OnItineraryChooseListener {
        void onItineraryChoose(PlaceItem item);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog builderSent;
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        placeItem = getArguments().getParcelable("Place");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View root = inflater.inflate(R.layout.place_list_item, null);

        NetworkImageView placeImage = (NetworkImageView)root.findViewById(R.id.img_place);

        TextView placeName = (TextView)root.findViewById(R.id.txt_place_name);
        TextView address = (TextView)root.findViewById(R.id.txt_place_address);
        TextView rating = (TextView)root.findViewById(R.id.txt_ratings);
        builder.setView(root).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
            }
        });
        final AlertDialog dialog = builder.create();
        root.findViewById(R.id.btn_add_place).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itineraryCallback.onItineraryChoose(placeItem);
            }
        });

        ImageLoader imageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        placeName.setText(placeItem.placeName);

        placeImage.setImageUrl(placeItem.icon, imageLoader);
        address.setText(placeItem.address);
        rating.setText("(" + placeItem.rating + "/5.0)");
                // Add action buttons

        return dialog;
    }


}
