package com.lisners.patient.Activity.Home.HomeStack;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.Activity.Auth.ChangePasswordActivity;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    TextView tvHeader;
    ImageButton btn_header_left;
    LinearLayout lv_change_password;
    DProgressbar dProgressbar;
    SwitchCompat toggleSwitch;
    ProgressBar pb_noti_loader;
    boolean ischeckLister = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        // Inflate the layout for this fragment
        dProgressbar = new DProgressbar(getContext());
        return view;
    }

    private void init(View view) {
        tvHeader = view.findViewById(R.id.tvHeader);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_header_menu);
        lv_change_password = view.findViewById(R.id.lv_change_password);
        toggleSwitch = view.findViewById(R.id.toggleSwitch);
        pb_noti_loader = view.findViewById(R.id.pb_noti_loader);
        btn_header_left.setOnClickListener(this);
        lv_change_password.setOnClickListener(this);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ischeckLister)
                    setProfile(isChecked);
            }
        });
        tvHeader.setText("Settings");
        getProfile();
    }

    public void getProfile() {

        pb_noti_loader.setVisibility(View.VISIBLE);

        GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_PROFILE, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {

                if (jsonObject.has("status") && jsonObject.has("data")) {
                    User user = new Gson().fromJson(jsonObject.getString("data"), User.class);
                    if (user.get_Notify() == 1)
                        toggleSwitch.setChecked(true);
                    else
                        toggleSwitch.setChecked(false);

                }
                ischeckLister = true;
                pb_noti_loader.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                ischeckLister = true;
                pb_noti_loader.setVisibility(View.GONE);
            }
        });
        apiHandler.execute();
    }

    public void setProfile(boolean check) {
        pb_noti_loader.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        if (check)
            params.put("is_notify", "1");
        else
            params.put("is_notify", "0");

        PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET_UPDATE, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("jsonObject", new Gson().toJson(jsonObject));
                pb_noti_loader.setVisibility(View.GONE);
                if (jsonObject.has("message"))
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if (jsonObject.has("errors")) {
                    JSONObject errObj = jsonObject.getJSONObject("errors");
                    for (Iterator<String> it = errObj.keys(); it.hasNext(); ) {
                        String key = it.next();
                        if (key != null)
                            Toast.makeText(getContext(), UtilsFunctions.errorShow(errObj.getJSONArray(key)), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(ANError error) {
                pb_noti_loader.setVisibility(View.GONE);
                if(error.getErrorBody()!=null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(),APIErrorModel.class);
                    if(apiErrorModel.getMessage()!=null)
                        Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        postApiHandler.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;
            case R.id.lv_change_password:
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                intent.putExtra("FROM","SETTING");
                startActivity(intent);
                break;
        }
    }
}