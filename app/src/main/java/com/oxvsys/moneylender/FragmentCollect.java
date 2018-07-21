package com.oxvsys.moneylender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.logged_agent;

public class FragmentCollect extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Customer selected_customer;
    Long deposited = 0L;
    //    Long file_amt;
    Long remaining_amt;
    int fields_loaded = 0;
    int months_passed;
    int months_left = 0;
    int months_duration;
    Long final_file_amount_interest;
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
//        final TextView expected_amount_view = view.findViewById(R.id.expected_amount_view);
        final TextView remaining_money_field = view.findViewById(R.id.remaining_money_field);
        final ImageView dialer_view = view.findViewById(R.id.dialer_view);
        final TextView last_date_field = view.findViewById(R.id.last_date_field);
        final TextView info_field = view.findViewById(R.id.info_field);
        final TextView start_date_field = view.findViewById(R.id.start_date_field);
        final TextView inr_sign = view.findViewById(R.id.inr_sign);
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

//                        file_amt = account.getFile_amt();
//                        Long deposited = account.getDeposited();
                        remaining_amt = account.getR_amt();


                        customer_mobile_field.setText(selected_customer.getMobile());
                        customer_name_field.setText(selected_customer.getName());
                        customer_id_field.setText(selected_customer.getId());


                        customer_loan_amount_field.setText("₹ " + String.valueOf(account.getFile_amt()));
                        customer_deposited_field.setText("₹ " + String.valueOf(account.getDeposited()));
                        Long days_diff = 0L;
                        if (account.getType().equals("0")) {
                            customer_account_type_field.setText("Daily Basis");

                            Calendar c_date_cal = Calendar.getInstance();
                            c_date_cal.setTimeInMillis(account.getC_date().getTimeInMillis());
                            loan_duration_field.setText(String.valueOf(account.getDuration()) + " days");
                            remaining_money_field.setText("₹ "+String.valueOf(account.getR_amt()));


                            days_diff = TimeUnit.MILLISECONDS.toDays(c_date_cal.getTimeInMillis() - curr_cal.getTimeInMillis());

//                            final_file_amount_interest = file_amt;

                            Calendar last_pay_date = selected_customer.getAccounts1().get(0).getLast_pay_date();

                            if (last_pay_date != null) {
                                days_diff = TimeUnit.MILLISECONDS.toDays(curr_cal.getTimeInMillis() - last_pay_date.getTimeInMillis());
                                info_field.setText("Last Payment received on " + MainActivity.CaltoStringDate(last_pay_date));
                                if (days_diff < 1) {
                                    inr_sign.setText("");
                                    amount_field.setText("");
                                    amount_field.setHint("");
                                    fab.setVisibility(View.INVISIBLE);
                                    amount_field.setEnabled(false);
                                }
                            }

                            last_date_field.setText(String.valueOf(MainActivity.CaltoStringDate(account.getC_date())) + " (" + days_diff + " days left)");
                            start_date_field.setText(MainActivity.CaltoStringDate(account.getO_date()));
                        } else if (account.getType().equals("1")) {
                            customer_account_type_field.setText("Monthly basis (" + account.getRoi() + "% annual interest)");
                            loan_duration_field.setText(String.valueOf(account.getDuration()) + " months");

                            if (remaining_amt != 0) {

                                float roi = account.getRoi();
                                months_duration = Integer.parseInt(account.getDuration().toString());
                                float roi_per_month = roi / 12;
                                Calendar o_date_cal = account.getO_date();
                                days_diff = TimeUnit.MILLISECONDS.toDays(curr_cal.getTimeInMillis() - o_date_cal.getTimeInMillis());
                                months_passed = Integer.parseInt(String.valueOf(days_diff / 30).split("\\.")[0]);
                                months_left = months_duration - months_passed;

                                Calendar last_int_calc_cal = account.getLast_int_calc();

                                Long days_diff_without_calc = TimeUnit.MILLISECONDS.toDays(curr_cal.getTimeInMillis() - last_int_calc_cal.getTimeInMillis());

                                int months_passed_without_calc = Integer.parseInt(String.valueOf(days_diff_without_calc / 30).split("\\.")[0]);
                                if (months_passed_without_calc > 0) {
                                    remaining_amt = compoundInterest(remaining_amt, roi_per_month, months_passed_without_calc);
                                    database.getReference("customers")
                                            .child(selected_customer.getId())
                                            .child("accounts")
                                            .child(selected_account.getNo())
                                            .child("r_amt")
                                            .setValue(remaining_amt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference("customers")
                                                    .child(selected_customer.getId())
                                                    .child("accounts")
                                                    .child(selected_account.getNo())
                                                    .child("last_int_calc").setValue(MainActivity.CaltoStringDate(curr_cal));
                                            remaining_money_field.setText("₹ " + String.valueOf(remaining_amt));
                                        }
                                    });
                                } else {
                                    remaining_money_field.setText("₹ " + String.valueOf(remaining_amt));
                                }


                                if (months_passed < 1) {
                                    fab.setVisibility(View.INVISIBLE);
                                    fields_loaded -= 1;
                                    inr_sign.setVisibility(View.INVISIBLE);
                                    amount_field.setVisibility(View.INVISIBLE);
                                }


                            } else {
                                fab.setVisibility(View.INVISIBLE);
                                fields_loaded -= 1;
                                amount_field.setVisibility(View.INVISIBLE);
                                remaining_money_field.setText("₹ 0");
                            }
                            start_date_field.setText(MainActivity.CaltoStringDate(account.getO_date()));
                            last_date_field.setText(String.valueOf(MainActivity.CaltoStringDate(account.getC_date())) + " (" + months_left + " months left)");

                            Calendar last_pay_date = selected_customer.getAccounts1().get(0).getLast_pay_date();

                            if (last_pay_date != null) {
                                days_diff = TimeUnit.MILLISECONDS.toDays(curr_cal.getTimeInMillis() - last_pay_date.getTimeInMillis());
                                info_field.setText("Last Payment received on " + MainActivity.CaltoStringDate(last_pay_date));
                                int months_passed = (int) (days_diff / 30);
                                if (months_passed < 1) {
                                    inr_sign.setText("");
                                    amount_field.setText("");
                                    amount_field.setHint("");
                                    fab.setVisibility(View.INVISIBLE);
                                    amount_field.setEnabled(false);
                                }
                            }
                        }
                        fields_loaded += 1;
                        if (fields_loaded == 1) {
                            fab.setVisibility(View.VISIBLE);
                        }
