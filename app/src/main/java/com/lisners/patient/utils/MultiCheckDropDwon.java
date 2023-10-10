package com.lisners.patient.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.Adaptors.MultiCheckAdaptar;
import com.lisners.patient.ApiModal.ModelLanguage;
import com.lisners.patient.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiCheckDropDwon {

    MultiCheckAdaptar adaptor;
    String[] strings;
    ArrayList<ModelLanguage> industresList;

    OnItemClickListener onItemClickListener;
    Dialog dg_industries;
     RecyclerView indview ;
      Set<Integer> selected_index;
    public interface OnItemClickListener {
        void onClick(ArrayList<ModelLanguage> selected_spaci);
    }

    public MultiCheckDropDwon(Context context, ArrayList<ModelLanguage> industries2, OnItemClickListener listener) {
        onItemClickListener = listener;
        industresList = new ArrayList<>();
        dg_industries = new Dialog(context);
        selected_index = new HashSet<>();
        industresList.addAll(industries2);

        dg_industries.setContentView(R.layout.dailogbox_dwondwon_recycle);
        indview = (RecyclerView) dg_industries.findViewById(R.id.dialog_list);
        final TextView title = (TextView) dg_industries.findViewById(R.id.tv_dialogTitle);
        title.setVisibility(View.VISIBLE);
        title.setText("Select  Specialization");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        indview.setLayoutManager(linearLayoutManager);

        adaptor = new MultiCheckAdaptar(context, industresList, new MultiCheckAdaptar.OnItemClickListener() {
            @Override
            public void onClick(ModelLanguage jobs, int pos, boolean type) {
                jobs.setCheck(type);
                industresList.get(pos).setCheck(type);
                selected_index.add(pos);
            }
        });

        dg_industries.setCancelable(false);
        dg_industries.show();

        indview.setAdapter(adaptor);
        ImageButton close_btn_ind = (ImageButton) dg_industries.findViewById(R.id.btn_dialog_close);
        Button done = (Button) dg_industries.findViewById(R.id.btn_save);
        done.setVisibility(View.VISIBLE);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 listener.onClick(industresList);
                 dg_industries.dismiss();
                selected_index.clear();

            }
        });
        close_btn_ind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int pos : selected_index) industresList.get(pos).setCheck(false);
                adaptor.updateList(industresList);
                selected_index.clear();
                dg_industries.dismiss();
            }
        });
        dg_industries.setCancelable(false);
        dg_industries.show();
    }

    public void show() {
        adaptor.updateList(industresList);
        dg_industries.show();
    }

    public void show(Set<ModelLanguage> selected_spaci) {
        adaptor.updateList(industresList);
        dg_industries.show();
    }


    public void updateList(ArrayList<ModelLanguage> list){
        industresList.clear();
        industresList.addAll(list);
        adaptor.updateList(industresList);
    }


}
