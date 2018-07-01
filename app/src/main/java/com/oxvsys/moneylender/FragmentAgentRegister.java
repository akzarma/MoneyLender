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

import com.google.firebase.database.DatabaseReference;

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
        // Required empty public constructor
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
        final EditText name_view = view.findViewById(R.id.agent_register_name);
        final EditText aadhar = view.findViewById(R.id.agent_register_aadhar);
        final EditText address_view = view.findViewById(R.id.agent_register_address);
        final EditText mobile_view = view.findViewById(R.id.agent_register_mobile);
        Button register_button = view.findViewById(R.id.agent_register_button);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unique_agent_id = "agent_" + mobile_view.getText().toString();

                DatabaseReference ref = database.getReference("agents").child(unique_agent_id);
                ref.child("mobile").setValue(mobile_view.getText().toString());
                ref.child("name").setValue(name_view.getText().toString());
                ref.child("aadhar").setValue(aadhar.getText().toString());
                ref.child("address").setValue(address_view.getText().toString());
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
