package com.lisners.patient.Activity.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.R;
import com.lisners.patient.fcm.FcmService;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.restApi.viewmodel.LoginViewModel;
import com.lisners.patient.zWork.utils.ViewModelUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHeader, tvSignInSignUp, tvForgotPassword;
    private Button btn_sign_in;
    private EditText edit_phone, edit_password;
    StoreData storeData;
    FcmService fcmService;

    LoginViewModel loginVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        tvHeader.setText("Login");
        fcmService = new FcmService();
        tvSignInSignUp.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);

    }

    private void init() {
        loginVM = ViewModelUtils.getViewModel(LoginViewModel.class, this);

        storeData = new StoreData(this);
        tvHeader = findViewById(R.id.tvHeader);
        tvSignInSignUp = findViewById(R.id.tvSignInSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        edit_phone = findViewById(R.id.edit_login_mobile);
        edit_password = findViewById(R.id.edit_login_password);
    }


    private void onSignIn() {
        String stPhone = edit_phone.getText().toString();
        String stPass = edit_password.getText().toString();

        if (stPhone.isEmpty())
            edit_phone.setError("");
        else if (stPass.isEmpty())
            edit_password.setError("");
        else {
            DProgressbar dProgressbar = new DProgressbar(this);
            dProgressbar.show();
            Map<String, String> prams = new HashMap<>();
            prams.put("mobile_no", stPhone);
            prams.put("password", stPass);
            prams.put("device_id", fcmService.getToken());
            prams.put("device_type", "android");
            Log.e("Params", new Gson().toJson(prams));


            loginVM
                    .login(prams)
                    .observe(this, response -> {
                        dProgressbar.dismiss();

                        if (response.getStatus()) {

                            Map<String, String> resParams = new HashMap<>();
                            resParams.put(ConstantValues.USER_TOKEN, response.getToken());
                            resParams.put(ConstantValues.USER_DATA, new Gson().toJson(response.getData()));

                            storeData.setMultiData(resParams, new StoreData.SetListener() {
                                @Override
                                public void setOK() {
                                    if(response.getData().getIsProfileComplete() == 1) {

                                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                    else {
                                        startActivity(new Intent(LoginActivity.this, SignUpFormActivity.class));

                                    }
                                }
                            });
                        } else {
                            if (response.getType() != null && !response.getType().isEmpty() && response.getType().equalsIgnoreCase("validation")) {
                                JsonObject jsonObjet = response.getErrors();
                                if (jsonObjet.has("mobile_no"))
                                    edit_phone.setError(jsonObjet.get("mobile_no").getAsString());
                                else if (jsonObjet.has("password")) {
                                    edit_password.setError(jsonObjet.get("password").getAsString());
                                }

                            } else {
                                Toast.makeText(LoginActivity.this,
                                        response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    });




           /* PostApiHandler postApiHandler = new PostApiHandler(LoginActivity.this, URLs.SEND_LOGIN, prams, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("JSONObject",new Gson().toJson(jsonObject));
                    if(jsonObject.getBoolean("status")){


                        Map<String,String> params = new HashMap<>();
                        params.put(ConstantValues.USER_TOKEN, jsonObject.getString("token"));
                        params.put(ConstantValues.USER_DATA, jsonObject.getString("data"));
                        storeData.setMultiData(params, new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            }
                        });
                    }else if(jsonObject.has("errors")){
                        JSONObject errObj =  jsonObject.getJSONObject("errors");
                        if(errObj.has("mobile_no"))
                            Toast.makeText(LoginActivity.this, UtilsFunctions.errorShow(errObj.getJSONArray("mobile_no")) , Toast.LENGTH_SHORT).show();
                    }
                    if (jsonObject.has("message"))
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    dProgressbar.dismiss();
                }

                @Override
                public void onError(ANError anError) {
                    dProgressbar.dismiss();
                }
            });
            postApiHandler.execute();
        */
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignInSignUp:

                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

                break;
            case R.id.tvForgotPassword:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.btn_sign_in:
                onSignIn();
                break;
        }
    }
}