package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class NotificationClickedActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    VolleySingleton volleySingleton = VolleySingleton.getInstance();
    UserInformationSingleton userInformationSingleton = UserInformationSingleton.getInstance();
    private FusedLocationProviderClient fusedLocationClient;
    MyFileIO myFileIO = new MyFileIO();
    String globalFilename;
    String globalRequestorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_clicked);

        TextView sendLocationPromptTextView = findViewById(R.id.sendLocationPrompt);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        final Intent intent = getIntent();
        final Context currentContext = this;
        String type = intent.getStringExtra("type");
        String username = intent.getStringExtra("username");
        sendLocationPromptTextView.setText(getString(R.string.received_loc_req_message) + " " + username);
        final String requestorEmail = intent.getStringExtra("requestorEmail");
        globalRequestorEmail = requestorEmail;
        Log.d("NotificationClicked", "Type is " + type);
        final String filename = getString(R.string.sent_requests_file);
        globalFilename = filename;
        if (type.equals("positive")){
            doPositive(filename, requestorEmail);
        }
        else if (type.equals("negative")){
            //Send the denial to the server
            doNegative(requestorEmail);
        }
        else {
            showAcceptDenyLocationRequest(username);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MyFirebaseMessagingService.SIMPLE_NOTIFICATION_ID);
    }

    private void showAcceptDenyLocationRequest(String username){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.received_loc_req_title) + " " + username);
        builder.setMessage(getString(R.string.received_loc_req_message) + " " + username);
        builder.setNegativeButton(R.string.notification_negative, this);
        builder.setPositiveButton(R.string.notification_positive, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doNegative(View v){
        doNegative(globalRequestorEmail);
    }

    public void doPositive(View v){
        doPositive(globalFilename, globalRequestorEmail);
    }

    public void doNegative(String requestorEmail){
        volleySingleton.sendLocationDenied(requestorEmail, userInformationSingleton.getUsername());
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MyFirebaseMessagingService.SIMPLE_NOTIFICATION_ID);
        Intent returnToMain = new Intent(this, MainActivity.class);
        startActivity(returnToMain);
    }

    public void doPositive(final String filename, final String requestorEmail){
        final Context currentContext = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    Log.d("In contact", "List item click in on success " + filename);
                    if (location != null) {
                        // Logic to handle location object}
                        String lat = String.valueOf(location.getLatitude());
                        String lon = String.valueOf(location.getLongitude());
                        //Then we need to send the location

                        volleySingleton.sendLocationAccepted(requestorEmail, userInformationSingleton.getUsername(), lat, lon);
                        //Then here we need to save this location and username to the file,
                        //Put the string together with '/' as delimiter
                        String fileContents = userInformationSingleton.getUsername() + "/" + lon + "/" + lat + '\n';
                        //Write to the file
                        myFileIO.writeToFile(fileContents, filename, currentContext);
                    }
                }
            });
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MyFirebaseMessagingService.SIMPLE_NOTIFICATION_ID);
        Intent returnToMain = new Intent(this, MainActivity.class);
        startActivity(returnToMain);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                doPositive(globalFilename, globalRequestorEmail);
                Intent returnMain = new Intent(this, MainActivity.class);
                startActivity(returnMain);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                doNegative(globalRequestorEmail);
                Intent returnToMain = new Intent(this, MainActivity.class);
                startActivity(returnToMain);
                break;
            default:
                throw new IllegalArgumentException("There is only Postive and Negative wyd fam");
        }
    }
}
