package edu.uw.lbaker7.localtravelapp.activitites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import edu.uw.lbaker7.localtravelapp.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth.getInstance().signOut();

        /*final FirebaseController controller = FirebaseController.getInstance();
        FirebaseAuth auth = controller.getAuth();

        final FirebaseUser user = auth.getCurrentUser();

        if(user == null) {
            controller.signInOrCreateAccount("luke.baker7@gmail.com", "password", new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, task.getResult().getUser().getEmail());
                        //controller.addItinerary(new ItineraryListItem("Seattle Day Trip", "1/1/11"), null);
                    } else {
                        Log.w(TAG, task.getException());
                    }
                }
            });
        } else {
            Log.i(TAG, user.getEmail());
        }*/
    }
}
