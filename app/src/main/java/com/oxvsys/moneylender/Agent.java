package com.oxvsys.moneylender;

public class Agent {
    String id;
    String name;
    private String aadhar_id;
    private String mobile;
    private String email;

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

    public String getAadhar_id() {
        return aadhar_id;
    }

    public void setAadhar_id(String aadhar_id) {
        this.aadhar_id = aadhar_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
