package edu.uw.lbaker7.localtravelapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

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
        mUser = mAuth.getCurrentUser();
    }

    public static FirebaseController getInstance() {
        if (mInstance == null) {
            mInstance = new FirebaseController();
        }
        return mInstance;
    }

    /**
     * Gets the current user. This value is updated automatically on auth state changes. Will be null if no
     * user is logged in.
     */
    @Nullable
    public FirebaseUser getUser() {
        return mUser;
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
                if (task.isSuccessful()) {
                    List<String> providers = task.getResult().getProviders();
                    if (providers != null && !providers.isEmpty()) {
                        //Email is available
                        signIn(email, password, listener);
                    } else {
                        //Email is in use already
                        createAccount(email, password, listener);
                    }
                } else {
                    Log.w(TAG, task.getException());
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
     * Returns the key of the itinerary that was created. Returns null if the user is not signed in.
     */
    @Nullable
    public String addItinerary(ItineraryListItem itinerary) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference itineraryReference = mItinerariesReference.push();
            itineraryReference.setValue(itinerary);
            itineraryReference.child("owner").setValue(user.getUid());
            itineraryReference.child("ownerName").setValue(user.getDisplayName());

            String itineraryKey = itineraryReference.getKey();

            mUsersReference.child(user.getUid())
                    .child("itineraries")
                    .child(itineraryKey)
                    .setValue(true);
            return itineraryKey;
        }
        return null;
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

            mItinerariesReference.orderByChild("sharedTo").equalTo(mUser.getEmail())
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

    public void shareItineraryToUser(final String itineraryId, final String email) {
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                List<String> providers = task.getResult().getProviders();
                if (task.isSuccessful() && providers != null && !providers.isEmpty()) {
                    mUsersReference
                            .orderByChild("email")
                            .equalTo(email)
                            .limitToFirst(1)
                            .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            dataSnapshot.child("itineraries").child(itineraryId).child("sharedBy").getRef()
                                    .setValue(mAuth.getCurrentUser().getDisplayName());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mItinerariesReference.child(itineraryId).child("sharedTo").setValue(email);
                }
            }
        });
    }


}
