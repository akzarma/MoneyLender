package com.akzarma.moneylender;

import java.io.Serializable;

public class CustomerAmount implements Cloneable, Serializable {

    private Customer customer;
    private Long prin_amount_collected;
    private Long int_amount_collected;

    public void setBothAmountsCollected(String prin_int){
        if (prin_int.contains(",")) {
            this.prin_amount_collected = Long.parseLong(prin_int.split(",")[0]);
            this.int_amount_collected = Long.parseLong(prin_int.split(",")[1]);
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
