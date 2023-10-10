package com.lisners.patient.zWork.base;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lisners.patient.R;

import net.mrbin99.laravelechoandroid.Echo;
import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;
import net.mrbin99.laravelechoandroid.channel.SocketIOPrivateChannel;

public class SocketActivity extends AppCompatActivity {
    Echo echo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);


        EchoOptions options = new EchoOptions();

// Setup host of your Laravel Echo Server
        options.host = "http://3.109.207.193:6002";

        /*
         * Add headers for authorizing your users (private and presence channels).
         * This line can change matching how you have configured
         * your guards on your Laravel application
         */
        //options.headers.put("Authorization", "Bearer {token}");

        // Create the client
        echo = new Echo(options);
        echo.connect(new EchoCallback() {
            @Override
            public void call(Object... args) {
                Log.e("Socket","====>Success"+args);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        ((TextView)findViewById(R.id.tv)).setText(""+new Gson().toJson(args));


                    }
                });

                // Success connect
            }
        }, new EchoCallback() {
            @Override
            public void call(Object... args) {
                Log.e("Socket","====>Error"+args);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        ((TextView)findViewById(R.id.tv)).setText(""+new Gson().toJson(args));


                    }
                });

                // Error connect
            }
        });

        joinPublicChannel();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        echo.disconnect();
    }


    void joinPublicChannel(){
        echo.channel("status_update")
                .listen("StatusUpdate", new EchoCallback() {
                    @Override
                    public void call(Object... args) {
                        // Event thrown.
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                ((TextView)findViewById(R.id.tv)).setText(""+new Gson().toJson(args));


                            }
                        });
                    }
                });
    }

    void joinPrivateChannel(){
        SocketIOPrivateChannel privateChannel = echo.privateChannel("status_update");
        privateChannel.listen("StatusUpdate", new EchoCallback() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        ((TextView)findViewById(R.id.tv)).setText(""+new Gson().toJson(args));


                    }
                });
                // Event thrown.
            }
        });
    }



    void  leaveChannel(){
        echo.leave("channel-name");
    }
}