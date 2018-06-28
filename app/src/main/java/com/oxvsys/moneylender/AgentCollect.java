package com.oxvsys.moneylender;

import java.util.Calendar;
import java.util.List;

public class AgentCollect {
    private Agent agent;
    private String type; //"daily" or "monthly"
    private Calendar date;
    private List<AccountAmountCollect> accountAmountCollectList;

    public AgentCollect(Agent agent, String type, Calendar date, List<AccountAmountCollect> accountAmountCollectList) {
        this.agent = agent;
        this.type = type;
        this.date = date;
        this.accountAmountCollectList = accountAmountCollectList;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public List<AccountAmountCollect> getAccountAmountCollectList() {
        return accountAmountCollectList;
    }

    public void setAccountAmountCollectList(List<AccountAmountCollect> accountAmountCollectList) {
        this.accountAmountCollectList = accountAmountCollectList;
    }



}
