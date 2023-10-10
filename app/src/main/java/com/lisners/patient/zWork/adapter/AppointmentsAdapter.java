package com.lisners.patient.zWork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.lisners.patient.Adaptors.MyAppointmentAdaptor;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.databinding.ItemAppointmentBinding;
import com.lisners.patient.databinding.ItemProgressBinding;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.adapter.viewholder.FooterViewHolder;
import com.lisners.patient.zWork.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;


public class AppointmentsAdapter extends Adapter<ViewHolder> {


    private List<BookedAppointment> resultList;
    private Context context;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public boolean isFooterShow = false;

    OnClickListener listener;

    public interface OnClickListener {
        public void onClick(BookedAppointment appointment, int pos);
    }

    public AppointmentsAdapter(List<BookedAppointment> resultList, Context context, OnClickListener onClickListener) {
        this.resultList = resultList;
        this.context = context;
        this.listener = onClickListener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {

            ItemProgressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_progress, parent, false);
            return new FooterViewHolder(binding);
        } else if (viewType == TYPE_ITEM) {
            ItemAppointmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_appointment, parent, false);
            return new GenericViewHolder(binding);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            ((FooterViewHolder) holder).bind(isFooterShow);


        } else if (holder instanceof GenericViewHolder) {
            ((GenericViewHolder) holder).bind(resultList.get(position));
        }


    }


    @Override
    public int getItemViewType(int position) {
        return (position == resultList.size()) ? TYPE_FOOTER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        // System.out.println("I am call");
        return resultList.size() + 1;
    }


    /*
      Helpers
      _________________________________________________________________________________________________
       */
    void add(BookedAppointment r) {
        resultList.add(r);
        notifyItemInserted(resultList.size() - 1);
    }

    public void addAll(List<BookedAppointment> moveResults) {
        for (BookedAppointment result : moveResults) {
            add(result);
        }
    }

    public Integer getListSize() {
        return this.resultList.size();
    }


    class GenericViewHolder extends ViewHolder {

        ItemAppointmentBinding binding;

        GenericViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(resultList.get(getAdapterPosition()), getAdapterPosition());

                }
            });

        }

        void bind(BookedAppointment appointmentModel) {


            if (appointmentModel.getCall_type().equalsIgnoreCase("video")) {
                binding.ivPhoneIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_svg_video));
            } else {
                binding.ivPhoneIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_svg_phonecall));
            }


            User counselor = appointmentModel.getCounselor();
            AppointmentModel appointmentdetails = appointmentModel.getAppointment_detail();
            ArrayList<SpacializationMedel> specializations = appointmentModel.getSpecialization();

            UtilsFunctions.SetLOGO(context, counselor.getProfile_image(), binding.ivProfile);

            if (appointmentModel.getDate() != null)
                binding.tvDate.setText(DateUtil.dateFormatter(appointmentModel.getDate(), "dd-MM-yyyy", "dd MMM yyyy"));
            else {
                binding.tvDate.setText("");
            }
            if (appointmentdetails != null) {
                binding.tvTime.setText(String.format("%s - %s", DateUtil.dateFormatter(appointmentdetails.getStart_time(), "HH:mm:ss", "hh:mm a"), DateUtil.dateFormatter(appointmentdetails.getEnd_time(), "HH:mm:ss", "hh:mm a")));
            } else {
                binding.tvTime.setText("");
            }


            binding.tvName.setText(UtilsFunctions.splitCamelCase(counselor.getName()));

            if (appointmentModel.getIs_promotional() == 0) {
                binding.tvPrice.setText(appointmentModel.getTotal_amount() + "₹ / session");
            } else {
                binding.tvPrice.setText("Free appointment");
            }

            if (counselor.getCounselor_profile().getProfession() != null) {
                binding.tvProfession.setText(counselor.getCounselor_profile().getProfession().getTitle());
            }

            binding.tvSpacialize.setText("" + appointmentModel.getSpecialization_name());


            binding.status.setText(appointmentModel.getStatus_label());
            if (appointmentModel.getStatus() == 3) {
                binding.statusCard.setVisibility(View.VISIBLE);
                binding.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.cancel_color));
            } else if (appointmentModel.getStatus() == 2) {
                binding.statusCard.setVisibility(View.VISIBLE);
                binding.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.complete_color));
            } else if (appointmentModel.getStatus() == 1) {
                binding.statusCard.setVisibility(View.VISIBLE);
                binding.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.pending_color));
            } else {
                binding.statusCard.setVisibility(View.GONE);
                binding.statusCard.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            }

        }

    }


}
