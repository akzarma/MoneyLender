package com.akzarma.moneylender;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.akzarma.moneylender.HomeActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentKYC.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentKYC#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentKYC extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextInputLayout name_til, aadhar_til, occupation_til, mobile_til, dob_til, address_til,
            g_name_til, g_address_til, g_mobile_til;
    Long lastCustomerId = -1L;
    Customer selected_customer_to_update;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public FragmentKYC() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FragmentKYC.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentKYC newInstance(Customer param1) {
        FragmentKYC fragment = new FragmentKYC();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_kyc, container, false);

        final EditText name_field = view.findViewById(R.id.name_field);
        final EditText g_name_field = view.findViewById(R.id.guarantor_name_field);
        final EditText aadhar_field = view.findViewById(R.id.aadhar_field);
        final EditText occupation_field = view.findViewById(R.id.occupation_field);
        final EditText mobile_field = view.findViewById(R.id.mobile_field);
        final EditText g_mobile_field = view.findViewById(R.id.g_mobile_field);
        final EditText dob_field = view.findViewById(R.id.dob_field);
        final EditText address_field = view.findViewById(R.id.address_field);
        final EditText g_address_field = view.findViewById(R.id.g_address_field);
        final EditText customer_id_field = view.findViewById(R.id.customer_id_field);

        final FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_save_black_24dp));
        fab.setVisibility(View.INVISIBLE);

        name_til = view.findViewById(R.id.kyc_full_name_til);
        g_name_til = view.findViewById(R.id.guarantor_full_name_til);
        aadhar_til = view.findViewById(R.id.kyc_aadhar_til);
        occupation_til = view.findViewById(R.id.kyc_occupation_til);
        mobile_til = view.findViewById(R.id.kyc_mobile_til);
        g_mobile_til = view.findViewById(R.id.g_mobile_til);
        dob_til = view.findViewById(R.id.kyc_dob_til);
        address_til = view.findViewById(R.id.kyc_address_til);
        g_address_til = view.findViewById(R.id.g_address_til);

        if (getArguments() != null) {
            selected_customer_to_update = (Customer) getArguments().getSerializable(ARG_PARAM1);
        }

        final DatabaseReference cust_id_ref = database.getReference("lastCustomerId");
        if (selected_customer_to_update == null) {

            cust_id_ref.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lastCustomerId = Long.parseLong(String.valueOf(dataSnapshot.getValue()));
                    customer_id_field.setText("C" + String.valueOf(lastCustomerId + 1));
                    fab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    fab.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            customer_id_field.setText(selected_customer_to_update.getId());
            name_field.setText(selected_customer_to_update.getName());
            aadhar_field.setText(selected_customer_to_update.getAadhar());
            occupation_field.setText(selected_customer_to_update.getOccupation());
            mobile_field.setText(selected_customer_to_update.getMobile());
            dob_field.setText(selected_customer_to_update.getDOB());
            address_field.setText(selected_customer_to_update.getAddress());
            g_name_field.setText(selected_customer_to_update.getG_name());
            g_mobile_field.setText(selected_customer_to_update.getG_mobile());
            g_address_field.setText(selected_customer_to_update.getG_address());
            fab.setVisibility(View.VISIBLE);
        }

        dob_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        int month = selectedmonth + 1;
                        String date_gen = selectedday + "-" + month + "-" + selectedyear;
                        dob_field.setText(date_gen);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select DOB");
                mDatePicker.show();


                ;
            }
        });
        final RelativeLayout rl = view.findViewById(R.id.kyc_relative_layout);

//        Button save_button = view.findViewById(R.id.deposit_button);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (name_field.getText().toString().isEmpty()) {
                    name_til.setError("Name is required.");
                    return;
                } else if (aadhar_field.getText().toString().isEmpty()) {
                    name_til.setErrorEnabled(false);
                    aadhar_til.setError("Aadhar is required.");
                    return;
                }
//                else aadhar_til.setErrorEnabled(false);
                else if (aadhar_field.getText().toString().length() != 12) {
                    aadhar_til.setErrorEnabled(false);
                    aadhar_til.setError("Invalid Aadhaar ID");
                    return;
                }
//                else aadhar_til.setErrorEnabled(false);
                else if (occupation_field.getText().toString().isEmpty()) {
                    aadhar_til.setErrorEnabled(false);
                    occupation_til.setError("Occupation is required.");
                    return;
                }
