package com.lisners.patient.Activity.Home.AppointmentStack;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Adaptors.AppointmentViewPageAdapter;
import com.lisners.patient.R;
import com.lisners.patient.databinding.FragmentBookedAppointmentsBinding;
import com.lisners.patient.zWork.base.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BookedAppointmentFragment extends BaseFragment<FragmentBookedAppointmentsBinding> {


    @Override
    public int getLayoutResID() {
        return R.layout.fragment_booked_appointments;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMainLayout();
        initView();

    }


    private void initView() {
        TextView tvHeader = getFragmentBinding().getRoot().findViewById(R.id.tvHeader);
        ImageButton btn_header_left = getFragmentBinding().getRoot().findViewById(R.id.btn_header_left);

        btn_header_left.setImageResource(R.drawable.ic_svg_header_menu);
        tvHeader.setText("My Appointments");

        btn_header_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).openDrawer();

            }
        });



        //set Tabs
        setTabsWithViewPager();

    }


    private void setTabsWithViewPager() {


        LinkedHashMap<String, Fragment> fragmentsMap = getFragmentsMap();
        AppointmentViewPageAdapter adapter = new AppointmentViewPageAdapter(getChildFragmentManager(),new ArrayList<>());
        for (Map.Entry<String, Fragment> entry : fragmentsMap.entrySet()) {
            adapter.addItem((Fragment) entry.getValue(), (String) entry.getKey());
        }
        getFragmentBinding().viewPaper.setOffscreenPageLimit(fragmentsMap.size());
        getFragmentBinding().viewPaper.setAdapter(adapter);
        getFragmentBinding().tabLayoutProfile.setupWithViewPager(getFragmentBinding().viewPaper);





    }


    private LinkedHashMap<String, Fragment> getFragmentsMap() {

        LinkedHashMap<String, Fragment> map = new LinkedHashMap<>();
        map.put("Pending", PendingAppointmentsFragment.newInstance(PendingAppointmentsFragment.class, null));
        map.put("Completed", CompleteAppointmentsFragment.newInstance(CompleteAppointmentsFragment.class, null));

        return map;
    }

}

