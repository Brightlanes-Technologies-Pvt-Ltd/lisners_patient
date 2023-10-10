package com.lisners.patient.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.utils.DateUtil;

import java.util.ArrayList;

public class LastAppoitmentAdaptor extends RecyclerView.Adapter<LastAppoitmentAdaptor.MyViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<BookedAppointment> list;
    OnClickListener listener;

    public interface OnClickListener {
        public void onClick(BookedAppointment appointment, int pos);
    }

    public LastAppoitmentAdaptor(Context context, OnClickListener clickListener) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = new ArrayList<>();
            this.listener = clickListener;
        }
    }

    public void updateList(ArrayList<BookedAppointment> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void updateFirstList(ArrayList<BookedAppointment> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public LastAppoitmentAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_appointment, parent, false);
        LastAppoitmentAdaptor.MyViewHolder holder = new LastAppoitmentAdaptor.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LastAppoitmentAdaptor.MyViewHolder holder, final int position) {
        BookedAppointment appointmentModel = list.get(position);
        User counselor = appointmentModel.getCounselor();
        AppointmentModel appointmentDetail = appointmentModel.getAppointment_detail();

        ArrayList<SpacializationMedel> specializations = appointmentModel.getSpecialization();

        if (appointmentModel.getDate() != null)
            holder.tv_date.setText(DateUtil.dateFormatter(appointmentModel.getDate(), "dd-MM-yyyy", "dd MMM yyyy"));
        else {
            holder.tv_date.setText("");
        }

        if (appointmentModel.getAppointment_detail() != null) {
            holder.tv_time.setText(String.format("%s - %s", DateUtil.dateFormatter(appointmentDetail.getStart_time(), "HH:mm:ss", "hh:mm a"), DateUtil.dateFormatter(appointmentDetail.getEnd_time(), "HH:mm:ss", "hh:mm a")));
        } else {
            holder.tv_time.setText("");
        }

        holder.tv_name.setText(UtilsFunctions.splitCamelCase(counselor.getName()));

        if (appointmentModel.getIs_promotional() == 0) {
            holder.tvPrice.setText("₹ " + appointmentModel.getTotal_amount());
        } else {
            holder.tvPrice.setText("Free appointment");
        }

        if (counselor.getProfile_image() != null) {
            UtilsFunctions.SetLOGO(context, counselor.getProfile_image(), holder.iv_profile);
            holder.tv_short_name.setVisibility(View.GONE);
        } else {
            holder.tv_short_name.setText(UtilsFunctions.getFistLastChar(counselor.getName()));
            holder.tv_short_name.setVisibility(View.VISIBLE);
        }

        //holder.tv_spacialize.setText(UtilsFunctions.getSpecializeString(specializations));
        holder.tv_spacialize.setText(appointmentModel.getSpecialization_name());

        if (counselor.getCounselor_profile().getProfession() != null) {
            holder.tv_profession.setText(counselor.getCounselor_profile().getProfession().getTitle());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(appointmentModel, position);
            }
        });

        if (appointmentModel.getCall_type().equalsIgnoreCase("video")) {
            holder.iv_phone_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_svg_video));
        } else {
            holder.iv_phone_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_svg_phonecall));
        }


        holder.status.setText(appointmentModel.getStatus_label());
        if (appointmentModel.getStatus() == 3) {
            holder.status_card.setVisibility(View.VISIBLE);
            holder.status_card.setCardBackgroundColor(context.getResources().getColor(R.color.cancel_color));
        } else if (appointmentModel.getStatus() == 2) {
            holder.status_card.setVisibility(View.VISIBLE);
            holder.status_card.setCardBackgroundColor(context.getResources().getColor(R.color.complete_color));
        } else if (appointmentModel.getStatus() == 1) {
            holder.status_card.setVisibility(View.VISIBLE);
            holder.status_card.setCardBackgroundColor(context.getResources().getColor(R.color.pending_color));
        } else {
            holder.status_card.setVisibility(View.GONE);
            holder.status_card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_profession, tv_spacialize, tv_date, tv_time, tvPrice, tv_short_name;
        ImageView iv_profile, iv_phone_icon;
        TextView status;
        CardView status_card;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_profession = itemView.findViewById(R.id.tv_profession);
            tv_spacialize = itemView.findViewById(R.id.tv_spacialize);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tv_short_name = itemView.findViewById(R.id.tv_short_name);
            iv_phone_icon = itemView.findViewById(R.id.iv_phone_icon);
            status_card = itemView.findViewById(R.id.status_card);
            status = itemView.findViewById(R.id.status);
        }
    }
}
