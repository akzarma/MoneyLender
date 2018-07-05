package com.oxvsys.moneylender;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.logged_agent;

public class FragmentCollect extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Customer selected_customer;
    Long deposited = 0L;
    Long file_amt;
    int fields_loaded = 0;
    int months_passed;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentCollect() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentCollect.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCollect newInstance(Account account) {
        FragmentCollect fragment = new FragmentCollect();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collect_daily, container, false);
        final ProgressBar progressBar = view.findViewById(R.id.collect_money_progress);
        progressBar.setVisibility(View.VISIBLE);
        final EditText amount_field = view.findViewById(R.id.amount_field);
        final TextView customer_name_field = view.findViewById(R.id.customer_name_field);
        final TextView customer_id_field = view.findViewById(R.id.customer_id_field);
        final TextView customer_loan_amount_field = view.findViewById(R.id.customer_loan_amount_field);
        final TextView customer_deposited_field = view.findViewById(R.id.customer_deposited_field);
        final TextView customer_account_type_field = view.findViewById(R.id.customer_account_type_field);
        final TextView customer_mobile_field = view.findViewById(R.id.customer_mobile_field);
        final TextView customer_account_field = view.findViewById(R.id.customer_account_field);
        final TextView loan_duration_field = view.findViewById(R.id.loan_duration_field);
        final TextView expected_amount_view = view.findViewById(R.id.expected_amount_view);
        final TextView remaining_money_field = view.findViewById(R.id.remaining_money_field);
        final ImageView dialer_view = view.findViewById(R.id.dialer_view);
        final TextView last_date_field = view.findViewById(R.id.last_date_field);
//        final Button deposit_button = view.findViewById(R.id.deposit_button);
        final Account selected_account = (Account) getArguments().getSerializable(ARG_PARAM1);

        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));
        fab.setVisibility(View.INVISIBLE);
        customer_account_field.setText(selected_account.getNo());

//        final String logged_agent = getData("user_id", getContext());
//        String logged_agent = getData("user_id", getContext());

//        Account account = new Account();

        final Calendar curr_cal = Calendar.getInstance();
        curr_cal.set(Calendar.HOUR_OF_DAY, 0);
        curr_cal.set(Calendar.MINUTE, 0);
        curr_cal.set(Calendar.SECOND, 0);
        curr_cal.set(Calendar.MILLISECOND, 0);
        final String curr_date = MainActivity.CaltoStringDate(curr_cal);


//

        DatabaseReference account_customer_db_ref = database.getReference("agentAccount").child(logged_agent).child(selected_account.getNo());
        account_customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference customers_db_ref = database.getReference("customers").child(String.valueOf(dataSnapshot.getValue()));
                customers_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        selected_customer = dataSnapshot.getValue(Customer.class);
                        selected_customer.setId(dataSnapshot.getKey());
                        HashMap<String, Object> accounts = (HashMap<String, Object>) ((HashMap<String, Object>) dataSnapshot.getValue()).get("accounts");

                        Account account = new Account(accounts.get(selected_account.getNo()));
                        account.setNo(selected_account.getNo());
                        List<Account> accountList = new ArrayList<>();
//                                    Account account1 = new Account(account.getValue());
//                                    account.setNo(account.getKey());
                        accountList.add(account);
                        selected_customer.setAccounts1(accountList);

                        file_amt = account.getFile_amt();
                        Long deposited = account.getDeposited();
                        Long amt_left = file_amt - deposited;


                        customer_loan_amount_field.setText("₹ " + String.valueOf(account.getFile_amt()));
                        customer_deposited_field.setText("₹ " + String.valueOf(account.getDeposited()));
                        if (account.getType().equals("0")) {
                            customer_account_type_field.setText("Daily basis");
                            loan_duration_field.setText(String.valueOf(account.getDuration()) + " days");
                            remaining_money_field.setText(String.valueOf(amt_left));
                            Calendar today_cal = Calendar.getInstance();
                            today_cal.set(Calendar.HOUR_OF_DAY, 0);
                            today_cal.set(Calendar.MINUTE, 0);
                            today_cal.set(Calendar.SECOND, 0);
                            today_cal.set(Calendar.MILLISECOND, 0);
                            Calendar c_date_cal = Calendar.getInstance();
                            c_date_cal.setTimeInMillis(account.getC_date().getTimeInMillis());

                            Long days_diff = TimeUnit.MILLISECONDS.toDays(c_date_cal.getTimeInMillis() - today_cal.getTimeInMillis());


                            expected_amount_view.setText("Expected Amount   ₹ " +String.valueOf(amt_left / days_diff));
                            last_date_field.setText(String.valueOf(MainActivity.CaltoStringDate(account.getC_date())) + " (" + days_diff + " days left)");

                        } else if (account.getType().equals("1")) {
                            customer_account_type_field.setText("Monthly basis (" + account.getRoi() + "% annual interest)");
                            loan_duration_field.setText(String.valueOf(account.getDuration()) + " months");

                            if (amt_left != 0) {

                                float roi = account.getRoi();
                                Long months_duration = account.getDuration();
                                float roi_per_month_100 = roi / 1200;

                                Calendar today_cal = Calendar.getInstance();
                                today_cal.set(Calendar.HOUR, 0);
                                today_cal.set(Calendar.MINUTE, 0);
                                today_cal.set(Calendar.SECOND, 0);
                                today_cal.set(Calendar.MILLISECOND, 0);
                                Calendar o_date_cal = Calendar.getInstance();
                                o_date_cal.setTimeInMillis(account.getO_date().getTimeInMillis());

                                Long days_diff = TimeUnit.MILLISECONDS.toDays(today_cal.getTimeInMillis() - o_date_cal.getTimeInMillis());


                                months_passed = Integer.parseInt(String.valueOf(days_diff / 30).split("\\.")[0]);


                                int months_left = (int) (months_duration - months_passed);
                                int rcmnd_amt = (int) ((double) amt_left * (double) Math.pow((1 + roi_per_month_100), Double.parseDouble(String.valueOf(months_left))));

                                int curr_month_expected_amount = rcmnd_amt / months_left;
                                remaining_money_field.setText("₹ " + String.valueOf(amt_left) + " excl. interest");
                                expected_amount_view.setText("Expected Amount   ₹ " + String.valueOf(curr_month_expected_amount));
                                last_date_field.setText(String.valueOf(MainActivity.CaltoStringDate(account.getC_date())) + " (" + months_left + " months left)");
                            } else {
                                fab.setVisibility(View.INVISIBLE);
                                amount_field.setVisibility(View.INVISIBLE);
                                remaining_money_field.setText("0");
                                expected_amount_view.setText("₹ 0");
                            }
                        }
                        customer_mobile_field.setText(selected_customer.getMobile());


