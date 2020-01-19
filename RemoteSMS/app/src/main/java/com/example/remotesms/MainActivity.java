package com.example.remotesms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String websiteUrl = "http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com";
    private TextView mTextViewResult;
    private Button mButton;
    String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ask permission to send SMSs
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mTextViewResult = findViewById(R.id.text_view_result);
        OkHttpClient client = new OkHttpClient();
        String setUpUrl = websiteUrl + "/appSetup.php?phoneid=" + uniqueID;
        Request request = new Request.Builder()
                .url(setUpUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // if this is the first time the user is launching the app, the server will also
                        //   need the user's phone number (which will kinda be used as a username for the
                        //   web app). After that the socket connection can be made.
                        if (myResponse.equals("need phonenum")) {
                            Toast.makeText(getApplicationContext(),"Please set phone number!",Toast.LENGTH_SHORT).show();
                            // startActivity(new Intent(MainActivity.this, GetPhoneNumActivity.class));
                        } else {
                            // this would be some weird error
                            mTextViewResult.setText(myResponse);
                        }
                    }
                });
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        String TAG = "TAG";
                        if (!task.isSuccessful()) {
                            Log.w(TAG,"getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // send the token to RemoteSMS server
                        String websiteUrl = "http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com";
                        String uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        OkHttpClient client = new OkHttpClient();
                        String url = websiteUrl + "/appSetup.php?phoneid=" + uniqueID + "&fcmtoken=" + token;

                        // wait a little before trying to tell server to update fcmtoken
                        //   if we try to do this as imediatly after initializing the app, bad
                        //   things happen!
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this.getApplicationContext(),
                                    "couldnt sleep", Toast.LENGTH_SHORT).show();
                        }

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
                                        Toast.makeText(MainActivity.this.getApplicationContext(),
                                                myResponse, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        Log.w(TAG, token);
                        // Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


        startService(new Intent(MainActivity.this, MyFirebaseMessagingService.class));

        Button setPhoneNumberButton = (Button) findViewById(R.id.setPhoneNumber);

        setPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GetPhoneNumActivity.class));
            }
        });
    }
}
