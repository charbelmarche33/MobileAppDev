package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService implements DialogInterface.OnClickListener{

    private static String TAG = "MyFirebaseInstanceIdService";
    private VolleySingleton volleySingleton = VolleySingleton.getInstance();
    private UserInformationSingleton userInformationSingleton = UserInformationSingleton.getInstance();

    public String getToken(){
        return FirebaseInstanceId.getInstance().getToken();
    }

       @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (userInformationSingleton.getUsername()!=null) {
            sendRegistrationToServer(refreshedToken);
        }
    }

    public void sendRegistrationToServer(String token) {
        String username = userInformationSingleton.getUsername().toString();
        volleySingleton.registerContact(username, token);
    }

    public void sendLocationRequest(String email){
        try {
            String username = userInformationSingleton.getUsername().toString();
            volleySingleton.requestLoction(username, email);
        }
        catch (NullPointerException e){

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                throw new IllegalArgumentException("There can only be one option here");
        }
    }
}
