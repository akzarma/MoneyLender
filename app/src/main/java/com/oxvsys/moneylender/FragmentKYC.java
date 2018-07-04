package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.oxvsys.moneylender.HomeActivity.database;


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

    TextInputLayout name_til, aadhar_til, occupation_til, mobile_til, dob_til, address_til;

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
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentKYC.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentKYC newInstance(String param1, String param2) {
        FragmentKYC fragment = new FragmentKYC();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

        final View view = inflater.inflate(R.layout.fragment_fragment_kyc, container, false);

        final EditText name_field = view.findViewById(R.id.name_field);
        final EditText aadhar_field = view.findViewById(R.id.aadhar_field);
        final EditText occupation_field = view.findViewById(R.id.occupation_field);
        final EditText mobile_field = view.findViewById(R.id.mobile_field);
        final EditText dob_field = view.findViewById(R.id.dob_field);
        final EditText address_field = view.findViewById(R.id.address_field);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_black_24dp));

        name_til = view.findViewById(R.id.kyc_full_name_til);
        aadhar_til = view.findViewById(R.id.kyc_aadhar_til);
        occupation_til = view.findViewById(R.id.kyc_occupation_til);
        mobile_til = view.findViewById(R.id.kyc_mobile_til);
        dob_til = view.findViewById(R.id.kyc_dob_til);
        address_til = view.findViewById(R.id.kyc_address_til);

        dob_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
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
                }

                ;
            }
        });


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
                }
//                else address_til.setErrorEnabled(false);


                DatabaseReference customers = database.getReference("customers");
                String key = customers.push().getKey();
                Map<String, Object> id = new HashMap<>();
                Map<String, String> attrs = new HashMap<>();

                attrs.put("name", name_field.getText().toString());
                attrs.put("aadhar", aadhar_field.getText().toString());
                attrs.put("occupation", occupation_field.getText().toString());
                attrs.put("mobile", mobile_field.getText().toString());
                attrs.put("dob", dob_field.getText().toString());
                attrs.put("address", address_field.getText().toString());

                id.put(key, attrs);
                final RelativeLayout rl = view.findViewById(R.id.kyc_relative_layout);
                customers.updateChildren(id, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {

                            Snackbar snackbar = Snackbar
                                    .make(rl, "Customer Added Successfully!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            FragmentKYC fd = new FragmentKYC();
                            ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                    commit();

                        } else {
                            Toast.makeText(getContext(), "There is some error in saving the details.", Toast.LENGTH_SHORT).show();
                        }
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
