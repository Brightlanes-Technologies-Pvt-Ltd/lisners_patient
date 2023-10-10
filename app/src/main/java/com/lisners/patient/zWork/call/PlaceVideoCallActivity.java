package com.lisners.patient.zWork.call;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.databinding.ActivityPlaceVideoCallBinding;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PutApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class PlaceVideoCallActivity extends BaseActivity<ActivityPlaceVideoCallBinding> {
    private static final String LOG_TAG = PlaceVideoCallActivity.class.getSimpleName();

    private RtcEngine rtcEngine;

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    BookedAppointment appointment;
    User therapist;
    AppointmentModel appointmentDetail;

    long CALL_SEC = 600000;
    boolean call_status = false;

    CountDownTimer timer;

    boolean isTherapistJoined = false;
    long callStartTime;

    public static Intent makeIntent(Context context, BookedAppointment appointment) {
        Intent intent = new Intent(context, PlaceVideoCallActivity.class);
        intent.putExtra("appointment", appointment);
        return intent;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_place_video_call;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        appointment = getIntent().getParcelableExtra("appointment");
        appointment = GlobalData.call_appointment;

        therapist = appointment.getCounselor();
        appointmentDetail = appointment.getAppointment_detail();

        initBasicUi(true, false, false, "");
        permissionCheck();
        initUi();
    }

    private void initUi() {

        getBinding().audioBtn.setVisibility(View.GONE); // set the audio button hidden
        getBinding().leaveBtn.setVisibility(View.GONE); // set the leave button hidden
        getBinding().videoBtn.setVisibility(View.GONE); // set the video button hidden


        if (therapist.getProfile_image() != null)
            UtilsFunctions.SetLOGO(this, therapist.getProfile_image(), findViewById(R.id.iv_image));
        getBinding().tvProfileName.setText(UtilsFunctions.splitCamelCase(therapist.getName()));


    }

    private void initAgoraEngine() {

        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, " " + Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        setUpSession(appointment.getCall_type());

        if (appointmentDetail != null) {
            CALL_SEC = getTime(appointment.getCurrent_time()) - getTime(appointment.getDate() + " " + appointmentDetail.getStart_time());
        }
        setTimer(CALL_SEC);
    }

    private void setUpSession(String callType) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        if (callType.equals("video") && !call_status) {
            rtcEngine.enableVideo();
            rtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(width, height, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));

        } else if (!call_status) {
            rtcEngine.enableAudio(); // chnage call type
            audioCall();
        }

        callStartApi();

    }

    private void callStartApi() {
        if (appointment.getStatus() == 2) {
            joinChannel();
            return;
        }

        call_status = true;
        String URL = URLs.CONNECT_CALL + "/" + GlobalData.call_appointment.getId();
        GetApiHandler apiHandler = new GetApiHandler(this, URL, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("VI", new Gson().toJson(jsonObject));
                if (jsonObject.has("status")) {
                    joinChannel();
                }

            }

            @Override
            public void onError() {
                Log.e("ERROR", URL);
            }
        });
        apiHandler.execute();

    }

    private void callStopApi() {

        if (appointment.getStatus() == 2) {
            Intent intent3 = new Intent();
            setResult(ConstantValues.RESULT_CALL, intent3);
            finish();
            return;
        }

        if (timer != null)
            timer.cancel();
        if (isTherapistJoined) {

            String newString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            AppointmentModel appointmentModel = GlobalData.call_appointment.getAppointment_detail();
            String booked_id = GlobalData.call_appointment.getId() + "";
            if (appointmentModel != null)
                newString = GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time();
            long endTime = System.currentTimeMillis();

            long ss = 10;
            if (callStartTime > 1000)
                ss = (endTime - callStartTime) / 1000;

            Map<String, String> params = new HashMap<>();
            params.put("call_date", newString);
            params.put("call_time", ss + "");
            params.put("_method", "put");
            PutApiHandler postApiHandler = new PutApiHandler(this, URLs.STORE_CALL_APPOINTMENT + "/" + booked_id, params, new PutApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("DD", new Gson().toJson(jsonObject));

                    if (jsonObject.has("status")) {
                        Intent intent3 = new Intent();
                        setResult(ConstantValues.RESULT_CALL, intent3);
                        finish();
                    }
                }

                @Override
                public void onError(ANError error) {
                    if (error.getErrorBody() != null) {
                        APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            Toast.makeText(PlaceVideoCallActivity.this, apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            });
            postApiHandler.execute();
        } else
            finish();
    }

    private void audioCall() {

        getBinding().floatingVideoContainer.setVisibility(View.GONE);
        getBinding().videoBtn.setVisibility(View.GONE);

    }

    void permissionCheck() {
        if (checkPermission(REQUESTED_PERMISSIONS[0]) && checkPermission(REQUESTED_PERMISSIONS[1])) {
            initAgoraEngine();
        } else {
            askPermission(REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            permissionCheck();
        }
    }

    public long getTime(String dateSt) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = format.parse(dateSt);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }


    void setTimer(long time) {

        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                getBinding().tvTimer.setText(UtilsFunctions.calculateTime(sec));
            }

            @Override
            public void onFinish() {
                rtcEngine.leaveChannel();
                callStopApi();

            }
        };
    }


    public void joinChannel() {
        rtcEngine.joinChannel(null, appointment.getChenal_code(), "Extra Optional Data", 0);
        if (appointment.getCall_type().contains("video"))
            setupLocalVideoFeed();
        findViewById(R.id.audioBtn).setVisibility(View.VISIBLE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.VISIBLE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.VISIBLE); // set the video button hidden
    }

    // call disconnect by user
    public void onLeaveChannelClicked(View view) {
        rtcEngine.leaveChannel();
        getBinding().floatingVideoContainer.removeAllViews();
        getBinding().bgVideoContainer.removeAllViews();
        findViewById(R.id.audioBtn).setVisibility(View.GONE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.GONE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.GONE); // set the video button hidden

        callStopApi();

    }

    private void onRemoteUserLeft() {
        getBinding().bgVideoContainer.removeAllViews();
        callStopApi();
    }

    private void setupLocalVideoFeed() {

        // setup the container for the local user
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoSurface.setZOrderMediaOverlay(true);
        getBinding().floatingVideoContainer.addView(videoSurface);
        rtcEngine.setupLocalVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void setupRemoteVideoStream(int uid) {

        // ignore any new streams that join the session
        if (getBinding().bgVideoContainer.getChildCount() >= 1) {
            return;
        }

        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        getBinding().bgVideoContainer.addView(videoSurface);
        rtcEngine.setupRemoteVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, uid));
        rtcEngine.setRemoteSubscribeFallbackOption(io.agora.rtc.Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY);

    }

    private void onRemoteUserVideoToggle(int uid, int toggle) {

        SurfaceView videoSurface = (SurfaceView) getBinding().bgVideoContainer.getChildAt(0);
        videoSurface.setVisibility(toggle == 0 ? View.GONE : View.VISIBLE);

        // add an icon to let the other user know remote video has been disabled
        if (toggle == 0) {
            ImageView noCamera = new ImageView(this);
            noCamera.setImageResource(R.drawable.video_disabled);
            getBinding().bgVideoContainer.addView(noCamera);
        } else {
            ImageView noCamera = (ImageView) getBinding().bgVideoContainer.getChildAt(1);
            if (noCamera != null) {
                getBinding().bgVideoContainer.removeView(noCamera);
            }
        }
    }

    // call disconnect by therapist


    // Handle SDK Events
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getBinding().pbCallLoader.setVisibility(View.GONE);
                    setupRemoteVideoStream(uid);
                    startTimeCounter();
                }
            });
        }

        // remote user has left channel
        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        // remote user has toggled their video
        @Override
        public void onRemoteVideoStateChanged(final int uid, final int state, int reason, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoToggle(uid, state);
                }
            });
        }
    };

    private void startTimeCounter() {
        if (timer != null)
            timer.start();
        callStartTime = System.currentTimeMillis();
        isTherapistJoined = true;
    }


    public void onAudioMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.audio_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.audio_toggle_active_btn);
        }

        rtcEngine.muteLocalAudioStream(btn.isSelected());
    }

    public void onVideoMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.video_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.video_toggle_active_btn);
        }

        rtcEngine.muteLocalVideoStream(btn.isSelected());


        getBinding().floatingVideoContainer.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
        SurfaceView videoSurface = (SurfaceView) getBinding().floatingVideoContainer.getChildAt(0);
        videoSurface.setZOrderMediaOverlay(!btn.isSelected());
        videoSurface.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        rtcEngine.leaveChannel();
        RtcEngine.destroy();
        rtcEngine = null;

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onBackClicked() {

    }
}