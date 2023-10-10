package com.lisners.patient.zWork.utils.config;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lisners.patient.BuildConfig;
import com.lisners.patient.GlobalData;
import com.lisners.patient.zWork.call.callbacks.EngineEventListener;
import com.lisners.patient.zWork.call.callbacks.IEventListener;
import com.lisners.patient.zWork.daggerClient.ApiModule;
import com.lisners.patient.zWork.daggerClient.AppComponent;
import com.lisners.patient.zWork.daggerClient.AppModule;
import com.lisners.patient.zWork.daggerClient.DaggerAppComponent;
import com.lisners.patient.zWork.utils.customClasses.ReleaseTree;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

import io.agora.rtc.RtcEngine;
import io.agora.rtm.RtmCallManager;
import io.agora.rtm.RtmClient;
import timber.log.Timber;


public class MainApplication extends Application {
    public static final String TAG = MainApplication.class.getSimpleName();
    private static MainApplication mInstance;
    private MerlinsBeard merlinsBeard;
    private Merlin merlin;
    private RtcEngine mRtcEngine;
    private RtmClient mRtmClient;
    private RtmCallManager rtmCallManager;
    private EngineEventListener mEventListener;

    private AppComponent mAppComponent;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public void onCreate() {
        super.onCreate();
        mEventListener = new EngineEventListener();
        merlin = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .build(this);
        merlinsBeard = MerlinsBeard.from(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }


        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).apiModule(new ApiModule()).build();


        //AppConfig.DEVICE_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id");
        //StrictMode.setVmPolicy(new Builder().build());
        mInstance = this;
        FirebaseApp.initializeApp(this);
        // Enable Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

    }

    public AppComponent getAppComponent() {
        return this.mAppComponent;
    }

   /* @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return null;
    }*/

    private void initAgoraEngine(String appId) {


        if (TextUtils.isEmpty(appId)) {

            return;
            //throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
        }

        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), appId, mEventListener);
            mRtmClient = RtmClient.createInstance(getApplicationContext(), appId, mEventListener);
            rtmCallManager = mRtmClient.getRtmCallManager();
            rtmCallManager.setEventListener(mEventListener);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static MainApplication get(Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    public static MainApplication get(Application application) {
        return (MainApplication) application;
    }

    public Merlin getMerlin() {
        return merlin;
    }

    public MerlinsBeard getMerlinsBeard() {
        return merlinsBeard;
    }



    public RtcEngine rtcEngine() {
        if (mRtcEngine == null) {
            Log.e("initializing agora ","==>"+GlobalData.setting_agora_id_model.getValue());
            initAgoraEngine(GlobalData.setting_agora_id_model.getValue());
        }
        return mRtcEngine;
    }

    public RtmClient rtmClient() {
        return mRtmClient;
    }

    public void registerEventListener(IEventListener listener) {
        mEventListener.registerEventListener(listener);
    }

    public void removeEventListener(IEventListener listener) {
        mEventListener.removeEventListener(listener);
    }

    public RtmCallManager rtmCallManager() {
        return rtmCallManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        destroyEngine();
    }

    private void destroyEngine() {
        RtcEngine.destroy();

    }
}
