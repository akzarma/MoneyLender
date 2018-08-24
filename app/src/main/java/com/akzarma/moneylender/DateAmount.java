package com.akzarma.moneylender;

import java.io.Serializable;

public class DateAmount implements Serializable {
    String date;
    private Long amount_principal = 0L;
    private Long amount_interest = 0L;
    private Long remaining_prin = 0L;
    private Long remaining_int = 0L;

    public void setAllAmounts(String prin_int) {
        if (prin_int.contains(",")) {
            if (prin_int.split(",").length == 4) {
                this.amount_principal = Long.parseLong(prin_int.split(",")[0]);
                this.amount_interest = Long.parseLong(prin_int.split(",")[1]);
                this.remaining_prin = Long.parseLong(prin_int.split(",")[2]);
                this.remaining_int = Long.parseLong(prin_int.split(",")[3]);
            } else {
                this.amount_principal = Long.parseLong(prin_int.split(",")[0]);
                this.amount_interest = Long.parseLong(prin_int.split(",")[1]);
            }
        }

    }

    public Long getRemaining_prin() {
        return remaining_prin;
    }

    public void setRemaining_prin(Long remaining_prin) {
        this.remaining_prin = remaining_prin;
    }

    public Long getRemaining_int() {
        return remaining_int;
    }

    public void setRemaining_int(Long remaining_int) {
        this.remaining_int = remaining_int;
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
