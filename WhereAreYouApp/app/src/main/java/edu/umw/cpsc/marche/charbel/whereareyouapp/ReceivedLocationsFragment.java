package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedLocationsFragment extends android.support.v4.app.ListFragment {
    public List<String> namesArray;
    private ItemSelectedListener itemSelectedListener;
    private int position = -1;
    private final static String SELECTED = "Selected";
    private MyListAdapter myListAdapter;
    MyFileIO myFileIO = new MyFileIO();


    public ReceivedLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if (container != null){
            container.removeAllViews();
        }
        namesArray = new ArrayList<String>();
        return inflater.inflate(R.layout.fragment_location_requests, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        namesArray = new ArrayList<String>();

        // Inflate the layout for this fragment
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            itemSelectedListener = (ItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ItemSelectedListener");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        String path = getContext().getFilesDir() + "/" + getString(R.string.received_requests_file);
        namesArray = myFileIO.getAllFromFile(namesArray, path, getContext());

        myListAdapter = new MyListAdapter(this.getContext(), R.layout.rec_loc_list_item, namesArray);
        this.setListAdapter(myListAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(position >= 0)
            outState.putInt(SELECTED, position);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.position = position;
        //if () {
        //    v.setSelected(true);
        //}
        String lat = myListAdapter.getLatitude(v);
        String lon = myListAdapter.getLongitude(v);
        String contact = myListAdapter.getContactName(v);
        if (((MainActivity) getActivity()).getIsDualPane()){
            Log.i("ReceivedLocFrag","Updating Details");
            Log.d("Details", "Is not null");
            ((MainActivity) getActivity()).lat = lat;
            ((MainActivity) getActivity()).lon = lon;
            ((MainActivity) getActivity()).contact = contact;
            ((MainActivity) getActivity()).updateLocationDetail(lat, lon, contact);
        }
        else {
            Intent intent = new Intent(getActivity(), LocationDetail.class);
            intent.putExtra("Lat", lat);
            intent.putExtra("Lon", lon);
            intent.putExtra("Contact", contact);
            startActivityForResult(intent, 1);
        }
        itemSelectedListener.itemSelected(position);
    }

    private class MyListAdapter extends ArrayAdapter<String> {

        public MyListAdapter(@NonNull Context context, int resource, List<String> listItems) {
            super(context, resource, listItems);
        }

        @Override
        public View getView (int position,
                             View convertView,
                             ViewGroup parent){
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.rec_loc_list_item, parent, false);
            }
            String contactLatLong = getItem(position);
            String[] contactLatLongSplit = contactLatLong.split("/");
            ((TextView) convertView.findViewById(R.id.contact_name)).setText(contactLatLongSplit[0]);
            ((TextView) convertView.findViewById(R.id.longitude)).setText(contactLatLongSplit[1]);
            ((TextView) convertView.findViewById(R.id.latitude)).setText(contactLatLongSplit[2]);
            return convertView;
        }

        public String getLatitude(View convertView) {
            String lat = ((TextView) convertView.findViewById(R.id.latitude)).getText().toString();
            return lat;
        }

        public String getLongitude(View convertView) {
            String lat = ((TextView) convertView.findViewById(R.id.longitude)).getText().toString();
            return lat;
        }

        public String getContactName(View convertView) {
            String contact = ((TextView) convertView.findViewById(R.id.contact_name)).getText().toString();
            return contact;
        }
    }
}
