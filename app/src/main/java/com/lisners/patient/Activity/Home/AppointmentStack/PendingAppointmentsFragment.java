package com.lisners.patient.Activity.Home.AppointmentStack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.R;
import com.lisners.patient.databinding.FragmentPendingAppointmentsBinding;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.zWork.adapter.AppointmentsAdapter;
import com.lisners.patient.zWork.base.BaseFragment;
import com.lisners.patient.zWork.call.AgoraVideoCallActivity;
import com.lisners.patient.zWork.commens.EndlessRecyclerViewScrollListener;
import com.lisners.patient.zWork.restApi.viewmodel.ContentsViewModel;
import com.lisners.patient.zWork.utils.TimeUtils;
import com.lisners.patient.zWork.utils.Utils;
import com.lisners.patient.zWork.utils.ViewModelUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class PendingAppointmentsFragment extends BaseFragment<FragmentPendingAppointmentsBinding> {
    ContentsViewModel contentsVM;
    AppointmentsAdapter appointmentsAdapter;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_pending_appointments;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentsVM = ViewModelUtils.getViewModel(ContentsViewModel.class, this);


        initView();


    }

    private void initView() {


        //top
        Utils.setNestedScrollingEnabledFalse(getFragmentBinding().rvPending);
        getFragmentBinding().rvPending.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));


        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) getFragmentBinding().rvPending.getLayoutManager()) {

            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {

                Timber.e("Page No %d", page);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNextData(page);
                    }
                }, 0);

            }

            @Override
            public int getStartPage() {
                return 1;
            }
        };

        getFragmentBinding().rvPending.addOnScrollListener(scrollListener);

        getData();

    }


    private void getData() {
        hideError();
        showProgress();
        contentsVM
                .getPendingAppointments(1)
                .observe(getViewLifecycleOwner(), response -> {
                    hideProgress();

                    if (response.getStatus() && response.getData() != null
                            && response.getData().getData() != null && response.getData().getData().size() > 0) {
                        showMainLayout();


                        appointmentsAdapter = new AppointmentsAdapter(response.getData().getData(), getActivity(), onClickListener);
                        getFragmentBinding().rvPending.setAdapter(appointmentsAdapter);

                        appointmentsAdapter.isFooterShow = true;
                        appointmentsAdapter.notifyItemChanged(appointmentsAdapter.getListSize());
                    } else {
                        showError(response.getMessage());
                    }

                });
    }

    private void getNextData(int page) {
        contentsVM
                .getPendingAppointments(page)
                .observe(getViewLifecycleOwner(), response -> {

                    if (response.getStatus() && response.getData() != null
                            && response.getData().getData() != null && response.getData().getData().size() > 0) {
                        appointmentsAdapter.addAll(response.getData().getData());
                    } else {
                        appointmentsAdapter.isFooterShow = false;
                        appointmentsAdapter.notifyItemChanged(appointmentsAdapter.getListSize());
                    }

                });

    }

    AppointmentsAdapter.OnClickListener onClickListener = new AppointmentsAdapter.OnClickListener() {
        @Override
        public void onClick(BookedAppointment appointment, int pos) {

            // pending 1
            // complete 2
            // reject 3

            if (appointment.getStatus() == 2) {

            } else if (appointment.getStatus() == 1) {
                long statTime = Calendar.getInstance().getTimeInMillis();
                if (TimeUtils.getTimeDifferenceInMiliSecondsForCall(statTime, getENDTime_Plus_30M(appointment.getDate() + " " + appointment.getAppointment_detail().getStart_time())) > 10000) {
                    callDailog(appointment);
                }
                else {
                    Toast.makeText(getActivity(), "Call not place. your appointment time is over", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getActivity(), "Call not place. your appointment time is over", Toast.LENGTH_SHORT).show();
            }

        }
    };


    public void callDailog(BookedAppointment appointment) {
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
}

