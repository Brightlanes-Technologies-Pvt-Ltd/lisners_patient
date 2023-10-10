package com.lisners.patient.Activity.Home.HomeStack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.utils.RazorpayActivity;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;


public class AddMoneyWalletFragment extends Fragment implements View.OnClickListener {
    TextView tvHeader;
    ImageButton btn_header_left;
    Button btn_add_money ;
    EditText edit_price ;
    TextView tvWalletMoney2 ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_money_wallet, container, false);
        // Inflate the layout for this fragment
        init(view);
        return view;
    }

    private void init(View view) {
        tvHeader = view.findViewById(R.id.tvHeader);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_add_money = view.findViewById(R.id.btn_add_money);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        edit_price = view.findViewById(R.id.edit_price);
        tvWalletMoney2 = view.findViewById(R.id.tvWalletMoney2);
        tvWalletMoney2.setText(GlobalData.wallet);
        btn_add_money.setOnClickListener(this);
        btn_header_left.setOnClickListener(this);
        tvHeader.setText("Add Money");


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_header_left:
                getFragmentManager().popBackStack();
                break;

            case R.id.btn_add_money:
                String  price = edit_price.getText().toString();
                if(!price.isEmpty()) {
                    ((HomeActivity)getContext()).getSetting();
                    Intent intent = new Intent(getContext(), RazorpayActivity.class);
                    intent.putExtra("PRICE",price);
                    startActivityForResult(intent,11);
                }else
                    Toast.makeText(getContext(), "Enter amount", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11&& resultCode == Activity.RESULT_OK){
            getFragmentManager().popBackStack();
        }

    }
}