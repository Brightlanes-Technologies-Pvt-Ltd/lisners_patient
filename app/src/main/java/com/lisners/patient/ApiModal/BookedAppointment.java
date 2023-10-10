package com.lisners.patient.ApiModal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BookedAppointment implements Parcelable {
    int id, appointment_detail_id, status, user_id, counselor_id;
    String date;



    String status_label;
    String specialization_id;
    String specialization_name;
    String call_type;
    String current_time;
    String chenal_code;
    String call_rate;
    String add_notes;
    String prescriprion;
    String call_date;
    String extra_charges;
    String call_time;
    String total_amount;
    String share_amount;
    String rating;
    String comment;
    int is_promotional;
    User user;
    User counselor;
    AppointmentModel appointment_detail;
    ArrayList<SpacializationMedel> specialization;

    public BookedAppointment() {
    }


    protected BookedAppointment(Parcel in) {
        id = in.readInt();
        appointment_detail_id = in.readInt();
        status = in.readInt();
        user_id = in.readInt();
        counselor_id = in.readInt();
        date = in.readString();
        status_label = in.readString();
        specialization_id = in.readString();
        specialization_name = in.readString();
        call_type = in.readString();
        current_time = in.readString();
        chenal_code = in.readString();
        call_rate = in.readString();
        add_notes = in.readString();
        prescriprion = in.readString();
        call_date = in.readString();
        extra_charges = in.readString();
        call_time = in.readString();
        total_amount = in.readString();
        share_amount = in.readString();
        rating = in.readString();
        comment = in.readString();
        is_promotional = in.readInt();
        appointment_detail = in.readParcelable(AppointmentModel.class.getClassLoader());
        specialization = in.createTypedArrayList(SpacializationMedel.CREATOR);
    }

    public static final Creator<BookedAppointment> CREATOR = new Creator<BookedAppointment>() {
        @Override
        public BookedAppointment createFromParcel(Parcel in) {
            return new BookedAppointment(in);
        }

        @Override
        public BookedAppointment[] newArray(int size) {
            return new BookedAppointment[size];
        }
    };

    public int getIs_promotional() {
        return is_promotional;
    }

    public void setIs_promotional(int is_promotional) {
        this.is_promotional = is_promotional;
    }


    public String getSpecialization_name() {
        return specialization_name;
    }

    public void setSpecialization_name(String specialization_name) {
        this.specialization_name = specialization_name;
    }

    public String getSpecialization_id() {
        return specialization_id;
    }

    public void setCategory_id(String specialization_id) {
        this.specialization_id = specialization_id;
    }


    public ArrayList<SpacializationMedel> getSpecialization() {
        return specialization;
    }

    public String getRating() {
        if (rating != null)
            return rating;
        return "0";
    }

    public String getComment() {

        return comment;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChenal_code() {
        return chenal_code;
    }

    public int getAppointment_detail_id() {
        return appointment_detail_id;
    }

    public int getStatus() {
        return status;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getCounselor_id() {
        return counselor_id;
    }

    public String getDate() {
        return date;
    }

    public String getCall_type() {
        return call_type;
    }

    public String getCall_rate() {
        return call_rate;
    }

    public String getAdd_notes() {
        return add_notes;
    }

    public String getPrescriprion() {
        return prescriprion;
    }

    public String getCall_date() {
        return call_date;
    }

    public String getCall_time() {
        return call_time;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public User getUser() {
        return user;
    }

    public User getCounselor() {
        return counselor;
    }

    public AppointmentModel getAppointment_detail() {
        return appointment_detail;
    }

    public String getStatus_label() {
        return status_label;
    }

    public void setStatus_label(String status_label) {
        this.status_label = status_label;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(appointment_detail_id);
        parcel.writeInt(status);
        parcel.writeInt(user_id);
        parcel.writeInt(counselor_id);
        parcel.writeString(date);
        parcel.writeString(status_label);
        parcel.writeString(specialization_id);
        parcel.writeString(specialization_name);
        parcel.writeString(call_type);
        parcel.writeString(current_time);
        parcel.writeString(chenal_code);
        parcel.writeString(call_rate);
        parcel.writeString(add_notes);
        parcel.writeString(prescriprion);
        parcel.writeString(call_date);
        parcel.writeString(extra_charges);
        parcel.writeString(call_time);
        parcel.writeString(total_amount);
        parcel.writeString(share_amount);
        parcel.writeString(rating);
        parcel.writeString(comment);
        parcel.writeInt(is_promotional);
        parcel.writeParcelable(appointment_detail, i);
        parcel.writeTypedList(specialization);
    }
}
