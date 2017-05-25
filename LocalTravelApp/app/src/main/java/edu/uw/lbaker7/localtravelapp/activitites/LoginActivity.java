package edu.uw.lbaker7.localtravelapp.activitites;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uw.lbaker7.localtravelapp.FirebaseController;
import edu.uw.lbaker7.localtravelapp.ItineraryListItem;
import edu.uw.lbaker7.localtravelapp.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth.getInstance().signOut();

        final FirebaseController controller = FirebaseController.getInstance();
        FirebaseAuth auth = controller.getAuth();

        final FirebaseUser user = auth.getCurrentUser();

        if(user == null) {
            controller.signInOrCreateAccount("luke.baker7@gmail.com", "password", new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, task.getResult().getUser().getEmail());
                        controller.addItinerary(new ItineraryListItem("Seattle Day Trip", "1/1/11"), null);
                    } else {
                        Log.w(TAG, task.getException());
                    }
                }
            });
        } else {
            Log.i(TAG, user.getEmail());
        }

        View button = findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getItineraries(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(TAG, dataSnapshot.child("itineraryName").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //private void makeItineraries
}
