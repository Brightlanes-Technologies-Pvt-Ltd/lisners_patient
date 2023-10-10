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

public class SpacializationAdaptor extends RecyclerView.Adapter<SpacializationAdaptor.MyViewHolder> {
    Context context ;
    private LayoutInflater inflater;
    ArrayList<SpacializationMedel> list ;

    public SpacializationAdaptor(Context context,  ArrayList<SpacializationMedel> list) {
        this.context = context;
         if (context != null) {
            inflater = LayoutInflater.from(context);
             this.list = new ArrayList<>();
            this.list.addAll(list)   ;
        }
    }

    public  SpacializationAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_spacilization, parent, false);
        SpacializationAdaptor.MyViewHolder holder = new  SpacializationAdaptor.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final  SpacializationAdaptor.MyViewHolder holder, final int position) {
        SpacializationMedel medel = list.get(position);
        if(medel.isCheck()) {
            holder.lv_top.setBackgroundResource(R.drawable.button_primary);
            holder.tv_first_word.setBackgroundResource(R.drawable.button_light_primary);
        }
        else {
            holder.lv_top.setBackgroundResource(R.drawable.button_light_primary);
            holder.tv_first_word.setBackgroundResource(R.drawable.button_primary);
        }

        holder.tv_title.setText(medel.getTitle());
        holder.tv_first_word.setText(medel.getTitle().charAt(0)+"");
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
    TextView tv_first_word , tv_title ;
    LinearLayout lv_top ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_first_word = itemView.findViewById(R.id.tv_first_word);
            lv_top = itemView.findViewById(R.id.lv_top);
        }
    }

}
