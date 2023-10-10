package com.lisners.patient;

import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.ModelLanguage;
import com.lisners.patient.ApiModal.SettingModel;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.zWork.restApi.pojo.ProfessionDatum;
import com.lisners.patient.zWork.restApi.pojo.SettingPojo;

import java.util.ArrayList;
import java.util.Map;

public class GlobalData {
    public static String wallet = "00.0";
    public static SpacializationMedel home_selected_spacialization;
    public static ArrayList<SpacializationMedel> spacializationMedels;
    public static User bookAppointmentTherapist;
    public static BookedAppointment appointment;

    // Advance Search
    public static ArrayList<ModelLanguage> advanceLanguage;
    public static ArrayList<ProfessionDatum> professionDatumArrayList;
    public static String advanceAddress = "";
    public static String advanceGender = "";
    public static String st_Language = "";
    public static Map<String, String> advanceParams;
    //public static SettingModel setting_model;
    public static SettingPojo setting_razor_pay_model;
    public static SettingPojo setting_agora_id_model;


    public static BookedAppointment call_appointment;

}
