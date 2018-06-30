package com.oxvsys.moneylender;

public class AccountAmountCollect{
    private Account account;
    private Long amount;

    AccountAmountCollect(Account account, Long amount){
        this.account = account;
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}