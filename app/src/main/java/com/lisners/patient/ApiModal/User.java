package com.lisners.patient.ApiModal;

import java.util.ArrayList;

public class User {

    int id ,role_id , is_notify;
    int is_profile_complete;


    String deleted_at;
    String  name , email ,title, mobile_no , email_verified_at , city , address, clinic_name , profile_image  ,gender,dob , push_notify ,newsletter_notify ,is_saved ,is_online ,avg_rating ;
    CounselorProfile counselor_profile ;
    ArrayList<SpacializationMedel> specialization ;
    ArrayList<LanguagesModel> languages ;

    public int getIs_profile_complete() {
        return is_profile_complete;
    }

    public void setIs_profile_complete(int is_profile_complete) {
        this.is_profile_complete = is_profile_complete;
    }

    public int getId() {
        return id;
    }

    public int getRole_id() {
        return role_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getEmail_verified_at() {
        return email_verified_at;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public String getProfile_image() {
        return profile_image;
    }
    public String getTitle() {
        return title;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getPush_notify() {
        return push_notify;
    }

    public String getNewsletter_notify() {
        return newsletter_notify;
    }

    public int getIs_saved() {
        if(is_saved!=null)
            return  Integer.parseInt(is_saved);
        else
            return 0;
    }

    public void set_Save(String save){
        this.is_saved = save ;
    }

    public int get_Notify() {
        return is_notify;
    }

    public void set_Notify(int save){
        this.is_notify = save ;
    }

    public int getIs_online() {
        if(is_online!=null)
        return  Integer.parseInt(is_online);
        else
        return 0;
    }

    public String getAvg_rating() {
        return avg_rating;
    }

    public CounselorProfile getCounselor_profile() {
        return counselor_profile;
    }

    public ArrayList<SpacializationMedel> getSpecialization() {
        return specialization;
    }

    public ArrayList<LanguagesModel> getLanguages() {
        return languages;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
