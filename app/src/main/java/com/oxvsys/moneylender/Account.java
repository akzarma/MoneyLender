package com.oxvsys.moneylender;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public class Account implements Serializable {
    private String no;
    private Long disb_amt;
    private Long file_amt;
    private Long deposited = 0L;
    private Calendar o_date;
    private Calendar c_date;
    private int roi;
    private String type; //    "0" or  "1"
    private Long duration;
    private String lf_no;

    public Long getFile_amt() {
        return file_amt;
    }

    public void setFile_amt(Long file_amt) {
        this.file_amt = file_amt;
    }

    public void setO_date(Calendar o_date) {
        this.o_date = o_date;
    }

    public void setC_date(Calendar c_date) {
        this.c_date = c_date;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getLf_no() {
        return lf_no;
    }

    public void setLf_no(String lf_no) {
        this.lf_no = lf_no;
    }

    public Account(Object value) {
        HashMap<String, Object> value1 = (HashMap<String, Object>) value;
        this.disb_amt = Long.parseLong(value1.get("disb_amt").toString());
        if (!value1.get("o_date").equals("")) {
            this.setO_date(value1.get("o_date").toString());
            MainActivity.CaltoStringDate(this.o_date);
        }
        if (!value1.get("c_date").equals("")) {
            this.setC_date(value1.get("c_date").toString());
            MainActivity.CaltoStringDate(this.c_date);
        }

        if (value1.get("roi") != null)
            this.roi = Integer.parseInt(value1.get("roi").toString());
        if (value1.get("deposited") != null)
            this.deposited = Long.parseLong(value1.get("deposited").toString());

        this.type = value1.get("type").toString();
        if(value1.get("duration")!=null)
            this.duration = Long.parseLong(value1.get("duration").toString());
        if(value1.get("lf_no")!=null)
            this.lf_no = value1.get("lf_no").toString();
        if(value1.get("file_amt")!=null)
            this.file_amt = Long.parseLong(value1.get("file_amt").toString());





    }

    public Account() {

    }

    public Long getDeposited() {
        return deposited;
    }

    public void setDeposited(Long deposited) {
        this.deposited = deposited;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public long getDisb_amt() {
        return disb_amt;
    }

    public void setDisb_amt(Long disb_amt) {
        this.disb_amt = disb_amt;
    }

    public Calendar getO_date() {
        return o_date;
    }

    public void setO_date(String o_date) {
        this.o_date = MainActivity.StringDateToCal(o_date);
    }

    public Calendar getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = MainActivity.StringDateToCal(c_date);
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
