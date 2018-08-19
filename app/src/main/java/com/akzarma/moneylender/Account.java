package com.akzarma.moneylender;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public class Account implements Serializable {
    private String no;
    private Long disb_amt;
    private Long file_amt;
    private Long deposited_principle = 0L;
    private Long deposited_int = 0L;
    private Calendar o_date;
    private Calendar c_date;
    private Calendar last_int_calc;
    private String info;
    private Long r_amt;
    private long interest = 0;

    private boolean active = true;
    private Calendar last_pay_date;
    private double roi = 0.0;
    private String type; //    "0" or  "1"
    private Long duration = 0L;
    private String lf_no = "";

    public HashMap<String, String> getMap() {
        HashMap<String, String> attrs = new HashMap<>();
        attrs.put("disb_amt", String.valueOf(this.disb_amt));
        attrs.put("file_amt", String.valueOf(this.file_amt));
        attrs.put("deposited", String.valueOf(this.deposited_principle) + "," + String.valueOf(this.deposited_int));
        attrs.put("o_date", MainActivity.CaltoStringDate(this.o_date));
        attrs.put("c_date", MainActivity.CaltoStringDate(this.c_date));
        if (this.last_int_calc != null)
            attrs.put("last_int_calc", MainActivity.CaltoStringDate(this.last_int_calc));
        attrs.put("info", this.info);
        if (this.r_amt != null)
            attrs.put("r_amt", String.valueOf(this.r_amt));
        if (this.last_pay_date != null) {
            attrs.put("last_pay_date", MainActivity.CaltoStringDate(this.last_pay_date));
        }
        attrs.put("roi", String.valueOf(this.roi));
        attrs.put("type", this.type);
        attrs.put("duration", String.valueOf(this.duration));
        attrs.put("lf_no", this.lf_no);
        attrs.put("r_int", String.valueOf(this.interest));
        attrs.put("active", String.valueOf(this.active));
        return attrs;
    }

    public Account(Object value) {
        HashMap<String, Object> value1 = (HashMap<String, Object>) value;
        this.disb_amt = Long.parseLong(value1.get("disb_amt").toString());
        if (!value1.get("o_date").equals("")) {
            this.setO_date(value1.get("o_date").toString());
            MainActivity.CaltoStringDate(this.o_date);
        }
        if (value1.get("info") != null)
            this.info = value1.get("info").toString();
        if (!value1.get("c_date").equals("")) {
            this.setC_date(value1.get("c_date").toString());
            MainActivity.CaltoStringDate(this.c_date);
        }
        if (value1.get("last_pay_date") != null) {
            this.setLast_pay_date(value1.get("last_pay_date").toString());
            MainActivity.CaltoStringDate(this.last_pay_date);
        }
        if (value1.get("roi") != null)
            this.roi = Double.parseDouble(value1.get("roi").toString());
        if (value1.get("deposited") != null) {
            String deposited_val = value1.get("deposited").toString();
            if (deposited_val.contains(",")) {
                this.deposited_principle = Long.parseLong(deposited_val.split(",")[0]);
                this.deposited_int = Long.parseLong(deposited_val.split(",")[1]);
            }
        }
        this.type = value1.get("type").toString();
        if (value1.get("duration") != null)
            this.duration = Long.parseLong(value1.get("duration").toString());
        if (value1.get("lf_no") != null)
            this.lf_no = value1.get("lf_no").toString();
        if (value1.get("file_amt") != null)
            this.file_amt = Long.parseLong(value1.get("file_amt").toString());

        if (value1.get("r_amt") != null)
            this.r_amt = Long.parseLong(value1.get("r_amt").toString());
        if (value1.get("last_int_calc") != null) {
            this.setLast_int_calc(value1.get("last_int_calc").toString());
            MainActivity.CaltoStringDate(this.last_int_calc);
        }
        if (value1.get("r_int") != null) {
            this.interest = Long.parseLong(value1.get("r_int").toString());
        }
        if (value1.get("active") != null)
            this.active = Boolean.parseBoolean(value1.get("active").toString());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getInterest() {
        return interest;
    }

    public void setInterest(long interest) {
        this.interest = interest;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getFile_amt() {
        return file_amt;
    }

    public Calendar getLast_int_calc() {
        return last_int_calc;
    }

    public void setLast_int_calc(String last_int_calc) {
        this.last_int_calc = MainActivity.StringDateToCal(last_int_calc);
    }

    public Long getR_amt() {
        return r_amt;
    }

    public void setR_amt(Long r_amt) {
        this.r_amt = r_amt;
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


    public Account() {

    }

    public Long getDeposited_principle() {
        return deposited_principle;
    }

    public void setDeposited_principle(Long deposited_principle) {
        this.deposited_principle = deposited_principle;
    }

    public Long getDeposited_int() {
        return deposited_int;
    }

    public void setDeposited_int(Long deposited_int) {
        this.deposited_int = deposited_int;
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

    public Calendar getLast_pay_date() {
        return last_pay_date;
    }

    public void setLast_pay_date(String last_pay_date) {
        this.last_pay_date = MainActivity.StringDateToCal(last_pay_date);
    }

    public double getRoi() {
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
