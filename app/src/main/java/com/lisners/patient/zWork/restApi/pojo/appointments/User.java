
package com.lisners.patient.zWork.restApi.pojo.appointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

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
    private String gender;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("address")
    @Expose
    private Object address;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
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
