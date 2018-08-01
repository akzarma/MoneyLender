package com.akzarma.moneylender;

import java.io.Serializable;

public class CustomerAmount implements Cloneable, Serializable {
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    private Customer customer;

    public Long getAmount_collected() {
        return amount_collected;
    }

    public void setAmount_collected(Long amount_collected) {
        this.amount_collected = amount_collected;
    }

    private Long amount_collected;
}
