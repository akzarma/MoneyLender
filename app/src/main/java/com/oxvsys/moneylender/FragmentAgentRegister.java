package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import static com.oxvsys.moneylender.HomeActivity.database;


public class FragmentAgentRegister extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText name_view;

    private OnFragmentInteractionListener mListener;

    public FragmentAgentRegister() {

    }

    public static FragmentAgentRegister newInstance(String param1, String param2) {
        FragmentAgentRegister fragment = new FragmentAgentRegister();
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
        View view = inflater.inflate(R.layout.fragment_agent_register, container, false);
        final TextInputLayout name_til , aadhaar_til , mobile_til , address_til;

        name_til = view.findViewById(R.id.agent_name_til);
        aadhaar_til = view.findViewById(R.id.agent_aadhaar_til);
        mobile_til = view.findViewById(R.id.agent_mobile_til);
        address_til = view.findViewById(R.id.agent_address_til);

        final EditText name_view = view.findViewById(R.id.agent_register_name);
        final EditText aadhar = view.findViewById(R.id.agent_register_aadhar);
        final EditText address_view = view.findViewById(R.id.agent_register_address);
        final EditText mobile_view = view.findViewById(R.id.agent_register_mobile);
        Button register_button = view.findViewById(R.id.agent_register_button);

        final RelativeLayout rl = view.findViewById(R.id.agent_register_relative_layout);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name_view.getText().toString().equals("")){
                    name_til.setError("Name required.");
                }

                if (aadhar.getText().toString().equals("")){
                    name_til.setErrorEnabled(false);
                    aadhaar_til.setError("Aadhaar required.");
                }

                if (address_view.getText().toString().equals("")){
                    aadhaar_til.setErrorEnabled(false);
                    address_til.setError("Address required.");
                }

                if (mobile_view.getText().toString().equals("")){
                    address_til.setErrorEnabled(false);
                    mobile_til.setError("Mobile required.");
                }else mobile_til.setErrorEnabled(false);

                String unique_agent_id = "agent_" + mobile_view.getText().toString();

                DatabaseReference ref = database.getReference("agents").child(unique_agent_id);

                HashMap<String , String> attrs = new HashMap<>();
                HashMap<String , Object> id = new HashMap<>();

                attrs.put("mobile" , mobile_view.getText().toString());
                attrs.put("name" , name_view.getText().toString());
                attrs.put("aadhaar" , aadhar.getText().toString());
                attrs.put("address" , address_view.getText().toString());

                id.put(unique_agent_id , attrs);
                ref.updateChildren(id, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Snackbar snackbar = Snackbar
                                .make(rl, "Agent added successfully!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentKYC fd = new FragmentKYC();
                        ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                commit();
                    }
                });

//                ref.child("mobile").setValue(mobile_view.getText().toString());
//                ref.child("name").setValue(name_view.getText().toString());
//                ref.child("aadhar").setValue(aadhar.getText().toString());
//                ref.child("address").setValue(address_view.getText().toString());



            }
        });


        return view;
    }

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
