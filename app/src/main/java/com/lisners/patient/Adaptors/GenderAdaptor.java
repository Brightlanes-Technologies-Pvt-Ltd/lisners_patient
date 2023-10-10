package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.GlobalData;
import com.lisners.patient.R;



public class GenderAdaptor extends RecyclerView.Adapter<GenderAdaptor.MyViewHolder> {
    Context context ;
    private LayoutInflater inflater;
    String[] strings ;


    public GenderAdaptor(Context context, String[] list ) {
        this.context = context;
         if (context != null) {
            inflater = LayoutInflater.from(context);
            strings =list ;


        }
    }


     public  GenderAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_gender, parent, false);
         GenderAdaptor.MyViewHolder holder = new  GenderAdaptor.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final  GenderAdaptor.MyViewHolder holder, final int position) {
      if(GlobalData.advanceGender.equals(strings[position]) ) {
          holder.lv_cover.setBackgroundResource(R.drawable.button_primary);
          holder.tv_word.setTextColor(context.getResources().getColor(R.color.white));
          /*holder.vi_check.setBackgroundResource(R.drawable.border_circle_white);*/
      }else
      {
          holder.lv_cover.setBackgroundResource(R.drawable.border_round);
          holder.tv_word.setTextColor(context.getResources().getColor(R.color.primary));
          /*holder.vi_check.setBackgroundResource(R.drawable.border_round);*/
      }
      holder.tv_word.setText(strings[position]);

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              GlobalData.advanceGender = strings[position] ;
              notifyDataSetChanged();

          }
      });
    }

    @Override
    public int getItemCount() {
        return  strings.length;
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
