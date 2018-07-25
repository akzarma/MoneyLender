package com.oxvsys.moneylender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterAgentAccountPayment extends RecyclerView.Adapter<AdapterAgentAccountPayment.AgentAccountPaymentHolder> {

    private List<DateAmount> dateAmountList;
    private CustomerAmount customerAmount;
    private Context context;

    public AdapterAgentAccountPayment(List<DateAmount> dataset, Context context, CustomerAmount customerAmount){
        this.customerAmount = customerAmount;
        this.dateAmountList = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public AgentAccountPaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_date_amount, parent, false);
        return new AgentAccountPaymentHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AgentAccountPaymentHolder holder, int position) {
        final DateAmount dateAmount = dateAmountList.get(position);

        holder.date.setText(dateAmount.getDate());
        holder.amount_deposited.setText("₹ "+String.valueOf(dateAmount.getAmount()));
    }

    @Override
    public int getItemCount() {
        return dateAmountList.size();
    }

    class AgentAccountPaymentHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView amount_deposited;
        CardView cardView;


        AgentAccountPaymentHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.date_amount_cardview);
            date = view.findViewById(R.id.date_field);
            amount_deposited = view.findViewById(R.id.amount_deposited_field);


        }
    }
}


