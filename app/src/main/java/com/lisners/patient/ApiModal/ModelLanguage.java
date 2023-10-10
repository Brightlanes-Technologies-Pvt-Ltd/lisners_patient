package com.lisners.patient.ApiModal;

public class ModelLanguage {
    Long id ;
    String   name ,title ;
    boolean check =false ;

    public ModelLanguage(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        if(name!=null)
        return name;
        else return title ;
    }

    public boolean isCheck() {
        return check;
    }
}

