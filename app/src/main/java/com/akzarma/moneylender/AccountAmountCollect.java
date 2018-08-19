package com.akzarma.moneylender;

public class AccountAmountCollect {
    private Account account;
    private Long prin_amount_collected;
    private Long int_amount_collected;

    AccountAmountCollect(){

    }
    AccountAmountCollect(Account account, String prin_int) {
        this.account = account;
        if (prin_int.contains(",")) {
            this.prin_amount_collected = Long.parseLong(prin_int.split(",")[0]);
            this.int_amount_collected = Long.parseLong(prin_int.split(",")[1]);
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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