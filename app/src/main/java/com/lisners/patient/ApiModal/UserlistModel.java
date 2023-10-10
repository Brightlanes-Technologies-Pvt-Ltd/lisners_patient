package com.lisners.patient.ApiModal;

import java.util.ArrayList;

public class UserlistModel {
    int total , count , per_page , current_page , last_page=1 ;
    ArrayList<User> data ;

    public int getTotal() {
        return total;
    }

    public int getCount() {
        return count;
    }

    public int getPer_page() {
        return per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public ArrayList<User> getData() {
        return data;
    }

    public void setData(ArrayList<User> data){
        this.data = data ;
    }
}
