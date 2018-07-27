package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.CaltoStringDate;
import static com.oxvsys.moneylender.MainActivity.logged_agent;
import static com.oxvsys.moneylender.MainActivity.logged_type;


public class FragmentAccountTypeInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentAccountTypeInfo";
    HashMap<String, CustomerAmount> customer_amount_map = new HashMap<>();
    int row = 0, accountCol = 1, amountCol = 3;
    int serialCol = 0;
    //    HashMap<String, CustomerAmount> monthly_customer_amount_map = new HashMap<>();
    private Calendar sel_calendar;
    private String selected_account_type;
    private int loop_count = 0;
    private boolean inactive_cust_required = false;

    private AdapterCustomerDailyInfo mAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentAccountTypeInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentAccountTypeInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAccountTypeInfo newInstance(Calendar cal, String accountType) {
        FragmentAccountTypeInfo fragment = new FragmentAccountTypeInfo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, cal);
        args.putString(ARG_PARAM2, accountType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_type_info, container, false);


        final TextView textView = view.findViewById(R.id.no_customer_under_monthly);
        textView.setVisibility(View.INVISIBLE);
        final CardView heading_card = view.findViewById(R.id.heading_card_view);

        final RecyclerView recycler = view.findViewById(R.id.customer_daily_basis_recycler);

//        final String logged_agent = getData("user_id", getContext());
//        final String logged_type = getData("user_type", getContext());
        final ProgressBar progressBar = view.findViewById(R.id.account_type_progress);
        progressBar.setVisibility(View.VISIBLE);
        final Button date_button = (Button) view.findViewById(R.id.dashboard_title_button);
        selected_account_type = getArguments().getString(ARG_PARAM2);
        sel_calendar = (Calendar) getArguments().getSerializable(ARG_PARAM1);
        final String cal_str = CaltoStringDate(sel_calendar);
        date_button.setText(cal_str);


        final Button getReport = view.findViewById(R.id.get_report);
        getReport.setVisibility(View.INVISIBLE);


        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.get_cash_96));
//        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentSelectAccount fragmentSelectAccount = new FragmentSelectAccount();
                fragmentTransaction.replace(R.id.fragment_container, fragmentSelectAccount).addToBackStack(null).
                        commit();


            }
        });


