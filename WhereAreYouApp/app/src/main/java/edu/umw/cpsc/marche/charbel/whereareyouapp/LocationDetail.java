package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LocationDetail extends AppCompatActivity {
    public String lat;
    public String lon;
    public String contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("Lat")!= null) {
            lat = bundle.getString("Lat");
        }
        if(bundle.getString("Lon")!= null) {
            lon = bundle.getString("Lon");
        }
        if(bundle.getString("Contact")!= null) {
            contact = bundle.getString("Contact");
        }
        //Get contact name
        setContentView(R.layout.activity_location_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Set fragment to the contact list fragment
        LocationDetailFragment locationDetailFragment = new LocationDetailFragment();
        fragmentTransaction.replace(R.id.fragment_loc_detail, locationDetailFragment);

        //Commit changes
        fragmentTransaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
