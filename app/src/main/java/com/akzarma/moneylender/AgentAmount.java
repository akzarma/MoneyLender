package com.akzarma.moneylender;

public class AgentAmount {
    private Agent agent;
    private Long prin_amount_collected;
    private Long int_amount_collected;

    public void setBothAmountsCollected(String prin_int){
        if (prin_int.contains(",")) {
            this.prin_amount_collected = Long.parseLong(prin_int.split(",")[0]);
            this.int_amount_collected = Long.parseLong(prin_int.split(",")[1]);
        }
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Long getPrin_amount_collected() {
        return prin_amount_collected;
    }

    public void setPrin_amount_collected(Long prin_amount_collected) {
        this.prin_amount_collected = prin_amount_collected;
    }

    public Long getInt_amount_collected() {
        return int_amount_collected;
    }

    public void setInt_amount_collected(Long int_amount_collected) {
        this.int_amount_collected = int_amount_collected;
    }
}
