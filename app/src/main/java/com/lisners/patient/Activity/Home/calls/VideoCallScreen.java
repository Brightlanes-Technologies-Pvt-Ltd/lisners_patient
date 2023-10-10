package com.lisners.patient.Activity.Home.calls;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PutApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;

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


public class VideoCallScreen extends AppCompatActivity {
    private RtcEngine mRtcEngine;
    TextView tv_profile_name  ,tv_timer;
    boolean tarepistjoin = false ;
    long CALL_SEC = 600000;
    // Permissions
    CountDownTimer countDownTimer ;
    long startTime  ;
    DProgressbar dProgressbar ;
    boolean call_status = false ;
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    private static final String LOG_TAG = VideoCallScreen.class.getSimpleName();

    // Handle SDK Events
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set first remote user to the main bg video container
                    setupRemoteVideoStream(uid);
                }
            });
        }

        // remote user has left channel
        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        // remote user has toggled their video
        @Override
        public void onUserMuteVideo(final int uid, final boolean toggle) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoToggle(uid, toggle);
                }
            });
        }
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            if(countDownTimer!=null)
                countDownTimer.start();
            startTime = System.currentTimeMillis();
            tarepistjoin = true;

        }



    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);
        dProgressbar  = new DProgressbar(this);
        tv_profile_name = findViewById(R.id.tv_profile_name);
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initAgoraEngine();
        }


        findViewById(R.id.audioBtn).setVisibility(View.GONE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.GONE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.GONE); // set the video button hidden
    }



    public void callStart(){
        call_status =true ;
        String URL =URLs.CONNECT_CALL+"/"+ GlobalData.call_appointment.getId();
            GetApiHandler apiHandler = new GetApiHandler(this, URL, new GetApiHandler.OnClickListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) throws JSONException {
                         Log.e("VI",new Gson().toJson(jsonObject));
                        if(jsonObject.has("status")) {
                            callDStartNew();
                            if(GlobalData.call_appointment.getCall_type().contains("video"))
                             setupLocalVideoFeed();
                        }

                    }

                    @Override
                    public void onError() {
                        Log.e("ERROR",URL);
                    }
                });
        apiHandler.execute();
    }

    public void callDStartNew (){
        mRtcEngine.joinChannel(null, GlobalData.call_appointment.getChenal_code(), "Extra Optional Data", 0); // if you do not specify the uid, Agora will assign one.

        findViewById(R.id.pb_call_loader).setVisibility(View.GONE);
        findViewById(R.id.joinBtn).setVisibility(View.GONE); // set the join button hidden
        findViewById(R.id.audioBtn).setVisibility(View.VISIBLE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.VISIBLE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.VISIBLE); // set the video button hidden


    }
    private void initAgoraEngine() {

        try {
            //mRtcEngine = RtcEngine.create(this,GlobalData.setting_model.getAgora_app_id(), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG," "+ Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        if (GlobalData.call_appointment != null) {
            User therapist = GlobalData.call_appointment.getCounselor();
            Log.e("USER", new Gson().toJson(therapist));
            AppointmentModel appointmentModel = GlobalData.call_appointment.getAppointment_detail();
            if (therapist.getProfile_image() != null) {
                UtilsFunctions.SetLOGO(this, therapist.getProfile_image(), findViewById(R.id.iv_image));

                tv_profile_name.setText(UtilsFunctions.splitCamelCase(therapist.getName()));
            }

            setupSession(GlobalData.call_appointment.getCall_type());
            if (appointmentModel != null)
                CALL_SEC = getTime(GlobalData.call_appointment.getCurrent_time()) - getTime(GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time());
            startCounter(CALL_SEC);

        }

    }

    public  long getTime(String dateSt ) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = format.parse(dateSt);
            return  date.getTime();
        }catch (Exception e){
            return 0 ;
        }
    }

    public void startCounter(long TIME) {
        Log.e("JOIN",TIME+"");
        tv_timer = findViewById(R.id.tv_timer);
        tv_timer.setText("Start");
          countDownTimer =   new CountDownTimer(TIME , 1000) {
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                tv_timer.setText(UtilsFunctions.calculateTime(sec));
            }
            public void onFinish() {

                callClose();
            }
        } ;
    }


    private void setupSession(String type) {

        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        if(type.equals("video") && !call_status) {
            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_180x180, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));

            callStart();
        }
        else if(!call_status) {
            mRtcEngine.enableAudio(); // chnage call type
            audioCall();
            callStart();
        }

    }

    private void setupLocalVideoFeed() {
        // setup the container for the local user
        FrameLayout videoContainer = findViewById(R.id.floating_video_container);
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoSurface.setZOrderMediaOverlay(true);
        videoContainer.addView(videoSurface);
        mRtcEngine.setupLocalVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void setupRemoteVideoStream(int uid) {
        // setup ui element for the remote stream
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);
        // ignore any new streams that join the session
        if (videoContainer.getChildCount() >= 1) {
            return;
        }

        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoContainer.addView(videoSurface);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, uid));
        mRtcEngine.setRemoteSubscribeFallbackOption(io.agora.rtc.Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY);

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

        mRtcEngine.muteLocalAudioStream(btn.isSelected());
    }

    private void audioCall(){
        FrameLayout container = findViewById(R.id.floating_video_container);
        container.setVisibility(View.GONE);
        findViewById(R.id.videoBtn).setVisibility(View.GONE);
        callStart();
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

        mRtcEngine.muteLocalVideoStream(btn.isSelected());

        FrameLayout container = findViewById(R.id.floating_video_container);
        container.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
        SurfaceView videoSurface = (SurfaceView) container.getChildAt(0);
        videoSurface.setZOrderMediaOverlay(!btn.isSelected());
        videoSurface.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
    }

    // join the channel when user clicks UI button
    public void onjoinChannelClicked(View view) {
        mRtcEngine.joinChannel(null, "test-channel", "Extra Optional Data", 0); // if you do not specify the uid, Agora will assign one.
        setupLocalVideoFeed();
        findViewById(R.id.joinBtn).setVisibility(View.GONE); // set the join button hidden
        findViewById(R.id.audioBtn).setVisibility(View.VISIBLE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.VISIBLE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.VISIBLE); // set the video button hidden

    }

    public void onLeaveChannelClicked(View view) {
        leaveChannel();
        removeVideo(R.id.floating_video_container);
        removeVideo(R.id.bg_video_container);
        findViewById(R.id.joinBtn).setVisibility(View.VISIBLE); // set the join button visible
        findViewById(R.id.audioBtn).setVisibility(View.GONE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.GONE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.GONE); // set the video button hidden
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
        callClose();
    }

    private void removeVideo(int containerID) {
        FrameLayout videoContainer = findViewById(containerID);
        videoContainer.removeAllViews();

    }

    private void onRemoteUserVideoToggle(int uid, boolean toggle) {
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);

        SurfaceView videoSurface = (SurfaceView) videoContainer.getChildAt(0);
        videoSurface.setVisibility(toggle ? View.GONE : View.VISIBLE);

        // add an icon to let the other user know remote video has been disabled
        if(toggle){
            ImageView noCamera = new ImageView(this);
            noCamera.setImageResource(R.drawable.video_disabled);
            videoContainer.addView(noCamera);
        } else {
            ImageView noCamera = (ImageView) videoContainer.getChildAt(1);
            if(noCamera != null) {
                videoContainer.removeView(noCamera);
            }
        }
    }

    private void onRemoteUserLeft() {
        removeVideo(R.id.bg_video_container);
        callClose();
    }



    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA);
                    break;
                }

                initAgoraEngine();
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;


    }


    public void callClose()   {
        if(countDownTimer!=null)
            countDownTimer.cancel();
        if(tarepistjoin) {

            String newString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            AppointmentModel appointmentModel = GlobalData.call_appointment.getAppointment_detail();
            String booked_id = GlobalData.call_appointment.getId() + "";
            if (appointmentModel != null)
                newString = GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time();
            long endTime = System.currentTimeMillis();

            long ss = 10 ;
            if(startTime>1000)
              ss  = (endTime - startTime)/1000 ;

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
                    if(error.getErrorBody()!=null) {
                        APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(),APIErrorModel.class);
                        if(apiErrorModel.getMessage()!=null)
                            Toast.makeText(VideoCallScreen.this, apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            });
            postApiHandler.execute();
        }else
            finish();
    }


}