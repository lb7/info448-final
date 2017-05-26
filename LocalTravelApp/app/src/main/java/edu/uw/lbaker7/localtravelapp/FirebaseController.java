package edu.uw.lbaker7.localtravelapp;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseController implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "FirebaseController";

    private static FirebaseController mInstance;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mItinerariesReference;
    private DatabaseReference mUsersReference;
    private FirebaseUser mUser;

    private FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mItinerariesReference = mDatabase.getReference("/itineraries");
        mUsersReference = mDatabase.getReference("/users");

        mAuth.addAuthStateListener(this);
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

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mUser = firebaseAuth.getCurrentUser();
    }

    public FirebaseUser signInOrCreateAccount(final String email,
                                              final String password,
                                              @NonNull final OnCompleteListener<AuthResult> listener) {
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

    private void createAccount(String email,
                               String password,
                               @NonNull OnCompleteListener<AuthResult> listener) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();

                            DatabaseReference ref = mDatabase.getReference("/users");
                            ref.child(user.getUid()).child("email").setValue(user.getEmail());
                        }
                    }
                });
    }

    private void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    /**
     * Adds an itinerary to the current users list of itineraries
     *
     */
    public void addItinerary(ItineraryListItem itinerary) {
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

    /**
     * The listener will fire for all entries in the "itineraries" key of the Firebase database.
     * Call datasnapshot.getValue(ItineraryListItem.class) or use individual datasnapshot.child().getValue()
     * calls to get information on the itineraries
     */
    public void getItineraries(ChildEventListener listener) {
        if (mUser != null) {
            mItinerariesReference.orderByChild("owner").equalTo(mUser.getUid())
                    .addChildEventListener(listener);
        }
    }

    /**
     * Deletes the itinerary with the given id. Id can be obtained in the listener passed to
     * FirebaseController.getItineraries(). Use datasnapshot.getKey() to get the itinerary key. This should
     * be saved for later access
     */
    public void deleteItinerary(String itineraryId) {
        if (mUser != null) {
            mItinerariesReference.child(itineraryId).removeValue();
            mUsersReference.child(mUser.getUid()).child("itineraries").child(itineraryId).removeValue();
        }
    }

    /**
     * Add a placeId to the itinerary with the given id.
     */
    public void addPlaceToItinerary(String placeId, String itineraryId) {
        mItinerariesReference.child(itineraryId).child("places").child(placeId).setValue(true);
    }

    /**
     * Listener will fire on all places inside the "places" child of the specified itinerary
     */
    public void getPlacesFromItinerary(String itineraryId, ChildEventListener listener) {
        mItinerariesReference.child(itineraryId).child("places").addChildEventListener(listener);
    }

    /**
     * Deletes the place from the specified itinerary
     */
    public void deletePlaceFromItinerary(String placeId, String itineraryId) {
        mItinerariesReference.child(itineraryId).child("places").child(placeId).removeValue();
    }


}
