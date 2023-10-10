package com.lisners.patient.zWork.utils;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;


import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.databinding.DialogShowAppointmentCallOverBinding;
import com.lisners.patient.databinding.DialogShowFirstFreeUserBinding;
import com.lisners.patient.databinding.DialogShowSuccessBookAppointmentsBinding;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.restApi.pojo.bookAppointments.BookAppointmentPojo;

import java.sql.Date;
import java.util.Calendar;

public class DialogUtil {

    private DialogListener callBack;
    private Context context;
    private OnDateSetListener mDateSetListener;

    public interface ConfirmationDialogListener {
        void onNoClicked();

        void onYesClicked();
    }

    public interface DialogListener {
        void onDateSet(Date date_object, long date);
    }

    public DialogUtil(Context context, DialogListener listener) {
        this.context = context;
        this.callBack = listener;
    }

    public DialogUtil(Context context) {
        this.context = context;
    }

    public void showDatePickerDialog(boolean is_max_today, long dateMin) {
        // Get Current Date
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog = new DatePickerDialog(this.context, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                //calendar.setTimeZone(TimeZone.getTimeZone("IST"));
                callBack.onDateSet(new Date(calendar.getTimeInMillis()), calendar.getTimeInMillis());

            }
        }, mYear, mMonth, mDay);
        dialog.getDatePicker().setMinDate(dateMin);
        if (is_max_today) {
            dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        }
        dialog.show();


    }


    public AlertDialog.Builder createConfirmationDialog(int icon, String title, String message, final ConfirmationDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConfirmationDialogListener confirmationDialogListener = listener;
                if (confirmationDialogListener != null) {
                    dialog.dismiss();
                    confirmationDialogListener.onYesClicked();
                }
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConfirmationDialogListener confirmationDialogListener = listener;
                if (confirmationDialogListener != null) {
                    dialog.dismiss();
                    confirmationDialogListener.onNoClicked();
                }
            }
        });
        return builder;
    }


    public AlertDialog createBookAppointmentSuccessDialog(String title, String message, BookAppointmentPojo appointmentPojo, View.OnClickListener closeClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_show_success_book_appointments, null);
        DialogShowSuccessBookAppointmentsBinding binding = DataBindingUtil.bind(dialogView);
        binding.title.setText(title);
        binding.message.setText(message.replace("\\n", "\n"));


        UtilsFunctions.SetLOGO(context, appointmentPojo.getCounselor().getProfileImage(), binding.ivProfile);
        binding.tvDate.setText(UtilsFunctions.dateFormat(appointmentPojo.getAppointmentDetail().getCreatedAt()));
        String time = UtilsFunctions.convertTime(appointmentPojo.getAppointmentDetail().getStartTime()) + " to " + UtilsFunctions.convertTime(appointmentPojo.getAppointmentDetail().getEndTime());
        binding.tvTime.setText(time);
        binding.tvName.setText(UtilsFunctions.splitCamelCase(appointmentPojo.getCounselor().getName()));


        binding.tvPrice.setText(appointmentPojo.getTotalAmount() + "₹ / session");
        binding.tvSpacialize.setText(""+appointmentPojo.getSpecializationName());


        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        binding.closeBtn.setOnClickListener(closeClickListener);


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return alertDialog;
    }


    public AlertDialog createFirstFreeUserDialog(String title, String message, View.OnClickListener closeClickListener, View.OnClickListener okCloseListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_show_first_free_user, null);
        DialogShowFirstFreeUserBinding binding = DataBindingUtil.bind(dialogView);
        binding.title.setText(title);
        binding.message.setText(message.replace("\\n", "\n"));
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);


        binding.closeBtn.setOnClickListener(closeClickListener);
        binding.btnOk.setOnClickListener(okCloseListener);


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return alertDialog;
    }

    public AlertDialog createAppointmentCallOverDialog(String title, String message, View.OnClickListener closeClickListener, View.OnClickListener okCloseListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_show_appointment_call_over, null);
        DialogShowAppointmentCallOverBinding binding = DataBindingUtil.bind(dialogView);

        binding.title.setText(title);
        binding.message.setText(message.replace("\\n", "\n"));

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);


        binding.closeBtn.setOnClickListener(closeClickListener);
        binding.btnOk.setOnClickListener(okCloseListener);


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return alertDialog;
    }



}
