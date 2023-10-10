package com.lisners.patient.Activity.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.restApi.pojo.UserData;

import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {
    int SPLASH_DISPLAY_LENGTH = 3000;
    StoreData storeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        storeData = new StoreData(this);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToWelcome();
                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(mainIntent);
//                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /*private void checkNotification() {
        Intent intent = getIntent();
        Map<String, String> params = new HashMap<>();
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                try {
                    params.put(key, intent.getStringExtra(key) + "");
                } catch (Exception e) {
                }
            }
        }
        String st_json = new Gson().toJson(params);
        Log.e("CALLING_ACTION_A", st_json);
        try {
            Intent intent1 = null;

            String noti_Status = intent.getStringExtra("FROM_FCM");

            if (noti_Status != null && noti_Status.matches("FROM_FCM")) {
                intent1 = new Intent(SplashScreenActivity.this, HomeActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.putExtra("CALLING_ACTION", st_json);
                startActivity(intent1);
                finish();
            } else {

                Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {

            Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }*/

    private void checkNotification(){
        Intent homeActivityIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);

        try {
            Intent intent = getIntent();
            if (intent.getAction() != null && intent.getAction().equalsIgnoreCase("deepLink_intent")) {
                homeActivityIntent.setAction(intent.getAction());
                homeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                homeActivityIntent.putExtra("data", intent.getStringExtra("data"));

            } else if (intent.getExtras() != null) {
                // receiving data from notification when app background
                Map<String, String> dataMap = new HashMap<>();
                for (String key : intent.getExtras().keySet()) {
                    try {
                        dataMap.put(key, intent.getStringExtra(key) + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String type = dataMap.get("type");
                String data = new Gson().toJson(dataMap);

                if (type.equalsIgnoreCase("calling")) {
                    homeActivityIntent.setAction("calling_intent");
                    homeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    homeActivityIntent.putExtra("calling_data", data);
                } else {
                    homeActivityIntent.setAction("deepLink_intent");
                    homeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    homeActivityIntent.putExtra("data", data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        startActivity(homeActivityIntent);
        finish();
    }

    public void moveToWelcome() {
        storeData.getData(ConstantValues.USER_TOKEN, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                storeData.getData(ConstantValues.USER_DATA, new StoreData.GetListener() {
                    @Override
                    public void getOK(String val) {
                        if (val != null) {
                            try {
                                UserData user = new Gson().fromJson(val, UserData.class);
                                if (user.getIsProfileComplete() == 1) {
                                    checkNotification();
                                } else {
                                    startActivity(new Intent(SplashScreenActivity.this, SignUpFormActivity.class));

                                }
                            } catch (Exception e) {
                                startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                            }
                        } else {
                            startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                        }
                        finish();

                    }

                    @Override
                    public void onFail() {
                        startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onFail() {
                startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                finish();
            }
        });

    }
}