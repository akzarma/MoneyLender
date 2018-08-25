package com.akzarma.moneylender;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterAgentName extends RecyclerView.Adapter<AdapterAgentName.AgentNameHolder> {

    private ArrayList<ArrayList<String>> agentNameList;
    private FragmentManager fragmentManager;
    private Calendar from_cal, to_cal;

    AdapterAgentName(ArrayList<ArrayList<String>> agentNameList, FragmentManager fragmentManager, Calendar from_cal, Calendar to_cal) {
        this.agentNameList = agentNameList;
        this.fragmentManager = fragmentManager;
        this.from_cal = from_cal;
        this.to_cal = to_cal;
    }


    @NonNull
    @Override
    public AgentNameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_date_range, parent, false);
        return new AgentNameHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentNameHolder holder, int position) {
        final ArrayList<String> agentName = agentNameList.get(position);
        holder.date_date_range_textview.setText(agentName.get(0));
        holder.collection_date_range_textview.setText(agentName.get(1));
        holder.linearlayout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                FragmentDateRangeReport fragmentDateRangeReport = FragmentDateRangeReport.newInstance(from_cal, to_cal, agentName.get(0));
                ft.replace(R.id.fragment_container, fragmentDateRangeReport).addToBackStack(null).
                        commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return agentNameList.size();
    }

    class AgentNameHolder extends RecyclerView.ViewHolder {
        TextView date_date_range_textview;
        TextView collection_date_range_textview;
        CardView linearlayout_card;

        AgentNameHolder(View view) {
            super(view);
            linearlayout_card = view.findViewById(R.id.date_range_cardview);
            date_date_range_textview = view.findViewById(R.id.date_date_range_textview);
            collection_date_range_textview = view.findViewById(R.id.collection_date_range_textview);


        }
    }
}