//                                List<Account> accountList = new ArrayList<>();
//                                accountList.add(selected_account);
//                                selected_customer.setAccounts1(accountList);
                        customer_name_field.setText(selected_customer.getName());
                        customer_id_field.setText(selected_customer.getId());
                        fields_loaded += 1;
                        if (fields_loaded == 1) {
                            fab.setVisibility(View.VISIBLE);
                        }
                        file_amt = account.getFile_amt();

                        if (account.getType().equals("0")) {
                            DatabaseReference agent_collect_date_db_ref = database.getReference("agentCollect").child(logged_agent)
                                    .child(curr_date);
                            agent_collect_date_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(selected_account.getNo())) {
                                        amount_field.setText(String.valueOf(((HashMap<String, String>) dataSnapshot.getValue()).get(selected_account.getNo())));
                                        fab.setVisibility(View.INVISIBLE);
                                        amount_field.setEnabled(false);
                                    }
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
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dialer_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + customer_mobile_field.getText().toString()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (amount_field.getText().toString().length() == 0) {
                    amount_field.setError("Amount Money is required!");
                    return;
                }
                fab.setVisibility(View.INVISIBLE);
                final Long amount_recieved = Long.parseLong(String.valueOf(amount_field.getText()));

                //=========================================================================================================


//        agent.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(agent_id)){
//                    for(DataSnapshot dates:dataSnapshot.getChildren()){
//
//                    }
//                }else{
//                    HashMap<String, Object> agent_id_key_value = new HashMap<>();
//                    HashMap<String, Object> date_key_value = new HashMap<>();
//                    HashMap<String, Integer> account_key_value = new HashMap<>();
//                    account_key_value.put(selected_account.getNo(), amount_recieved);
//                    date_key_value.put(curr_date, account_key_value);
//                    agent_id_key_value.put(agent_id, date_key_value);
//                    agent.setValue(agent_id_key_value)
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }


//        });


                DatabaseReference agent_collect_date_db_ref = database.getReference("agentCollect").child(logged_agent)
                        .child(String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                                String.valueOf(curr_cal.get(Calendar.MONTH) + 1) + "-" +
                                String.valueOf(curr_cal.get(Calendar.YEAR)));
                agent_collect_date_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(selected_account.getNo())) {
                            amount_field.setText(((HashMap<String, String>) dataSnapshot.getValue()).get(selected_account.getNo()));
                            amount_field.setEnabled(false);
                            return;
//                                    Long prev_money = ((HashMap<String, Long>) dataSnapshot.getValue()).get(selected_account.getNo());
//                                    deposited -= prev_money;
                        }
//                                deposited += amount_recieved;
                        else {
                            DatabaseReference agent = database.getReference("agentCollect");
                            agent.child(logged_agent).child(String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                                    String.valueOf(curr_cal.get(Calendar.MONTH) + 1) + "-" +
                                    String.valueOf(curr_cal.get(Calendar.YEAR))).child(selected_account.getNo()).setValue(amount_recieved).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    database.getReference("customers")
                                            .child(selected_customer.getId()).child("accounts")
                                            .child(selected_customer.getAccounts1().get(0).getNo())
                                            .child("deposited").setValue(Long.parseLong(amount_field.getText().toString()))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d("deposited new money: ", "Account: " + selected_account.getNo());

                                                    FragmentManager fragmentManager = getFragmentManager();
                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                    FragmentAccountTypeInfo fragmentAccountTypeInfo = FragmentAccountTypeInfo.newInstance(Calendar.getInstance(), selected_customer.getAccounts1().get(0).getType());
                                                    fragmentTransaction.replace(R.id.fragment_container, fragmentAccountTypeInfo).addToBackStack(null).
                                                            commit();
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        fab.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Failed to collect money. Try again!", Toast.LENGTH_LONG).show();

                    }
                });
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
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
