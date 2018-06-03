package edu.umw.cpsc.marche.charbel.whereareyouapp;



import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Settings extends AppCompatActivity implements DialogInterface.OnClickListener{
    GoogleSignInSingleton googleSignInSingleton = GoogleSignInSingleton.getInstance();
    UserInformationSingleton userInformationSingleton = UserInformationSingleton.getInstance();
    VolleySingleton volleySingleton = VolleySingleton.getInstance();
    MyFileIO myFileIO = new MyFileIO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        EditText usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setText(userInformationSingleton.getUsername());
    }

    @Override
    public void onResume(){
        super.onResume();
        final EditText usernameEditText = findViewById(R.id.usernameEditText);
        Activity act = (Activity)this;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                usernameEditText.setText(userInformationSingleton.getUsername());
            }});
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    public void signOut(View view) {
        if (userInformationSingleton.getLoggedIn()) {
            googleSignInSingleton.getSignIn().signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                    userInformationSingleton.setLoggedIn(false);
                    showSignedOut();
                }
            });
        }
        else{
            showUserSignedIn();
        }
    }

    public void update(View view) {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        String username = usernameEditText.getText().toString();
        myFileIO.writeToFile(username+"\n", "username.txt", this);
        userInformationSingleton.setUsername(username);
        MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();
        String token = myFirebaseInstanceIdService.getToken();
        myFirebaseInstanceIdService.sendRegistrationToServer(token);
    }

    //*****************DIALOGS/oldstuff*******************

    private void showUserSignedIn(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cannot_sign_out_title);
        builder.setMessage(R.string.cannot_sign_out_message);
        builder.setNegativeButton(R.string.okay, this);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSignedOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sign_out_successful_title);
        builder.setMessage(R.string.sign_out_successful_message);
        builder.setNegativeButton(R.string.okay, this);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Called when the user touches the button */
    /*public void saveAndReturn(View view) {
        // Figure out if send and the value of everything
        RadioButton send = findViewById(R.id.radioSend);
        EditText latitudeEditText = findViewById(R.id.latitude);
        Float latitude = Float.valueOf(latitudeEditText.getText().toString());
        EditText longitudeEditText = findViewById(R.id.longitude);
        Float longitude = Float.valueOf(longitudeEditText.getText().toString());
        EditText contactNameEditText = findViewById(R.id.contact_name);
        String enteredContact = contactNameEditText.getText().toString();
        String filename;
        File file;
        if (send.isChecked()) {
            //Then save data to send file
            filename = getString(R.string.sent_requests_file);
            //file = new File(filename);
        }
        else {
            //Then save data to received file
            filename = getString(R.string.received_requests_file);
            //file = new File(filename);
        }
        //Put the string together with '/' as delimiter
        String fileContents = enteredContact + "/" + longitude + "/" + latitude + '\n';

        //Write to the file
        try {
            Log.d("Settings", "Starting to write to file " + filename);
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_APPEND);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Settings", "Finished write to file " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Go back
        onBackPressed();
    }*/

    private void showResultDialog(String result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result");
        builder.setMessage("The result is " + result);
        builder.setPositiveButton(R.string.next, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
