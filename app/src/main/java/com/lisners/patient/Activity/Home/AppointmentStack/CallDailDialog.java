package com.lisners.patient.Activity.Home.AppointmentStack;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;

import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.utils.DateUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CallDailDialog {
    final Dialog call_dialog;
    Context context;
    ImageView profilePic;
    TextView profile_name, tv_des_time, tv_des_time_range;
    Button btn_accept;
    ImageButton btn_close;
    OnClickListener listener;


    public interface OnClickListener {
        void onAccept();
    }


    public CallDailDialog(Context context, OnClickListener listener) {
        call_dialog = new Dialog(context);
        this.context = context;
        this.listener = listener;
        call_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        call_dialog.setContentView(R.layout.call_accept_dailog);
        call_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        call_dialog.setCancelable(false);
        profilePic = call_dialog.findViewById(R.id.profile_photo);
        profile_name = call_dialog.findViewById(R.id.profile_name);
        tv_des_time = call_dialog.findViewById(R.id.tv_des_time);
        tv_des_time_range = call_dialog.findViewById(R.id.tv_des_time_range);
        btn_accept = call_dialog.findViewById(R.id.btn_accept);
        btn_close = call_dialog.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onAccept();

                dismiss();
            }
        });

    }

    public void checkAndShow(BookedAppointment appointment) {
        GlobalData.call_appointment = appointment;

        GetApiHandler apiHandler = new GetApiHandler(context, URLs.GET_BOOK_APPOINTMENT_DETAILS + "/" + appointment.getId(), new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("jsonObject", new Gson().toJson(jsonObject));
                if (jsonObject.has("status")) {
                    GlobalData.call_appointment = new Gson().fromJson(jsonObject.getString("data"), BookedAppointment.class);
                    User therapist = GlobalData.call_appointment.getCounselor();
                    AppointmentModel appointmentModel = GlobalData.call_appointment.getAppointment_detail();
                    UtilsFunctions.SetLOGO(context, therapist.getProfile_image(), profilePic);

                    profile_name.setText(therapist.getName());
                    btn_accept.setVisibility(View.VISIBLE);
                    if (appointmentModel != null) {
                        tv_des_time.setText(DateUtil.dateFormatter(GlobalData.call_appointment.getDate(), "dd-MM-yyyy", "dd MMM yyyy"));
                        tv_des_time_range.setText(String.format("%s - %s", DateUtil.dateFormatter(appointmentModel.getStart_time(), "HH:mm:ss", "hh:mm a"), DateUtil.dateFormatter(appointmentModel.getEnd_time(), "HH:mm:ss", "hh:mm a")));

                       /* tv_des_time.setText(UtilsFunctions.dateFormat(GlobalData.call_appointment.getDate()));
                        tv_des_time_range.setText(UtilsFunctions.timeFormat(appointmentModel.getStart_time()));*/

                        long currentTime = Calendar.getInstance().getTimeInMillis();

                        if (currentTime > getENDTime_Plus_30M(GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time())) {
                            btn_accept.setVisibility(View.GONE);
                        } else if (currentTime < getTime(GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time())) {
                            long tm = getTime(GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time()) - currentTime;
                            btn_accept.setEnabled(false);
                            startCounter(tm);
                        } else {
                            btn_accept.setVisibility(View.VISIBLE);

                        }

                    }

                    show();
                }

            }

            @Override
            public void onError() {
                Log.e("GET_APPOINTMENT", URLs.GET_APPOINTMENT + "/" + appointment.getId());
            }
        });

        apiHandler.execute();

    }

    public void startCounter(long TIME) {
        new CountDownTimer(TIME, 1000) {
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;

                btn_accept.setText(UtilsFunctions.calculateTime(sec));
            }

            public void onFinish() {
                btn_accept.setEnabled(true);
                btn_accept.setVisibility(View.VISIBLE);
                btn_accept.setText("Call Now");
            }
        }.start();
    }


    public static long getENDTime_Plus_30M(String dateSt) {
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateSt);
            return date.getTime()+30 * 60 * 1000;
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getTime(String dateSt) {
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateSt);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public void show() {
        if (call_dialog != null)
            call_dialog.show();

    }

    public void dismiss() {
        if (call_dialog != null)
            call_dialog.dismiss();
    }

}
