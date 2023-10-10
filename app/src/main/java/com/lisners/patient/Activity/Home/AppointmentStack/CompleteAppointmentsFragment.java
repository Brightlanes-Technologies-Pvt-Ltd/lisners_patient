package com.lisners.patient.Activity.Home.AppointmentStack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Activity.Home.HomeStack.ConsultationDetailsFragment;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.databinding.FragmentCompleteAppointmentsBinding;
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

public class CompleteAppointmentsFragment extends BaseFragment<FragmentCompleteAppointmentsBinding> {
    ContentsViewModel contentsVM;
    AppointmentsAdapter appointmentsAdapter;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_complete_appointments;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentsVM = ViewModelUtils.getViewModel(ContentsViewModel.class, this);


        initView();


    }

    private void initView() {


        //top
        Utils.setNestedScrollingEnabledFalse(getFragmentBinding().rvCompleted);
        getFragmentBinding().rvCompleted.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));


        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) getFragmentBinding().rvCompleted.getLayoutManager()) {

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

        getFragmentBinding().rvCompleted.addOnScrollListener(scrollListener);

        getData();

    }


    private void getData() {
        hideError();
        showProgress();
        contentsVM
                .getCompletedAppointments(1)
                .observe(getViewLifecycleOwner(), response -> {
                    hideProgress();

                    if (response.getStatus() && response.getData() != null
                            && response.getData().getData() != null && response.getData().getData().size() > 0) {
                        showMainLayout();


                        appointmentsAdapter = new AppointmentsAdapter(response.getData().getData(), getActivity(), onClickListener);
                        getFragmentBinding().rvCompleted.setAdapter(appointmentsAdapter);

                        appointmentsAdapter.isFooterShow = true;
                        appointmentsAdapter.notifyItemChanged(appointmentsAdapter.getListSize());
                    } else {
                        showError(response.getMessage());
                    }

                });
    }

    private void getNextData(int page) {
        contentsVM
                .getCompletedAppointments(page)
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
            AppointmentModel appointmentModel = appointment.getAppointment_detail();

            if (appointment.getStatus() == 2) { // when status 2 and appointmet time not over user can call again in that time period and api not call again

                String appointmentDateEndTime = appointment.getDate();
                long statTime = Calendar.getInstance().getTimeInMillis();
                if (TimeUtils.getTimeDifferenceInMiliSecondsForCall(statTime, getENDTime_Plus_30M(appointment.getDate() + " " + appointment.getAppointment_detail().getStart_time()))>10000) {
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


            }

        }
    };


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

