package com.lisners.patient.Activity.Home.HomeStack;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Adaptors.TransactionAdaptor;
import com.lisners.patient.ApiModal.Transaction;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

public class WalletHistoryFragment extends Fragment implements View.OnClickListener {

    LinearLayout ll_walletHeader, ll_withdrawMoney;
    TextView tvHeader, tvWalletMoney;
    ImageButton btn_header_left;
    LinearLayout lv_add_wallet, lv_withdraw_history;
    TransactionAdaptor tadaptor;
    LinearLayoutManager linearLayoutManager;
    ProgressBar pb_loader;
    Transaction transaction;
    int page = 1;


    RecyclerView rv_history;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_history, container, false);
        init(view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {

        ll_withdrawMoney = view.findViewById(R.id.ll_withdrawMoney);
        ll_walletHeader = view.findViewById(R.id.ll_walletHeader);
        ll_walletHeader.setVisibility(View.VISIBLE);
        ll_withdrawMoney.setVisibility(View.GONE);
        tvHeader = view.findViewById(R.id.tvHeader);
        tvWalletMoney = view.findViewById(R.id.tvWalletMoney);

        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_header_menu);

        lv_withdraw_history = view.findViewById(R.id.lv_withdraw_history);
        lv_withdraw_history.setOnClickListener(this);
        lv_add_wallet = view.findViewById(R.id.lv_add_wallet);
        pb_loader = view.findViewById(R.id.pb_loader);
        lv_add_wallet.setOnClickListener(this);
        btn_header_left.setOnClickListener(this);
        tvHeader.setText("Wallet");

        rv_history = view.findViewById(R.id.rv_history);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_history.setLayoutManager(linearLayoutManager);
        tadaptor = new TransactionAdaptor(getContext());
        rv_history.addOnScrollListener(recyclerViewOnScrollListener);
        rv_history.setAdapter(tadaptor);
        transaction = new Transaction();
        getTransaction();
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0 && transaction.getLast_page() >= page && pb_loader.getVisibility() != View.VISIBLE
            ) {

                page++;
                getTransaction();
            }

        }
    };


    private void getTransaction() {
        pb_loader.setVisibility(View.VISIBLE);
        GetApiHandler getApiHandler = new GetApiHandler(getContext(), URLs.GET_TRANSACTION + page, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                pb_loader.setVisibility(View.GONE);
                try {


                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        JSONObject datajson = jsonObject.getJSONObject("data");
                        if (datajson.has("wallet"))
                            GlobalData.wallet = datajson.getString("wallet");
                        tvWalletMoney.setText("₹ " + GlobalData.wallet);
                        if (datajson.has("transaction")) {
                            transaction = new Gson().fromJson(datajson.getString("transaction"), Transaction.class);
                            tadaptor.updateList(transaction.getData());

                        }
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                pb_loader.setVisibility(View.GONE);
            }
        });
        getApiHandler.execute();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.btn_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;
            case R.id.lv_add_wallet:
                page = 1;
                transaction.replace(R.id.fragment_container, new AddMoneyWalletFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;
            case R.id.lv_withdraw_history:
                page = 1;
                transaction.replace(R.id.fragment_container, new WithdrawMoneyFragment());
                transaction.addToBackStack("HomeFragment");
                transaction.commit();
                break;
        }
    }











}