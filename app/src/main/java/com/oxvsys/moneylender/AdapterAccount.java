package com.oxvsys.moneylender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class AdapterAccount extends RecyclerView.Adapter<AdapterAccount.AccountHolder> {

    private List<Account> accountList;
    Context context;

    AdapterAccount(List<Account> accountList, Context context) {
        this.accountList = accountList;
        this.context = context;
    }


    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_account, parent, false);
        return new AccountHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterAccount.AccountHolder holder, int position) {
        final Account account = accountList.get(position);

        holder.lf_field.setText(account.getLf_no());
        holder.info_field.setText(account.getInfo());
        holder.customer_loan_amount_field.setText("₹ " + (String.valueOf(account.getFile_amt())));
        holder.customer_account_field.setText(account.getNo());
        holder.loan_duration_field.setText(String.valueOf(account.getDuration()));
        holder.start_date_field.setText(MainActivity.CaltoStringDate(account.getO_date()));
        holder.end_date_field.setText(MainActivity.CaltoStringDate(account.getC_date()));
        holder.customer_deposited_field.setText("₹ " + String.valueOf(account.getDeposited()));
        if (account.getType().equals("0")) {
            holder.customer_account_type_field.setText("Daily Basis");

            Calendar c_date_cal = Calendar.getInstance();
            c_date_cal.setTimeInMillis(account.getC_date().getTimeInMillis());
            holder.loan_duration_field.setText(String.valueOf(account.getDuration()) + " days");
        } else if (account.getType().equals("1")) {
            holder.customer_account_type_field.setText("Monthly basis (" + account.getRoi() + "% annual interest)");
            holder.loan_duration_field.setText(String.valueOf(account.getDuration()) + " months");
        }
    }


    @Override
    public int getItemCount() {
        return accountList.size();
    }

    class AccountHolder extends RecyclerView.ViewHolder {
        TextView lf_field;
        TextView info_field;
        TextView customer_loan_amount_field;
        TextView customer_deposited_field;
        TextView customer_account_type_field;
        TextView customer_account_field;
        TextView loan_duration_field;
        TextView start_date_field;
        TextView end_date_field;

        AccountHolder(View view) {
            super(view);
            lf_field = view.findViewById(R.id.lf_field);
            info_field = view.findViewById(R.id.info_field);
            customer_loan_amount_field = view.findViewById(R.id.customer_loan_amount_field);
            customer_deposited_field = view.findViewById(R.id.customer_deposited_field);
            customer_account_type_field = view.findViewById(R.id.customer_account_type_field);
            customer_account_field = view.findViewById(R.id.customer_account_field);
            loan_duration_field = view.findViewById(R.id.loan_duration_field);
            end_date_field = view.findViewById(R.id.last_date_field);
            start_date_field = view.findViewById(R.id.start_date_field);

        }
    }
}
