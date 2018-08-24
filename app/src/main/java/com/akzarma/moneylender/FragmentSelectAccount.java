package com.akzarma.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.akzarma.moneylender.HomeActivity.database;
import static com.akzarma.moneylender.MainActivity.logged_agent;

//import static com.oxvsys.moneylender.MainActivity.getData;

public class FragmentSelectAccount extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Account account_selected;
    Spinner spinner;
    int fields_loaded = 0;
    //    final List<String> spinner_account_name = new ArrayList<>();
    ArrayList<AccountString> accountStringList = new ArrayList<>();
    ArrayList<AccountString> filterlist = new ArrayList<>();


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fields_loaded = 0;
        View view = inflater.inflate(R.layout.fragment_select_account, container, false);

//        final String logged_agent = getData("user_id",getContext());
        final EditText search_edit = view.findViewById(R.id.search_edit);
        search_edit.setText("");
        search_edit.setVisibility(View.INVISIBLE);

        final TextView no_account_linked = view.findViewById(R.id.no_account_linked);
        no_account_linked.setVisibility(View.INVISIBLE);

        final ProgressBar progressBar = view.findViewById(R.id.select_account_progress);
        progressBar.setVisibility(View.VISIBLE);

        final FloatingActionButton fab = (FloatingActionButton) Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_chevron_right_black_24dp));
        fab.setVisibility(View.INVISIBLE);
//        final String logged_agent = getData("user_id", getContext());

        DatabaseReference agent_account_db_ref = database.getReference("agentAccount").child(logged_agent);
        spinner = view.findViewById(R.id.account_spinner);
        spinner.setAdapter(null);
        filterlist.clear();
        accountStringList.clear();


//        final Button next_button = view.findViewById(R.id.next_button);
        agent_account_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final HashMap<String, String> account_customer_map = new HashMap<>();

                for (DataSnapshot account : dataSnapshot.getChildren()) {
                    Account account1 = new Account();
                    account1.setNo(account.getKey());
                    account_customer_map.put(account.getKey(), account.getValue().toString());
                }

                DatabaseReference customers = database.getReference("customers");
                customers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        for (DataSnapshot single_customer : dataSnapshot.getChildren()) {
                            HashMap<String, Object> customer_map = new HashMap<>();
                            customer_map.put(single_customer.getKey(), single_customer.getValue());
                            HashMap<String, Object> account_map, single_cust_map;
                            single_cust_map = ((HashMap<String, Object>)
                                    customer_map.get(single_customer.getKey()));
                            account_map = ((HashMap<String, Object>) single_cust_map.get("accounts"));
                            boolean is_inactive = false;
                            if (single_cust_map.get("inactive") != null) {
                                is_inactive = Boolean.parseBoolean(single_cust_map.get("inactive").toString());
                            }

                            if (account_map != null && !is_inactive) {
                                for (Map.Entry<String, Object> account : account_map.entrySet()) {

                                    if (account_customer_map.containsKey(account.getKey())) {
                                        Account account1 = new Account(account.getValue());
                                        account1.setNo(account.getKey());
                                        if (account1.isActive()) {
                                            AccountString accountString = new AccountString();
                                            accountString.setAccount(account1);
                                            accountString.setInfo(((HashMap<String, Object>) customer_map.
                                                    get(single_customer.getKey())).get("name") + " (A/C: " +
                                                    account.getKey() + ")");
                                            accountStringList.add(accountString);
                                        }
                                    }
                                }
                            }
                        }

                        if (accountStringList.isEmpty()) {
                            spinner.setVisibility(View.INVISIBLE);
                            no_account_linked.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            search_edit.setVisibility(View.INVISIBLE);

                        } else {
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getInfoStringList(accountStringList));
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(null);
                            spinner.setAdapter(spinnerAdapter);
                            search_edit.setVisibility(View.VISIBLE);

                        }


                        fields_loaded += 1;
                        if (fields_loaded == 1 && !accountStringList.isEmpty()) {
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
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentCollect fragmentCollect = FragmentCollect.newInstance(account_selected);
                fragmentTransaction.replace(R.id.fragment_container, fragmentCollect).addToBackStack(null).commit();

            }
        });

        search_edit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals("")) {

                    filterlist = getFilter(s.toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getInfoStringList(filterlist));
                    spinner.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getInfoStringList(accountStringList));
                    spinner.setAdapter(null);
                    spinner.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

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
                if (!filterlist.isEmpty()) {
                    account_selected = filterlist.get(position).getAccount();
                } else {
                    account_selected = accountStringList.get(position).getAccount();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (!filterlist.isEmpty()) {
                    account_selected = filterlist.get(0).getAccount();
                } else {
                    account_selected = accountStringList.get(0).getAccount();
                }
            }
        });
        return view;
    }

    public ArrayList<String> getInfoStringList(ArrayList<AccountString> accountStringList) {
        ArrayList<String> account_info_list = new ArrayList<>();
        for (AccountString accountString : accountStringList) {
            account_info_list.add(accountString.getInfo());
        }
        return account_info_list;
    }

    public ArrayList<AccountString> getFilter(CharSequence charSequence) {
        ArrayList<AccountString> filterResultsData = new ArrayList<>();
        ;
        if (charSequence == null || charSequence.length() == 0) {
            return null;
        } else {


            for (AccountString accountString : accountStringList) {
                if (accountString.getInfo().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                    filterResultsData.add(accountString);
                }
            }

        }

        return filterResultsData;
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