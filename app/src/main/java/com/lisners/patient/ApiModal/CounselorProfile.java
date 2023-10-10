package com.lisners.patient.ApiModal;

public class CounselorProfile {
    int id;
    int user_id;

    int profession_id;
    Profession profession;

    int default_duration;
    String clinic_name , voice_call , video_call, description ;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public String getVoice_call() {
        return voice_call;
    }

    public String getVideo_call() {
        return video_call;
    }

    public String getDescription() {
        return description;
    }

    public int getDefault_duration() {
        return default_duration;
    }


    public int getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(int profession_id) {
        this.profession_id = profession_id;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }
}
