package com.akzarma.moneylender;

public class CustomerAgent {
    String cust_id;
    String agent_id;


    CustomerAgent(String a, String b){
        this.cust_id = a;
        this.agent_id = b;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }
}
