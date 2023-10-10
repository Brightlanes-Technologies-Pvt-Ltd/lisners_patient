package com.lisners.patient.ApiModal;

import android.os.Parcel;
import android.os.Parcelable;

public class SpacializationMedel implements Parcelable {
    int id , active;
    String title = "P" , image , description , link ;
    PivotModel pivot ;
    boolean check =false ;

    public SpacializationMedel(int id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    protected SpacializationMedel(Parcel in) {
        id = in.readInt();
        active = in.readInt();
        title = in.readString();
        image = in.readString();
        description = in.readString();
        link = in.readString();
        check = in.readByte() != 0;
    }

    public static final Creator<SpacializationMedel> CREATOR = new Creator<SpacializationMedel>() {
        @Override
        public SpacializationMedel createFromParcel(Parcel in) {
            return new SpacializationMedel(in);
        }

        @Override
        public SpacializationMedel[] newArray(int size) {
            return new SpacializationMedel[size];
        }
    };

    public boolean isCheck()
    {
        return check ;
    }

    public void setCheck(boolean check)
    {
        this.check = check ;
    }

    public int getId() {
        return id;
    }

    public int getActive() {
        return active;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(active);
        parcel.writeString(title);
        parcel.writeString(image);
        parcel.writeString(description);
        parcel.writeString(link);
        parcel.writeByte((byte) (check ? 1 : 0));
    }
}
