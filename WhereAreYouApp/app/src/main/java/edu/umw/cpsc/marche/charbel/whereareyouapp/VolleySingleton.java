package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Charbel on 4/20/2018.
 */

public class VolleySingleton {
    private static VolleySingleton instance = null;
    private NetworkInfo networkInfo;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    //private constructor to avoid client applications to use constructor
    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        createVolley(MyApplication.getAppContext());
    }

    public static VolleySingleton getInstance(){
        if(instance == null){
            instance = new VolleySingleton();
        }
        return instance;
    }

    private RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }

    private ImageLoader getImageLoader(){
        return this.mImageLoader;
    }

    private void createVolley(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectionManager.getActiveNetworkInfo();
    }

    public void registerContact(String username, String token) {
        final String[] result = new String[1];
        if(networkInfo != null && networkInfo.isConnected()){
            //do stuff
            Log.d("VolleySingleton", "In if token is " + token);
            RequestQueue queue = getRequestQueue();
            Log.d("VolleySingleton", "After new queue");
            String url ="https://cpsc470amobiledevelopment.appspot.com/messaging?type=register&id=cmarche@"
                    + username + ".edu" + "&token=" + token;
            Log.d("VolleySingleton", "made string url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            Log.d("VolleySingleton", "in response" + response);
                        }
                        else{
                            Log.d("VolleySingleton", "response is null");
                        }
                        // Display the first 500 characters of the response string.
                        result[0] = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleySingleton", "in error");
                        result[0] = "error";
                    }
                });
            queue.add(stringRequest);
            Log.d("VolleySingleton", "");
        }
    }

    public void requestLoction(String username, String email) {
        final String[] result = new String[1];
        if(networkInfo != null && networkInfo.isConnected()){
            //do stuff
            Log.d("VolleySingleton", "In if email is " + email);
            RequestQueue queue = getRequestQueue();
            Log.d("VolleySingleton", "After new queue");
            String url ="https://cpsc470amobiledevelopment.appspot.com/messaging?type=request&id=cmarche@"
                    + username + ".edu" + "&to=" + email;
            Log.d("VolleySingleton", "made string url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                Log.d("VolleySingleton", "in response " + response);
                            }
                            else{
                                Log.d("VolleySingleton", "response is null");
                            }
                            // Display the first 500 characters of the response string.
                            result[0] = response;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("VolleySingleton", "in error");
                            result[0] = "error";
                        }
                    });
            queue.add(stringRequest);
        }
    }

    public void sendLocationAccepted(String requestorEmail, String username, String lat, String lon) {
        final String[] result = new String[1];
        if(networkInfo != null && networkInfo.isConnected()){
            //do stuff
            RequestQueue queue = getRequestQueue();
            String url ="https://cpsc470amobiledevelopment.appspot.com/messaging?type=response&id=cmarche@"
                    + username + ".edu" + "&to=" + requestorEmail + "&denied=false&lat=" + lat + "&lon=" + lon;
            Log.d("VolleySingleton", "made string url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                Log.d("VolleySingleton", "in response " + response);
                            }
                            else{
                                Log.d("VolleySingleton", "response is null");
                            }
                            // Display the first 500 characters of the response string.
                            result[0] = response;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("VolleySingleton", "in error");
                            result[0] = "error";
                        }
                    });
            queue.add(stringRequest);
        }
    }

    public void sendLocationDenied(String requestorEmail, String username) {
        final String[] result = new String[1];
        if(networkInfo != null && networkInfo.isConnected()){
            //do stuff
            RequestQueue queue = getRequestQueue();
            String url ="https://cpsc470amobiledevelopment.appspot.com/messaging?type=response&id=cmarche@"
                    + username + ".edu" + "&to=" + requestorEmail + "&denied=true&lat=0&lon=0";
            Log.d("VolleySingleton", "made string url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                Log.d("VolleySingleton", "in response " + response);
                            }
                            else{
                                Log.d("VolleySingleton", "response is null");
                            }
                            // Display the first 500 characters of the response string.
                            result[0] = response;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("VolleySingleton", "in error");
                            result[0] = "error";
                        }
                    });
            queue.add(stringRequest);
        }
    }

    public String getInputReadFromEcho (String testText, Context context){
        final String[] result = new String[1];
        if(networkInfo != null && networkInfo.isConnected()){
            //do stuff
            RequestQueue queue = getRequestQueue();
            String url ="https://cpsc470amobiledevelopment.appspot.com/echo?name=" + "charbelmarche" + "&echo=" + testText;

            // Add StringRequest to the RequestQueue
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            Log.d("VolleySingleton", "in response");
                        }
                        else{
                            Log.d("VolleySingleton", "response is null");
                        }
                        // Display the first 500 characters of the response string.
                        result[0] = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleySingleton", "in error");
                        result[0] = "error";
                    }
                });
            Log.d("VolleySingleton", "about to return result");
            queue.add(stringRequest);
            if (result != null) {
                Log.d("VolleySingleton", "about to return result that isnt null");
                return result[0];
            }
            else {
                Log.d("VolleySingleton", "about to return result its null");
                return "well..";
            }
        }
        return "Did not enter if";
    }
}
