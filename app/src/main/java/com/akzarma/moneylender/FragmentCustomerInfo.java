package com.akzarma.moneylender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.akzarma.moneylender.HomeActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCustomerInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCustomerInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCustomerInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    Customer selected_customer;


    private OnFragmentInteractionListener mListener;

    public FragmentCustomerInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentCustomerInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCustomerInfo newInstance(Customer customer) {
        FragmentCustomerInfo fragment = new FragmentCustomerInfo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, customer);
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
        View view = inflater.inflate(R.layout.fragment_customer_info, container, false);
        assert getArguments() != null;
        selected_customer = (Customer) getArguments().getSerializable(ARG_PARAM1);
        RecyclerView recyclerView = view.findViewById(R.id.accounts_cust_recycler);
        final FloatingActionButton fab = (FloatingActionButton) Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        TextView name_field = view.findViewById(R.id.name_field);
        TextView g_name_field = view.findViewById(R.id.g_name_field);
        TextView aadhar_field = view.findViewById(R.id.aadhar_field);
        TextView dob_field = view.findViewById(R.id.dob_field);
        TextView id_field = view.findViewById(R.id.id_field);
        TextView occupation_field = view.findViewById(R.id.occupation_field);
        TextView address_field = view.findViewById(R.id.address_field);
        TextView g_address_field = view.findViewById(R.id.g_address_field);
        TextView mobile_field = view.findViewById(R.id.mobile_field);
        TextView g_mobile_field = view.findViewById(R.id.g_mobile_field);
        ImageView dialer_view = view.findViewById(R.id.dialer_view);
        ImageView g_dialer_view = view.findViewById(R.id.g_dialer_view);
        Button edit_cust_button = view.findViewById(R.id.edit_cust_button);
        Button delete_customer = view.findViewById(R.id.delete_customer);

        name_field.setText(selected_customer.getName());
        aadhar_field.setText(selected_customer.getAadhar());
        dob_field.setText(selected_customer.getDOB());
        id_field.setText(selected_customer.getId());
        occupation_field.setText(selected_customer.getOccupation());
        address_field.setText(selected_customer.getAddress());
        mobile_field.setText(selected_customer.getMobile());
        g_name_field.setText(selected_customer.getG_name());
        g_mobile_field.setText(selected_customer.getG_mobile());
        g_address_field.setText(selected_customer.getG_address());

        edit_cust_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                FragmentKYC far = FragmentKYC.newInstance(selected_customer);
                fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
                        commit();
            }
        });

        delete_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm?")
                        .setMessage("Are you sure you want to delete the customer?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref = database.getReference("customers").child(selected_customer.getId());
                        Map<String,Object> invalid = new HashMap<>();
                        invalid.put("invalid","true");
                        ref.updateChildren(invalid, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError == null) {

                                    assert getFragmentManager() != null;
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    FragmentShowCustomer fd = FragmentShowCustomer.newInstance("0");
                                    ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                            commit();
                                }else {

                                    Toast.makeText(getContext(),"Error deleting the customer. Please try again later",Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                    }
                });
                builder.show();
            }
        });

        dialer_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + selected_customer.getMobile()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        g_dialer_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + selected_customer.getG_mobile()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });


        AdapterAccount mAdapter = new AdapterAccount(selected_customer, getFragmentManager(), getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


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