//        List<CustomerAmount> customerAmountList = new ArrayList<>();
        final HashMap<String, AccountAmountCollect> cust_account_amount = new HashMap<>();

        if (!logged_agent.equals("ERROR")) {

            if (logged_type.equals("agent")) {
                final HashMap<String, AgentAmount> agentAmountHashMap = new HashMap<>();
                DatabaseReference agent_collect_db_ref = database.getReference("agentCollect").child(logged_agent).child(cal_str);
                agent_collect_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        final HashMap<String, String> account_amount_map = new HashMap<>();

                        for (DataSnapshot each_account : dataSnapshot.getChildren()) {
                            account_amount_map.put(each_account.getKey(), String.valueOf(each_account.getValue()));
                            Agent agent = new Agent();
                            agent.setId(logged_agent);
                            AgentAmount agentAmount = new AgentAmount();
                            agentAmount.setAgent(agent);
                            agentAmount.setAmount_collected(Long.parseLong(String.valueOf(each_account.getValue())));
                            agentAmountHashMap.put(each_account.getKey(), agentAmount);
                        }

                        DatabaseReference agent_account_db_ref = database.getReference("agentAccount").child(logged_agent);
                        agent_account_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, String> account_customer_map = (HashMap<String, String>) dataSnapshot.getValue();
                                for (Map.Entry<String, String> account_amount : account_amount_map.entrySet()) {
                                    String customer_id = account_customer_map.get(account_amount.getKey());
                                    Account account = new Account();
                                    account.setNo(account_amount.getKey());
                                    AccountAmountCollect accountAmountCollect = new AccountAmountCollect(account, Long.parseLong(String.valueOf(account_amount.getValue())));

                                    cust_account_amount.put(customer_id, accountAmountCollect);

                                }

                                DatabaseReference customer_db_ref = database.getReference("customers");
                                customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final HashMap<String, AccountAmountCollect> cust_account_amount_copy = new HashMap<>();
                                        cust_account_amount_copy.putAll(cust_account_amount);
                                        final HashMap<String, String> account_amount_map_copy = new HashMap<>();
                                        account_amount_map_copy.putAll(account_amount_map);
                                        for (Map.Entry<String, AccountAmountCollect> each_cust_ac_amt : cust_account_amount.entrySet()) {
                                            if (dataSnapshot.hasChild(each_cust_ac_amt.getKey())) {
                                                Customer curr_customer = dataSnapshot.child(each_cust_ac_amt.getKey()).getValue(Customer.class);
                                                curr_customer.setId(each_cust_ac_amt.getKey());
                                                HashMap<String, Object> accounts = (HashMap<String, Object>) ((HashMap<String, Object>) dataSnapshot.child(each_cust_ac_amt.getKey()).getValue()).get("accounts");
                                                if (accounts != null) {
                                                    for (Map.Entry<String, String> each_acc_amt : account_amount_map.entrySet()) {
                                                        if (accounts.containsKey(each_acc_amt.getKey())) {

                                                            Log.d("customer_collect_daily", each_acc_amt.getValue().toString());
                                                            Long amount_collected = Long.parseLong(String.valueOf(each_acc_amt.getValue()));
                                                            Object account = accounts.get(each_acc_amt.getKey());
                                                            List<Account> accountList = new ArrayList<Account>();
                                                            Account account1 = new Account(account);
                                                            account1.setNo(each_acc_amt.getKey());
                                                            accountList.add(account1);
                                                            Customer newCustomer = (Customer) curr_customer.clone();
                                                            newCustomer.setAccounts1(accountList);
                                                            CustomerAmount customerAmount = new CustomerAmount();
                                                            customerAmount.setCustomer(newCustomer);
                                                            customerAmount.setAmount_collected(amount_collected);

                                                            if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
                                                                customer_amount_map.put(each_acc_amt.getKey(), customerAmount);
                                                            account_amount_map_copy.remove(each_acc_amt.getKey());
                                                            cust_account_amount_copy.remove(each_cust_ac_amt.getKey());
//                                                                        else if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
//                                                                            monthly_customer_amount_map.put(account.getKey(), customerAmount);

                                                        } else {
                                                            inactive_cust_required = true;
                                                        }
//                                    HashMap<String, Object> accountMap = (HashMap<String, Object>) account.getValue();
//                                    total_amount += Integer.parseInt(accountMap.get("amt").toString());
//                                    Log.d("customer_daily_amap", accountMap.get("amt").toString());
//                                    Log.d("customer_daily", "onDataChange: " + account.getValue());
                                                    }
                                                }

                                            } else {
                                                inactive_cust_required = true;
                                            }
                                        }

                                        if (inactive_cust_required) {
                                            DatabaseReference customer_db_ref = database.getReference("inactive/customers");
                                            customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (Map.Entry<String, AccountAmountCollect> each_cust_ac_amt : cust_account_amount_copy.entrySet()) {
                                                        if (dataSnapshot.hasChild(each_cust_ac_amt.getKey())) {
                                                            Customer curr_customer = dataSnapshot.child(each_cust_ac_amt.getKey()).getValue(Customer.class);
                                                            curr_customer.setId(each_cust_ac_amt.getKey());
                                                            HashMap<String, Object> accounts = (HashMap<String, Object>) ((HashMap<String, Object>) dataSnapshot.child(each_cust_ac_amt.getKey()).getValue()).get("accounts");
                                                            if (accounts != null) {
                                                                for (Map.Entry<String, String> each_acc_amt : account_amount_map_copy.entrySet()) {
                                                                    if (accounts.containsKey(each_acc_amt.getKey())) {

                                                                        Log.d("customer_collect_daily", each_acc_amt.getValue().toString());
                                                                        Long amount_collected = Long.parseLong(String.valueOf(each_acc_amt.getValue()));
                                                                        Object account = accounts.get(each_acc_amt.getKey());
                                                                        List<Account> accountList = new ArrayList<Account>();
                                                                        Account account1 = new Account(account);
                                                                        account1.setNo(each_acc_amt.getKey());
                                                                        accountList.add(account1);
                                                                        Customer newCustomer = (Customer) curr_customer.clone();
                                                                        newCustomer.setAccounts1(accountList);
                                                                        CustomerAmount customerAmount = new CustomerAmount();
                                                                        customerAmount.setCustomer(newCustomer);
                                                                        customerAmount.setAmount_collected(amount_collected);

                                                                        if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
                                                                            customer_amount_map.put(each_acc_amt.getKey(), customerAmount);
//                                                                        else if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
//                                                                            monthly_customer_amount_map.put(account.getKey(), customerAmount);

                                                                    }
//                                    HashMap<String, Object> accountMap = (HashMap<String, Object>) account.getValue();
//                                    total_amount += Integer.parseInt(accountMap.get("amt").toString());
//                                    Log.d("customer_daily_amap", accountMap.get("amt").toString());
//                                    Log.d("customer_daily", "onDataChange: " + account.getValue());
                                                                }
                                                            }

                                                        }
                                                    }

                                                    if (customer_amount_map.isEmpty()) {
                                                        textView.setVisibility(View.VISIBLE);
                                                        heading_card.setVisibility(View.INVISIBLE);
                                                        date_button.setVisibility(View.VISIBLE);
                                                    }
                                                    mAdapter = new AdapterCustomerDailyInfo(customer_amount_map,
                                                            agentAmountHashMap
                                                            , sel_calendar, getContext(), getFragmentManager());
                                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                                    recycler.setLayoutManager(mLayoutManager);
                                                    recycler.setItemAnimator(new DefaultItemAnimator());
                                                    recycler.setAdapter(mAdapter);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    getReport.setVisibility(View.VISIBLE);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        } else {

                                            if (customer_amount_map.isEmpty()) {
                                                textView.setVisibility(View.VISIBLE);
                                                heading_card.setVisibility(View.INVISIBLE);
                                                date_button.setVisibility(View.VISIBLE);
                                            }
                                            mAdapter = new AdapterCustomerDailyInfo(customer_amount_map,
                                                    agentAmountHashMap,
                                                    sel_calendar, getContext(), getFragmentManager());
                                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                            recycler.setLayoutManager(mLayoutManager);
                                            recycler.setItemAnimator(new DefaultItemAnimator());
                                            recycler.setAdapter(mAdapter);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getReport.setVisibility(View.VISIBLE);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("onCancelled", databaseError.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (logged_type.equals("admin")) {


                DatabaseReference agent_collect_db_ref = database.getReference("agentCollect");
                agent_collect_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        final HashMap<String, String> account_amount_map = new HashMap<>();
                        HashMap<String, Object> agent_object_map = (HashMap<String, Object>) dataSnapshot.getValue();
                        final HashMap<String, AgentAmount> agentAmountHashMap = new HashMap<>();
                        if (agent_object_map != null) {
                            for (Map.Entry<String, Object> each_agent_map : agent_object_map.entrySet()) {
                                HashMap<String, String> hashMap = (HashMap<String, String>) (((HashMap<String, Object>) each_agent_map.getValue()).get(cal_str));
                                if (hashMap != null)
                                    account_amount_map.putAll(hashMap);
                                for (Map.Entry<String, String> each_account_amount_map : account_amount_map.entrySet()) {
                                    Agent agent = new Agent();
                                    agent.setId(each_agent_map.getKey());
                                    AgentAmount agentAmount = new AgentAmount();
                                    agentAmount.setAgent(agent);
                                    agentAmount.setAmount_collected(Long.parseLong(String.valueOf(each_account_amount_map.getValue())));
                                    if (!agentAmountHashMap.containsKey(each_account_amount_map.getKey()))
                                        agentAmountHashMap.put(each_account_amount_map.getKey(), agentAmount);
                                }
//                                account_amount_map.clear();
                            }
                        }


//                        final HashMap<String, String> account_amount_map = new HashMap<>();
//
//                        for (DataSnapshot each_account : dataSnapshot.getChildren()) {
//                            account_amount_map.put(each_account.getKey(), each_account.getValue().toString());
//                        }

                        DatabaseReference agent_account_db_ref = database.getReference("agentAccount");
                        agent_account_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                HashMap<String, Object> agent_account_obj_map = (HashMap<String, Object>) dataSnapshot.getValue();
//                                HashMap<String, AccountAmountCollect> cust_ac_amt_map = new HashMap<>();
                                if(agent_account_obj_map!=null) {
                                    for (Map.Entry<String, Object> each_agent_ac_obj_map : agent_account_obj_map.entrySet()) {
                                        HashMap<String, String> ac_cust_map = ((HashMap<String, String>) each_agent_ac_obj_map.getValue());
                                        for (Map.Entry<String, String> each_ac_cust_map : ac_cust_map.entrySet()) {
                                            if (agentAmountHashMap.containsKey(each_ac_cust_map.getKey())) {
                                                Long amt = agentAmountHashMap.get(each_ac_cust_map.getKey()).getAmount_collected();
                                                String cust_id = each_ac_cust_map.getValue();
//                                           String account_no = each_ac_cust_map.getKey();
                                                Account account = new Account();
                                                account.setNo(each_ac_cust_map.getKey());
                                                AccountAmountCollect accountAmountCollect = new AccountAmountCollect(account, amt);
                                                cust_account_amount.put(cust_id, accountAmountCollect);
                                            }
                                        }

                                    }
                                }

                                DatabaseReference customer_db_ref = database.getReference("customers");
                                customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final HashMap<String, AccountAmountCollect> cust_account_amount_copy = new HashMap<>();
                                        cust_account_amount_copy.putAll(cust_account_amount);
                                        final HashMap<String, String> account_amount_map_copy = new HashMap<>();
                                        account_amount_map_copy.putAll(account_amount_map);
                                        for (Map.Entry<String, AccountAmountCollect> each_cust_ac_amt : cust_account_amount.entrySet()) {
                                            if (dataSnapshot.hasChild(each_cust_ac_amt.getKey())) {
                                                Customer curr_customer = dataSnapshot.child(each_cust_ac_amt.getKey()).getValue(Customer.class);
                                                curr_customer.setId(each_cust_ac_amt.getKey());
                                                HashMap<String, Object> accounts = (HashMap<String, Object>) ((HashMap<String, Object>) dataSnapshot.child(each_cust_ac_amt.getKey()).getValue()).get("accounts");
                                                if (accounts != null) {
                                                    for (Map.Entry<String, String> each_acc_amt : account_amount_map.entrySet()) {
                                                        if (accounts.containsKey(each_acc_amt.getKey())) {

//                                                            Log.d("customer_collect_daily", each_acc_amt.getValue().toString());
                                                            Long amount_collected = Long.parseLong(String.valueOf(each_acc_amt.getValue()));
                                                            Object account = accounts.get(each_acc_amt.getKey());
                                                            List<Account> accountList = new ArrayList<Account>();
                                                            Account account1 = new Account(account);
                                                            account1.setNo(each_acc_amt.getKey());
                                                            accountList.add(account1);
                                                            Customer newCustomer = (Customer) curr_customer.clone();
                                                            newCustomer.setAccounts1(accountList);
                                                            CustomerAmount customerAmount = new CustomerAmount();
                                                            customerAmount.setCustomer(newCustomer);
                                                            customerAmount.setAmount_collected(amount_collected);

                                                            if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
                                                                customer_amount_map.put(each_acc_amt.getKey(), customerAmount);
                                                            account_amount_map_copy.remove(each_acc_amt.getKey());
                                                            cust_account_amount_copy.remove(each_cust_ac_amt.getKey());
//                                                                        else if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
//                                                                            monthly_customer_amount_map.put(account.getKey(), customerAmount);

                                                        } else {
                                                            inactive_cust_required = true;
                                                        }
//                                    HashMap<String, Object> accountMap = (HashMap<String, Object>) account.getValue();
//                                    total_amount += Integer.parseInt(accountMap.get("amt").toString());
//                                    Log.d("customer_daily_amap", accountMap.get("amt").toString());
//                                    Log.d("customer_daily", "onDataChange: " + account.getValue());
                                                    }
                                                }

                                            } else {
                                                inactive_cust_required = true;
                                            }
                                        }

                                        if (inactive_cust_required) {
                                            DatabaseReference customer_db_ref = database.getReference("inactive/customers");
                                            customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (Map.Entry<String, AccountAmountCollect> each_cust_ac_amt : cust_account_amount_copy.entrySet()) {
                                                        if (dataSnapshot.hasChild(each_cust_ac_amt.getKey())) {
                                                            Customer curr_customer = dataSnapshot.child(each_cust_ac_amt.getKey()).getValue(Customer.class);
                                                            curr_customer.setId(each_cust_ac_amt.getKey());
                                                            HashMap<String, Object> accounts = (HashMap<String, Object>) ((HashMap<String, Object>) dataSnapshot.child(each_cust_ac_amt.getKey()).getValue()).get("accounts");
                                                            if (accounts != null) {
                                                                for (Map.Entry<String, String> each_acc_amt : account_amount_map_copy.entrySet()) {
                                                                    if (accounts.containsKey(each_acc_amt.getKey())) {
                                                                        Long amount_collected = Long.parseLong(String.valueOf(each_acc_amt.getValue()));
                                                                        Object account = accounts.get(each_acc_amt.getKey());
                                                                        List<Account> accountList = new ArrayList<Account>();
                                                                        Account account1 = new Account(account);
                                                                        account1.setNo(each_acc_amt.getKey());
                                                                        accountList.add(account1);
                                                                        Customer newCustomer = (Customer) curr_customer.clone();
                                                                        newCustomer.setAccounts1(accountList);
                                                                        CustomerAmount customerAmount = new CustomerAmount();
                                                                        customerAmount.setCustomer(newCustomer);
                                                                        customerAmount.setAmount_collected(amount_collected);

                                                                        if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
                                                                            customer_amount_map.put(each_acc_amt.getKey(), customerAmount);
                                                                        account_amount_map_copy.remove(each_acc_amt.getKey());
                                                                        cust_account_amount_copy.remove(each_cust_ac_amt.getKey());
//                                                                        else if (customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
//                                                                            monthly_customer_amount_map.put(account.getKey(), customerAmount);

                                                                    }
//                                    HashMap<String, Object> accountMap = (HashMap<String, Object>) account.getValue();
//                                    total_amount += Integer.parseInt(accountMap.get("amt").toString());
//                                    Log.d("customer_daily_amap", accountMap.get("amt").toString());
//                                    Log.d("customer_daily", "onDataChange: " + account.getValue());
                                                                }
                                                            }

                                                        }
                                                    }


                                                    if (customer_amount_map.isEmpty()) {
                                                        textView.setVisibility(View.VISIBLE);
                                                        heading_card.setVisibility(View.INVISIBLE);
                                                        date_button.setVisibility(View.VISIBLE);
                                                    }
                                                    mAdapter = new AdapterCustomerDailyInfo(customer_amount_map, agentAmountHashMap,
                                                            sel_calendar, getContext(), getFragmentManager());
                                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                                    recycler.setLayoutManager(mLayoutManager);
                                                    recycler.setItemAnimator(new DefaultItemAnimator());
                                                    recycler.setAdapter(mAdapter);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    getReport.setVisibility(View.VISIBLE);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        }
                                        if (customer_amount_map.isEmpty()) {
                                            textView.setVisibility(View.VISIBLE);
                                            heading_card.setVisibility(View.INVISIBLE);
                                            date_button.setVisibility(View.VISIBLE);
                                        }
                                        mAdapter = new AdapterCustomerDailyInfo(customer_amount_map,
                                                agentAmountHashMap,
                                                sel_calendar, getContext(), getFragmentManager());
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                        recycler.setLayoutManager(mLayoutManager);
                                        recycler.setItemAnimator(new DefaultItemAnimator());
                                        recycler.setAdapter(mAdapter);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        getReport.setVisibility(View.VISIBLE);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("onCancelled", databaseError.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });


            }
        }

        getReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    Log.d(TAG, "onCreate: " + "true file");
                } else Log.d(TAG, "onCreate: " + "not writable");
                File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MoneyLender");
                File DocsDirectory = new File(root.getAbsolutePath(), "Reports");
                DocsDirectory.mkdirs();
                File actualDoc = new File(DocsDirectory.getAbsolutePath(), cal_str + ".xls");
                try {

                    final WritableWorkbook workbook = Workbook.createWorkbook(actualDoc);
                    final WritableSheet sheet = workbook.createSheet("Test Sheet", 0);
                    Label heading = new Label(0, row, "Report for " + cal_str);
                    row++;
                    int custCol = 2;
                    Label account_label = new Label(accountCol, row, "Account No");
                    Label cust_label = new Label(custCol, row, "Customer (ID)");
                    Label amount_label = new Label(amountCol, row, "Amount Deposited");
                    Label sr_number = new Label(serialCol, row, "Sr.No");
                    row++;



                    try {
                        sheet.addCell(heading);
                        sheet.addCell(account_label);
                        sheet.addCell(cust_label);
                        sheet.addCell(amount_label);
                        sheet.addCell(sr_number);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                    Number amount_collected;
                    Label customer_label;
                    long total_amount = 0;
                    for(CustomerAmount each_customer_amt: customer_amount_map.values()){
                        Number ser_no = new Number(serialCol, row, row - 1);
                        Label account_number = new Label(accountCol, row, String.valueOf(each_customer_amt.getCustomer().getAccounts1().get(0).getNo()));
                        customer_label = new Label(custCol, row, String.valueOf(each_customer_amt.getCustomer().getName())+" ("+
                                each_customer_amt.getCustomer().getId()+")");
                        amount_collected = new Number(amountCol, row,each_customer_amt.getAmount_collected());
                        total_amount+= each_customer_amt.getAmount_collected();
                        row++;
                        try {
                            sheet.addCell(account_number);
                            sheet.addCell(amount_collected);
                            sheet.addCell(customer_label);
                            sheet.addCell(ser_no);
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }
                    }



                    try {
                        row++;
                        amount_collected = new Number(amountCol, row, total_amount);
                        sheet.addCell(amount_collected);
                        customer_label = new Label(custCol, row, "Total Collection");
                        sheet.addCell(customer_label);
                        workbook.write();
                        workbook.close();
                        Toast.makeText(getContext(), "File saved in Documents folder", Toast.LENGTH_LONG).show();
                    } catch (IOException | WriteException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }


            }
        });


        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear = sel_calendar.get(Calendar.YEAR);
                int mMonth = sel_calendar.get(Calendar.MONTH);
                int mDay = sel_calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar selected_cal = Calendar.getInstance();
                        selected_cal.set(selectedyear, selectedmonth, selectedday);
                        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
                        selected_cal.set(Calendar.MINUTE, 0);
                        selected_cal.set(Calendar.SECOND, 0);
                        selected_cal.set(Calendar.MILLISECOND, 0);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentAccountTypeInfo fd = FragmentAccountTypeInfo.newInstance(selected_cal, selected_account_type);
                        ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                commit();
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
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
