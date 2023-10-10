package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.R;

import java.util.ArrayList;

public class Spacialization_New_Adaptor extends RecyclerView.Adapter<Spacialization_New_Adaptor.MyViewHolder> {
    Context context ;
    private LayoutInflater inflater;
    ArrayList<SpacializationMedel> list ;

    public Spacialization_New_Adaptor(Context context, ArrayList<SpacializationMedel> list) {
        this.context = context;
         if (context != null) {
            inflater = LayoutInflater.from(context);
             this.list = new ArrayList<>();
            this.list.addAll(list)   ;
        }
    }

    public  Spacialization_New_Adaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_spacilization_like_gender, parent, false);
         Spacialization_New_Adaptor.MyViewHolder holder = new  Spacialization_New_Adaptor.MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final  Spacialization_New_Adaptor.MyViewHolder holder, final int position) {
        SpacializationMedel medel = list.get(position);
        if(medel.isCheck()) {
            holder.lv_cover.setBackgroundResource(R.drawable.button_primary);
            holder.tv_word.setTextColor(context.getResources().getColor(R.color.white));
            /*holder.vi_check.setBackgroundResource(R.drawable.border_circle_white);*/
        }
        else {
            holder.lv_cover.setBackgroundResource(R.drawable.border_round);
            holder.tv_word.setTextColor(context.getResources().getColor(R.color.primary));
            /*holder.vi_check.setBackgroundResource(R.drawable.border_round);*/
        }

        holder.tv_word.setText(medel.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medel.setCheck(!medel.isCheck());
                list.set(position,medel);
                notifyDataSetChanged();
             }
        });
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_word  ;
        LinearLayout lv_cover ;
        /*View vi_check ;*/
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lv_cover = itemView.findViewById(R.id.lv_cover);
            tv_word = itemView.findViewById(R.id.tv_word);
            /*vi_check = itemView.findViewById(R.id.vi_check);*/
        }
    }
}
