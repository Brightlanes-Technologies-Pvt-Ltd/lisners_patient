package com.lisners.patient.ApiModal;

public class TransactionCard {
Long id ,user_id, amount ,credit ,debit;
        int status ,is_withdrawl ;
    String user_name;
    String user_from;
    String is_referral;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    String created_at ;

    public Long getId() {
        return id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getCredit() {
        return credit;
    }

    public Long getDebit() {
        return debit;
    }

    public int getStatus() {
        return status;
    }

    public int getIs_withdrawl() {
        return is_withdrawl;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_from() {
        return user_from;
    }

    public String getIs_referral() {
        return is_referral;
    }
}
