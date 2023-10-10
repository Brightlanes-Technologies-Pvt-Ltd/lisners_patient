package com.lisners.patient.Activity.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lisners.patient.R;
import com.lisners.patient.fcm.FcmService;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.aabhasjindal.otptextview.OtpTextView;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvHeader;
    private Button btnForgotPassword, btn_otp_submit;
    private AlertDialog alert;
    private ImageButton btn_header_left;
    private EditText edit_number;
    private TextView txt_timer, txt_resend_otp;
    private OtpTextView otpTextView;
    private LinearLayout lv_resend_otp;
    FcmService fcmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        fcmService = new FcmService();
        init();

    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_layout, null);
        AlertDialog.Builder rateDialog = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.DialogAnimation);
        rateDialog.setView(view);
        alert = rateDialog.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initDialog(view);
        startCounter();
        alert.show();
    }

    public void startCounter() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                Long sec = millisUntilFinished / 1000;
                String str = sec < 10 ? "0:0" + sec : "0:" + sec;
                if (sec > 0 && txt_timer != null)
                    txt_timer.setText(str);
                else if (txt_timer != null) {
                    txt_timer.setText("");
                    lv_resend_otp.setVisibility(View.GONE);
                }
            }

            public void onFinish() {
                lv_resend_otp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void verifyOTP() {
        StoreData storeData = new StoreData(this);
        String otp = otpTextView.getOTP().toString();
        String phone = edit_number.getText().toString();
        Map<String, String> params = new HashMap<>();
        if (otp.trim().length() >= 4) {
            final DProgressbar dProgressbar = new DProgressbar(this);
            dProgressbar.show();
            params.put("mobile_no", phone);
            params.put("otp", otp);
            params.put("device_id", fcmService.getToken());
            params.put("device_type", "android");

            PostApiHandler senOtp = new PostApiHandler(URLs.FORGOT_VERIFY, params, new PostApiHandler.OnClickListener() {

                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("JSONObject", new Gson().toJson(jsonObject));
                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        Map<String, String> params = new HashMap<>();
                        params.put(ConstantValues.USER_TOKEN, jsonObject.getString("token"));
                        params.put(ConstantValues.USER_DATA, jsonObject.getString("data"));
                        storeData.setMultiData(params, new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                                startActivity(intent);
                                dProgressbar.dismiss();
                            }
                        });

//
                    }
                    dProgressbar.dismiss();
                    if (jsonObject.has("message"))
                        Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Server Error", Toast.LENGTH_SHORT).show();
                    dProgressbar.dismiss();
                }
            });
            senOtp.execute();
        } else
            Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show();
    }

    private void sendOTP() {
        String phone = edit_number.getText().toString();

        if (!phone.trim().isEmpty()) {
            final DProgressbar dProgressbar = new DProgressbar(this);
            dProgressbar.show();
            Map<String, String> params = new HashMap<>();
            params.put("mobile_no", phone);
            PostApiHandler senOtp = new PostApiHandler(URLs.FORGOT_CHECK_MOBILE, params, new PostApiHandler.OnClickListener() {

                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    dProgressbar.dismiss();

                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        showBottomSheetDialog();
                    } else {
                        if (jsonObject.has("type") && !jsonObject.getString("type").isEmpty() && jsonObject.getString("type").equalsIgnoreCase("validation")) {
                            JSONObject jsonObjet = jsonObject.getJSONObject("errors");
                            if (jsonObjet.has("mobile_no")) {
                                edit_number.setError(UtilsFunctions.errorShow(jsonObjet.getJSONArray("mobile_no")));
                            }

                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Server Error", Toast.LENGTH_SHORT).show();
                    dProgressbar.dismiss();
                }

            });
            senOtp.execute();
        } else
            edit_number.setError("Enter Mobile Number");
    }


    private void initDialog(View view) {
        Button submit = view.findViewById(R.id.btn_otp_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class));
                alert.dismiss();
            }
        });

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
    }

    private void init() {
        tvHeader = findViewById(R.id.tvHeader);
        btn_header_left = findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        btn_header_left.setOnClickListener(this);
        tvHeader.setText("Forgot Password");
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        edit_number = findViewById(R.id.editForgotMobile);
        btnForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnForgotPassword:
                sendOTP();
                break;
            case R.id.btn_header_left:
                finish();
                break;
        }
    }
}