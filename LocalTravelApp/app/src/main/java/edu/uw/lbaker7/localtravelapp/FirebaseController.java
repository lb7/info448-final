package edu.uw.lbaker7.localtravelapp;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseController {
    private static FirebaseController mInstance;

    private FirebaseAuth mAuth;

    private FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
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
}