//                else occupation_til.setErrorEnabled(false);
                else if (mobile_field.getText().toString().isEmpty()) {
                    occupation_til.setErrorEnabled(false);
                    mobile_til.setError("Mobile is required.");
                    return;
                }
//                else mobile_til.setErrorEnabled(false);
                else if (dob_field.getText().toString().isEmpty()) {
                    mobile_til.setErrorEnabled(false);
                    dob_til.setError("DOB is required.");
                    return;
                }
//                else dob_til.setErrorEnabled(false);
                else if (address_field.getText().toString().isEmpty()) {
                    dob_til.setErrorEnabled(false);
                    address_til.setError("Address is required.");
                    return;
                } else if (g_address_field.getText().toString().isEmpty()) {
                    address_til.setErrorEnabled(false);
                    g_address_til.setError("Address is required.");
                    return;
                } else if (g_mobile_field.getText().toString().isEmpty()) {
                    g_address_til.setErrorEnabled(false);
                    g_mobile_til.setError("Mobile is required.");
                    return;
                } else if (g_name_field.getText().toString().isEmpty()) {
                    g_mobile_til.setErrorEnabled(false);
                    g_name_til.setError("Name is required.");
                    return;
                }
                fab.setVisibility(View.INVISIBLE);
//                else address_til.setErrorEnabled(false);

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Add Customer?")
                        .setMessage(String.valueOf(name_field.getText()));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selected_customer_to_update == null) {
                            DatabaseReference customers = database.getReference("customers");
                            String key = customer_id_field.getText().toString();
                            Map<String, Object> id = new HashMap<>();
                            Map<String, Object> attrs = new HashMap<>();
                            String name = name_field.getText().toString();
                            attrs.put("name", name.substring(0, 1).toUpperCase() + name.substring(1));
                            attrs.put("aadhar", aadhar_field.getText().toString());
                            attrs.put("occupation", occupation_field.getText().toString());
                            attrs.put("mobile", mobile_field.getText().toString());
                            attrs.put("dob", dob_field.getText().toString());
                            attrs.put("address", address_field.getText().toString());
                            attrs.put("g_name", g_name_field.getText().toString());
                            attrs.put("g_mobile", g_mobile_field.getText().toString());
                            attrs.put("g_address", g_address_field.getText().toString());

                            id.put(key, attrs);

                            customers.updateChildren(id, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Snackbar snackbar = Snackbar
                                                .make(rl, "Customer Added Successfully!", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        cust_id_ref.setValue(lastCustomerId + 1);
                                        assert getFragmentManager() != null;
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        FragmentKYC fd = new FragmentKYC();
                                        ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                                commit();

                                    } else {
                                        Toast.makeText(getContext(), "There is some error in saving the details.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            DatabaseReference cust_ref = database.getReference("customers").child(selected_customer_to_update.getId());
                            cust_ref.child("name").setValue(name_field.getText().toString());
                            cust_ref.child("aadhar").setValue(aadhar_field.getText().toString());
                            cust_ref.child("occupation").setValue(occupation_field.getText().toString());
                            cust_ref.child("mobile").setValue(mobile_field.getText().toString());
                            cust_ref.child("dob").setValue(dob_field.getText().toString());
                            cust_ref.child("address").setValue(address_field.getText().toString());
                            cust_ref.child("g_name").setValue(g_name_field.getText().toString());
                            cust_ref.child("g_mobile").setValue(g_mobile_field.getText().toString());
                            cust_ref.child("g_address").setValue(g_address_field.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar snackbar = Snackbar
                                            .make(rl, "Customer Updated Successfully!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    assert getFragmentManager() != null;
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    FragmentKYC fd = new FragmentKYC();
                                    ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                            commit();
                                }
                            });


                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"Cancelled",Toast.LENGTH_LONG).show();
                    }
                });



//                customers.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot id) {
//                        // This method is called once with the initial value and again
//                        // whenever data at this location is updated.
//                        for(DataSnapshot each_id:id.getChildren()){
////                            for(DataSnapshot attr:each_id.getChildren()){
////                                String value = attr.getValue(String.class);
////                                Log.d("DataSnapshot customers ", "Value is: " + value);
////                            }
//
//                            Log.d("DataSnapshot customers ", "Value is: " + each_id.toString());
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        // Failed to read value
//                        Log.w(TAG, "Failed to read value.", error.toException());
//                    }
//                });

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