//                        file_amt = account.getFile_amt();


                        if (account.getType().equals("0")) {
//                            if (last_pay_date == null) {
//                                expected_amount_view.setText("Expected Amount   ₹ " + String.valueOf(amt_left / days_diff));
//                            } else {
//                                if (!curr_date.equals(MainActivity.CaltoStringDate(last_pay_date))) {
//                                    expected_amount_view.setText("Expected Amount   ₹ " + String.valueOf(amt_left / days_diff));
//                                }
//                                if (last_pay_date != null && MainActivity.CaltoStringDate(last_pay_date).equals(curr_date)) {
//                                    DatabaseReference agent_collect_date_db_ref = database.getReference("agentCollect").child(logged_agent)
//                                            .child(MainActivity.CaltoStringDate(last_pay_date));
//                                    agent_collect_date_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.hasChild(selected_account.getNo())) {
//
//                                                Long last_amt = ((HashMap<String, Long>) dataSnapshot.getValue()).get(selected_account.getNo());
//                                                if (last_amt != null) {
//                                                    expected_amount_view.setText("");
//                                                    inr_sign.setText("");
//                                                    amount_field.setText("");
//                                                    amount_field.setHint("");
//                                                    info_field.setText("Last Amount of ₹ " + last_amt + " received on " + MainActivity.CaltoStringDate(last_pay_date));
//                                                } else {
//                                                    amount_field.setText("0");
//                                                }
//                                                fab.setVisibility(View.INVISIBLE);
//                                                amount_field.setEnabled(false);
//
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//                            }
                        } else if (account.getType().equals("1")) {


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
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                fab.setVisibility(View.INVISIBLE);
                final Long amount_recieved = Long.parseLong(String.valueOf(amount_field.getText()));

                if (amount_recieved <= remaining_amt) {
                    DatabaseReference account_deposited_db_ref = database.getReference("customers")
                            .child(selected_customer.getId()).child("accounts")
                            .child(selected_customer.getAccounts1().get(0).getNo())
                            .child("deposited");
                    account_deposited_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Object get_value = dataSnapshot.getValue();
                            if (get_value != null) {
                                deposited = Long.parseLong(dataSnapshot.getValue().toString());
                            } else {
                                deposited = 0L;
                            }


                            deposited += amount_recieved;

                            DatabaseReference agent = database.getReference("agentCollect");
                            agent.child(logged_agent).child(curr_date)
                                    .child(selected_account.getNo())
                                    .setValue(amount_recieved).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    database.getReference("customers")
                                            .child(selected_customer.getId()).child("accounts")
                                            .child(selected_account.getNo())
                                            .child("deposited").setValue(deposited).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference("customers")
                                                    .child(selected_customer.getId()).child("accounts")
                                                    .child(selected_account.getNo())
                                                    .child("last_pay_date").setValue(MainActivity.CaltoStringDate(curr_cal)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    remaining_amt = remaining_amt - amount_recieved;
                                                    database.getReference("customers")
                                                            .child(selected_customer.getId()).child("accounts")
                                                            .child(selected_account.getNo())
                                                            .child("r_amt").setValue(remaining_amt);
                                                    Log.d("deposited new money: ", "Account: " + selected_account.getNo());


                                                    if (remaining_amt == 0) {
                                                        DatabaseReference customer_accounts = database.getReference("customers").child(selected_customer.getId());
                                                        customer_accounts.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull final DataSnapshot selected_customer_dataSnapshot) {
                                                                DatabaseReference inactive_cust_ref = database.getReference("inactive/customers");
                                                                inactive_cust_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.hasChild(selected_customer.getId())) {
                                                                            Map<String, Object> account_id_map = new HashMap<>();
                                                                            Map<String, String> account_attrs = selected_customer.getAccounts1().get(0).getMap();
                                                                            account_id_map.put(selected_account.getNo(), account_attrs);
                                                                            database.getReference("inactive/customers")
                                                                                    .child(selected_customer.getId())
                                                                                    .child("accounts")
                                                                                    .updateChildren(account_id_map, new DatabaseReference.CompletionListener() {
                                                                                        @Override
                                                                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                                                            Log.d("inactive: ", "The customer is transferred with A/C: " + selected_account.getNo());
                                                                                            if (((HashMap<String, String>) (((HashMap<String, Object>) selected_customer_dataSnapshot.getValue()).get("accounts"))).entrySet().size() <= 1) {
                                                                                                //only one or zero account is there which has deposited == file_amount
                                                                                                //so move the whole customer object to another tree which has path "inactive/customer_id"
                                                                                                selected_customer_dataSnapshot.getRef().removeValue();
                                                                                            } else {
                                                                                                database.getReference("customers").child(selected_customer.getId()).child("accounts")
                                                                                                        .child(selected_account.getNo()).removeValue();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        } else {
                                                                            Map<String, Object> attrs = new HashMap<>();
                                                                            Map<String, Object> account_id_map = new HashMap<>();

                                                                            Account curr_account = selected_customer.getAccounts1().get(0);
                                                                            curr_account.setLast_pay_date((MainActivity.CaltoStringDate(curr_cal)));
                                                                            curr_account.setDeposited(deposited);
                                                                            curr_account.setR_amt(remaining_amt);
                                                                            Map<String, String> account_attrs = selected_customer.getAccounts1().get(0).getMap();

                                                                            attrs.put("name", selected_customer.getName());
                                                                            attrs.put("aadhar", selected_customer.getAadhar_id());
                                                                            attrs.put("occupation", selected_customer.getOccupation());
                                                                            attrs.put("mobile", selected_customer.getMobile());
                                                                            attrs.put("dob", selected_customer.getDOB());
                                                                            attrs.put("address", selected_customer.getAddress());

                                                                            account_id_map.put(selected_account.getNo(), account_attrs);

                                                                            attrs.put("accounts", account_id_map);

                                                                            database.getReference("inactive/customers").child(selected_customer.getId()).updateChildren(attrs, new DatabaseReference.CompletionListener() {
                                                                                @Override
                                                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                                                    Log.d("inactive: ", "The customer is transferred with A/C: " + selected_account.getNo());
                                                                                    if (((HashMap<String, String>) (((HashMap<String, Object>) selected_customer_dataSnapshot.getValue()).get("accounts"))).entrySet().size() <= 1) {
                                                                                        //only one or zero account is there which has deposited == file_amount
                                                                                        //so move the whole customer object to another tree which has path "inactive/customer_id"
                                                                                        selected_customer_dataSnapshot.getRef().removeValue();
                                                                                    } else {
                                                                                        database.getReference("customers").child(selected_customer.getId()).child("accounts")
                                                                                                .child(selected_account.getNo()).removeValue();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
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
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            fab.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Failed to collect money. Try again!", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    amount_field.setError("Amount Money should be less than remaing amount!");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
            }
        });


        return view;
    }


    private Long compoundInterest(Long amt, double roi_per_month, int months) {
        return (long) ((double) amt * (Math.pow((1 + roi_per_month / 100), months)));
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
