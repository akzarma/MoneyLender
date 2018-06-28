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

import java.util.Calendar;
import java.util.List;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.AgentHolder> {

    private List<AgentCollect> agentCollectList;
    private Context context;
    private Calendar selected_cal;

    public AgentAdapter(List<AgentCollect> dataset, Calendar selected_cal, Context context){
        this.agentCollectList = dataset;
        this.selected_cal = selected_cal;
        this.context = context;
    }

    @NonNull
    @Override
    public AgentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_layout, parent, false);
        return new AgentHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AgentHolder holder, int position) {
        AgentCollect agentCollect = agentCollectList.get(position);
        List<AccountAmountCollect>  accountAmountCollectList = agentCollect.getAccountAmountCollectList();
        int total_collect = 0;
        for(AccountAmountCollect accountAmountCollect:accountAmountCollectList){
            total_collect += accountAmountCollect.getAmount();
        }
        holder.agent_name.setText(agentCollect.getAgent().getName());
        holder.total_collection.setText("₹ "+String.valueOf(total_collect));
    }

    @Override
    public int getItemCount() {
        return agentCollectList.size();
    }

    class AgentHolder extends RecyclerView.ViewHolder {
        TextView agent_id;
        TextView agent_name;
        TextView total_collection;
        CardView cardView;


        AgentHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.fragment_day_cardview);
            agent_name = view.findViewById(R.id.class_textView);
            total_collection = view.findViewById(R.id.subject_textview);


        }
    }
}


