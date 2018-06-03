package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by Charbel on 4/20/2018.
 */

public class GoogleSignInSingleton {

    private static final GoogleSignInSingleton GOOGLE_SIGN_IN_SINGLETON = new GoogleSignInSingleton();
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;

    //private constructor to avoid client applications to use constructor
    private GoogleSignInSingleton(){}

    public static GoogleSignInSingleton getInstance(){
        return GOOGLE_SIGN_IN_SINGLETON;
    }

    public GoogleSignInClient setAndGetSignIn(Context context) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
        return googleSignInClient;
    }

    public GoogleSignInClient getSignIn(){
        return googleSignInClient;
    }
}