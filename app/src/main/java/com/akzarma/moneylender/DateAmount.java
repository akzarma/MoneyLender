package com.akzarma.moneylender;

import java.io.Serializable;

public class DateAmount implements Serializable {
    String date;
    private Long amount_principal = 0L;
    private Long amount_interest = 0L;

    public void setBothAmounts(String prin_int) {
        if (prin_int.contains(",")) {
            this.amount_principal = Long.parseLong(prin_int.split(",")[0]);
            this.amount_interest = Long.parseLong(prin_int.split(",")[1]);
        }
    }

    public Long getAmount_interest() {
        return amount_interest;
    }

    public void setAmount_interest(Long amount_interest) {
        this.amount_interest = amount_interest;
    }

    public Long getAmount_principal() {
        return amount_principal;
    }

    public void setAmount_principal(Long amount_principal) {
        this.amount_principal = amount_principal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
