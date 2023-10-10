package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.lisners.patient.R;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.restApi.pojo.timeSlot.TimeSlot;


import java.util.Date;
import java.util.List;

public class AvailabilityAdaptor extends RecyclerView.Adapter<AvailabilityAdaptor.MyViewHolder> {
    Context context ;
    private LayoutInflater inflater;



    Date selectedDate;

    List<TimeSlot> list ;
    OnClickListener  listener ;
    int idx =-1 ;

    public interface OnClickListener{
        public void click(TimeSlot timeSlot , int poss);
    }


    public AvailabilityAdaptor(Context context, Date selectedDate, List<TimeSlot> list , OnClickListener  listener  ) {
        this.listener = listener ;
        this.context = context;
         if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list =list ;
            this.selectedDate = selectedDate;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public  AvailabilityAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_availability, parent, false);
        AvailabilityAdaptor.MyViewHolder holder = new  AvailabilityAdaptor.MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final  AvailabilityAdaptor.MyViewHolder holder, final int position) {
        TimeSlot timeSlot = list.get(position);
           if(timeSlot.getIsBooked()==1)
               holder.lv_item.setBackgroundResource(R.drawable.button_gray);
           else if (idx == position)
                holder.lv_item.setBackgroundResource(R.drawable.button_light_primary);
            else
                holder.lv_item.setBackgroundResource(R.drawable.border_round_thin);



        String bw = UtilsFunctions.convertTime(timeSlot.getStartTime())+" - "+UtilsFunctions.convertTime(timeSlot.getEndTime());
        holder.textView.setText(bw);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeSlot.getIsBooked()!=1) {
                    idx = position;
                    listener.click(timeSlot, position);
                    notifyDataSetChanged();
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }


    public List<TimeSlot> getList() {
        return list;
    }


    public Date getSelectedDate() {
        return selectedDate;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
    TextView textView ;
    LinearLayout lv_item ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_time);
            lv_item = itemView.findViewById(R.id.lv_item);
        }
    }
}
