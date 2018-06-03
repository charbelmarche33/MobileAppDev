package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemSelectedListener, DialogInterface.OnClickListener {
    public boolean isDualPane;
    public String lat;
    public String lon;
    public String contact;
    private int index = -1;
    private static final int READ_CONTACTS_PERMISSION_ID = 1;
    private LocationDetailFragment detail;
    private int isContactDialog = 0;
    private static int RC_SIGN_IN = 25; //25 is arbitrary
    UserInformationSingleton userInformationSingleton = UserInformationSingleton.getInstance();
    GoogleSignInSingleton googleSignInSingleton = GoogleSignInSingleton.getInstance();
    MyFileIO myFileIO = new MyFileIO();
    VolleySingleton volleySingleton = VolleySingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for permissions!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            isContactDialog = 1;
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){
                showPermissionRationale();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_ID);
            }
        }

        // Check for permissions!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isContactDialog = 0;
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                showPermissionRationaleLocation();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

        attachFragments();

        if (userInformationSingleton.getLoggedIn() == false) {
            //HideFragments
            hideFragments();
        }
        else {
            //User logged in
            if (getIntent() != null ){
                //Delete the notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(MyFirebaseMessagingService.SIMPLE_NOTIFICATION_ID);

                if(getIntent().getStringExtra("username") != null) {
                    Log.d("Main Activity", "Intent is NOTNULL");


                    //Then we came here from the "you just got a location" notification
                    Intent intent = getIntent();
                    contact = intent.getStringExtra("username");
                    lat = intent.getStringExtra("lat");
                    lon = intent.getStringExtra("lon");

                    //Write lat long username to file...
                    String filename = getString(R.string.received_requests_file);

                    //Then here we need to save this location and username to the file,
                    //Put the string together with '/' as delimiter
                    String fileContents = contact + "/" + lon + "/" + lat + '\n';
                    myFileIO.writeToFile(fileContents, filename, this);

                    if (isDualPane) {
                        Log.d("Main Activity", "in dual pane");
                        TabLayout tabLayout = findViewById(R.id.tabLayoutDualPane);
                        TabLayout.Tab tab = tabLayout.getTabAt(1);
                        tab.select();
                        updateLocationDetail(lat, lon, contact);
                        //Now it is selected set that location somehow...
                    } else {
                        //Make Intent to location detail
                        Intent intentLocDet = new Intent(this, LocationDetail.class);
                        intentLocDet.putExtra("Lat", lat);
                        intentLocDet.putExtra("Lon", lon);
                        intentLocDet.putExtra("Contact", contact);
                        startActivityForResult(intentLocDet, 1);
                    }
                }
            }
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            com.google.android.gms.common.SignInButton loginButton = findViewById(R.id.sign_in_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Do log in stuff
                    showSignIn();
                }
            });
            userInformationSingleton.setLoggedIn(false);
            loginButton.setVisibility(View.VISIBLE);
            hideFragments();
        }
        else {
            userInformationSingleton.setLoggedIn(true);
            com.google.android.gms.common.SignInButton loginButton = findViewById(R.id.sign_in_button);
            loginButton.setVisibility(View.GONE);
            Log.d("Thinks they logged in","showing fucking frag");
            showFragments();
        }

        List<String> pastUserNames = new ArrayList<String>();
        String path = getFilesDir() + "/username.txt";
        pastUserNames = myFileIO.getAllFromFile(pastUserNames, path, this);

        //The last username that was entered
        if (pastUserNames.size() != 0) {
            Log.d("This is username we use", pastUserNames.get(pastUserNames.size() - 1));
            userInformationSingleton.setUsername(pastUserNames.get(pastUserNames.size() - 1));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (userInformationSingleton.getLoggedIn() == false) {
            //HideFragments
            hideFragments();
            showLogIn();
            Log.d("User not logged in","Hide the motherfucking fragments");
        }
        else{
            Log.d("Thinks they logged in","showing fucking frag in resume");
            showFragments();
            hideLogIn();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                if (isContactDialog == 1) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_ID);
                    break;
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                finishAndRemoveTask();
                System.exit(0);
            default:
                throw new IllegalArgumentException("There can only be one option here");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Sign in Success!
                userInformationSingleton.setLoggedIn(true);
                userInformationSingleton.setUserEmail(account.getEmail());
                hideLogIn();
                Log.d("Hi","Hi we are here hello");
            }
            catch (ApiException e) {
                // The ApiException status code indicates the failure reason.
                // Sign in fail
                showSignInError();
                userInformationSingleton.setLoggedIn(false);
                com.google.android.gms.common.SignInButton loginButton = findViewById(R.id.sign_in_button);
                loginButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i("MainActivity", "Save instance state: " + index);
        if (index >= 0)
            savedInstanceState.putInt("index", index);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("contact dialog", String.valueOf(isContactDialog));
        switch (requestCode) {
            case READ_CONTACTS_PERMISSION_ID: {
                // If request was denied, the results array is empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContactListFragment clf;
                    if (isDualPane) {
                        clf = (ContactListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_dual_pain_one);
                    }
                    else {
                        clf = (ContactListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    }
                    clf.setupAdapter();
                }
                else {
                    showClosingOutOfApp();
                }
            }
            case 0: {
                if (isContactDialog ==1 || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("contact dialog", String.valueOf(isContactDialog));
                }
                else {
                    showClosingOutOfAppLocation();
                }
            }
        }
    }

    public void showLogIn(){
        final com.google.android.gms.common.SignInButton loginButton = findViewById(R.id.sign_in_button);
        Activity act = (Activity)this;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                loginButton.setVisibility(View.VISIBLE);
                Log.d("Thinks they logged in","showing fucking frag in showlogin");
                hideFragments();
            }});
    }

    public void hideLogIn(){
        final com.google.android.gms.common.SignInButton loginButton = findViewById(R.id.sign_in_button);
        Activity act = (Activity)this;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                loginButton.setVisibility(View.GONE);
            }});
    }
    /** Called when the user touches the button */
    public void viewSettings(MenuItem menuItem) {
        // Do something in response to button click
        Intent intent = new Intent(this, Settings.class);
        startActivityForResult(intent, 1);
    }

    public boolean getIsDualPane(){
        return isDualPane;
    }

    /** Called when the user touches the button */
    public void updateLocationDetail(String lat, String lon, String contact) {
        if (detail != null) {
            detail.updateView(lat, lon, contact, isDualPane);
        }
    }

    @Override
    public void itemSelected(int index) {
        this.index = index;
        //Nothing is done here :/
    }

    public void hideFragments(){
        Activity act = (Activity)this;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(isDualPane){
                    FrameLayout fragContainerOne = (FrameLayout) findViewById(R.id.fragment_container_dual_pain_one);
                    FrameLayout fragContainerTwo = (FrameLayout) findViewById(R.id.fragment_container_dual_pain_two);
                    fragContainerOne.setVisibility(View.GONE);
                    fragContainerTwo.setVisibility(View.GONE);
                }
                else {
                    FrameLayout fragContainer = (FrameLayout) findViewById(R.id.fragment_container);
                    fragContainer.setVisibility(View.GONE);
                }
            }});
    }

    public void showFragments(){
        Log.d("SHOW THE FRAGEMEMMEMEM", "FUCKIN THE THING IS HERE");
        Activity act = (Activity)this;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(isDualPane){
                    FrameLayout fragContainerOne = (FrameLayout) findViewById(R.id.fragment_container_dual_pain_one);
                    FrameLayout fragContainerTwo = (FrameLayout) findViewById(R.id.fragment_container_dual_pain_two);
                    fragContainerOne.setVisibility(View.VISIBLE);
                    fragContainerTwo.setVisibility(View.VISIBLE);
                }
                else {
                    FrameLayout fragContainer = (FrameLayout) findViewById(R.id.fragment_container);
                    fragContainer.setVisibility(View.VISIBLE);
                }
            }});
    }


    public void attachFragments(){
        // Do the orientation stuff
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches +
                yInches*yInches);
        if (diagonalInches >= 6.5){
            // 6.5inch device or bigger, then you could do the dual pane option
            if(metrics.heightPixels > metrics.widthPixels) {
                //Do something if portrait
                isDualPane = false;
                setContentView(R.layout.activity_main);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                //Set fragment to the contact list fragment
                ContactListFragment contactListFrag = new ContactListFragment();
                ft.replace(R.id.fragment_container, contactListFrag);

                //Commit changes
                ft.commit();

                //Select tabs, have three tabs!
                TabLayout tabLayout = findViewById(R.id.tabLayout);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        //Change fragment to proper fragment
                        int tabSelected = tab.getPosition();
                        switch(tabSelected) {
                            case 0:
                                Log.i("MainActivity","In case 0");
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                //Set fragment to the contact list fragment
                                ContactListFragment contactListFrag = new ContactListFragment();
                                fragmentTransaction.replace(R.id.fragment_container, contactListFrag);

                                //Commit changes
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                Log.i("MainActivity","In case 1");
                                FragmentManager fragmentManager1 = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();

                                //Set fragment to the received locations fragment
                                ReceivedLocationsFragment receivedLocationsFrag = new ReceivedLocationsFragment();
                                fragmentTransaction1.replace(R.id.fragment_container, receivedLocationsFrag);

                                //Commit changes
                                fragmentTransaction1.commit();
                                break;
                            case 2:
                                Log.i("MainActivity","In case 2");
                                FragmentManager fragmentManager2 = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();

                                //Set fragment to the location request fragment
                                LocationRequestsFragment locationRequestsFrag = new LocationRequestsFragment();
                                fragmentTransaction2.replace(R.id.fragment_container, locationRequestsFrag);

                                //Commit changes
                                fragmentTransaction2.commit();
                                break;
                            default:
                                //There was an error, catch that error
                                throw new IndexOutOfBoundsException("You are missing the implementation for " +
                                        "that tab. Tab selected is " + tabSelected + ". Add it.");
                        }
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        //Do nothing
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        //Do nothing
                    }
                });
                TabLayout.Tab tabToBeSelected = tabLayout.getTabAt(0);
                tabToBeSelected.select();
            }
            else {
                //Do something if landscape
                isDualPane = true;
                setContentView(R.layout.activity_main_dual_pane);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                //Set fragment to the contact list fragment
                ContactListFragment contactListFrag = new ContactListFragment();
                LocationRequestsFragment locationRequestsFrag = new LocationRequestsFragment();
                ft.replace(R.id.fragment_container_dual_pain_one, contactListFrag);
                ft.replace(R.id.fragment_container_dual_pain_two, locationRequestsFrag);

                //Commit changes
                ft.commit();

                //Select tabs, have two tabs!
                TabLayout tabLayout = findViewById(R.id.tabLayoutDualPane);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        //Change fragment to proper fragment
                        int tabSelected = tab.getPosition();
                        switch (tabSelected) {
                            case 0:
                                Log.i("MainActivity", "In case 0");
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                //Set fragment to the contact list fragment
                                ContactListFragment contactListFrag = new ContactListFragment();
                                LocationRequestsFragment locationRequestsFrag = new LocationRequestsFragment();
                                fragmentTransaction.replace(R.id.fragment_container_dual_pain_one, contactListFrag);
                                fragmentTransaction.replace(R.id.fragment_container_dual_pain_two, locationRequestsFrag);

                                //Commit changes
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                Log.i("MainActivity", "In case 1");
                                FragmentManager fragmentManager1 = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();

                                //Set fragment to the received locations fragment
                                ReceivedLocationsFragment receivedLocationsFrag = new ReceivedLocationsFragment();
                                LocationDetailFragment locationDetailFragment = new LocationDetailFragment();
                                fragmentTransaction1.replace(R.id.fragment_container_dual_pain_one, receivedLocationsFrag);
                                fragmentTransaction1.replace(R.id.fragment_container_dual_pain_two, locationDetailFragment);
                                detail = locationDetailFragment;

                                //Commit changes
                                fragmentTransaction1.commit();
                                break;
                            default:
                                //There was an error, catch that error
                                throw new IndexOutOfBoundsException("You are missing the implementation for " +
                                        "that tab in dualpane. Tab selected is " + tabSelected + ". Add it.");
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        //Do nothing
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        //Do nothing
                    }
                });
                TabLayout.Tab tabToBeSelected = tabLayout.getTabAt(0);
                tabToBeSelected.select();
            }
        }
        else {
            // smaller device just keep it single pane
            //Do something if portrait
            isDualPane = false;
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            //Set fragment to the contact list fragment
            ContactListFragment contactListFrag = new ContactListFragment();
            ft.replace(R.id.fragment_container, contactListFrag);

            //Commit changes
            ft.commit();

            //Select tabs, have three tabs!
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //Change fragment to proper fragment
                    int tabSelected = tab.getPosition();
                    switch(tabSelected) {
                        case 0:
                            Log.i("MainActivity","In case 0");
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            //Set fragment to the contact list fragment
                            ContactListFragment contactListFrag = new ContactListFragment();
                            fragmentTransaction.replace(R.id.fragment_container, contactListFrag);

                            //Commit changes
                            fragmentTransaction.commit();
                            break;
                        case 1:
                            Log.i("MainActivity","In case 1");
                            FragmentManager fragmentManager1 = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();

                            //Set fragment to the received locations fragment
                            ReceivedLocationsFragment receivedLocationsFrag = new ReceivedLocationsFragment();
                            fragmentTransaction1.replace(R.id.fragment_container, receivedLocationsFrag);

                            //Commit changes
                            fragmentTransaction1.commit();
                            break;
                        case 2:
                            Log.i("MainActivity","In case 2");
                            FragmentManager fragmentManager2 = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();

                            //Set fragment to the location request fragment
                            LocationRequestsFragment locationRequestsFrag = new LocationRequestsFragment();
                            fragmentTransaction2.replace(R.id.fragment_container, locationRequestsFrag);

                            //Commit changes
                            fragmentTransaction2.commit();
                            break;
                        default:
                            //There was an error, catch that error
                            throw new IndexOutOfBoundsException("You are missing the implementation for " +
                                    "that tab. Tab selected is " + tabSelected + ". Add it.");
                    }
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //Do nothing
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    //Do nothing
                }
            });
            TabLayout.Tab tabToBeSelected = tabLayout.getTabAt(0);
            tabToBeSelected.select();
        }
    }

    //***************DIALOGS*******************

    private void showClosingOutOfApp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cannot_access_contacts_title);
        builder.setMessage(R.string.cannot_access_contacts_message);
        builder.setNegativeButton(R.string.okay, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showClosingOutOfAppLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cannot_access_location_title);
        builder.setMessage(R.string.cannot_access_location_message);
        builder.setNegativeButton(R.string.okay, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPermissionRationale(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton(R.string.next, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPermissionRationaleLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_dialog_title_location);
        builder.setMessage(R.string.permission_dialog_message_location);
        builder.setPositiveButton(R.string.next, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showSignIn() {
        GoogleSignInClient googleSignInClient = googleSignInSingleton.setAndGetSignIn(this);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showSignInError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cannot_sign_in_title);
        builder.setMessage(R.string.cannot_sign_in_message);
        builder.setNegativeButton(R.string.okay, this);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
