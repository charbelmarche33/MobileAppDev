package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "MyFirebaseMessagingService";
    static final public int SIMPLE_NOTIFICATION_ID = 5;
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleNow(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    public void handleNow(Map<String,String> messages){
        Log.d("Handling now", messages.toString());
        if (messages.get("type").equals("request")) {
            Log.d("Handle Now", "Handling now!");
            String requestorEmail = messages.get("requestor");
            Log.d("MyFirebaseMessaging", "requestor is " + requestorEmail);

            //
            String username = getUsernameFromEmail(requestorEmail);
            showReceivedRequestExpandedNotification(username, requestorEmail);
        }

        else if (messages.get("type").equals("response")){
            if(messages.get("denied").equals("true")){
                //Then the request was denied show a notification with no actions and an appropriate message. Onclick send to mainactivity.
                String responderEmail = messages.get("responder");
                String username = getUsernameFromEmail(responderEmail);
                showReceivedResponseDenied(username);

            }
            else if (messages.get("denied").equals("false")){
                //Then request was accepted show notification c no actions, appropriate message. On click take to detail screen (handle single and dual pane)
                //There you need to show a map with location marked with marker that has the title of the name of the contact....wat
                String responderEmail = messages.get("responder");
                String username = getUsernameFromEmail(responderEmail);
                String lat = messages.get("lat");
                String lon = messages.get("lon");
                Log.d("FireBaseMessage", "lat is " + lat + " lon is " + lon);
                showReceivedResponseAccepted(username, lat, lon);

            }
            else {
                //What is going on!?!?!?!?
                throw new IllegalArgumentException(this.toString() + " Okay dude someone in on the server is messing things up all them now and tell them that you about to throw hands.");
            }
        }
    }

    @Override
    public void onDeletedMessages() {
//        In some situations, FCM may not deliver a message. This occurs when there are too many
//        messages (>100) pending for your app on a particular device at the time it connects or
//        if the device hasn't connected to FCM in more than one month. In these cases, you may
//        receive a callback to FirebaseMessagingService.onDeletedMessages() When the app instance
//        receives this callback, it should perform a full sync with your app server. If you
//        haven't sent a message to the app on that device within the last 4 weeks, FCM won't
//        call onDeletedMessages().
    }

    private String getUsernameFromEmail(String email){
        int indexOfAtSign = (email.indexOf("@")) + 1;
        int indexOfDot = email.indexOf(".");
        if (indexOfDot == -1){
            //If no dot, then set the index to end of string
            indexOfDot = email.length();
        }
        String username = email.substring(indexOfAtSign, indexOfDot);
        return username;
    }

    public void showReceivedRequestExpandedNotification(String username, String requestorEmail){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(getResources().getString(R.string.received_loc_req_title) + " " + username);
        builder.setContentText(getResources().getString(R.string.received_loc_req_message) + " " + username);

        Intent resultIntent = new Intent(this,NotificationClickedActivity.class);
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("requestorEmail", requestorEmail);
        resultIntent.putExtra("type", "none");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationClickedActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        resultIntent = new Intent(this,NotificationClickedActivity.class);
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("requestorEmail", requestorEmail);
        resultIntent.putExtra("type", "negative");
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationClickedActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent negativePendingIntent = stackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_launcher_foreground,getResources().getString(R.string.notification_negative),negativePendingIntent);

        resultIntent = new Intent(this,NotificationClickedActivity.class);
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("requestorEmail", requestorEmail);
        resultIntent.putExtra("type", "positive");
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationClickedActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent positivePendingIntent = stackBuilder.getPendingIntent(2,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_launcher_foreground,getResources().getString(R.string.notification_positive),positivePendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SIMPLE_NOTIFICATION_ID,builder.build());
    }

    public void showReceivedResponseDenied(String username){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(username + " " + getResources().getString(R.string.denied_loc_res_title));
        builder.setContentText(getResources().getString(R.string.denied_loc_res_message));

        Intent resultIntent = new Intent(this,MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SIMPLE_NOTIFICATION_ID,builder.build());
    }

    public void showReceivedResponseAccepted(String username, String lat, String lon){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(username + " " + getResources().getString(R.string.accepted_loc_res_title));
        builder.setContentText(getResources().getString(R.string.accepted_loc_res_message));

        Intent resultIntent = new Intent(this,MainActivity.class);
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("lat", lat);
        resultIntent.putExtra("lon", lon);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SIMPLE_NOTIFICATION_ID,builder.build());
    }
}
