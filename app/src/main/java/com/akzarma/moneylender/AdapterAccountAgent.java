package com.akzarma.moneylender;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterAccountAgent extends RecyclerView.Adapter<AdapterAccountAgent.AccountAgentHolder> {

    private List<AccountAgent> accountAgentList;

    AdapterAccountAgent(List<AccountAgent> accountAgentList) {
        Collections.sort(accountAgentList, new Comparator<AccountAgent>() {
            @Override
            public int compare(AccountAgent o1, AccountAgent o2) {
                return o1.getAccount().getNo().compareTo(o2.getAccount().getNo());
            }
        });
        this.accountAgentList = accountAgentList;
    }


    @NonNull
    @Override
    public AccountAgentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_date_amount, parent, false);
        return new AccountAgentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAccountAgent.AccountAgentHolder holder, int position) {
        final AccountAgent accountAgent = accountAgentList.get(position);
        holder.date.setText(accountAgent.getAccount().getNo());
        holder.amount_deposited.setText(accountAgent.getAgent().getId());
    }


    @Override
    public int getItemCount() {
        return accountAgentList.size();
    }

    class AccountAgentHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView amount_deposited;

        AccountAgentHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date_field);
            amount_deposited = view.findViewById(R.id.amount_deposited_field);

        }
    }
}
