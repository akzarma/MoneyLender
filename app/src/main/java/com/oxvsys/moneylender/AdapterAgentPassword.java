package com.oxvsys.moneylender;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterAgentPassword extends RecyclerView.Adapter<AdapterAgentPassword.AgentPasswordHolder> {

    private ArrayList<ArrayList<String>> agentPassList;

    AdapterAgentPassword(ArrayList<ArrayList<String>> agentPassList) {
        this.agentPassList = agentPassList;
    }


    @NonNull
    @Override
    public AgentPasswordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_date_range, parent, false);
        return new AgentPasswordHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentPasswordHolder holder, int position) {
        ArrayList<String> agentPass = agentPassList.get(position);
        holder.date_date_range_textview.setText(agentPass.get(0));
        holder.collection_date_range_textview.setText(agentPass.get(1));
    }


    @Override
    public int getItemCount() {
        return agentPassList.size();
    }

    class AgentPasswordHolder extends RecyclerView.ViewHolder {
        TextView date_date_range_textview;
        TextView collection_date_range_textview;

        AgentPasswordHolder(View view) {
            super(view);
            date_date_range_textview = view.findViewById(R.id.date_date_range_textview);
            collection_date_range_textview = view.findViewById(R.id.collection_date_range_textview);


        }
    }
}

