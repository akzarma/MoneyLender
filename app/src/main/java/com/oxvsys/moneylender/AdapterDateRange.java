package com.oxvsys.moneylender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.oxvsys.moneylender.MainActivity.StringDateToCal;

public class AdapterDateRange extends RecyclerView.Adapter<AdapterDateRange.DateRangeHolder> {

    private List<DateAmount> dateAmountList;
    Context context;

    AdapterDateRange(List<DateAmount> dateAmountList, Context context) {
        Collections.sort(dateAmountList, new Comparator<DateAmount>() {
            @Override
            public int compare(DateAmount o1, DateAmount o2) {
                return StringDateToCal(o1.getDate()).compareTo(StringDateToCal(o2.getDate()));
            }
        });
        this.dateAmountList = dateAmountList;
        this.context = context;
    }


    @NonNull
    @Override
    public DateRangeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_date_range, parent, false);
        return new AdapterDateRange.DateRangeHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterDateRange.DateRangeHolder holder, int position) {
        DateAmount dateAmount = dateAmountList.get(position);
        holder.date_date_range_textview.setText(dateAmount.getDate());
        holder.collection_date_range_textview.setText("₹ " + String.valueOf(dateAmount.getAmount()));
    }


    @Override
    public int getItemCount() {
        return dateAmountList.size();
    }

    class DateRangeHolder extends RecyclerView.ViewHolder {
        TextView date_date_range_textview;
        TextView collection_date_range_textview;

        DateRangeHolder(View view) {
            super(view);
            date_date_range_textview = view.findViewById(R.id.date_date_range_textview);
            collection_date_range_textview = view.findViewById(R.id.collection_date_range_textview);


        }
    }
}
