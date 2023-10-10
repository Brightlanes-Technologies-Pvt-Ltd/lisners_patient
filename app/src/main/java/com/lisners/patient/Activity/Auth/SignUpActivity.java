package com.lisners.patient.Activity.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lisners.patient.Adaptors.AvailabilityAdaptor;
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

import in.aabhasjindal.otptextview.OtpTextView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHeader, tvSignUpSignIn;
    private Button btn_signUp;
    private AlertDialog alert;
    private EditText edit_mobile;
    private Button btn_otp_submit;
    private EditText edit_signUp_mobile;
    private TextView txt_timer, txt_resend_otp;
    private OtpTextView otpTextView;
    private LinearLayout lv_resend_otp;
    FcmService fcmService;


    LoginViewModel loginVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        tvHeader.setText("Create an Account");
        fcmService = new FcmService();
        tvSignUpSignIn.setOnClickListener(this);
        btn_signUp.setOnClickListener(this);

    }

    private void init() {

        loginVM = ViewModelUtils.getViewModel(LoginViewModel.class, this);

        tvHeader = findViewById(R.id.tvHeader);
        edit_mobile = findViewById(R.id.edit_signUp_mobile);
        tvSignUpSignIn = findViewById(R.id.tvSignUpSignIn);
        edit_signUp_mobile = findViewById(R.id.edit_signUp_mobile);
        btn_signUp = findViewById(R.id.btn_signUp);
    }

    public void showBottomSheetDialog() {

        View view = getLayoutInflater().inflate(R.layout.bottomsheet_layout, null);
        AlertDialog.Builder rateDialog = new AlertDialog.Builder(SignUpActivity.this, R.style.DialogAnimation);
        rateDialog.setView(view);
        alert = rateDialog.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initDialog(view);
        alert.show();
    }

    private void initDialog(View view) {

        Button submit = view.findViewById(R.id.btn_otp_submit);
        txt_timer = view.findViewById(R.id.txt_timer);
        txt_resend_otp = view.findViewById(R.id.tvSheetResendCode);
        otpTextView = view.findViewById(R.id.otpTextView);
        lv_resend_otp = view.findViewById(R.id.lv_resend_otp);
        btn_otp_submit = view.findViewById(R.id.btn_otp_submit);
        txt_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP();
            }
        });

        btn_otp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOTP();
            }
        });
        startCounter();
    }

    private void verifyOTP() {
        StoreData storeData = new StoreData(this);
        String otp = otpTextView.getOTP();
        String phone = edit_signUp_mobile.getText().toString();
        Map<String, String> params = new HashMap<>();
        if (otp.trim().length() >= 4) {
            final DProgressbar dProgressbar = new DProgressbar(this);
            dProgressbar.show();
            params.put("mobile_no", phone);
            params.put("otp", otp);
            params.put("device_id", fcmService.getToken());
            params.put("device_type", "android");


            loginVM
                    .verifySignUpOtp(params)
                    .observe(this, response -> {
                        dProgressbar.dismiss();

                        if (response.getStatus()) {
                            Map<String, String> resParams = new HashMap<>();
                            resParams.put(ConstantValues.USER_TOKEN, response.getToken());
                            resParams.put(ConstantValues.USER_DATA, new Gson().toJson(response.getData()));
                            storeData.setMultiData(resParams, new StoreData.SetListener() {
                                @Override
                                public void setOK() {
                                    startActivity(new Intent(SignUpActivity.this, SignUpFormActivity.class));
                                    alert.dismiss();
                                }
                            });

                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    response.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

/*            PostApiHandler senOtp = new PostApiHandler(URLs.SEND_VERIFY_SIGN_UP, params, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("JSONObject", new Gson().toJson(jsonObject));
                    dProgressbar.dismiss();

                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        Map<String, String> params = new HashMap<>();
                        params.put(ConstantValues.USER_TOKEN, jsonObject.getString("token"));
                        params.put(ConstantValues.USER_DATA, jsonObject.getString("data"));
                        storeData.setMultiData(params, new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                startActivity(new Intent(SignUpActivity.this, SignUpFormActivity.class));
                                alert.dismiss();

                            }
                        });

                    }

                    if (jsonObject.has("message"))
                        Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(ANError anError) {
                    dProgressbar.dismiss();
                }
            });
            senOtp.execute();*/
        } else
            Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show();
    }

    private void sendOTP() {

        String phone = edit_signUp_mobile.getText().toString();
        Map<String, String> params = new HashMap<>();

        if (!phone.trim().isEmpty()) {
            final DProgressbar dProgressbar = new DProgressbar(this);
            dProgressbar.show();
            params.put("mobile_no", phone);


            loginVM
                    .signUpSendOtp(phone)
                    .observe(this, response -> {
                        dProgressbar.dismiss();

                        if (response.getStatus()) {
                            showBottomSheetDialog();
                        } else {
                            if (response.getType() != null && !response.getType().isEmpty() && response.getType().equalsIgnoreCase("validation")) {
                                JsonObject jsonObjet = response.getErrors();
                                edit_signUp_mobile.setError(jsonObjet.get("mobile_no").getAsString());
                                Log.e("error msg", jsonObjet.get("mobile_no").getAsString());

                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    });


         /*   PostApiHandler senOtp = new PostApiHandler(URLs.SEND_OTP_SIGN_UP, params, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("jsonObject", new Gson().toJson(jsonObject));
                    dProgressbar.dismiss();
                    if (jsonObject.has("status") && jsonObject.getBoolean("status"))
                        showBottomSheetDialog();
                    else if (jsonObject.has("message"))
                        edit_signUp_mobile.setError(jsonObject.getString("message"));
                    else if (jsonObject.has("errors")) {
                        JSONObject errObj = jsonObject.getJSONObject("errors");
                        Toast.makeText(SignUpActivity.this, UtilsFunctions.errorShow(errObj.getJSONArray("mobile_no")), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(ANError anError) {
                    dProgressbar.dismiss();
                }
            });

            senOtp.execute();*/

        } else
            edit_signUp_mobile.setError("Enter Mobile Number");
    }

    public void startCounter() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                Long sec = millisUntilFinished / 1000;
                String str = sec < 10 ? "0:0" + sec : "0:" + sec;

                if (sec > 0)
                    txt_timer.setText(str);
                else
                    txt_timer.setText("");
                lv_resend_otp.setVisibility(View.GONE);

            }

            public void onFinish() {
                lv_resend_otp.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvSignUpSignIn:
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                break;

            case R.id.btn_signUp:
                sendOTP();
                //showBottomSheetDialog();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}