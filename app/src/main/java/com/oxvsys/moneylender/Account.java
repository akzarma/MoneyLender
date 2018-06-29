package com.oxvsys.moneylender;

import java.util.Calendar;

public class Account {
        private String no;
        private int amt;
        private Calendar o_date;
        private Calendar c_date;
        private int roi;
        private String type; //    "0" or  "1"

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public Calendar getO_date() {
        return o_date;
    }

    public void setO_date(Calendar o_date) {
        this.o_date = o_date;
    }

    public Calendar getC_date() {
        return c_date;
    }

    public void setC_date(Calendar c_date) {
        this.c_date = c_date;
    }

    public int getRoi() {
        return roi;
    }

    public void setRoi(int roi) {
        this.roi = roi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
