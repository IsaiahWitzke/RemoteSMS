package com.example.remotesms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPhoneNumActivity extends AppCompatActivity {

    private String websiteUrl = "http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com";
    private TextView phoneNumEditText;
    private TextView helpTextView;
    private String uniqueID;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_num);

        configureSubmitPhoneNumButton();
    }

    private void configureSubmitPhoneNumButton() {
        Button submitButton = (Button) findViewById(R.id.submitPhoneNumButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                phoneNumEditText = (TextView) findViewById(R.id.phoneNumEditText);
                helpTextView = (TextView) findViewById(R.id.helpTextView);
                phoneNum = phoneNumEditText.getText().toString();

                OkHttpClient client = new OkHttpClient();
                String url = websiteUrl + "/appSetup.php?phoneid=" + uniqueID +
                        "&newphonenumber=" + phoneNum;
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
                        GetPhoneNumActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                helpTextView.setText(myResponse);
                            }
                        });
                    }
                });
            }
        });
    }
}
