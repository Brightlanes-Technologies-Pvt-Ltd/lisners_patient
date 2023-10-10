package com.lisners.patient.Activity.Home.calls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.PutApiHandler;
import com.lisners.patient.utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CallActivity extends AppCompatActivity {
   ImageButton btn_call_close ;
    Date date;
    TextView tv_timer ;
    Long sec   ;
    int CALL_LIMIT =60000*10 ;
    DProgressbar dProgressbar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        btn_call_close = findViewById(R.id.btn_call_close);
        tv_timer = findViewById(R.id.tv_timer);
        dProgressbar = new DProgressbar(this);
        date = new Date();
        btn_call_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callClose();
                }catch (ParseException e)
                {}
            }
        });
        startCounter();
    }

    public void startCounter() {
        new CountDownTimer(CALL_LIMIT, 1000) {
            public void onTick(long millisUntilFinished) {
                Long remainder  = millisUntilFinished / 1000;
                sec = remainder ;
                Long mins = remainder / 60;
                remainder = remainder - mins * 60;
                Long secs = remainder;
                String str = sec < 10 ? "0:0" + sec : mins+":"+secs ;

                if (sec > 0)
                    tv_timer.setText(str);
                else
                    tv_timer.setText("");


            }

            public void onFinish() {

            }
        }.start();
    }



    public void callClose() throws ParseException {
        dProgressbar.show();

        String booked_id = getIntent().getStringExtra("BOOK_ID");
        String booked_time = getIntent().getStringExtra("BOOK_TIME");
//       if(booked_time!=null)
//          date = new SimpleDateFormat("UTC").parse(booked_time);
        String newString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        Map<String ,String> params = new HashMap<>();
        params.put("call_date",newString);
        params.put("call_time","100");
        params.put("_method","put");
        PutApiHandler postApiHandler = new PutApiHandler(this, URLs.STORE_CALL_APPOINTMENT+"/"+booked_id, params, new PutApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("DD",new Gson().toJson(jsonObject));
                dProgressbar.dismiss();
                if(jsonObject.has("status")){
                    Intent intent3 = new Intent();
                    setResult(ConstantValues.RESULT_CALL, intent3);
                    finish();
                }
            }

            @Override
            public void onError(ANError error) {
                dProgressbar.dismiss();
                if(error.getErrorBody()!=null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(),APIErrorModel.class);
                    if(apiErrorModel.getMessage()!=null)
                        Toast.makeText(CallActivity.this, apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        postApiHandler.execute();
    }

}