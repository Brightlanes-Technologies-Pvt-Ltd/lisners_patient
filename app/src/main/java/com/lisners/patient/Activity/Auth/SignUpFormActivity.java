package com.lisners.patient.Activity.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.WebviewActivity;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.utils.DialogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFormActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvHeader,tvTerms,tvPrivacy;
    private Button btnSUpDetailSubmit;
    ImageButton btn_header_left;
    EditText edit_name, edit_email, edit_pass, edit_cfmPass, edit_age;
    CheckBox check_box;
    boolean isCheckeds = false;
    StoreData storeData;
    String st_name, st_email, st_pass, st_cfm_pass, st_age;

    RadioButton radioMale, radioFemale, radioOther;


    DialogUtil dialogUtil = new DialogUtil(this);
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);
        init();
        btnSUpDetailSubmit.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
    }

    private void init() {

        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        radioOther = findViewById(R.id.radioOther);

        tvHeader = findViewById(R.id.tvHeader);
        btn_header_left = findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        btn_header_left.setOnClickListener(this);
        tvHeader.setText("Create an Account");
        storeData = new StoreData(this);
        edit_name = findViewById(R.id.etSUpDetailFirstName);
        edit_email = findViewById(R.id.etSUpDetailEmail);
        edit_pass = findViewById(R.id.etSUpDetailPassword);
        edit_cfmPass = findViewById(R.id.etSUpDetailCnfPassword);
        edit_age = findViewById(R.id.etAge);
        check_box = findViewById(R.id.checkSUpDetailAgreeTerms);
        btnSUpDetailSubmit = findViewById(R.id.btnSUpDetailSubmit);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy = findViewById(R.id.tvPrivacy);

        check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckeds = isChecked;
            }
        });

    }


    public void signUpApi() {
        st_name = edit_name.getText().toString().trim();
        st_email = edit_email.getText().toString().trim();
        st_pass = edit_pass.getText().toString();
        st_cfm_pass = edit_cfmPass.getText().toString();
        st_age = edit_age.getText().toString();

        if (st_name.isEmpty())
            edit_name.setError("Enter Name");
        else if (st_email.isEmpty())
            edit_email.setError("Enter Email");
        else if (!UtilsFunctions.isValidEmail(st_email))
            edit_email.setError("Invalid Email");
        else if (st_age.isEmpty())
            edit_age.setError("Enter Age");
        else if (st_pass.isEmpty())
            edit_pass.setError("Enter Password");
        else if (st_pass.length() <= 7)
            edit_cfmPass.setError("Your password must be more than 8 characters");
        else if (st_cfm_pass.isEmpty())
            edit_cfmPass.setError("Enter Confirm Password");
        else if (!st_pass.equals(st_cfm_pass))
            edit_cfmPass.setError("Confirm password is not the same as password");
        else if (!isCheckeds)
            Toast.makeText(this, "Kindly accept the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show();
        else {

            DProgressbar dProgressbar = new DProgressbar(this);
            dProgressbar.show();
            Map<String, String> params = new HashMap<>();
            params.put("name", st_name);
            params.put("email", st_email);
            params.put("password", st_pass);
            params.put("confirm_password", st_cfm_pass);
            params.put("age", st_age);
            params.put("gender", radioMale.isChecked() ? "male" : radioFemale.isChecked() ? "female" : "Other");

            PostApiHandler postApiHandler = new PostApiHandler(this, URLs.SIGNUP_UPDATE, params, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    dProgressbar.dismiss();
                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        storeData.setData(ConstantValues.USER_DATA, jsonObject.getString("data"), new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                showFirstUserDialog();
                            }
                        });

                    } else {
                        if (jsonObject.has("type") && jsonObject.getString("type").equalsIgnoreCase("validation")) {
                            if (jsonObject.has("errors")) {
                                JSONObject errObj = jsonObject.getJSONObject("errors");
                                if (errObj.has("name"))
                                    edit_name.setError(UtilsFunctions.errorShow(errObj.getJSONArray("name")));
                                else if (errObj.has("email"))
                                    edit_email.setError(UtilsFunctions.errorShow(errObj.getJSONArray("email")));
                                else if (errObj.has("password"))
                                    edit_pass.setError(UtilsFunctions.errorShow(errObj.getJSONArray("password")));
                                else if (errObj.has("confirm_password"))
                                    edit_cfmPass.setError(UtilsFunctions.errorShow(errObj.getJSONArray("confirm_password")));


                            }

                        } else {
                            Toast.makeText(SignUpFormActivity.this,
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(SignUpFormActivity.this,
                            "Server Error", Toast.LENGTH_SHORT).show();
                    dProgressbar.dismiss();
                }
            });
            postApiHandler.execute();
        }


    }


    private void showFirstUserDialog() {
        alertDialog = dialogUtil.createFirstFreeUserDialog("","Dear user you got your first appointment free", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();

                Intent i = new Intent(SignUpFormActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSUpDetailSubmit:
                signUpApi();

                break;
            case R.id.btn_header_left:
                finish();
                break;
            case R.id.tvPrivacy:
                Intent intent = new Intent(this, TermsAndCondWebView.class);
                intent.putExtra("webViewString", "https://www.lisners.com/privacy-policy/");
                startActivity(intent);
                break;
            case R.id.tvTerms:
                Intent intentView = new Intent(this, TermsAndCondWebView.class);
                intentView.putExtra("webViewString", "https://www.lisners.com/terms-of-use/");
                startActivity(intentView);
                break;
        }
    }
}