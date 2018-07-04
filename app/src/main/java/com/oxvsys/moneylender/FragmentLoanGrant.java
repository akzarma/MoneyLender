package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oxvsys.moneylender.HomeActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentLoanGrant.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentLoanGrant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLoanGrant extends Fragment {
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
    String account_type_selected;
    String selected_days;
    int fields_loaded = 0;

    private OnFragmentInteractionListener mListener;

    public FragmentLoanGrant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentLoanGrant.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLoanGrant newInstance(Customer customer) {
        FragmentLoanGrant fragment = new FragmentLoanGrant();
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
        fields_loaded = 0;

        View view = inflater.inflate(R.layout.fragment_loan_grant, container, false);

        final Button save_button = view.findViewById(R.id.grant_button_monthly);
        final EditText account_no = view.findViewById(R.id.account_number_monthly_grant);
        final EditText edit_amount = view.findViewById(R.id.amount_monthly_grant);
        final EditText edit_o_date = view.findViewById(R.id.start_date_monthly_grant);
        final EditText edit_c_date = view.findViewById(R.id.end_date_monthly_grant);
        final EditText edit_roi = view.findViewById(R.id.rate_of_interest_grant);
        final TextView prefix_account_no = view.findViewById(R.id.account_number_prefix_field);
        spinner = view.findViewById(R.id.agent_spinner);
        Spinner account_type_spinner = view.findViewById(R.id.account_type_field);
        final EditText file_duration_field = view.findViewById(R.id.file_duration_monthly_grant);
        final EditText additional_info_monthly_grant = view.findViewById(R.id.additional_info_monthly_grant);
        final TextInputLayout loan_grant_roi_til = view.findViewById(R.id.loan_grant_roi_til);
        final Spinner payment_duration_spinner = view.findViewById(R.id.loan_grant_daily_payment_options_spinner);
        final TextInputLayout loan_grant_duration_til = view.findViewById(R.id.loan_grant_duration_til);
        final EditText months_field = view.findViewById(R.id.file_duration_monthly_grant);
        final EditText lf_number_field = view.findViewById(R.id.lf_number_field);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));

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
                            String date_gen = selectedday + "-" + month + "-" + selectedyear;
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
                            int month = selectedmonth + 1;
                            String date_gen = selectedday + "-" + month + "-" + selectedyear;
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
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prefix_account_no.setText((dataSnapshot.getValue(Long.class) + 1) + " - ");
                lastAccountNo = dataSnapshot.getValue(Long.class);
                fields_loaded+=1;
                if(fields_loaded==2){
                    save_button.setVisibility(View.VISIBLE);
                }
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
                fields_loaded+=1;
                if(fields_loaded==2){
                    save_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        payment_duration_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    selected_days = "100";
                }else if(position == 1){
                    selected_days = "200";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        account_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    account_type_selected = "0";  //daily basis
                    loan_grant_roi_til.setVisibility(View.INVISIBLE);
                    loan_grant_duration_til.setVisibility(View.INVISIBLE);
                    payment_duration_spinner.setVisibility(View.VISIBLE);
                }
                else if(position == 1){
                    account_type_selected = "1"; //monthly
                    loan_grant_roi_til.setVisibility(View.VISIBLE);
                    loan_grant_duration_til.setVisibility(View.VISIBLE);
                    payment_duration_spinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                if( edit_amount.getText().toString().length() == 0 ) {
                    edit_amount.setError("Amount is required!");
                    return;
                }else if( file_duration_field.getText().toString().length() == 0 ) {
                    file_duration_field.setError("Duration is required!");
                    return;
                }else if( edit_o_date.getText().toString().length() == 0 ) {
                    edit_o_date.setError("Opening date is required!");
                    return;
                }else if( edit_c_date.getText().toString().length() == 0 ) {
                    edit_c_date.setError("Closing date is required!");
                    return;
                }
                if(account_type_selected.equals("1")){
                    if(edit_roi.getText().toString().length()==0){
                        edit_roi.setError("Rate of interest is required for Monthly basis account!");
                        return;
                    }
                }
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
                String roi = edit_roi.getText().toString();
                String info = additional_info_monthly_grant.getText().toString();
                String lf_number = lf_number_field.getText().toString();

                DatabaseReference customers = database.getReference("customers").child(selected_customer.getId()).child("accounts");

                Map<String, String> account_number_details = new HashMap<>();

//                account_number_details.put("no", final_account_no);
//                account_number_details.put("customer", selected_customer.getId());
                account_number_details.put("amt", amount);
                account_number_details.put("o_date", o_date);
                account_number_details.put("c_date", c_date);
                account_number_details.put("info", info);
                if(account_type_selected.equals("1")) {
                    account_number_details.put("roi", roi);
                    account_number_details.put("duration", months_field.getText().toString());
                }else if(account_type_selected.equals("0")){
                    account_number_details.put("duration", selected_days);
                }
                account_number_details.put("type", account_type_selected);
                account_number_details.put("lf_no", lf_number);

                Map<String, Object> map = new HashMap<>();
                map.put(final_account_no, account_number_details);

                customers.updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Log.d("monthly stuff", "onComplete: " + databaseError);
                        DatabaseReference lastAccountNo_ref = database.getReference("lastAccountNo");
                        lastAccountNo_ref.setValue(lastAccountNo + 1);

                        //Update accountType
                        DatabaseReference account_type_db_ref = database.getReference("accountType");
                        Map<String, Object> account_type_map = new HashMap<>();
                        account_type_map.put(final_account_no, account_type_selected);
                        account_type_db_ref.updateChildren(account_type_map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Log.d("accountType query: ", "data updated.");

                            }
                        });


                        //Update accountCustomer
//                        DatabaseReference account_customer_db_ref = database.getReference("accountCustomer");
//                        Map<String, Object> account_customer_map = new HashMap<>();
//                        account_type_map.put(final_account_no, selected_customer.getId());
//                        account_customer_db_ref.updateChildren(account_customer_map, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                Log.d("accountCustomer query: ", "data updated.");
//                            }
//                        });
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
