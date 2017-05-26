package edu.uw.lbaker7.localtravelapp.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.activitites.MapsActivity;


public class SetDisplayNameDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set display name")
                .setView(inflater.inflate(R.layout.dialog_display_name, null))
                .setPositiveButton(R.string.setDisplayName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        TextView nameText = (TextView) ((Dialog) dialog).findViewById(R.id.nameField);

                        if (user != null) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameText.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdate);
                            startActivity(new Intent(getContext(), MapsActivity.class));
                            dialog.dismiss();
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        return builder.create();
    }
}
