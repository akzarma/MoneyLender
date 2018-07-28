package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.logged_agent;

//import static com.oxvsys.moneylender.MainActivity.getData;

public class FragmentSelectAccount extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Account account_selected;
    Spinner spinner;
    int fields_loaded = 0;
    List<String> customer_ids = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentSelectAccount() {

    }

    public static FragmentSelectAccount newInstance(String param1, String param2) {
        FragmentSelectAccount fragment = new FragmentSelectAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        fields_loaded = 0;
        View view = inflater.inflate(R.layout.fragment_select_account, container, false);

//        final String logged_agent = getData("user_id",getContext());
        final List<Account> accountList = new ArrayList<>();
        final List<Account> spinner_accountList = new ArrayList<>();
        final TextView no_account_linked = view.findViewById(R.id.no_account_linked);
        no_account_linked.setVisibility(View.INVISIBLE);

        final ProgressBar progressBar = view.findViewById(R.id.select_account_progress);
        progressBar.setVisibility(View.VISIBLE);

        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));
        fab.setVisibility(View.INVISIBLE);
//        final String logged_agent = getData("user_id", getContext());

        DatabaseReference agent_account_db_ref = database.getReference("agentAccount").child(logged_agent);
        spinner = view.findViewById(R.id.account_spinner);
        spinner.setAdapter(null);


//        final Button next_button = view.findViewById(R.id.next_button);
        agent_account_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final HashMap<String, String> account_customer_map = new HashMap<>();

                for (DataSnapshot account : dataSnapshot.getChildren()) {
                    Account account1 = new Account();
                    account1.setNo(account.getKey());
                    account_customer_map.put(account.getKey(), account.getValue().toString());
                    accountList.add(account1);
                }

//                Collections.sort(accountList, new Comparator<Account>() {
//                    @Override
//                    public int compare(Account o1, Account o2) {
//                        return o1.getNo().compareTo(o2.getNo());
//                    }
//                });

//                DatabaseReference accountType = database.getReference("accountType");
//                accountType.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot account : dataSnapshot.getChildren()){
//                            if()
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                final List<String> spinner_account_name = new ArrayList<>();
                DatabaseReference customers = database.getReference("customers");
                customers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        for (DataSnapshot single_customer : dataSnapshot.getChildren()) {
                            HashMap<String, Object> customer_map = new HashMap<>();
                            customer_map.put(single_customer.getKey(), single_customer.getValue());
                            HashMap<String, Object> account_map;
                            account_map = (HashMap<String, Object>) ((HashMap<String, Object>)
                                    customer_map.get(single_customer.getKey())).get("accounts");

                            if (account_map != null) {
                                for (Map.Entry<String, Object> account : account_map.entrySet()) {
                                    if (account_customer_map.containsKey(account.getKey())) {
                                        spinner_account_name.add(((HashMap<String, Object>) customer_map.
                                                get(single_customer.getKey())).get("name") + " (A/C: " +
                                                account.getKey() + ")");
                                        Account account1 = new Account();
                                        account1.setNo(account.getKey());
                                        spinner_accountList.add(account1);
                                    }
                                }
                            }
                        }

                        if (spinner_account_name.isEmpty()) {
                            spinner.setVisibility(View.INVISIBLE);
                            no_account_linked.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinner_account_name);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerAdapter);
                        }


                        fields_loaded += 1;
                        if (fields_loaded == 1 && !spinner_account_name.isEmpty()) {
                            fab.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentCollect fragmentCollect = FragmentCollect.newInstance(account_selected);
                fragmentTransaction.replace(R.id.fragment_container, fragmentCollect).addToBackStack(null).commit();
                accountList.clear();
            }
        });

//        next_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account_selected = spinner_accountList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                account_selected = spinner_accountList.get(0);
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
        void onFragmentInteraction(Uri uri);
    }
}