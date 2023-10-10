package com.lisners.patient.Activity.Home.HomeStack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.AppointmentStack.AppointmentProfileFragment;
import com.lisners.patient.Activity.Home.AppointmentStack.CallDailDialog;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.HomeStack.HomeAdapters.HomeProblemTileListAdapter;
import com.lisners.patient.Activity.Home.NotificationFragment;
import com.lisners.patient.Adaptors.LastAppoitmentAdaptor;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.ListAppointment;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.call.AgoraVideoCallActivity;
import com.lisners.patient.zWork.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class HomeFragment extends Fragment implements View.OnClickListener {
    TextView tv_error_no_last_appointment;

    RecyclerView rvHomeProblem, rv_last_appointment;
    ImageButton btn_home_header_left;
    EditText edit_search;
    ImageButton btn_filter, btn_home_header_right;
    LinearLayout lv_search, lv_wallet;
    HomeProblemTileListAdapter homeProblemTileListAdapter;
    LastAppoitmentAdaptor lastAppoitmentAdaptor;
    TextView tvWalletTextHeader, tvHeaderUserName;
    ListAppointment lastAppointment;
    DProgressbar dProgressbar;
    SwipeRefreshLayout swipe_container;
    StoreData storeData;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }



    private void init(View view) {
        tv_error_no_last_appointment = view.findViewById(R.id.tv_error_no_last_appointment);

        btn_home_header_left = view.findViewById(R.id.btn_home_header_left);
        btn_home_header_left.setOnClickListener(this);
        edit_search = view.findViewById(R.id.etHeaderSearch);
        btn_filter = view.findViewById(R.id.btn_filter);
        rvHomeProblem = view.findViewById(R.id.rvHomeProblems);
        rv_last_appointment = view.findViewById(R.id.rv_last_appointment);
        tvWalletTextHeader = view.findViewById(R.id.tvWalletTextHeader);
        tvHeaderUserName = view.findViewById(R.id.tvHeaderUserName);
        btn_home_header_right = view.findViewById(R.id.btn_home_header_right);
        swipe_container = view.findViewById(R.id.swipe_container);
        lv_wallet = view.findViewById(R.id.lv_wallet);
        lv_search = view.findViewById(R.id.lv_search);

        lv_search.setVisibility(View.GONE);
        btn_home_header_right.setOnClickListener(this);
        lv_wallet.setOnClickListener(this);
        tvWalletTextHeader.setText("₹ " + GlobalData.wallet);
        storeData = new StoreData(getContext());
        // Specialization
        dProgressbar = new DProgressbar(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        rvHomeProblem.setLayoutManager(gridLayoutManager);

        homeProblemTileListAdapter = new HomeProblemTileListAdapter(getContext(), new HomeProblemTileListAdapter.OnClickListener() {
            @Override
            public void onClick(SpacializationMedel medel, int pos) {
                GlobalData.home_selected_spacialization = medel;
                HomeProblemFragment homeProblemFragment = new HomeProblemFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, homeProblemFragment);
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
            }
        });
        rvHomeProblem.setAdapter(homeProblemTileListAdapter);

        // LAST Appointment
        LinearLayoutManager inearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_last_appointment.setLayoutManager(inearLayoutManager);
        lastAppoitmentAdaptor = new LastAppoitmentAdaptor(getContext(), new LastAppoitmentAdaptor.OnClickListener() {
            @Override
            public void onClick(BookedAppointment appointment, int pos) {
                AppointmentModel appointmentModel = appointment.getAppointment_detail();
                long statTime = Calendar.getInstance().getTimeInMillis();
                if (appointment.getStatus() == 2) { // when status 2 and appointmet time not over user can call again in that time period and api not call again

                    String appointmentDateEndTime = appointment.getDate();

                    if (TimeUtils.getTimeDifferenceInMiliSecondsForCall(statTime, getENDTime_Plus_30M(appointment.getDate() + " " + appointment.getAppointment_detail().getStart_time())) > 10000) {
                        CallDailDialog callDailDialog = new CallDailDialog(getContext(), new CallDailDialog.OnClickListener() {
                            @Override
                            public void onAccept() {
                                ((HomeActivity) getContext()).getSetting();
                                //Intent intent = new Intent(getContext(), VideoCallScreen.class);
                                Intent intent = AgoraVideoCallActivity.makeIntent(getContext(), appointment);
                                startActivityForResult(intent, ConstantValues.REQUEST_CALL);
                            }
                        });
                        callDailDialog.checkAndShow(appointment);
                    } else {
                        GlobalData.call_appointment = appointment;
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new ConsultationDetailsFragment());
                        transaction.addToBackStack("HomeFragment");
                        transaction.commit();
                    }

                } else if (appointment.getStatus() == 1 && TimeUtils.getTimeDifferenceInMiliSecondsForCall(statTime, getENDTime_Plus_30M(appointment.getDate() + " " + appointment.getAppointment_detail().getStart_time())) > 10000) {


                    CallDailDialog callDailDialog = new CallDailDialog(getContext(), new CallDailDialog.OnClickListener() {
                        @Override
                        public void onAccept() {
                            ((HomeActivity) getContext()).getSetting();
                            //Intent intent = new Intent(getContext(), VideoCallScreen.class);
                            Intent intent = AgoraVideoCallActivity.makeIntent(getContext(), appointment);
                            startActivityForResult(intent, ConstantValues.REQUEST_CALL);
                        }
                    });
                    callDailDialog.checkAndShow(appointment);

                } else {
                    Toast.makeText(getActivity(), "Call not place. your appointment time is over", Toast.LENGTH_SHORT).show();
                }

            }
        });
        rv_last_appointment.setAdapter(lastAppoitmentAdaptor);

        // SEARCH
        edit_search.setFocusableInTouchMode(false);

        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GlobalData.spacializationMedels = null;
                homeProblemTileListAdapter = null;
                swipe_container.setRefreshing(true);
                //getSpecialization();
                getWallet();

            }
        });

        //getSpecialization();
        getWallet();
        setProfile();
    }


    private void setProfile() {
        storeData.getData(ConstantValues.USER_DATA, new StoreData.GetListener() {
            @Override
            public void getOK(String val) {
                if (val != null) {
                    User user = new Gson().fromJson(val, User.class);
                    tvHeaderUserName.setText("Hey " + UtilsFunctions.showFirstName(UtilsFunctions.splitCamelCase(user.getName())));
                }

            }

            @Override
            public void onFail() {

            }
        });
    }


    private void getSpecialization() {

        if (GlobalData.spacializationMedels != null && homeProblemTileListAdapter != null) {
            homeProblemTileListAdapter.updateList(GlobalData.spacializationMedels);
            swipe_container.setRefreshing(false);
        } else {
            dProgressbar.show();
            GlobalData.spacializationMedels = new ArrayList<>();
            GetApiHandler getapi = new GetApiHandler(getContext(), URLs.GET_SPECIALIZATION, new GetApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    swipe_container.setRefreshing(false);
                    if (jsonObject.has("status") && jsonObject.has("data")) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            SpacializationMedel spacializationMedel = new Gson().fromJson(dataArray.getString(i), SpacializationMedel.class);
                            GlobalData.spacializationMedels.add(spacializationMedel);
                        }
                        homeProblemTileListAdapter.updateList(GlobalData.spacializationMedels);
                    }
                    dProgressbar.dismiss();

                }

                @Override
                public void onError() {
                    dProgressbar.dismiss();
                    swipe_container.setRefreshing(false);
                }
            });
            getapi.execute();
        }
        //getList();
    }

    private void getWallet() {
        GetApiHandler getapi = new GetApiHandler(getContext(), URLs.GET_WALLET, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                if (jsonObject.has("status") && jsonObject.has("data")) {
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        GlobalData.wallet = jsonObject1.getString("wallet");
                        tvWalletTextHeader.setText("₹ " + GlobalData.wallet);
                    }
                }

            }

            @Override
            public void onError() {

            }
        });
        getapi.execute();

    }

    private void getList() {
        tv_error_no_last_appointment.setVisibility(View.GONE);

        GetApiHandler getapi = new GetApiHandler(getContext(), URLs.GET_COMPETE_USER_APPOINTMENT, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                //dProgressbar.dismiss();
                try {
                    if (jsonObject.has("status") && jsonObject.has("data")) {
                        lastAppointment = new Gson().fromJson(jsonObject.getString("data"), ListAppointment.class);
                        lastAppoitmentAdaptor.updateFirstList(lastAppointment.getData());
                    } else {
                        tv_error_no_last_appointment.setVisibility(View.VISIBLE);
                        tv_error_no_last_appointment.setText(jsonObject.getString("message"));
                    }

                } catch (Exception e) {
                    tv_error_no_last_appointment.setVisibility(View.VISIBLE);
                    tv_error_no_last_appointment.setText("");
                    e.printStackTrace();
                }


            }

            @Override
            public void onError() {
                tv_error_no_last_appointment.setVisibility(View.VISIBLE);
                tv_error_no_last_appointment.setText("No appointments");
                //dProgressbar.dismiss();
            }
        });
        getapi.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.btn_home_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;
            case R.id.btn_home_header_right:
                transaction.replace(R.id.fragment_container, new NotificationFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;
            case R.id.cv_item:
                transaction.replace(R.id.fragment_container, new AppointmentProfileFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;
            case R.id.btn_filter:
                transaction.replace(R.id.fragment_container, new AdvanceSearchFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;
            case R.id.lv_wallet:
                transaction.replace(R.id.fragment_container, new WalletHistoryFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;

        }
    }

    public static long getENDTime_Plus_30M(String dateSt) {
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateSt);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

}