package com.oxvsys.moneylender;

public class AccountAmountCollect{
    private Account account;
    private int amount_collected;

    AccountAmountCollect(Account account, int amount){
        this.account = account;
        this.amount_collected = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getAmount() {
        return amount_collected;
    }

    public void setAmount(int amount) {
        this.amount_collected = amount;
    }
}