package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.R;
import com.lisners.patient.utils.UtilsFunctions;

import java.util.ArrayList;

public class SearchAppoitAdaptor extends RecyclerView.Adapter<SearchAppoitAdaptor.MyViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<AppointmentModel> list;
    OnClickListener listener ;

    public SearchAppoitAdaptor(Context context, ArrayList<AppointmentModel> list) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = list;
        }
    }

    public SearchAppoitAdaptor(Context context, ArrayList<AppointmentModel> list,OnClickListener onClickListener ) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = list;
            this.listener =onClickListener;
        }
    }

    public interface OnClickListener {
        public void  onClick(AppointmentModel appointment , int pos);
    }


    public SearchAppoitAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_appointment, parent, false);
        SearchAppoitAdaptor.MyViewHolder holder = new SearchAppoitAdaptor.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAppoitAdaptor.MyViewHolder holder, final int position) {
        AppointmentModel appointmentModel = list.get(position);
        holder.tv_name.setText(UtilsFunctions.splitCamelCase(appointmentModel.getName()));
        holder.tv_spacialize.setText(appointmentModel.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                listener.onClick(appointmentModel,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_spacialize;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_spacialize = itemView.findViewById(R.id.tv_spacialize);
        }
    }
}
