package com.oxvsys.moneylender;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.util.NumberUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.logged_agent;
import static com.oxvsys.moneylender.MainActivity.logged_type;

public class CustomerDailyInfoAdapter extends RecyclerView.Adapter<CustomerDailyInfoAdapter.CustomerHolder> {

    private HashMap<String, CustomerAmount> account_amount_map;
    private HashMap<String, AgentAmount> agentAmountHashMap;
    private List<CustomerAmount> customerAmountList;
    private Context context;
    private Calendar sel_calendar;
    private FragmentManager fragmentManager;


    public CustomerDailyInfoAdapter(HashMap<String, CustomerAmount> dataset,
                                    HashMap<String, AgentAmount> agentAmountHashMap,
                                    Calendar sel_calendar, Context context, FragmentManager fragmentManager) {
        this.account_amount_map = dataset;
        this.context = context;
        this.sel_calendar = sel_calendar;
        this.fragmentManager = fragmentManager;
        this.agentAmountHashMap = agentAmountHashMap;

        this.customerAmountList = new ArrayList<>(dataset.values());
        Collections.sort(this.customerAmountList, new Comparator<CustomerAmount>() {
            @Override
            public int compare(CustomerAmount o1, CustomerAmount o2) {
                if (NumberUtils.isNumeric(o1.getCustomer().getAccounts1().get(0).getNo()) &&
                        NumberUtils.isNumeric(o2.getCustomer().getAccounts1().get(0).getNo())) {
                    Long o1_account_no = Long.parseLong(o1.getCustomer().getAccounts1().get(0).getNo());
                    Long o2_account_no = Long.parseLong(o2.getCustomer().getAccounts1().get(0).getNo());
                    if (o1_account_no >= o2_account_no) {
                        return 1;
                    } else return 0;
                } else return 0;


            }
        });
        Log.d("customer_daily_list", "CustomerDailyInfoAdapter: " + customerAmountList);

    }

    @NonNull
    @Override
    public CustomerDailyInfoAdapter.CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_layout, parent, false);
        return new CustomerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerDailyInfoAdapter.CustomerHolder holder, int position) {
        final CustomerAmount customerAmount = customerAmountList.get(position);


        Log.d("customer_daily", customerAmount.toString());
//        final String logged_agent = MainActivity.getData("user_id", context);
        holder.agent_id.setText(customerAmount.getCustomer().getAccounts1().get(0).getNo());
        holder.agent_name.setText(customerAmount.getCustomer().getName());
        holder.total_collection.setText(String.valueOf(customerAmount.getAmount_collected()));


        final List<DateAmount> dateAmountList = new ArrayList<>();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar o_date = customerAmount.getCustomer().getAccounts1().get(0).getO_date();
                final Calendar c_date = customerAmount.getCustomer().getAccounts1().get(0).getC_date();
                String curr_agent = agentAmountHashMap.get(customerAmount.getCustomer().getAccounts1().get(0).getNo()).getAgent().getId();

                DatabaseReference date_amount_db_ref = database.getReference("agentCollect").child(curr_agent);
                date_amount_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot date : dataSnapshot.getChildren()) {
                            Calendar curr_date = MainActivity.StringDateToCal(date.getKey());
                            if ((curr_date.after(o_date) && curr_date.before(c_date)) ||
                                    (curr_date.equals(o_date) || curr_date.equals(c_date))) {
                                DateAmount dateAmount = new DateAmount();
                                dateAmount.setDate(date.getKey());
                                Long curr_amount = ((HashMap<String, Long>) date.getValue())
                                        .get(customerAmount.getCustomer().getAccounts1().get(0).getNo());

                                if (curr_amount != null) {
                                    dateAmount.setAmount(curr_amount);
                                    dateAmountList.add(dateAmount);

                                }
                            }
                        }
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        FragmentDateAmount fragmentDateAmount = FragmentDateAmount.newInstance(dateAmountList, customerAmount);
                        fragmentTransaction.replace(R.id.fragment_container, fragmentDateAmount).addToBackStack(null).
                                commit();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


//        final AgentCollect agentCollect = agentCollectList.get(position);
//        List<AccountAmountCollect> accountAmountCollectList = agentCollect.getAccountAmountCollectList();
//        int total_collect = 0;
//        for (AccountAmountCollect accountAmountCollect : accountAmountCollectList) {
//            total_collect += accountAmountCollect.getAmount();
//        }
//
//        DatabaseReference agent_info_db_ref = database.getReference("agents").child(agentCollect.getAgent().getId());
//        agent_info_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                agentCollect.getAgent().setName(((HashMap<String, String>) dataSnapshot.getValue()).get("name").toString().split(" ")[0]);
//                agentCollect.getAgent().setMobile(((HashMap<String, String>) dataSnapshot.getValue()).get("mobile").toString());
//                holder.agent_name.setText(agentCollect.getAgent().getName());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        holder.total_collection.setText("₹ " + String.valueOf(total_collect));
//        holder.agent_id.setText(agentCollect.getAgent().getId());
    }

    @Override
    public int getItemCount() {
        return customerAmountList.size();
    }


    public class CustomerHolder extends RecyclerView.ViewHolder {
        TextView agent_id;
        TextView agent_name;
        TextView total_collection;
        CardView cardView;

        public CustomerHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.fragment_day_cardview);
            agent_id = view.findViewById(R.id.agent_id_field);
            agent_name = view.findViewById(R.id.class_textView);
            total_collection = view.findViewById(R.id.subject_textview);
        }
    }
}
