
package com.lisners.patient.zWork.restApi.pojo.bookAppointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Counselor {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("role_id")
    @Expose
    private String roleId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("dob")
    @Expose
    private Object dob;
    @SerializedName("gender")
    @Expose
    private Object gender;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("counselor_profile")
    @Expose
    private CounselorProfile counselorProfile;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("newsletter_notify")
    @Expose
    private Object newsletterNotify;
    @SerializedName("push_notify")
    @Expose
    private Object pushNotify;
    @SerializedName("is_profile_complete")
    @Expose
    private int isProfileComplete;
    @SerializedName("is_online")
    @Expose
    private int isOnline;
    @SerializedName("is_promotional")
    @Expose
    private int isPromotional;
    @SerializedName("is_notify")
    @Expose
    private int isNotify;

    @SerializedName("avg_rating")
    @Expose
    private String avgRating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Object getDob() {
        return dob;
    }

    public void setDob(Object dob) {
        this.dob = dob;
    }

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CounselorProfile getCounselorProfile() {
        return counselorProfile;
    }

    public void setCounselorProfile(CounselorProfile counselorProfile) {
        this.counselorProfile = counselorProfile;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Object getNewsletterNotify() {
        return newsletterNotify;
    }

    public void setNewsletterNotify(Object newsletterNotify) {
        this.newsletterNotify = newsletterNotify;
    }

    public Object getPushNotify() {
        return pushNotify;
    }

    public void setPushNotify(Object pushNotify) {
        this.pushNotify = pushNotify;
    }

    public int getIsProfileComplete() {
        return isProfileComplete;
    }

    public void setIsProfileComplete(int isProfileComplete) {
        this.isProfileComplete = isProfileComplete;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getIsPromotional() {
        return isPromotional;
    }

    public void setIsPromotional(int isPromotional) {
        this.isPromotional = isPromotional;
    }

    public int getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(int isNotify) {
        this.isNotify = isNotify;
    }



    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

}
