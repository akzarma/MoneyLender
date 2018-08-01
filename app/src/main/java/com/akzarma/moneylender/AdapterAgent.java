package com.akzarma.moneylender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.akzarma.moneylender.HomeActivity.database;

public class AdapterAgent extends RecyclerView.Adapter<AdapterAgent.AgentHolder> {

    private List<AgentCollect> agentCollectList;

    AdapterAgent(List<AgentCollect> dataset) {
        this.agentCollectList = dataset;
    }

    @NonNull
    @Override
    public AgentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_layout, parent, false);
        TextView tap_text = itemView.findViewById(R.id.tap_text);
        View bar = itemView.findViewById(R.id.hori_line);
        tap_text.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);
        return new AgentHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AgentHolder holder, int position) {
        final AgentCollect agentCollect = agentCollectList.get(position);
        List<AccountAmountCollect> accountAmountCollectList = agentCollect.getAccountAmountCollectList();
        int total_collect = 0;
        for (AccountAmountCollect accountAmountCollect : accountAmountCollectList) {
            total_collect += accountAmountCollect.getAmount();
        }

        DatabaseReference agent_info_db_ref = database.getReference("agents").child(agentCollect.getAgent().getId());
        agent_info_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                agentCollect.getAgent().setName(((HashMap<String, String>) dataSnapshot.getValue()).get("name").split(" ")[0]);
                agentCollect.getAgent().setMobile(((HashMap<String, String>) dataSnapshot.getValue()).get("mobile"));
                holder.agent_name.setText(agentCollect.getAgent().getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.total_collection.setText("₹ " + String.valueOf(total_collect));
        holder.agent_id.setText(agentCollect.getAgent().getId());

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
            agent_id = view.findViewById(R.id.agent_id_field);
            agent_name = view.findViewById(R.id.class_textView);
            total_collection = view.findViewById(R.id.subject_textview);


        }
    }
}


