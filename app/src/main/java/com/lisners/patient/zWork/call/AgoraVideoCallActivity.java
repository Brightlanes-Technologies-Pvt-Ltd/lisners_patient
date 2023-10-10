package com.lisners.patient.zWork.call;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.databinding.ActivityAgoraVideoCallBinding;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.PutApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.base.SocketSingleton;
import com.lisners.patient.zWork.call.callbacks.IEventListener;
import com.lisners.patient.zWork.restApi.viewmodel.ContentsViewModel;
import com.lisners.patient.zWork.utils.DateUtil;
import com.lisners.patient.zWork.utils.DialogUtil;
import com.lisners.patient.zWork.utils.TimeUtils;
import com.lisners.patient.zWork.utils.ViewModelUtils;
import com.lisners.patient.zWork.utils.aModel.CallData;
import com.lisners.patient.zWork.utils.config.MainApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AgoraVideoCallActivity extends BaseCallActivity<ActivityAgoraVideoCallBinding> {
    private static final String LOG_TAG = AgoraVideoCallActivity.class.getSimpleName();

    BookedAppointment appointment;
    User therapist;
    AppointmentModel appointmentDetail;

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    boolean isTherapistJoined = false;

    long statTime = 0;
    long endTime = 0;


    DialogUtil dialogUtil;
    AlertDialog alertDialog;
    ContentsViewModel contentsVM;

    String rtmToken = "";

    IntentFilter filter = new IntentFilter("call_cut");
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("smsBroadcastReceiver", "onReceive");
            if (intent != null) {
                Log.e("rec===?>", "" + intent.getStringExtra("data"));
                Log.e("rec===?>", "" + "" + appointment.getId() + "---" + appointment.getCounselor_id() + "---" + appointment.getUser_id());

                CallData callData = new Gson().fromJson(intent.getStringExtra("data"), CallData.class);
                if (callData.getBookAppointmentId().equals("" + appointment.getId()) && callData.getCounsellorId().equals("" + appointment.getCounselor_id()) && callData.getPatientId().equals("" + appointment.getUser_id())) {
                    isRejectByCounsellor = true;
                    finishCall("Call rejected by counsellor");
                }
            }
        }
    };


    public static Intent makeIntent(Context context, BookedAppointment appointment) {
        Intent intent = new Intent(context, AgoraVideoCallActivity.class);
        intent.putExtra("appointment", appointment);
        return intent;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_agora_video_call;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        contentsVM = ViewModelUtils.getViewModel(ContentsViewModel.class, this);


        dialogUtil = new DialogUtil(getContext());

        appointment = getIntent().getParcelableExtra("appointment");
        appointment = GlobalData.call_appointment;

        therapist = appointment.getCounselor();
        appointmentDetail = appointment.getAppointment_detail();

        initBasicUi(true, false, false, "");


        getBinding().btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked(view);
            }
        });

        getBinding().btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked(view);
            }
        });


        getBinding().btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked(view);
            }
        });


        permissionCheck();
        iniUI();
        connectToSocket();
    }

    private void iniUI() {


        getBinding().btnMute.setActivated(true);
        if (therapist.getProfile_image() != null) {
            UtilsFunctions.SetLOGO(this, therapist.getProfile_image(), getBinding().ivProfile);
            getBinding().ivImageChar.setVisibility(View.GONE);
        } else {
            getBinding().ivImageChar.setText(UtilsFunctions.getFistLastChar(therapist.getName()));
            getBinding().ivImageChar.setVisibility(View.VISIBLE);

        }

        getBinding().tvProfileName.setText(UtilsFunctions.splitCamelCase(therapist.getName()));


    }

    void permissionCheck() {
        if (checkPermission(REQUESTED_PERMISSIONS[0]) && checkPermission(REQUESTED_PERMISSIONS[1])) {
            //initAgoraEngine();
            showProgressDialog();
            contentsVM
                    .getRtmToken(appointment.getChenal_code())
                    .observe(this, response -> {
                        closeProgressDialog();

                        if (response.getStatus() && response.getData() != null) {
                            rtmToken = response.getData();
                            initAgoraEngine(response.getData());
                        } else {
                            finishCall("Call not Placed.Some error occurred");
                        }

                    });


        } else {
            askPermission(REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

    }

    private void initAgoraEngine(String token) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        rtcEngine().enableDualStreamMode(true);
        if (appointment.getCall_type().equals("video")) {
            getBinding().btnSwitchCamera.setVisibility(View.VISIBLE);
            getBinding().btnSpeaker.setVisibility(View.INVISIBLE);
            rtcEngine().enableVideo();
            rtcEngine().setVideoEncoderConfiguration(
                    new VideoEncoderConfiguration(
                            width,
                            height,
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT)

            );

            getBinding().remotePreviewLayout.setVisibility(View.VISIBLE);
            getBinding().localPreviewLayout.setVisibility(View.VISIBLE);
        } else {
            getBinding().btnSwitchCamera.setVisibility(View.INVISIBLE);
            getBinding().btnSpeaker.setVisibility(View.VISIBLE);
            //rtcEngine().setEnableSpeakerphone(true);
            rtcEngine().disableVideo();
            rtcEngine().enableAudio();
            getBinding().remotePreviewLayout.setVisibility(View.INVISIBLE);
            getBinding().localPreviewLayout.setVisibility(View.GONE);
        }


        setupLocalPreview();
        joinRtcChannel(token, appointment.getChenal_code(), "", Integer.parseInt("0"));
    }

    private void setupLocalPreview() {
        SurfaceView surfaceView = setupVideo(Integer.parseInt("0"), true);
        surfaceView.setZOrderOnTop(true);
        getBinding().localPreviewLayout.addView(surfaceView);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            permissionCheck();
        }
    }


    private void callStartApi(String rtmToken) {

      /*  String URL = URLs.CONNECT_CALL + "/" + GlobalData.call_appointment.getId();
        GetApiHandler apiHandler = new GetApiHandler(this, URL, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("VI", new Gson().toJson(jsonObject));
                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                    getBinding().buttonLayout.setVisibility(View.VISIBLE);
                } else {
                    finishCall("Call not Placed.Some error occurred");
                    Toast.makeText(getContext(), "Call not placed", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError() {
                finishCall("Call not Placed.Some error occurred");
                Toast.makeText(getContext(), "Call not placed", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", URL);
            }
        });
        apiHandler.execute();*/


        Map<String, String> params = new HashMap<>();
        params.put("book_appointment_id", "" + GlobalData.call_appointment.getId());
        params.put("token", rtmToken);
        PostApiHandler postApiHandler = new PostApiHandler(this, URLs.CONNECT_CALL, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("DD", new Gson().toJson(jsonObject));

                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                    getBinding().buttonLayout.setVisibility(View.VISIBLE);
                } else {
                    finishCall("Call not Placed.Some error occurred");
                    Toast.makeText(getContext(), "Call not placed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError error) {
                finishCall("Call not Placed.Some error occurred");
                Toast.makeText(getContext(), "Call not placed", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", URLs.CONNECT_CALL);
                /*if (error.getErrorBody() != null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                    if (apiErrorModel.getMessage() != null)
                        Toast.makeText(AgoraVideoCallActivity.this, apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }*/

            }
        });
        postApiHandler.execute();

    }


    boolean stopApiCall = true;


    private void callStopApi() {

        if (stopApiCall) {
            stopApiCall = false;
        } else {
            return;
        }

        if (isTherapistJoined) {
            showProgressDialog();

            endTime = Calendar.getInstance().getTimeInMillis();

            /*String newString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());*/
            AppointmentModel appointmentModel = GlobalData.call_appointment.getAppointment_detail();
            String booked_id = GlobalData.call_appointment.getId() + "";
           /* if (appointmentModel != null)
                newString = GlobalData.call_appointment.getDate() + " " + appointmentModel.getStart_time();*/

            String end_date = DateUtil.getDateInStringFromDate(Calendar.getInstance().getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");


            long diff = TimeUtils.getTimeDifferenceInSecondsForCall(statTime, endTime);

            Map<String, String> params = new HashMap<>();
            params.put("call_date", end_date);
            params.put("call_time", diff + "");
            params.put("_method", "put");
            PutApiHandler postApiHandler = new PutApiHandler(this, URLs.STORE_CALL_APPOINTMENT + "/" + booked_id, params, new PutApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("DD", new Gson().toJson(jsonObject));
                    stopApiCall = true;
                    closeProgressDialog();
                    if (jsonObject.has("status")) {
                        Intent intent3 = new Intent();
                        setResult(ConstantValues.RESULT_CALL, intent3);
                        finishCall("You decide to end the session. If it got disconnected by mistake/network issues, you can call the therapist again");
                    }
                }

                @Override
                public void onError(ANError error) {
                    stopApiCall = true;
                    closeProgressDialog();
                    if (error.getErrorBody() != null) {
                        APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            Toast.makeText(AgoraVideoCallActivity.this, apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            });
            postApiHandler.execute();
        } else {
            stopApiCall = true;
            finishCall("You decide to end the session. If it got disconnected by mistake/network issues, you can call the therapist again");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, filter);
        registerEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        removeEventListener(this);
    }

    private void registerEventListener(IEventListener listener) {
        application().registerEventListener(listener);
    }

    private void removeEventListener(IEventListener listener) {
        application().removeEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        removeEventListener(this);
        onDestroySocket();
    }


    public MainApplication application() {
        return (MainApplication) getApplication();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }


    protected void joinRtcChannel(String accessToken, String channel, String info, int uid) {
       /* String accessToken = getString(R.string.agora_access_token);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "<#YOUR ACCESS TOKEN#>")) {
            accessToken = null;
        }*/
        /*String aToken = new String(new Base64(true).encodeAsString(accessToken.getBytes(StandardCharsets.UTF_8)));*/
        Log.e("rtmToken ==>", "" + accessToken);
        Log.e("channel ==>", "" + channel);
        Log.e("info ==>", "" + info);
        Log.e("uid ==>", "" + uid);


        rtcEngine().joinChannel(accessToken, channel, info, uid);
    }

    protected void leaveChannel() {
        rtcEngine().leaveChannel();
    }


    protected SurfaceView setupVideo(int uid, boolean local) {
        SurfaceView surfaceView = RtcEngine.
                CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(new VideoCanvas(surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid));
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }

        return surfaceView;
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        if (!rtmToken.isEmpty())
            callStartApi(rtmToken);
    }

    @Override
    public void onUserJoined(final int uid, int elapsed) {
        //if (uid != mPeerUid) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isCallPickedUp = true;
                statTime = Calendar.getInstance().getTimeInMillis();
                startCounter(TimeUtils.getTimeDifferenceInMiliSecondsForCall(statTime, getENDTime_Plus_30M(appointment.getDate() + " " + appointmentDetail.getStart_time())));
                getBinding().tvTimer.setVisibility(View.VISIBLE);

                isTherapistJoined = true;
                getBinding().loader.setVisibility(View.GONE);
                if (getBinding().remotePreviewLayout.getChildCount() == 0) {
                    SurfaceView surfaceView = setupVideo(uid, false);
                    getBinding().remotePreviewLayout.addView(surfaceView);
                }


            }
        });
    }

    CountDownTimer timer;

    private void startCounter(long miliSeconds) {

        timer = new CountDownTimer(miliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                getBinding().tvTimer.setText(UtilsFunctions.calculateTime(sec));
            }

            @Override
            public void onFinish() {
                callStopApi();

            }
        };

        if (timer != null)
            timer.start();
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        //if (uid != mPeerUid) return;
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        }

    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_endcall:
                if (timer != null) {
                    timer.cancel();
                    timer.onFinish();
                } else {
                    callStopApi();
                }
                attemptSendCallRejectToSocket("" + GlobalData.call_appointment.getId(), "" + GlobalData.call_appointment.getCounselor_id(), "" + GlobalData.call_appointment.getUser_id());

                break;
            case R.id.btn_mute:
                rtcEngine().muteLocalAudioStream(getBinding().btnMute.isActivated());
                getBinding().btnMute.setActivated(!getBinding().btnMute.isActivated());
                break;
            case R.id.btn_switch_camera:
                rtcEngine().switchCamera();
                break;

            case R.id.btn_speaker:
                rtcEngine().setEnableSpeakerphone(!getBinding().btnSpeaker.isActivated());
                getBinding().btnSpeaker.setActivated(!getBinding().btnSpeaker.isActivated());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        } else {
            callStopApi();
        }
    }

    @Override
    public void onBackClicked() {
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        } else {
            callStopApi();
        }
    }

    public void finishCall(String msg) {
        leaveChannel();
        getBinding().getRoot().setVisibility(View.GONE);
        alertDialog = dialogUtil.createAppointmentCallOverDialog("", msg, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.cancel();
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                finish();

            }
        });
        alertDialog.show();

    }

    @Override
    public void finish() {
        super.finish();
    }


    public static int getSystemStatusBarHeight(Context context) {
        int id = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        return id > 0 ? context.getResources().getDimensionPixelSize(id) : id;
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

    public static long getENDTime_Plus_30M(String dateSt) {
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateSt);
            return date.getTime() + 30 * 60 * 1000;
        } catch (Exception e) {
            return 0;
        }
    }


    Socket mSocket;

    void connectToSocket() {
        setOneMinuteRing();
        SocketSingleton socketSingleton = SocketSingleton.getSync(AgoraVideoCallActivity.this);
        mSocket = socketSingleton.getSocket();

        mSocket.on("patient-call-reject", onCallCutSocketEvent);
        mSocket.connect();
    }


    private Emitter.Listener onCallCutSocketEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }*/

                    //((TextView) findViewById(R.id.tv)).setText("" + new Gson().toJson(args));


                    // add the message to view

                }
            });
        }
    };


    private void attemptSendCallRejectToSocket(String bookAppointmentId, String counsellorId, String patientId) {

        CallData callData = new CallData(bookAppointmentId, counsellorId, patientId);
        String msg = new Gson().toJson(callData);
        mSocket.emit("patient-call-reject", msg);
    }

    protected void onDestroySocket() {


        mSocket.disconnect();
        mSocket.off("patient-call-reject", onCallCutSocketEvent);


    }

    boolean isCallPickedUp = false;
    boolean isRejectByCounsellor = false;

    void setOneMinuteRing() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if (!isCallPickedUp && !isRejectByCounsellor) {
                        attemptSendCallRejectToSocket("" + GlobalData.call_appointment.getId(), "" + GlobalData.call_appointment.getCounselor_id(), "" + GlobalData.call_appointment.getUser_id());
                        callStopApi();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(mainIntent);
//                finish();
            }
        }, 1000 * 40);

    }
}