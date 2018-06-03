package edu.umw.cpsc.marche.charbel.whereareyouapp;


import android.content.Context;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationRequestsFragment extends android.support.v4.app.ListFragment {
    public List<String> namesArray;
    private ItemSelectedListener itemSelectedListener;
    private int position = -1;
    private final static String SELECTED = "Selected";
    MyFileIO myFileIO = new MyFileIO();

    public LocationRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if (container != null){
            container.removeAllViews();
        }


        // Array to be added to adapter
        namesArray = new ArrayList<String>();
        return inflater.inflate(R.layout.fragment_location_requests, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        String path = getContext().getFilesDir() + "/" + getString(R.string.sent_requests_file);
        namesArray = myFileIO.getAllFromFile(namesArray, path, getContext());

        //Add array to adapter
        this.setListAdapter(new MyListAdapter(this.getContext(), R.layout.location_req_list_item, namesArray));

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(position >= 0)
            outState.putInt(SELECTED, position);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.position = position;
        //if (isDualPane) {
        //    v.setSelected(true);
        //}
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
                convertView = getLayoutInflater().inflate(R.layout.location_req_list_item, parent, false);
            }
            String contactLatLong = getItem(position);
            String[] contactLatLongSplit = contactLatLong.split("/");
            ((TextView) convertView.findViewById(R.id.contact_name)).setText(contactLatLongSplit[0]);
            ((TextView) convertView.findViewById(R.id.longitude)).setText(contactLatLongSplit[1]);
            ((TextView) convertView.findViewById(R.id.latitude)).setText(contactLatLongSplit[2]);
            return convertView;
        }
    }

}
