package com.oxvsys.moneylender;

import java.io.Serializable;

public class DateAmount implements Serializable {
    String date;
    private Long amount;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
