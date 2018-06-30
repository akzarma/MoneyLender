package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oxvsys.moneylender.LoginActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDailyLoan.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDailyLoan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDailyLoan extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Agent agent_selected;
    List<Agent> agentList = new ArrayList<>();
    //    String cust_id = "8";
    Spinner spinner;
    Long lastAccountNo;

    private OnFragmentInteractionListener mListener;

    public FragmentDailyLoan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentDailyLoan.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDailyLoan newInstance(Customer customer) {
        FragmentDailyLoan fragment = new FragmentDailyLoan();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_fragment_daily_loan, container, false);

        Button save_button = view.findViewById(R.id.grant_button_monthly);
        final EditText account_no = view.findViewById(R.id.account_number_monthly_grant);
        final EditText edit_amount = view.findViewById(R.id.amount_monthly_grant);
        final EditText edit_o_date = view.findViewById(R.id.start_date_monthly_grant);
        final EditText edit_c_date = view.findViewById(R.id.end_date_monthly_grant);
//        final EditText edit_roi = view.findViewById(R.id.roi_monthly_grant);
        final TextView prefix_account_no = view.findViewById(R.id.account_number_prefix_field);
        spinner = view.findViewById(R.id.agent_spinner);

        final Customer selected_customer = (Customer) getArguments().getSerializable(ARG_PARAM1);

        edit_o_date.setOnClickListener(new View.OnClickListener() {
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
                            String date_gen = selectedday + "/" + month + "/" + selectedyear;
                            edit_o_date.setText(date_gen);
                        }
                    }, mYear, mMonth, mDay);
                    mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                }

                ;
            }
        });

        edit_c_date.setOnClickListener(new View.OnClickListener() {
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
                            String date_gen = selectedday + "/" + month + "/" + selectedyear;
                            edit_c_date.setText(date_gen);
                        }
                    }, mYear, mMonth, mDay);
                    mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                }

                ;
            }
        });

        DatabaseReference myref = database.getReference("lastAccountNo");
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prefix_account_no.setText((dataSnapshot.getValue(Long.class) + 1) + " - ");
                lastAccountNo = dataSnapshot.getValue(Long.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference agent_db_ref = database.getReference("agents");

        agent_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot agent : dataSnapshot.getChildren()) {
                    Agent agent1 = agent.getValue(Agent.class);
                    agent1.setId(agent.getKey());
                    agentList.add(agent1);
                }
                Collections.sort(agentList, new Comparator<Agent>() {
                    @Override
                    public int compare(Agent o1, Agent o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });
                List<String> agents = new ArrayList<>();
                for (Agent each : agentList) {
                    agents.add(each.getId() + " - " + each.getName().split(" ")[0]);
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, agents);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                agent_selected = agentList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                agent_selected = agentList.get(0);
            }
        });
        save_button.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v) {
                final String final_account_no;
                if (account_no.getText().toString().equals("")) {
                    final_account_no = String.valueOf(lastAccountNo + 1);
                } else {
                    final_account_no = (lastAccountNo + 1) + "-" + account_no.getText().toString();
                }

                DatabaseReference agent_customer = database.getReference("agentAccount");
                agent_customer.child(agent_selected.getId()).child(final_account_no).setValue(selected_customer.getId());

                String o_date = edit_o_date.getText().toString();
                String c_date = edit_c_date.getText().toString();
                String amount = edit_amount.getText().toString();
//                String roi = edit_roi.getText().toString();

                DatabaseReference customers = database.getReference("customers").child(selected_customer.getId()).child("accounts");

                Map<String, String> account_number_details = new HashMap<>();

                account_number_details.put("no", final_account_no);
                account_number_details.put("amt", amount);
                account_number_details.put("o_date", o_date);
                account_number_details.put("c_date", c_date);
//                account_number_details.put("roi", roi);
                account_number_details.put("type", "monthly");

                Map<String, Object> map = new HashMap<>();
                map.put(final_account_no, account_number_details);

                customers.updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Log.d("monthly stuff", "onComplete: " + databaseError);
                        DatabaseReference lastAccountNo_ref = database.getReference("lastAccountNo");
                        lastAccountNo_ref.setValue(lastAccountNo + 1);
                    }
                });
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
