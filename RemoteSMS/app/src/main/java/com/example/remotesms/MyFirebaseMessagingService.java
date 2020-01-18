package com.example.remotesms;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(final String token) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),
                        "aaaaaa " + token, Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        String websiteUrl = "http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com";
        String uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        OkHttpClient client = new OkHttpClient();
        String url = websiteUrl + "/appSetup.php?phoneid=" + uniqueID + "&fcmtoken=" + token;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String myResponse = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),
                                myResponse, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        final SmsManager smsManager = SmsManager.getDefault();

        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // actually do messaging stuff here
            Handler handler = new Handler(Looper.getMainLooper());
            final String msgData = remoteMessage.getData().toString();

            try {
                final JSONObject obj = new JSONObject(msgData);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /*
                            Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),
                                    obj.getString("phoneNum") + " " + obj.getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                                    */
                            smsManager.sendTextMessage(obj.getString("phoneNum"), null, obj.getString("msg"), null, null);
                            Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),
                                    "Message Sent!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),
                                    "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                final String errorMsg = e.getMessage();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),
                                "error" + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
