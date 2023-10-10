package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.CalendarModel;
import com.lisners.patient.R;
import com.lisners.patient.utils.UtilsFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarAdaptor extends RecyclerView.Adapter<CalendarAdaptor.MyViewHolder> {
    Context context;
    private LayoutInflater inflater;
    List<Date> list ;
    public int index = -1;

    OnClickListener listener ;

    public interface OnClickListener {
        public void  onClick(Date date  , int pos);
    }

    public CalendarAdaptor(Context context , OnClickListener listener ) {
        this.context = context;
        this.listener = listener ;

        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = new ArrayList<>();
        }
    }

   public void updateList(List<Date> list){
        this.list.clear();
       this.list.addAll(list)  ;
       notifyDataSetChanged();
   }



    public CalendarAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.calendar_layout, parent, false);
        CalendarAdaptor.MyViewHolder holder = new CalendarAdaptor.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CalendarAdaptor.MyViewHolder holder, final int position) {
        Date date = list.get(position);
        if(position==index) {
            holder.tv_date.setBackgroundResource(R.drawable.border_circle_primary_background);
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.white));

        }else
        {
            holder.tv_date.setBackgroundResource(R.drawable.border_circle_transparent);
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.primary));
        }
        holder.tv_month.setText(UtilsFunctions.getMonths(date.getMonth()));
        holder.tv_date.setText(date.getDate()+"");
        holder.tv_day.setText(UtilsFunctions.getDay(date.getDay()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position ;
                notifyDataSetChanged();
                if(listener!=null)
                 listener.onClick(date,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_month , tv_date , tv_day;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_day = itemView.findViewById(R.id.tv_day);
        }
    }
}
