package com.lisners.patient.Activity.Home.HomeStack;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lisners.patient.Activity.Auth.LoginActivity;
import com.lisners.patient.Activity.Auth.SignUpFormActivity;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.restApi.viewmodel.LoginViewModel;
import com.lisners.patient.zWork.restApi.viewmodel.WithdrawViewModel;
import com.lisners.patient.zWork.utils.ViewModelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class WithdrawMoneyFragment extends Fragment implements View.OnClickListener {
    TextView tvHeader, tvWalletMoney2;
    ImageButton btn_header_left;
    EditText edit_amount, edit_holder_name, edit_account_number, edit_ifsc_code, edit_back_name;
    Button btn_submit;
    DProgressbar dProgressbar;
    WithdrawViewModel withdrawVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_money, container, false);
        init(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void init(View view) {

        withdrawVM = ViewModelUtils.getViewModel(WithdrawViewModel.class, this);

        tvHeader = view.findViewById(R.id.tvHeader);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        dProgressbar = new DProgressbar(getContext());
        edit_amount = view.findViewById(R.id.edit_amount);
        edit_holder_name = view.findViewById(R.id.edit_holder_name);
        edit_account_number = view.findViewById(R.id.edit_account_number);
        edit_ifsc_code = view.findViewById(R.id.edit_ifsc_code);
        edit_back_name = view.findViewById(R.id.edit_back_name);
        tvWalletMoney2 = view.findViewById(R.id.tvWalletMoney2);
        btn_submit = view.findViewById(R.id.btn_submit);
        tvWalletMoney2.setText(GlobalData.wallet);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        btn_header_left.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        tvHeader.setText("Withdraw Money");


    }

    private void addWithdrowDetails() {
        String st_amount = edit_amount.getText().toString();
        String st_holder_name = edit_holder_name.getText().toString();
        String st_account_number = edit_account_number.getText().toString();
        String st_ifsc = edit_ifsc_code.getText().toString();
        String st_bank_name = edit_back_name.getText().toString();
        if (st_amount.isEmpty()) {
            edit_amount.setError("Enter Amount");
            edit_amount.setCursorVisible(true);
        } else if (!Pattern.matches("([0-9]*[.])?[0-9]+", st_amount.toString())) {
            edit_amount.setError("please enter amount");
            edit_amount.setCursorVisible(true);
        } else if (Double.parseDouble(st_amount) <= 0) {
            edit_amount.setError("Amount should be greater than Zero");
            edit_amount.setCursorVisible(true);
        } else if (st_holder_name.isEmpty()) {
            edit_holder_name.setError("Enter Holder Name");
            edit_holder_name.setCursorVisible(true);
        } else if (st_account_number.isEmpty()) {
            edit_account_number.setError("Enter Account number");
            edit_account_number.setCursorVisible(true);
        } else if (st_account_number.length() < 6) {
            edit_account_number.setError("Enter valid account number");
            edit_account_number.setCursorVisible(true);
        } else if (st_ifsc.isEmpty()) {
            edit_ifsc_code.setError("Enter ifsc code");
            edit_ifsc_code.setCursorVisible(true);
        } else if (st_bank_name.isEmpty()) {
            edit_back_name.setError("Enter Bank Name");
            edit_back_name.setCursorVisible(true);
        } else {
            dProgressbar.show();
            Map<String, String> params = new HashMap<>();
            params.put("amount", st_amount);
            params.put("account_no", st_account_number);
            params.put("holder_name", st_holder_name);
            params.put("ifsc_code", st_ifsc);
            params.put("bank_name", st_bank_name);


            withdrawVM
                    .submitWithdrawRequest(params)
                    .observe(this, response -> {
                        dProgressbar.dismiss();

                        if (response.getStatus() && response.getData() != null && response.getData().getWallet() != null) {

                            GlobalData.wallet = response.getData().getWallet();

                            tvWalletMoney2.setText(GlobalData.wallet);
                            Toast.makeText(getActivity(),
                                    response.getMessage(), Toast.LENGTH_SHORT).show();


                            getActivity().getSupportFragmentManager().popBackStack();

                        } else {
                            if (response.getType() != null && !response.getType().isEmpty() && response.getType().equalsIgnoreCase("validation")) {
                                JsonObject jsonObjet = response.getErrors();


                                if (jsonObjet.has("amount"))
                                    edit_amount.setError(jsonObjet.get("amount").getAsString());
                                else if (jsonObjet.has("account_no")) {
                                    edit_account_number.setError(jsonObjet.get("account_no").getAsString());
                                } else if (jsonObjet.has("holder_name")) {
                                    edit_holder_name.setError(jsonObjet.get("holder_name").getAsString());
                                } else if (jsonObjet.has("ifsc_code")) {
                                    edit_ifsc_code.setError(jsonObjet.get("ifsc_code").getAsString());
                                } else if (jsonObjet.has("bank_name")) {
                                    edit_back_name.setError(jsonObjet.get("bank_name").getAsString());
                                }


                            } else {
                                Toast.makeText(getActivity(),
                                        response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    });


           /* PostApiHandler apiHandler = new PostApiHandler(getContext(), URLs.WITHDRAW_REQUEST, params, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("jsonObject", new Gson().toJson(jsonObject));
                    if (jsonObject.has("status") && jsonObject.getBoolean("status") && jsonObject.has("data")) {
                        JSONObject dataJson = jsonObject.getJSONObject("data");
                        if (dataJson.has("wallet"))
                            GlobalData.wallet = dataJson.getString("wallet");

                        tvWalletMoney2.setText(GlobalData.wallet);
                        Toast.makeText(getActivity(),
                                jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        if (jsonObject.has("type") && jsonObject.getString("type").equalsIgnoreCase("validation")) {
                            if (jsonObject.has("errors")) {
                                JSONObject errObj = jsonObject.getJSONObject("errors");
                                if (errObj.has("amount"))
                                    edit_amount.setError(UtilsFunctions.errorShow(errObj.getJSONArray("amount")));
                                else if (errObj.has("account_no"))
                                    edit_account_number.setError(UtilsFunctions.errorShow(errObj.getJSONArray("account_no")));
                                else if (errObj.has("holder_name"))
                                    edit_holder_name.setError(UtilsFunctions.errorShow(errObj.getJSONArray("holder_name")));
                                else if (errObj.has("ifsc_code"))
                                    edit_ifsc_code.setError(UtilsFunctions.errorShow(errObj.getJSONArray("ifsc_code")));
                                else if (errObj.has("bank_name"))
                                    edit_back_name.setError(UtilsFunctions.errorShow(errObj.getJSONArray("bank_name")));


                            }

                        } else {
                            Toast.makeText(getActivity(),
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dProgressbar.dismiss();
                }

                @Override
                public void onError(ANError error) {
                    if (error.getErrorBody() != null) {
                        APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    dProgressbar.dismiss();
                }
            });
            apiHandler.execute();*/
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_header_left:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_submit:
                addWithdrowDetails();
                break;
        }
    }
}