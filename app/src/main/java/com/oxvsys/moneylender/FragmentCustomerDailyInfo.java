package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oxvsys.moneylender.LoginActivity.database;

public class FragmentCustomerDailyInfo extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private CustomerDailyInfoAdapter mAdapter;
    private TextView currentDay_textView;
    Calendar sel_calendar;
    HashMap<String, CustomerAmount> customer_amount_map;

    private OnFragmentInteractionListener mListener;

    public FragmentCustomerDailyInfo() {
        // Required empty public constructor
    }

    public static FragmentCustomerDailyInfo newInstance(Calendar sel_cal) {
        FragmentCustomerDailyInfo fragment = new FragmentCustomerDailyInfo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, sel_cal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_daily_info, container, false);

        sel_calendar = (Calendar) getArguments().getSerializable(ARG_PARAM1);
        recyclerView = view.findViewById(R.id.customer_daily_info_recycler);

        int month = sel_calendar.get(Calendar.MONTH) + 1;
        final String sel_date = sel_calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                month + "-" +
                sel_calendar.get(Calendar.YEAR);

        DatabaseReference agentDailyCollects = database.getReference("agentCollect");

        agentDailyCollects.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Agent> agents = new ArrayList<>();
                for (final DataSnapshot each_agent : dataSnapshot.getChildren()) {
                    final Agent curr_agent = new Agent();
                    curr_agent.setId(each_agent.getKey());
                    agents.add(curr_agent);
                    final HashMap<String, Object> account_amount_map = new HashMap<>();
                    final List<AgentCollect> agentCollectList = new ArrayList<>();

                    for (DataSnapshot date : each_agent.getChildren()) {
                        if (date.getKey().equals(sel_date)) {
                            List<AccountAmountCollect> accountAmountCollectList = new ArrayList<>();
                            for (DataSnapshot each_account : date.getChildren()) {
                                account_amount_map.put(each_account.getKey(), each_account.getValue().toString());
                            }
                        }
                    }
                    customer_amount_map = new HashMap<>();


//                    for(AgentCollect agentCollect : )
                    final DatabaseReference customerAccount = database.getReference("customers");
                    customerAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot each_customer : dataSnapshot.getChildren()) {

//                                int total_amount = 0;
                                HashMap<String, Object> customer;

                                Customer curr_customer = each_customer.getValue(Customer.class);
                                curr_customer.setId(each_customer.getKey());
                                customer = (HashMap<String, Object>) each_customer.getValue();
                                HashMap<String, Object> accounts = (HashMap<String, Object>) customer.get("accounts");

                                for (Map.Entry<String, Object> account : accounts.entrySet()) {
                                    if (account_amount_map.containsKey(account.getKey())) {
                                        Log.d("customer_collect_daily", account.getValue().toString());
                                        Long amount_collected =Long.parseLong(account_amount_map.get(account.getKey()).toString());

                                        List<Account> accountList = new ArrayList<Account>();
                                        Account account1 = new Account(account.getValue());
                                        account1.setNo(account.getKey());
                                        accountList.add(account1);
                                        Customer newCustomer = (Customer)curr_customer.clone();
                                        newCustomer.setAccounts1(accountList);
                                        CustomerAmount customerAmount = new CustomerAmount();
                                        customerAmount.setCustomer(newCustomer);
                                        customerAmount.setAmount_collected(amount_collected);

                                        account_amount_map.remove(account.getKey());

                                        customer_amount_map.put(account.getKey() , customerAmount);
                                    }
//                                    HashMap<String, Object> accountMap = (HashMap<String, Object>) account.getValue();
//                                    total_amount += Integer.parseInt(accountMap.get("amt").toString());
//                                    Log.d("customer_daily_amap", accountMap.get("amt").toString());
//                                    Log.d("customer_daily", "onDataChange: " + account.getValue());
                                }


//                                Log.d("customer_daily", "onDataChange: " + accounts.get("3").toString());
                            }

                            mAdapter = new CustomerDailyInfoAdapter(customer_amount_map, sel_calendar, getContext());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
