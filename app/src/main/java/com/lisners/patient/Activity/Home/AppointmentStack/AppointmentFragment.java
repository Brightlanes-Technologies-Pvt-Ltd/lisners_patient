package com.lisners.patient.Activity.Home.AppointmentStack;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Adaptors.MyAppointmentAdaptor;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.ListAppointment;
import com.lisners.patient.R;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

public class AppointmentFragment extends Fragment implements View.OnClickListener {
    TextView tvHeader , tv_no_result;
    ImageButton btn_header_left;

    RecyclerView rv_appointment ;
    MyAppointmentAdaptor appointmentAdaptor ;
    DProgressbar dProgressbar ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        init(view);
        fetchAppointment();
        return view;
    }

    private void init(View view) {
        tvHeader = view.findViewById(R.id.tvHeader);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_header_menu);
        tv_no_result = view.findViewById(R.id.tv_no_result);
//        cv_item = view.findViewById(R.id.cv_item);
        rv_appointment = view.findViewById(R.id.rv_appointment);
        btn_header_left.setOnClickListener(this);
        tvHeader.setText("My Appointments");
        dProgressbar = new DProgressbar(getContext());
//        cv_item.setOnClickListener(this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_appointment.setLayoutManager(linearLayoutManager );
        appointmentAdaptor =new  MyAppointmentAdaptor(getContext(), new MyAppointmentAdaptor.OnClickListener() {
            @Override
            public void onClick(BookedAppointment appointment, int pos) {

                callDailog(appointment);
            }
        });
        rv_appointment.setAdapter(appointmentAdaptor);
    }

    public void callDailog(BookedAppointment appointment){
        CallDailDialog callDailDialog = new CallDailDialog(getContext(), new CallDailDialog.OnClickListener() {
            @Override
            public void onAccept() {
                ((HomeActivity)getContext()).getSetting();
                /*Intent intent = new Intent(getContext(), VideoCallScreen.class);
                startActivityForResult(intent, ConstantValues.REQUEST_CALL);*/

            }
        });
        callDailDialog.checkAndShow(appointment);
    }

    public void fetchAppointment(){
        dProgressbar.show();
        GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_PENDING_USER_APPOINTMENT , new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                if(jsonObject.has("status") && jsonObject.has("data")){
                    ListAppointment userlistModel = new Gson().fromJson(jsonObject.getString("data"),ListAppointment.class);
                    appointmentAdaptor.updateList(userlistModel.getData());
                    Log.e("USERS",new Gson().toJson(userlistModel));
                    if(userlistModel.getTotal()<=0)
                    {
                        tv_no_result.setVisibility(View.VISIBLE);
                        rv_appointment.setVisibility(View.GONE);
                    }
                    else
                    {
                        tv_no_result.setVisibility(View.GONE);
                        rv_appointment.setVisibility(View.VISIBLE);
                    }
                }else if(appointmentAdaptor.getItemCount()<=0){
                    tv_no_result.setVisibility(View.VISIBLE);
                    rv_appointment.setVisibility(View.GONE);
                }
                dProgressbar.dismiss();
            }

            @Override
            public void onError() {
                dProgressbar.dismiss();
                if(appointmentAdaptor.getItemCount()<=0)
                {
                    tv_no_result.setVisibility(View.VISIBLE);
                    rv_appointment.setVisibility(View.GONE);
                }
            }
        });
        apiHandler.execute();
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;

//            case R.id.cv_item:

//                break;
        }
    }
}