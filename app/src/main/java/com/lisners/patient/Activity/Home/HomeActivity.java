package com.lisners.patient.Activity.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.androidnetworking.error.ANError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.lisners.patient.Activity.Auth.WelcomeActivity;
import com.lisners.patient.Activity.Home.AppointmentStack.BookedAppointmentFragment;
import com.lisners.patient.Activity.Home.HomeStack.AdvanceSearchFragment;
import com.lisners.patient.Activity.Home.HomeStack.ConsultationDetailsFragment;
import com.lisners.patient.Activity.Home.HomeStack.FavouriteFragment;
import com.lisners.patient.Activity.Home.HomeStack.HomeFragment;
import com.lisners.patient.Activity.Home.HomeStack.SettingsFragment;
import com.lisners.patient.Activity.Home.HomeStack.WalletHistoryFragment;
import com.lisners.patient.Activity.Home.ProfileStack.EditProfileFragment;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.NotiClickModel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.ActivityResultBus;
import com.lisners.patient.utils.ActivityResultEvent;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.base.SocketSingleton;
import com.lisners.patient.zWork.restApi.pojo.SettingPojo;
import com.lisners.patient.zWork.restApi.viewmodel.SettingViewModel;
import com.lisners.patient.zWork.utils.ViewModelUtils;
import com.lisners.patient.zWork.utils.config.MainApplication;
import com.lisners.patient.zWork.utils.helperClasses.ConnectionMessageLayout;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import timber.log.Timber;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {


    BottomNavigationView bottom_tab_navigation;
    NavigationView navigationView;
    FragmentManager fm;
    DrawerLayout mDrawer;
    StoreData storeData;
    DProgressbar dProgressbar;
    AlertDialog alertDialog = null;

    Merlin merlin;
    SettingViewModel settingVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dProgressbar = new DProgressbar(this);
        merlin = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks().build(this);
        init();
    }

    private void init() {

        settingVM = ViewModelUtils.getViewModel(SettingViewModel.class, this);


        mDrawer = findViewById(R.id.drawer_layout);
        bottom_tab_navigation = findViewById(R.id.bottomNavigationView);
        bottom_tab_navigation.setOnNavigationItemSelectedListener(this);
        navigationView = findViewById(R.id.nav_view);

        if (storeData == null) {
            storeData = new StoreData(this);
            fm = this.getSupportFragmentManager();
            if (navigationView != null) {
                setupDrawerContent(navigationView);
            }
            loadFragment(new HomeFragment());
        }
        setProfile();
        getSetting();
        getProfile();
        connectToSocket();
        receiveNotification();
    }

    private void callNewScreen(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
    }


    private void receiveNotification() {
        Intent intent = getIntent();
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase("deepLink_intent")) {
            String data = getIntent().getStringExtra("data");
            Map<String, String> dataMap = new Gson().fromJson(data, Map.class);

            if (dataMap.get("type").equals("book_appointment_details")) {

                GlobalData.call_appointment = new BookedAppointment();
                GlobalData.call_appointment.setId(Integer.parseInt(dataMap.get("book_appointment_id")));
                callNewScreen(new ConsultationDetailsFragment());

            } else if (dataMap.get("type").equals("profile")) {
                bottom_tab_navigation.setSelectedItemId(R.id.menu_action_profile);
            } else if (dataMap.get("type").equals("appointment")) {
                bottom_tab_navigation.setSelectedItemId(R.id.menu_action_appointment);
            } else if (dataMap.get("type").equals("transaction_list")) {
                bottom_tab_navigation.setSelectedItemId(R.id.nav_wallet);
            }

        }

    }


    private void getNotificatiom() {
        String action = getIntent().getStringExtra("CALLING_ACTION");
        Log.e("[CALLING_ACTION]", action + "");
        if (action != null) {
            NotiClickModel noti = new Gson().fromJson(action, NotiClickModel.class);
            if (noti.getKey().equals("transaction_list"))
                callNewScreen(new WalletHistoryFragment());
            else if (noti.getKey().equals("appointment"))
                callNewScreen(new BookedAppointmentFragment());
            else if (noti.getKey().equals("book_appointment_details"))
                callNewScreen(new BookedAppointmentFragment());

        }
    }

    public void setProfile() {
        View header = navigationView.getHeaderView(0);
        ImageView imag = (ImageView) header.findViewById(R.id.iv_profile_drawer);
        TextView tv_name = (TextView) header.findViewById(R.id.header_title);
        TextView tv_char = (TextView) header.findViewById(R.id.iv_char_drawer);
        storeData.getData(ConstantValues.USER_DATA, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                if (val != null) {
                    User user = new Gson().fromJson(val, User.class);

                    tv_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                    if (user.getProfile_image() != null) {
                        UtilsFunctions.SetLOGOWithRoundedCorners(HomeActivity.this, user.getProfile_image(), imag, 20);
                        tv_char.setVisibility(View.GONE);
                    } else
                        tv_char.setText(UtilsFunctions.getFistLastChar(user.getName()));

                }

            }

            @Override
            public void onFail() {

            }
        });
    }


    private void userLogOut() {

        //dProgressbar.show();
        Map<String, String> params = new HashMap<>();
        PostApiHandler postApiHandler = new PostApiHandler(HomeActivity.this, URLs.LOGOUT, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                //dProgressbar.dismiss();

                if (jsonObject.has("status")) {
                    storeData.clearData(ConstantValues.USER_TOKEN);
                    Intent i = new Intent(HomeActivity.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();

                }
            }

            @Override
            public void onError(ANError error) {
                //dProgressbar.dismiss();
                storeData.clearData(ConstantValues.USER_TOKEN);
                Intent i = new Intent(HomeActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        postApiHandler.execute();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_action_home:
                bottom_tab_navigation.getMenu().setGroupCheckable(0, true, true);
                fragment = new HomeFragment();
                break;

            case R.id.menu_action_appointment:
                bottom_tab_navigation.getMenu().setGroupCheckable(0, true, true);
                fragment = new BookedAppointmentFragment();
                break;

            case R.id.menu_action_search:
                bottom_tab_navigation.getMenu().setGroupCheckable(0, true, true);
                transaction.replace(R.id.fragment_container, new AdvanceSearchFragment());
                transaction.addToBackStack("SearchFragment");
                transaction.commit();
                closeDrawer();
                return false;
//                transaction.replace(R.id.fragment_container, new SearchFragment());
//                transaction.addToBackStack("HomeFragment");
//                transaction.commit();
//                item.setChecked(true);

            case R.id.menu_action_profile:
                bottom_tab_navigation.getMenu().setGroupCheckable(0, true, true);
                fragment = new EditProfileFragment();
                break;

            case R.id.nav_home:
                item.setChecked(true);
                closeDrawer();
                bottom_tab_navigation.setSelectedItemId(R.id.menu_action_home);
                return true;


            case R.id.nav_profile:
                item.setChecked(true);
                closeDrawer();
                bottom_tab_navigation.setSelectedItemId(R.id.menu_action_profile);
//                closeDrawer();
//                transaction.replace(R.id.fragment_container, new EditProfileFragment());
//                transaction.addToBackStack("HomeFragment");
//                transaction.commit();
//                item.setChecked(true);
                return true;

            case R.id.nav_appointments:
                item.setChecked(true);
                closeDrawer();
                //fragment = new BookedAppointmentFragment();
                bottom_tab_navigation.setSelectedItemId(R.id.menu_action_appointment);
                return true;

            case R.id.nav_wallet:
                item.setChecked(true);
                fragment = new WalletHistoryFragment();
                closeDrawer();
                bottom_tab_navigation.getMenu().setGroupCheckable(0, false, true);
//                transaction.replace(R.id.fragment_container, new WalletHistoryFragment());
//                transaction.addToBackStack("HomeFragment");
//                transaction.commit();
//                item.setChecked(true);

                break;
            case R.id.nav_setting:
                item.setChecked(true);
                closeDrawer();
                fragment = new SettingsFragment();
                bottom_tab_navigation.getMenu().setGroupCheckable(0, false, true);
//                transaction.replace(R.id.fragment_container, new SettingsFragment());
//                transaction.addToBackStack("HomeFragment");
//                transaction.commit();

                break;

            case R.id.nav_favourite:
                item.setChecked(true);
                closeDrawer();
                fragment = new FavouriteFragment();
                bottom_tab_navigation.getMenu().setGroupCheckable(0, false, true);
                break;
            case R.id.nav_logout:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you want to logout?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                userLogOut();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.hide();
                    }
                });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                closeDrawer();
                break;
            case R.id.nav_faq:
                closeDrawer();
                item.setChecked(true);
                startActivity(new Intent(this, Faqs.class));
                return false;

            case R.id.nav_contact:
                closeDrawer();
                Intent intent = new Intent(this, WebviewActivity.class);
                intent.putExtra("SLUG", "contact");
                startActivity(intent);
                return false;
            case R.id.nav_helpy:
                item.setChecked(true);
                Intent intentChat = new Intent(this, ChatActivity.class);
                startActivity(intentChat);
                closeDrawer();
                return false;
            case R.id.nav_voice_analysis:
                item.setChecked(true);
                ///todo need to implement Voice analysis functionality
                closeDrawer();
                return false;
            case R.id.nav_term_condition:
                closeDrawer();
                Intent intent2 = new Intent(this, WebviewActivity.class);
                intent2.putExtra("SLUG", "terms-conditions");
                startActivity(intent2);
                return false;

            case R.id.nav_about_us:
                closeDrawer();
                Intent intent3 = new Intent(this, WebviewActivity.class);
                intent3.putExtra("SLUG", "about-us");
                startActivity(intent3);
                return false;
        }
        return loadFragment(fragment);
    }

    public void closeDrawer() {
        mDrawer.closeDrawer(Gravity.LEFT);
    }

    public void openDrawer() {
        mDrawer.openDrawer(Gravity.LEFT);
    }

    private boolean loadFragment(Fragment fragment) {
        String tag;
        if (fragment instanceof HomeFragment) {
            tag = "home";
        } else {
            tag = "";
        }
        try {
            if (fragment != null) {
                fm.beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
        Log.e("requestCode", requestCode + " " + resultCode);
        if (resultCode == ConstantValues.RESULT_CALL) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ConsultationDetailsFragment());
            transaction.addToBackStack("HomeFragment");
            transaction.commit();

        }

    }

    public void getSetting() {

        final String[] oldRazorPayId = {""};
        final String[] oldAgoraAppId = {""};
        storeData.getData(ConstantValues.USER_SETTING_RAZOR_PAY, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                if (!val.isEmpty()) {
                    GlobalData.setting_razor_pay_model = new Gson().fromJson(val, SettingPojo.class);
                    oldRazorPayId[0] = GlobalData.setting_razor_pay_model.getValue();
                }

            }

            @Override
            public void onFail() {


            }
        });


        storeData.getData(ConstantValues.USER_SETTING_AGORA_APP_ID, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                if (!val.isEmpty()) {
                    Log.e("print==>",""+val);
                    GlobalData.setting_agora_id_model = new Gson().fromJson(val, SettingPojo.class);
                    oldAgoraAppId[0] = GlobalData.setting_agora_id_model.getValue();
                }

            }

            @Override
            public void onFail() {


            }
        });
        /*storeData.getData(ConstantValues.USER_SETTING_RAZOR_PAY, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                GlobalData.setting_razor_pay_model = new Gson().fromJson(val, SettingPojo.class);

            }

            @Override
            public void onFail() {

                PostApiHandler apiHandler = new PostApiHandler(HomeActivity.this, URLs.GET_SETTING, null, new PostApiHandler.OnClickListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) throws JSONException {
                        if (jsonObject.has("status") && jsonObject.has("data")) {
                            GlobalData.setting_model = new Gson().fromJson(jsonObject.getString("data"), SettingModel.class);
                            storeData.setData(ConstantValues.USER_SETTING, jsonObject.getString("data"), new StoreData.SetListener() {
                                @Override
                                public void setOK() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                    }
                });

                apiHandler.execute();
            }
        });
*/

        settingVM
                .getSetting("razor_pay_app_id")
                .observe(HomeActivity.this, response -> {
                    if (response.getStatus() && response.getData() != null) {


                        GlobalData.setting_razor_pay_model = response.getData();
                        storeData.setData(ConstantValues.USER_SETTING_RAZOR_PAY, new Gson().toJson(response.getData()), new StoreData.SetListener() {
                            @Override
                            public void setOK() {

                            }
                        });

                    }

                });


        settingVM
                .getSetting("agora_app_id")
                .observe(HomeActivity.this, response -> {
                    if (response.getStatus() && response.getData() != null) {


                        if (!oldAgoraAppId[0].equals(response.getData().getValue())) {
                            GlobalData.setting_agora_id_model = response.getData();

                            storeData.setData(ConstantValues.USER_SETTING_AGORA_APP_ID, new Gson().toJson(response.getData()), new StoreData.SetListener() {
                                @Override
                                public void setOK() {

                                }
                            });
                        }

                    }

                });

    }


    public MainApplication application() {
        return (MainApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }


    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
    }

    @Override
    public void onConnect() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ConnectionMessageLayout.closeLayout((TextView) findViewById(R.id.textview), HomeActivity.this);

            }
        });
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ConnectionMessageLayout.showLayout((TextView) findViewById(R.id.textview), HomeActivity.this);

            }
        });

    }

    public void getProfile() {


        settingVM
                .getProfile()
                .observe(HomeActivity.this, response -> {
                    if (response.getStatus() && response.getData() != null) {


                        storeData.setData(ConstantValues.USER_DATA, new Gson().toJson(response.getData()), new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                setProfile();
                            }
                        });
                        if (response.getData().getDeleted_at() != null) {
                            userLogOut();
                        }

                    } else {
                        if (response.getErrorStatus() == BasePojo.ErrorCode.UNAUTHORIZED) {
                            userLogOut();
                        }

                    }

                });

        /*GetApiHandler apiHandler = new GetApiHandler(this, URLs.GET_PROFILE, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {

                if (jsonObject.has("status") && jsonObject.has("data")) {
                    User user = new Gson().fromJson(jsonObject.getString("data"), User.class);
                    storeData.setData(ConstantValues.USER_DATA, jsonObject.getString("data"), new StoreData.SetListener() {
                        @Override
                        public void setOK() {
                            setProfile();
                        }
                    });
                    if (user.getDeleted_at() != null) {
                        userLogOut();
                    }

                } else {

                }
            }

            @Override
            public void onError() {

            }
        });
        apiHandler.execute();*/
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag("home") != null && getSupportFragmentManager().findFragmentByTag("home").isResumed())
            backPressed();
        else
            super.onBackPressed();

    }

    void backPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(HomeActivity.this, "Tap back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                Timber.e("backdrops ====>");
            }
        }, 4000);
    }


    Socket mSocket;

    void connectToSocket() {
        SocketSingleton socketSingleton = SocketSingleton.getSync(HomeActivity.this);
        mSocket = socketSingleton.getSocket();

        mSocket.on("status_update", onNewMessage);
        mSocket.on("patient-call-cut", onCallCut);

        mSocket.connect();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
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

                    Intent intent = new Intent();
                    intent.setAction("online_event");
                    intent.putExtra("data", new Gson().toJson(args));
                    sendBroadcast(intent);

                    // add the message to view

                }
            });
        }
    };

    private Emitter.Listener onCallCut = new Emitter.Listener() {

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

                    Intent intent = new Intent();
                    intent.setAction("call_cut");
                    intent.putExtra("data", (String) args[0]);
                    sendBroadcast(intent);

                    Log.e("event--->", "" + args[0]);

                    // add the message to view

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("status_update", onNewMessage);
        mSocket.off("patient-call-cut", onCallCut);

    }
}