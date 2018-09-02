package com.akzarma.moneylender;

public class AgentAmount {
    private Agent agent;
    private Long prin_amount_collected;
    private Long int_amount_collected;
    private Long remaining_prin;
    private Long remaining_int;

    public void setAllAmountsCollected(String prin_int){
        if (prin_int.contains(",")) {
            if (prin_int.split(",").length == 4) {
                this.prin_amount_collected = Long.parseLong(prin_int.split(",")[0]);
                this.int_amount_collected = Long.parseLong(prin_int.split(",")[1]);
                this.remaining_prin = Long.parseLong(prin_int.split(",")[2]);
                this.remaining_int = Long.parseLong(prin_int.split(",")[3]);
            } else {
                this.prin_amount_collected = Long.parseLong(prin_int.split(",")[0]);
                this.int_amount_collected = Long.parseLong(prin_int.split(",")[1]);
            }
        }
    }

    public Long getRemaining_prin() {
        return remaining_prin;
    }

    public void setRemaining_prin(Long remaining_prin) {
        this.remaining_prin = remaining_prin;
    }

    public Long getRemaining_int() {
        return remaining_int;
    }

    public void setRemaining_int(Long remaining_int) {
        this.remaining_int = remaining_int;
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
