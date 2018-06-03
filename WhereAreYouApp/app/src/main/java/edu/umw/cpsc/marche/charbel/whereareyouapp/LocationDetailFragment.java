package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationDetailFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    public LocationDetailFragment() {
        // Required empty public constructor
    }
    private GoogleMap mMap;
    private MapView mapView;
    String latitude = null;
    String longitude = null;
    String contact = null;
    boolean isDualPane = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            if (isDualPane == false) {
                //Then dual pane
                latitude = ((LocationDetail) getActivity()).lat;
                longitude = ((LocationDetail) getActivity()).lon;
                contact = ((LocationDetail) getActivity()).contact;
            }
        }
        catch (ClassCastException e){
            Intent intentLocDet = new Intent(getContext(), LocationDetail.class);
            intentLocDet.putExtra("Lat", latitude);
            intentLocDet.putExtra("Lon", longitude);
            intentLocDet.putExtra("Contact", contact);
        }
        isDualPane = false;
        if (latitude != null && longitude != null) {
            Double lat = Double.parseDouble(latitude);
            Double lon = Double.parseDouble(longitude);
            Log.d("Lat: ", lat.toString());
            Log.d("Lon: ", lon.toString());
            Log.d("Contact: ", contact);
            doMarker(lat, lon);
        }
    }

    private void doMarker(Double lat, Double lon){
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LatLng loc = new LatLng(lat, lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 10);
        mMap.animateCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions().position(loc).title(contact + "'s location!"));
    }

    public void updateView(String latitude, String longitude, String contact, boolean isDualPane){
        Log.d("LocDetFrag: ", "we here bro");
        this.latitude = latitude;
        this.longitude = longitude;
        this.contact = contact;
        this.isDualPane = (isDualPane);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null){
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location_detail, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);


        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
