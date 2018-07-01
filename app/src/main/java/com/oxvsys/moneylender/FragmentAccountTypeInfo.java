package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.CaltoStringDate;
import static com.oxvsys.moneylender.MainActivity.getData;


public class FragmentAccountTypeInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    HashMap<String, CustomerAmount> daily_customer_amount_map = new HashMap<>();
    HashMap<String, CustomerAmount> monthly_customer_amount_map = new HashMap<>();
    private Calendar sel_calendar;
    private String selected_account_type;

    private CustomerDailyInfoAdapter mAdapter;

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

        final RecyclerView recycler = view.findViewById(R.id.customer_daily_basis_recycler);

        final String logged_agent = getData("user_id", getContext());
        Button date_button = (Button) view.findViewById(R.id.dashboard_title_button);
        selected_account_type = getArguments().getString(ARG_PARAM2);
        sel_calendar = (Calendar) getArguments().getSerializable(ARG_PARAM1);
        String cal_str = CaltoStringDate(sel_calendar);
        date_button.setText(cal_str);

        List<CustomerAmount> customerAmountList = new ArrayList<>();
        final HashMap<String, AccountAmountCollect> cust_account_amount = new HashMap<>();

        if (!logged_agent.equals("ERROR")) {

            DatabaseReference agent_collect_db_ref = database.getReference("agentCollect").child(logged_agent).child(cal_str);
            agent_collect_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final HashMap<String, String> account_amount_map = new HashMap<>();

                    for (DataSnapshot each_account : dataSnapshot.getChildren()) {
                        account_amount_map.put(each_account.getKey(), each_account.getValue().toString());
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
                                AccountAmountCollect accountAmountCollect = new AccountAmountCollect(account, Long.parseLong(account_amount.getValue()));

                                cust_account_amount.put(customer_id, accountAmountCollect);

                            }

                            DatabaseReference customer_db_ref = database.getReference("customers");
                            customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot each_customer : dataSnapshot.getChildren()) {
                                        if (cust_account_amount.containsKey(each_customer.getKey())) {
                                            Customer curr_customer = each_customer.getValue(Customer.class);
                                            curr_customer.setId(each_customer.getKey());
                                            HashMap<String, Object> accounts = (HashMap<String, Object>) ((HashMap<String, Object>) each_customer.getValue()).get("accounts");
                                            if (accounts!=null) {
                                                for (Map.Entry<String, Object> account : accounts.entrySet()) {
                                                    if (account_amount_map.containsKey(account.getKey())) {
                                                        Log.d("customer_collect_daily", account.getValue().toString());
                                                        Long amount_collected = Long.parseLong(account_amount_map.get(account.getKey()).toString());

                                                        List<Account> accountList = new ArrayList<Account>();
                                                        Account account1 = new Account(account.getValue());
                                                        account1.setNo(account.getKey());
                                                        accountList.add(account1);
                                                        Customer newCustomer = (Customer) curr_customer.clone();
                                                        newCustomer.setAccounts1(accountList);
                                                        CustomerAmount customerAmount = new CustomerAmount();
                                                        customerAmount.setCustomer(newCustomer);
                                                        customerAmount.setAmount_collected(amount_collected);

                                                        account_amount_map.remove(account.getKey());
                                                        if(customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
                                                            daily_customer_amount_map.put(account.getKey(), customerAmount);
                                                        else if(customerAmount.getCustomer().getAccounts1().get(0).getType().equals(selected_account_type))
                                                            monthly_customer_amount_map.put(account.getKey(), customerAmount);

                                                    }
//                                    HashMap<String, Object> accountMap = (HashMap<String, Object>) account.getValue();
//                                    total_amount += Integer.parseInt(accountMap.get("amt").toString());
//                                    Log.d("customer_daily_amap", accountMap.get("amt").toString());
//                                    Log.d("customer_daily", "onDataChange: " + account.getValue());
                                                }
                                            }

                                        }
                                    }

                                    mAdapter = new CustomerDailyInfoAdapter(daily_customer_amount_map,sel_calendar, getContext(), getFragmentManager());
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    recycler.setLayoutManager(mLayoutManager);
                                    recycler.setItemAnimator(new DefaultItemAnimator());
                                    recycler.setAdapter(mAdapter);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}