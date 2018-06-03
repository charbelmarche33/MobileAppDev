package edu.umw.cpsc.marche.charbel.whereareyouapp;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.GRAY;
import static android.graphics.Color.WHITE;

public class ContactListFragment extends ListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>{
    public List<String> contactNamesArray;
    private ItemSelectedListener itemSelectedListener;
    private int position = -1;
    private final static String SELECTED = "Selected";
    private SimpleCursorAdapter simpleCursorAdapter;
    private String contactFilter;
    private static final int CONTACT_LOADER_ID = 0;
    private FusedLocationProviderClient fusedLocationClient;
    UserInformationSingleton userInformationSingleton = UserInformationSingleton.getInstance();
    MyFileIO myFileIO = new MyFileIO();

    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        setEmptyText(getString(R.string.empty_contacts));
        setHasOptionsMenu(true);

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            setupAdapter();
        }
    }

    public void setupAdapter(){
        simpleCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contact_list_item,
                null,
                new String[] { ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.DATA},
                new int[] { R.id.contact_image, R.id.contact_name, R.id.email}, 0);
        setListAdapter(simpleCursorAdapter);

        getLoaderManager().initLoader(CONTACT_LOADER_ID, null, this);
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
        Log.d("In contact", "List item click 3");
        ArrayList<View> listitems = l.getTouchables();
        for (View item : listitems) {
            item.setBackgroundColor(WHITE);
        }
        v.setBackgroundColor(GRAY);
        Log.d("In contact", "List item click 4");
        Cursor cursor = simpleCursorAdapter.getCursor();
        cursor.moveToPosition(position);
        Log.d("In contact", "List item click 5");
        final String name = cursor.getString(1);
        TextView emailTextView = (TextView) v.findViewById(R.id.email);
        final String email = emailTextView.getText().toString();
        Log.d("ContactListFrag", "name is " + name + " email " + email);
        final String filename = getString(R.string.received_requests_file);
        //This is from writing the location to the file
        /**if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    Log.d("In contact", "List item click in on success " + filename);
                    if (location != null) {
                        // Logic to handle location object}
                        Double lat = location.getLatitude();
                        Double lon = location.getLongitude();
                        //Put the string together with '/' as delimiter
                        String fileContents = name + "/" + lon + "/" + lat + '\n';
                        //Write to the file
                        myFileIO.writeToFile(fileContents, filename, getContext());
                    }
                }
            });
        }**/
        MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();
        myFirebaseInstanceIdService.sendLocationRequest(email);
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
                convertView = getLayoutInflater().inflate(R.layout.contact_list_item, parent, false);
            }
            String name = getItem(position);
            ((TextView) convertView.findViewById(R.id.contact_name)).setText(name);
            return convertView;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == CONTACT_LOADER_ID) {
            Uri baseUri;
            if (contactFilter != null) {
                baseUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI, Uri.encode(contactFilter));
            }
            else {
                baseUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            }

            String select = "((" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " NOTNULL) AND ("
                    + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                    + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " != '' ) AND ("
                    + ContactsContract.CommonDataKinds.Email.DATA + " NOTNULL))";
            loader = new CursorLoader(getActivity(), baseUri,
                    CONTACTS_SUMMARY_PROJECTION, select, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        }
        return loader;
    }

    // These are the Contacts rows that we will retrieve.
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
            ContactsContract.CommonDataKinds.Email.DATA
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Not actually performing a search, so do nothing
        return true;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        simpleCursorAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()is about to be closed.
        // Make sure we are no longer using it.
        simpleCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
}
