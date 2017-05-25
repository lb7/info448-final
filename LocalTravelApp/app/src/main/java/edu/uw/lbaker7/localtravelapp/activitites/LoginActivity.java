package edu.uw.lbaker7.localtravelapp.activitites;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.uw.lbaker7.localtravelapp.FirebaseController;
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
                    } else {
                        Log.w(TAG, task.getException());
                    }
                }
            });
        } else {
            Log.i(TAG, user.getEmail());
        }
    }

    //private void makeItineraries
}
