package edu.uw.lbaker7.localtravelapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseController {

    private static final String TAG = "FirebaseController";

    private static FirebaseController mInstance;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mItinerariesReference;
    private DatabaseReference mUsersReference;

    private FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mItinerariesReference = mDatabase.getReference("/itineraries");
        mUsersReference = mDatabase.getReference("/users");
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
        mAuth.fetchProvidersForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (task.isSuccessful() && task.getResult().getProviders().size() > 0) {
                    //Email is available
                    signIn(email, password, listener);
                } else {
                    //Email is in use already
                    createAccount(email, password, listener);
                }
            }
        });
        return null;
    }

    private void createAccount(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();

                            DatabaseReference ref = mDatabase.getReference("/users");
                            // TODO: 5/24/2017 Remove placeholder name
                            ref.child(user.getUid()).child("name").setValue("Luke Baker");
                        }
                    }
                });
    }

    private void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void addItinerary(ItineraryListItem itinerary, @Nullable OnCompleteListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference itineraryReference = mItinerariesReference.push();
            itineraryReference.setValue(itinerary);
            itineraryReference.child("owner").setValue(mAuth.getCurrentUser().getUid());

            mUsersReference.child(user.getUid())
                    .child("itineraries")
                    .child(itineraryReference.getKey())
                    .setValue(true);
        }
    }

    public void getItineraries(final ChildEventListener listener) {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mItinerariesReference.orderByChild("owner").equalTo(user.getUid())
                    .addChildEventListener(listener);
        }
    }
}
