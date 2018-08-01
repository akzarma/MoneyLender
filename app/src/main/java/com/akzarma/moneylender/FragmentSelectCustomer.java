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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.akzarma.moneylender.HomeActivity.database;
import static com.akzarma.moneylender.MainActivity.logged_type;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSelectCustomer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSelectCustomer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSelectCustomer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Spinner spinner;
    Customer customer_selected = new Customer();
    ArrayList<Customer> customerList = new ArrayList<>();
    ArrayList<Customer> filterList = new ArrayList<>();
    int fields_loaded = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentSelectCustomer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSelectCustomer.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSelectCustomer newInstance(String param1, String param2) {
        FragmentSelectCustomer fragment = new FragmentSelectCustomer();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_select_customer, container, false);
        final EditText search_edit = view.findViewById(R.id.search_edit);
        search_edit.setText("");
        search_edit.setVisibility(View.INVISIBLE);
        final ProgressBar progressBar = view.findViewById(R.id.select_customer_progress);
        progressBar.setVisibility(View.VISIBLE);

//        final String agent_id = getData("user_id", getContext());
//        final String logged_type = getData("user_type", getContext());


        DatabaseReference customers_db_ref = database.getReference("customers");

        spinner = view.findViewById(R.id.customer_spinner);
        spinner.setAdapter(null);
        filterList.clear();
        customerList.clear();

//        final Button next_button = view.findViewById(R.id.customer_next_button);
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));
        fab.setVisibility(View.INVISIBLE);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        Button next_button = view.findViewById(R.id.customer_next_button);
        if (logged_type.equals("admin")) {
            customers_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot customer : dataSnapshot.getChildren()) {
                        Customer customer1 = customer.getValue(Customer.class);
                        customer1.setId(customer.getKey());
                        if (!containsId(customerList, customer1.getId())) {
                            customerList.add(customer1);
                        }
                    }
                    //customerList and customers list are in sync do not operate them individually

                    fields_loaded += 1;
                    if (fields_loaded == 2) {
                        fab.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getInfoStringList(customerList));
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);
                        search_edit.setVisibility(View.VISIBLE);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            DatabaseReference inactive_customers_db_ref = database.getReference("inactive").child("customers");

            inactive_customers_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot customer : dataSnapshot.getChildren()) {
                        Customer customer1 = customer.getValue(Customer.class);
                        customer1.setId(customer.getKey());
                        if (!containsId(customerList, customer1.getId())) {
                            customerList.add(customer1);
                        }
                    }

                    fields_loaded += 1;
                    if (fields_loaded == 2) {
                        if (!customerList.isEmpty())
                            fab.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getInfoStringList(customerList));
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);
                        search_edit.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentLoanGrant fragmentLoanGrant = FragmentLoanGrant.newInstance(customer_selected);
                    fragmentTransaction.replace(R.id.fragment_container, fragmentLoanGrant).addToBackStack(null).commit();

                }
            });

            search_edit.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().equals("")) {
                        filterList = getFilter(s.toString());
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getInfoStringList(filterList));
                        spinner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getInfoStringList(customerList));
                        spinner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!filterList.isEmpty()) {

                        customer_selected = filterList.get(position);
                    } else {

                        customer_selected = customerList.get(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    if (!filterList.isEmpty()) {

                        customer_selected = filterList.get(0);
                    } else {

                        customer_selected = customerList.get(0);
                    }
                }
            });
        }


        return view;
    }

    public ArrayList<String> getInfoStringList(ArrayList<Customer> customers) {
        ArrayList<String> cust_info_list = new ArrayList<>();
        for (Customer customer : customers) {
            int name_len = customer.getName().split(" ").length;
            String cust_name = "No Name";
            if (name_len > 1) {
                cust_name = customer.getName().split(" ")[0] + ' ' +
                        customer.getName().split(" ")[name_len - 1];
            } else if (name_len == 1) {
                cust_name = customer.getName().split(" ")[0];
            }
            cust_info_list.add(cust_name + " (" + customer.getId() + ")");
        }
        return cust_info_list;
    }

    public ArrayList<Customer> getFilter(CharSequence charSequence) {
        ArrayList<Customer> filterResultsData = new ArrayList<>();

        if (charSequence == null || charSequence.length() == 0) {
            return null;
        } else {


            for (Customer customer : customerList) {
                ArrayList<Customer> one_item = new ArrayList<>();
                one_item.add(customer);
                String info = getInfoStringList(one_item).get(0);
                if (info.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                    filterResultsData.add(customer);
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
