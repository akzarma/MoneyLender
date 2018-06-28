package com.oxvsys.moneylender;

public class AccountAmountCollect{
    private Account account;
    private int amount;

    AccountAmountCollect(Account account, int amount){
        this.account = account;
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}