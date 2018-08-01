package com.akzarma.moneylender;

public class AccountAmountCollect {
    private Account account;
    private Long amount_collected;

    AccountAmountCollect(Account account, Long amount) {
        this.account = account;
        this.amount_collected = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getAmount() {
        return amount_collected;
    }

    public void setAmount(Long amount) {
        this.amount_collected = amount;
    }
}