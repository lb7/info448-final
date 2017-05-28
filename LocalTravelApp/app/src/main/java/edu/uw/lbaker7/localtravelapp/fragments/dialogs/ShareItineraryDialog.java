package edu.uw.lbaker7.localtravelapp.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import edu.uw.lbaker7.localtravelapp.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Ryan Magee on 5/26/2017.
 */

public class ShareItineraryDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_share, null))
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TextView emailView = (TextView) ((Dialog)dialog).findViewById(R.id.enterEmail);
                        String email = emailView.getText().toString();
                        Log.v(TAG, "email entered: " + email);
                        //share itinerary through firebase
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
