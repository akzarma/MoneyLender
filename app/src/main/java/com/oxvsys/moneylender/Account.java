package com.oxvsys.moneylender;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public class Account implements Serializable {
        private String no;
        private long amt;
        private Calendar o_date;
        private Calendar c_date;
        private int roi;
        private String type; //    "0" or  "1"

    public Account(Object value) {
        HashMap<String, Object> value1 = (HashMap<String, Object>) value;
        this.amt = Long.parseLong(value1.get("amt").toString());
        if (!value1.get("o_date").equals(""))
            this.setO_date(value1.get("o_date").toString());
        if (!value1.get("c_date").equals(""))
            this.setO_date(value1.get("c_date").toString());

        if (value1.get("roi") !=null)
            this.roi = Integer.parseInt(value1.get("roi").toString());

        this.type = value1.get("type").toString();


    }

    public Account() {

    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public long getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public Calendar getO_date() {
        return o_date;
    }

    public void setO_date(String  o_date) {
        String[] date = o_date.split("-");
        Calendar selected_cal = Calendar.getInstance();
        selected_cal.set(Integer.parseInt(date[2]),
                Integer.parseInt(date[1])-1,
                Integer.parseInt(date[0]));
        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
        selected_cal.set(Calendar.MINUTE, 0);
        selected_cal.set(Calendar.SECOND, 0);
        selected_cal.set(Calendar.MILLISECOND, 0);
        this.o_date = selected_cal;
    }

    public Calendar getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        String[] date = c_date.split("-");
        Calendar selected_cal = Calendar.getInstance();
        selected_cal.set(Integer.parseInt(date[2]),
                Integer.parseInt(date[1])-1,
                Integer.parseInt(date[0]));
        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
        selected_cal.set(Calendar.MINUTE, 0);
        selected_cal.set(Calendar.SECOND, 0);
        selected_cal.set(Calendar.MILLISECOND, 0);
        this.c_date = selected_cal;
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
