package edu.uw.lbaker7.localtravelapp;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseController {

    private static final String TAG = "FirebaseController";

    private static FirebaseController mInstance;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    public static FirebaseController getInstance() {
        if (mInstance == null) {
            mInstance = new FirebaseController();
        }
        return mInstance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseUser signInOrCreateAccount(final String email,
                                              final String password,
                                              final OnCompleteListener<AuthResult> listener) {

        mAuth.fetchProvidersForEmail("luke.baker7@gmail.com")
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (task.isSuccessful() && task.getResult().getProviders().size() > 0) {
                    //Email is available
                    createAccount(email, password, listener);
                } else {
                    //Email is in use already
                    signIn(email, password, listener);
                }
            }
        });
        return null;
    }

    private void createAccount(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    private void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void getItineraries(ValueEventListener listener) {
        DatabaseReference ref = mDatabase.getReference("itineraries");
        ref.addValueEventListener(listener);
    }
}
