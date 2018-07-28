package com.oxvsys.moneylender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.oxvsys.moneylender.HomeActivity.database;
import static com.oxvsys.moneylender.MainActivity.getData;
import static com.oxvsys.moneylender.MainActivity.saveData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetPassword.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetPassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetPassword extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SetPassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetPassword.
     */
    // TODO: Rename and change types and number of parameters
    public static SetPassword newInstance(String param1, String param2) {
        SetPassword fragment = new SetPassword();
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
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_set_password, container, false);
        final EditText curr_pass_field = view.findViewById(R.id.curr_pass_field);
        final TextInputLayout curr_pass_til = view.findViewById(R.id.curr_pass_til);
        final TextInputLayout new_pass_til = view.findViewById(R.id.new_pass_til);
        final TextInputLayout new_pass1_til = view.findViewById(R.id.new_pass1_til);
        final EditText new_pass_field = view.findViewById(R.id.new_pass_field);
        final EditText new_pass1_field = view.findViewById(R.id.new_pass1_field);


        final String logged_user = getData("user_id", getContext());


        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new_pass_field.getText().toString().equals(new_pass1_field.getText().toString())) {
                    new_pass_til.setError("Password didn't match");
                    new_pass1_til.setError("Password didn't match");
                } else if (new_pass_field.getText().toString().length() < 6) {
                    new_pass_til.setError("Password length must be greater than 6");
                } else {
                    new_pass_til.setErrorEnabled(false);
                    new_pass1_til.setErrorEnabled(false);

                    final DatabaseReference users_ref = database.getReference("users").child(logged_user).child("pwd");
                    users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String get_pwd = String.valueOf(dataSnapshot.getValue());
                            if (!curr_pass_field.getText().toString().equals(get_pwd)) {
                                curr_pass_til.setError("Wrong Password");
                            } else {

                                curr_pass_til.setErrorEnabled(false);
                                users_ref.setValue(new_pass_field.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getContext(), "Password changed successfully!", Toast.LENGTH_LONG).show();
                                        saveData("user_pwd", new_pass_field.getText().toString(), getContext());
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
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
