package edu.uw.lbaker7.localtravelapp.activitites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import edu.uw.lbaker7.localtravelapp.FirebaseController;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.dialogs.SetDisplayNameDialog;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mController = FirebaseController.getInstance();

        View signInButton = findViewById(R.id.buttonLogin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView nameView = (TextView) findViewById(R.id.emailField);
                TextView passwordView = (TextView) findViewById(R.id.passwordField);

                CharSequence name = nameView.getText();
                CharSequence password = passwordView.getText();

                if (mController.getUser() == null && name.length() > 0 && password.length() > 0) {
                    mController.signInOrCreateAccount(
                            name.toString(),
                            password.toString(),
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();
                                        if (user.getDisplayName() == null) {
                                            new SetDisplayNameDialog().show(
                                                    getSupportFragmentManager(),
                                                    "displayNameDialog"
                                            );

                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                        }
                                    } else {
                                        Log.w(TAG, task.getException());
                                        Toast.makeText(LoginActivity.this, "There was a problem logging in" +
                                                ".", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }


        });

    }
}

