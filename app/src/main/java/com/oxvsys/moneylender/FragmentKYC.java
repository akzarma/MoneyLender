package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import static com.oxvsys.moneylender.MainActivity.database;


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

        View view = inflater.inflate(R.layout.fragment_fragment_kyc, container, false);

        final EditText name_field = (EditText) view.findViewById(R.id.name_field);
        final EditText aadhar_field = (EditText) view.findViewById(R.id.aadhar_field);
        final EditText occupation_field = (EditText) view.findViewById(R.id.occupation_field);
        final EditText mobile_field = (EditText) view.findViewById(R.id.mobile_field);
        final EditText dob_field = (EditText) view.findViewById(R.id.dob_field);
        final EditText address_field = (EditText) view.findViewById(R.id.address_field);
        Button save_button = (Button) view.findViewById(R.id.deposit_button);
        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                customers.updateChildren(id, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
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
