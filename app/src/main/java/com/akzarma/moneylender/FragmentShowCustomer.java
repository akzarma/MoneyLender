package com.akzarma.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.akzarma.moneylender.HomeActivity.database;
import static com.akzarma.moneylender.MainActivity.logged_agent;
import static com.akzarma.moneylender.MainActivity.logged_type;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentShowCustomer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentShowCustomer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentShowCustomer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int fields_loaded = 0;
    AdapterCustomer mAdapter;
    String account_type = "0";
    RecyclerView recycler;
    List<Customer> customerList = new ArrayList<>();
    List<Customer> filtered_customerList = new ArrayList<>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;
    private boolean agent_filtered = false;

    public FragmentShowCustomer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentShowCustomer.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentShowCustomer newInstance(String acc_type) {
        FragmentShowCustomer fragment = new FragmentShowCustomer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, acc_type);
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
        // Inflate the layout for this fragment
        fields_loaded = 0;
        View view = inflater.inflate(R.layout.fragment_show_customer, container, false);
        progressBar = view.findViewById(R.id.show_cust_progress);
        progressBar.setVisibility(View.VISIBLE);

        assert getArguments() != null;
        account_type = getArguments().getString(ARG_PARAM1);
        recycler = view.findViewById(R.id.show_cust_recycler);

//        final String agent_id = getData("user_id", getContext());
//        final String logged_type = getData("user_type", getContext());

        final EditText search_edit = view.findViewById(R.id.search_edit);
        search_edit.setText("");
        Button search_button = view.findViewById(R.id.search_button);
        final FloatingActionButton fab = (FloatingActionButton) Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        DatabaseReference customers_db_ref = database.getReference("customers");
        customers_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot customer : dataSnapshot.getChildren()) {
                    Customer customer1 = customer.getValue(Customer.class);
                    assert customer1 != null;
                    customer1.setId(customer.getKey());
                    boolean contains_monthly = false;
                    boolean contains_daily = false;
                    ArrayList<Account> accounts = new ArrayList<>();
                    for (DataSnapshot each_account : customer.child("accounts").getChildren()) {
                        Account account = new Account(each_account.getValue());
                        account.setNo(each_account.getKey());
                        accounts.add(account);
                        if (account.getType().equals("1")) {
                            contains_monthly = true;
                        }
                        if (account.getType().equals("0")) {
                            contains_daily = true;
                        }
                    }
                    customer1.setAccounts1(accounts);
                    if (!containsId(customerList, customer1.getId())) {
                        if (account_type.equals("1") && contains_monthly) {
                            customerList.add(customer1);
                        } else if (account_type.equals("0") && (contains_daily || !contains_monthly)) {
                            customerList.add(customer1);
                        }
                    }
                }
                //customerList and customers list are in sync do not operate them individually

                fields_loaded += 1;
                if (fields_loaded == 1) {
                    showAdapter();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

//        DatabaseReference inactive_customers_db_ref = database.getReference("inactive").child("customers");
//
//        inactive_customers_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot customer : dataSnapshot.getChildren()) {
//                    Customer customer1 = customer.getValue(Customer.class);
//                    assert customer1 != null;
//                    customer1.setId(customer.getKey());
//                    boolean contains_monthly = false;
//                    boolean contains_daily = false;
//                    ArrayList<Account> accounts = new ArrayList<>();
//                    for (DataSnapshot each_account : customer.child("accounts").getChildren()) {
//                        Account account = new Account(each_account.getValue());
//                        account.setNo(each_account.getKey());
//                        accounts.add(account);
//                        if (account.getType().equals("1")) {
//                            contains_monthly = true;
//                        }
//                        if (account.getType().equals("0")) {
//                            contains_daily = true;
//                        }
//                    }
//                    customer1.setAccounts1(accounts);
//                    if (!containsId(customerList
//                            , customer1.getId())) {
//                        if (account_type.equals("1") && contains_monthly) {
//                            customerList.add(customer1);
//                        } else if (account_type.equals("0") && contains_daily) {
//                            customerList.add(customer1);
//                        }
//                    }
//                }
//
//                //customerList and customers list are in sync do not operate them individually
//
//                fields_loaded += 1;
//                if (fields_loaded == 2) {
//                    showAdapter();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtered_customerList.clear();
                progressBar.setVisibility(View.VISIBLE);
                String search_key = search_edit.getText().toString();
                for(Customer customer: customerList){
                    if((customer.getName()+" "+customer.getId() + " "+ customer.getAddress()).toLowerCase().contains(search_key.toLowerCase())){
                        filtered_customerList.add(customer);
                    }
                }
                mAdapter = new AdapterCustomer(filtered_customerList, getContext(), getFragmentManager());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());
                recycler.setAdapter(mAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    private void showAdapter() {
        if (logged_type.equals("agent")) {
            DatabaseReference agent_account_db_ref = database.getReference("agentAccount").child(logged_agent);
            final List<Account> accountList = new ArrayList<>();
            agent_account_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot account : dataSnapshot.getChildren()) {
                        Account account1 = new Account();
                        account1.setNo(account.getKey());
                        accountList.add(account1);
                    }
                    List<Integer> cust_idx_to_remove = new ArrayList<>();
                    for (Customer each_cust : customerList) {
                        List<Integer> acc_idx_to_remove = new ArrayList<>();
                        for (Account each_acc : each_cust.getAccounts1()) {
                            boolean remove = true;
                            for (Account need_acc : accountList) {
                                if (each_acc.getNo().equals(need_acc.getNo())) {
                                    remove = false;
                                    break;
                                }
                            }
                            if (remove) {
                                acc_idx_to_remove.add(each_cust.getAccounts1().indexOf(each_acc));
                            }
                        }
                        Collections.sort(acc_idx_to_remove, new Comparator<Integer>() {
                            public int compare(Integer a, Integer b) {
                                //todo: handle null
                                return b.compareTo(a);
                            }
                        });
                        for (int idx : acc_idx_to_remove) {
                            if (idx != -1)
                                each_cust.getAccounts1().remove(idx);
                        }
                        if (each_cust.getAccounts1().isEmpty()) {
                            cust_idx_to_remove.add(customerList.indexOf(each_cust));
                        }

                    }

                    Collections.sort(cust_idx_to_remove, new Comparator<Integer>() {
                        public int compare(Integer a, Integer b) {
                            //todo: handle null
                            return b.compareTo(a);
                        }
                    });
                    for (int idx : cust_idx_to_remove) {
                        if (idx != -1)
                            customerList.remove(idx);
                    }

                    mAdapter = new AdapterCustomer(customerList, getContext(), getFragmentManager());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recycler.setLayoutManager(mLayoutManager);
                    recycler.setItemAnimator(new DefaultItemAnimator());
                    recycler.setAdapter(mAdapter);
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {

            mAdapter = new AdapterCustomer(customerList, getContext(), getFragmentManager());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(mLayoutManager);
            recycler.setItemAnimator(new DefaultItemAnimator());
            recycler.setAdapter(mAdapter);
            progressBar.setVisibility(View.INVISIBLE);
        }
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

    public boolean containsId(List<Customer> list, String id) {
        for (Customer each : list) {
            if (each.getId().equals(id))
                return true;
        }
        return false;
    }
}
