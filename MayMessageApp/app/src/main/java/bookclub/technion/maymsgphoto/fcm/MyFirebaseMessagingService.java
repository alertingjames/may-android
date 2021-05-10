package bookclub.technion.maymsgphoto.fcm;

/**
 * Created by a on 5/16/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.main.BranchActivity;
import bookclub.technion.maymsgphoto.main.ChatActivity;
import bookclub.technion.maymsgphoto.models.UserEntity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String email="", sender="", name="", photo="", message="";
    Bitmap bitmapPhoto=null;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            pushNotification(Commons.thisEntity.get_email().toString());
            pushNotificationSecret(Commons.thisEntity.get_email().toString());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            pushNotification(Commons.thisEntity.get_email().toString());
            pushNotificationSecret(Commons.thisEntity.get_email().toString());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

//        sendNotification(remoteMessage.getData().get("message"));

        //    sendNotification2(remoteMessage.getData().get("message"));

        pushNotification(Commons.thisEntity.get_email().toString());
        pushNotificationSecret(Commons.thisEntity.get_email().toString());

    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification2(String messageBody) {
        Intent intent = new Intent(this, BranchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sendbird_ic_launcher)
                .setContentTitle("Firebase")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void pushNotification(final String email) {

        final Firebase reference = new Firebase("https://maymessageapp.firebaseio.com/notification/"+ email.replace(".com",""));

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                try{
                    message = map.get("msg").toString(); Log.d("Msg===>",message);
                    sender = map.get("sender").toString(); Log.d("Email===>",sender);
                    photo = map.get("senderPhoto").toString();  Log.d("Photo===>",photo);
                    name = map.get("senderName").toString();    Log.d("Name===>",name);
                    //        time = map.get("time").toString();
                    Commons.notiEmail = sender + ".com";
                    Commons.firebase = reference;
                    Commons.mapping=map;

                    shownot();

                    //        showToast("You received a message!");
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.getKey().equals(Commons.thisEntity.get_email().replace(".com", ""))) {
//                    shownot();
//                    Toast.makeText(getApplicationContext(), "Fetching updated data: " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                shownot();
//                Toast.makeText(getApplicationContext(), "Data Removed." + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void pushNotificationSecret(final String email) {

        final Firebase referenceSecret = new Firebase("https://maymessageapp.firebaseio.com/secretnoti/"+ email.replace(".com",""));

        referenceSecret.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                try{
                    message = map.get("msg").toString(); Log.d("Msg===>",message);
                    sender = map.get("sender").toString(); Log.d("Email===>",sender);
                    photo = map.get("senderPhoto").toString();  Log.d("Photo===>",photo);
                    name = map.get("senderName").toString();    Log.d("Name===>",name);
                    //        time = map.get("time").toString();
                    Commons.notiEmail = sender + ".com";
                    Commons.firebaseSecret = referenceSecret;
                    Commons.mapping=map;

                    shownot();

                    //        showToast("You received a message!");
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.getKey().equals(Commons.thisEntity.get_email().replace(".com", ""))) {
//                    shownot();
//                    Toast.makeText(getApplicationContext(), "Fetching updated data: " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                shownot();
//                Toast.makeText(getApplicationContext(), "Data Removed." + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void shownot() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        long[] v = {500,1000};

        Commons.userEntity=new UserEntity();
        Commons.userEntity.set_photoUrl(photo);
        Commons.userEntity.set_name(name);
        Commons.userEntity.set_email(Commons.notiEmail);    Log.d("NotiEmail===>",Commons.notiEmail);

        if(photo.length()>0){
            try {
                bitmapPhoto= BitmapFactory.decodeStream((InputStream) new URL(photo).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);
            }
        }else bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new android.app.Notification.Builder(this)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.noti).setLargeIcon(bitmapPhoto)
                .setContentIntent(pIntent)
                //        .setSound(uri)
                //      .setVibrate(v)
                .setAutoCancel(true).build();

        notificationManager.notify(0, n);
    }
}