package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import static com.oxvsys.moneylender.LoginActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCollectDaily.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCollectDaily#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCollectDaily extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Customer selected_customer;
    Long deposited = 0L;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentCollectDaily() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentCollectDaily.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCollectDaily newInstance(Account account) {
        FragmentCollectDaily fragment = new FragmentCollectDaily();
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
        //==================HARD CODED=============================================================================
        final String agent_id = "agent_0"; //get logged in id and check type of user == "agent" and also customer in next line
//        Customer selected_customer = new Customer();
//        selected_customer.setId("A1");
        final EditText amount_field = view.findViewById(R.id.amount_field);
        final TextView customer_name_field = view.findViewById(R.id.customer_name_field);
        final TextView customer_id_field = view.findViewById(R.id.customer_id_field);
        final TextView customer_loan_amount_field = view.findViewById(R.id.customer_loan_amount_field);
        final TextView customer_deposited_field = view.findViewById(R.id.customer_deposited_field);
        final TextView customer_account_type_field = view.findViewById(R.id.customer_account_type_field);
        final TextView customer_mobile_field = view.findViewById(R.id.customer_mobile_field);
        final Button deposit_button = view.findViewById(R.id.deposit_button);
        final Account selected_account = (Account) getArguments().getSerializable(ARG_PARAM1);

        final String logged_agent = "agent_0";  //to be changed to dynamic logged in user


        DatabaseReference account_customer_db_ref = database.getReference("agentAccount").child(logged_agent).child(selected_account.getNo());
        account_customer_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference customers_db_ref = database.getReference("customers").child(dataSnapshot.getValue().toString());
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

                        customer_loan_amount_field.setText(String.valueOf(account.getAmt()));
                        customer_deposited_field.setText(String.valueOf(account.getDeposited()));
                        if (account.getType().equals("0"))
                            customer_account_type_field.setText("Daily basis");
                        else if (account.getType().equals("1"))
                            customer_account_type_field.setText("Monthly basis");
                        customer_mobile_field.setText(selected_customer.getMobile());


//                                List<Account> accountList = new ArrayList<>();
//                                accountList.add(selected_account);
//                                selected_customer.setAccounts1(accountList);
                        customer_name_field.setText(selected_customer.getName());
                        customer_id_field.setText(selected_customer.getId());
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


        deposit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount_field.getText().toString().length() == 0) {
                    amount_field.setError("Amount Money is required!");
                    return;
                }
                final Long amount_recieved = Long.parseLong(String.valueOf(amount_field.getText()));

                //=========================================================================================================

                final Calendar curr_cal = Calendar.getInstance();
                curr_cal.set(Calendar.HOUR_OF_DAY, 0);
                curr_cal.set(Calendar.MINUTE, 0);
                curr_cal.set(Calendar.SECOND, 0);
                curr_cal.set(Calendar.MILLISECOND, 0);
                final String curr_date = String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                        String.valueOf(curr_cal.get(Calendar.MONTH)) + "-" +
                        String.valueOf(curr_cal.get(Calendar.YEAR));


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


                        DatabaseReference agent_collect_date_db_ref = database.getReference("agentCollect").child(agent_id)
                                .child(String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                                        String.valueOf(curr_cal.get(Calendar.MONTH) + 1) + "-" +
                                        String.valueOf(curr_cal.get(Calendar.YEAR)));
                        agent_collect_date_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(selected_account.getNo())) {
                                    Long prev_money = ((HashMap<String, Long>) dataSnapshot.getValue()).get(selected_account.getNo());
                                    deposited -= prev_money;
                                }
                                deposited += amount_recieved;

                                DatabaseReference agent = database.getReference("agentCollect");
                                agent.child(agent_id).child(String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                                        String.valueOf(curr_cal.get(Calendar.MONTH) + 1) + "-" +
                                        String.valueOf(curr_cal.get(Calendar.YEAR))).child(selected_account.getNo()).setValue(amount_recieved).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        database.getReference("customers")
                                                .child(selected_customer.getId()).child("accounts")
                                                .child(selected_customer.getAccounts1().get(0).getNo())
                                                .child("deposited").setValue(deposited).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("deposited new money: ", "Account: " + selected_account.getNo());
                                            }
                                        });
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
