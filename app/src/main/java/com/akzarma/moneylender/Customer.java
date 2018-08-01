package com.akzarma.moneylender;

import java.io.Serializable;
import java.util.List;

public class Customer implements Serializable, Cloneable {
    private String id;
    private String name;
    private String occupation;
    private String aadhar;
    private String mobile;
    private String DOB;
    private String address;
    private String g_name = "";
    private String g_mobile = "";
    private String g_address = "";

    private List<Account> accounts1;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Could not clone customer.");
        }
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getG_mobile() {
        return g_mobile;
    }

    public void setG_mobile(String g_mobile) {
        this.g_mobile = g_mobile;
    }

    public String getG_address() {
        return g_address;
    }

    public void setG_address(String g_address) {
        this.g_address = g_address;
    }

    public List<Account> getAccounts1() {
        return accounts1;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccounts1(List<Account> accounts1) {
        this.accounts1 = accounts1;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }
}
