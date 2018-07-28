package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static com.oxvsys.moneylender.HomeActivity.database;


public class FragmentAgentRegister extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_register, container, false);
        final TextInputLayout name_til, aadhaar_til, mobile_til, address_til;

        name_til = view.findViewById(R.id.agent_name_til);
        aadhaar_til = view.findViewById(R.id.agent_aadhaar_til);
        mobile_til = view.findViewById(R.id.agent_mobile_til);
        address_til = view.findViewById(R.id.agent_address_til);

        final EditText name_view = view.findViewById(R.id.agent_register_name);
        final EditText aadhar = view.findViewById(R.id.agent_register_aadhar);
        final EditText address_view = view.findViewById(R.id.agent_register_address);
        final EditText mobile_view = view.findViewById(R.id.agent_register_mobile);
//        Button register_button = view.findViewById(R.id.agent_register_button);

        final RelativeLayout rl = view.findViewById(R.id.agent_register_relative_layout);

        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_save_black_24dp));
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name_view.getText().toString().equals("")) {
                    name_til.setError("Name required.");
                    return;
                }

                if (aadhar.getText().toString().equals("")) {
                    name_til.setErrorEnabled(false);
                    aadhaar_til.setError("Aadhaar required.");
                    return;
                }

                if (address_view.getText().toString().equals("")) {
                    aadhaar_til.setErrorEnabled(false);
                    address_til.setError("Address required.");
                    return;
                }

                if (mobile_view.getText().toString().length() != 10) {
                    address_til.setErrorEnabled(false);
                    mobile_til.setError("Enter a valid Mobile No.");
                    return;
                } else {
                    mobile_til.setErrorEnabled(false);
                }

                final String unique_agent_id = mobile_view.getText().toString();


                final DatabaseReference ref = database.getReference("agents");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(unique_agent_id)) {
                            address_til.setErrorEnabled(false);
                            mobile_til.setError("User with this mobile already exists.");
                        } else {
                            HashMap<String, String> attrs = new HashMap<>();
                            HashMap<String, Object> id = new HashMap<>();

                            attrs.put("mobile", mobile_view.getText().toString());
                            attrs.put("name", name_view.getText().toString());
                            attrs.put("aadhaar", aadhar.getText().toString());
                            attrs.put("address", address_view.getText().toString());

                            id.put(unique_agent_id, attrs);
                            ref.updateChildren(id, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    DatabaseReference users_ref = database.getReference("users").child(unique_agent_id);
                                    HashMap<String, Object> users_attrs = new HashMap<>();
                                    users_attrs.put("type", "agent");
                                    users_attrs.put("pwd", "1234");
                                    users_ref.updateChildren(users_attrs, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            Snackbar snackbar = Snackbar
                                                    .make(rl, "Agent added with Password 1234", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            assert getFragmentManager() != null;
                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            FragmentAgentRegister fd = new FragmentAgentRegister();
                                            ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                                    commit();
                                        }
                                    });

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
