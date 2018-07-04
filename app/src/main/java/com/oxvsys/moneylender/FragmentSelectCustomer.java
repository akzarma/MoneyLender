package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.getData;
import static com.oxvsys.moneylender.MainActivity.logged_type;


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
//        final String agent_id = getData("user_id", getContext());
//        final String logged_type = getData("user_type", getContext());

        final List<Customer> customerList = new ArrayList<>();
        DatabaseReference customers_db_ref = database.getReference("customers");
        spinner = view.findViewById(R.id.customer_spinner);
        spinner.setAdapter(null);

        final Button next_button = view.findViewById(R.id.customer_next_button);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        Button next_button = view.findViewById(R.id.customer_next_button);
        if (logged_type.equals("admin")) {
            customers_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot customer : dataSnapshot.getChildren()) {
                        Customer customer1 = customer.getValue(Customer.class);
                        customer1.setId(customer.getKey());
                        customerList.add(customer1);
                    }
                    Collections.sort(customerList, new Comparator<Customer>() {
                        @Override
                        public int compare(Customer o1, Customer o2) {
                            return o1.getId().compareTo(o2.getId());
                        }
                    });
                    List<String> customers = new ArrayList<>();
                    for (Customer each : customerList) {
                        customers.add(each.getId() + " - " + each.getName().split(" ")[0]);
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, customers);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);
                    fields_loaded+=1;
                    if(fields_loaded==1){
                        next_button.setVisibility(View.VISIBLE);
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
                    customerList.clear();
                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    customer_selected = customerList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    customer_selected = customerList.get(0);
                }
            });
        }
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
