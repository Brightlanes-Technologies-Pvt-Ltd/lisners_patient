package com.lisners.patient.zWork.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.lisners.patient.Activity.Auth.SplashScreenActivity;
import com.lisners.patient.R;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //FcmPrefsUtil.setFcmKey(getApplicationContext(), token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // both noti and data received in for_ground
        try {
            if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
                RemoteMessage.Notification
                        notification = remoteMessage.getNotification();

                Map<String, String> data = remoteMessage.getData();

                String title = notification.getTitle();
                String message = notification.getBody();

                String type = data.get("type");
                String all_data = new Gson().toJson(data);

                long when = System.currentTimeMillis();


                //JSONObject payload = data.getJSONObject("payload");

                Log.e(TAG, "title: " + title);
                Log.e(TAG, "message: " + message);
                Log.e(TAG, "type: " + type);
                Log.e(TAG, "all_data: " + all_data);


                Bitmap imageBitmap = null;
                /*if (!imageUrl.isEmpty() && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                    imageBitmap = getBitmapFromURL(imageUrl);
                }*/


                if (isAppIsInBackground(getApplicationContext())) {


                } else {
                    // show notification
                    if (type.equalsIgnoreCase("calling")) {

                    }
                    else {
                        PendingIntent pendingIntent = getCallPendingIntent(all_data) ;
                        makeNotification(title, message, when, type, all_data,pendingIntent);
                    }

                    //send broadcast also
                    //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(HomeActivity.notificationReceiveIntent));
                    // send broadcast to main activity
                }

            }
        } catch (
                Exception e) {
            e.printStackTrace();
            Log.d("MYFCMLIST", "Error parsing FCM message" + e.getMessage());
        }


    }




    void makeBigImageNotification(String title, String message, long when, Bitmap imageBitmap, String deepLink) {

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(imageBitmap);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.default_notification_channel_id));

        notificationBuilder
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(getPendingIntent(deepLink))
                .setSound(getSound())
                .setStyle(bigPictureStyle)
                .setWhen(when)
                .setColor(getResources().getColor(R.color.transparent_primary))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(imageBitmap)
                .setContentText(message);


        getNotificationManager().notify(createNotificationId(), notificationBuilder.build());


    }

    int makeNotification(String title, String message, long when, String type, String all_data, PendingIntent pendingIntent) {

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(message);
      /*  NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);*/
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_default");

        notificationBuilder
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSound(getSound())
                .setStyle(bigTextStyle)
                .setWhen(when)
                .setColor(getResources().getColor(R.color.transparent_primary))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_stat_name))
                .setContentText(message);

        int noti_id = createNotificationId();
        getNotificationManager().notify(noti_id, notificationBuilder.build());
        return noti_id;


    }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private int createNotificationId() {
        Date date = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(date));

    }

    private Uri getSound() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return alarmSound;
    }

    private  NotificationManager getNotificationManager() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel chan1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan1 = new NotificationChannel("channel_default",
                    "Default", NotificationManager.IMPORTANCE_HIGH);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mNotificationManager.createNotificationChannel(chan1);
        }
        return mNotificationManager;
    }

    private PendingIntent getPendingIntent(String deeplinkData) {

        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (!deeplinkData.isEmpty()) {
            notificationIntent.setAction("deepLink_intent");
            notificationIntent.putExtra("data",deeplinkData);
        }

        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0,
                notificationIntent,
                0);


        return contentPendingIntent;
    }


    private PendingIntent getCallPendingIntent(String callingData) {

        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (!callingData.isEmpty()) {
            notificationIntent.setAction("calling_intent");
            notificationIntent.putExtra("calling_data", callingData);
        }

        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0,
                notificationIntent,
                0);


        return contentPendingIntent;
    }


    private Intent getCallBroadcastIntent(String callingData,int notificationId) {
        Intent notificationIntent = new Intent();

        if (!callingData.isEmpty()) {
            notificationIntent.setAction("calling_intent");
            notificationIntent.putExtra("calling_data", callingData);
            notificationIntent.putExtra("noti_id",notificationId);
        }
        return notificationIntent;
    }
}






