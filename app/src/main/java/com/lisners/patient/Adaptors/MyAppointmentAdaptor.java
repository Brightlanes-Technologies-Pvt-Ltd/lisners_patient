package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.utils.UtilsFunctions;

import java.util.ArrayList;

public class MyAppointmentAdaptor extends RecyclerView.Adapter<MyAppointmentAdaptor.MyViewHolder> {
    LayoutInflater layoutInflater ;
    Context context ;
    ArrayList<BookedAppointment> list ;
    OnClickListener listener ;
    TherapistListAdapter listAdapter;
    public interface OnClickListener {
        public void  onClick(BookedAppointment appointment ,int pos);
    }


    public MyAppointmentAdaptor(Context context , OnClickListener onClickListener) {
        if(context!=null) {
            this.layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.listener = onClickListener;
        }
        list = new ArrayList<>();
    }

    public void updateList(ArrayList<BookedAppointment> users)
    {
        this.list.addAll(users);
        notifyDataSetChanged();

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_appointment,parent,false);
        MyAppointmentAdaptor.MyViewHolder holder = new MyAppointmentAdaptor.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookedAppointment appointmentModel = list.get(position);
        User counselor =  appointmentModel.getCounselor();
        AppointmentModel appointmentdetails = appointmentModel.getAppointment_detail();
        ArrayList<SpacializationMedel> specializations = appointmentModel.getSpecialization();

        UtilsFunctions.SetLOGO(context,counselor.getProfile_image(),holder.iv_profile);
        holder.tv_date.setText(UtilsFunctions.dateFormat(appointmentModel.getDate()));
        if(appointmentdetails!=null)
        holder.tv_time.setText(UtilsFunctions.timeFormat(appointmentdetails.getStart_time())+" to "+UtilsFunctions.timeFormat(appointmentdetails.getEnd_time()));

        holder.tv_name.setText(UtilsFunctions.splitCamelCase(counselor.getName()));

        holder.tvPrice.setText(appointmentModel.getCall_rate()+"₹ / session");
        holder.tv_spacialize.setText( UtilsFunctions.getSpecializeString(specializations));

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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name, tv_spacialize , tv_date , tv_time ,tvPrice;
        ImageView iv_profile ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_spacialize = itemView.findViewById(R.id.tv_spacialize);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
