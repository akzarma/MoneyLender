package com.oxvsys.moneylender;

public class AgentAmount {
    private Agent agent;
    private Long amount_collected;


    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Long getAmount_collected() {
        return amount_collected;
    }

    public void setAmount_collected(Long amount_collected) {
        this.amount_collected = amount_collected;
    }
}
