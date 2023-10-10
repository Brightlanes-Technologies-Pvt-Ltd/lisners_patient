package com.lisners.patient.ApiModal;

import android.os.Parcel;
import android.os.Parcelable;

public class AppointmentModel implements Parcelable {
    long id , appointment_id;
    String name , title , email , profile_image , dob , gender , mobile_no , newsletter_notify , push_notify , start_time , end_time;


    protected AppointmentModel(Parcel in) {
        id = in.readLong();
        appointment_id = in.readLong();
        name = in.readString();
        title = in.readString();
        email = in.readString();
        profile_image = in.readString();
        dob = in.readString();
        gender = in.readString();
        mobile_no = in.readString();
        newsletter_notify = in.readString();
        push_notify = in.readString();
        start_time = in.readString();
        end_time = in.readString();
    }

    public static final Creator<AppointmentModel> CREATOR = new Creator<AppointmentModel>() {
        @Override
        public AppointmentModel createFromParcel(Parcel in) {
            return new AppointmentModel(in);
        }

        @Override
        public AppointmentModel[] newArray(int size) {
            return new AppointmentModel[size];
        }
    };

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(appointment_id);
        parcel.writeString(name);
        parcel.writeString(title);
        parcel.writeString(email);
        parcel.writeString(profile_image);
        parcel.writeString(dob);
        parcel.writeString(gender);
        parcel.writeString(mobile_no);
        parcel.writeString(newsletter_notify);
        parcel.writeString(push_notify);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
    }
}
