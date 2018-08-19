package com.akzarma.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

import static com.akzarma.moneylender.HomeActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentLoanGrant.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentLoanGrant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLoanGrant extends Fragment implements GrantLoanDialogFragment.NoticeDialogListener {
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
    String selected_days = "100";
    int fields_loaded = 0;
    Calendar curr_cal;
    String final_account_no;

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

//        final Button save_button = view.findViewById(R.id.grant_button_monthly);
        final EditText account_no = view.findViewById(R.id.account_number_monthly_grant);
        final EditText disb_amount_field = view.findViewById(R.id.disbursement_amount_field);
        final EditText file_amount_field = view.findViewById(R.id.file_amount_field);
        final EditText discount_amount_field = view.findViewById(R.id.discount_amount_field);
        final TextInputLayout discount_amount_til = view.findViewById(R.id.discount_amount_til);
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

        final FloatingActionButton fab = (FloatingActionButton) Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_save_black_24dp));
        fab.setVisibility(View.INVISIBLE);
        assert getArguments() != null;
        final Customer selected_customer = (Customer) getArguments().getSerializable(ARG_PARAM1);

        curr_cal = Calendar.getInstance();
        edit_o_date.setText(MainActivity.CaltoStringDate(curr_cal));


        months_field.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (months_field.getText().toString().length() != 0) {
                    int months = Integer.parseInt(months_field.getText().toString());
                    Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                    o_cal.add(Calendar.DAY_OF_YEAR, 30 * months);
                    edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
                }
                return false;
            }
        });


        file_amount_field.setOnKeyListener(new View.OnKeyListener()

        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Long file_amount = 0L;
                Long disb_amount = 0L;
                if (file_amount_field.getText().toString().length() != 0)
                    file_amount = Long.parseLong(file_amount_field.getText().toString());
                if (disb_amount_field.getText().toString().length() != 0)
                    disb_amount = Long.parseLong(disb_amount_field.getText().toString());
                if ((disb_amount - file_amount) >= 0) {
                    file_amount_field.setText(String.valueOf(disb_amount - file_amount));
                }
                return false;
            }
        });
        discount_amount_field.setOnKeyListener(new View.OnKeyListener()

        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(discount_amount_field.getText().toString().length()!=0 && disb_amount_field.getText().toString().length()!=0) {
//                Long file_amount = Long.parseLong(file_amount_field.getText().toString());
                Long discount_amount = 0L;
                Long disb_amount = 0L;
                if (discount_amount_field.getText().toString().length() != 0)
                    discount_amount = Long.parseLong(discount_amount_field.getText().toString());
                if (disb_amount_field.getText().toString().length() != 0)
                    disb_amount = Long.parseLong(disb_amount_field.getText().toString());
                if ((disb_amount - discount_amount) >= 0) {
                    file_amount_field.setText(String.valueOf(disb_amount - discount_amount));
                }
//                }
                return false;
            }
        });

        disb_amount_field.setOnKeyListener(new View.OnKeyListener()

        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Long discount_amount = 0L;
                Long disb_amount = 0L;
                if (discount_amount_field.getText().toString().length() != 0)
                    discount_amount = Long.parseLong(discount_amount_field.getText().toString());
                if (disb_amount_field.getText().toString().length() != 0)
                    disb_amount = Long.parseLong(disb_amount_field.getText().toString());
                if ((disb_amount - discount_amount) >= 0) {
                    file_amount_field.setText(String.valueOf(disb_amount - discount_amount));
                }
                return false;
            }
        });

        edit_o_date.setOnClickListener(new View.OnClickListener()

        {
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
                        edit_o_date.setText(date_gen);
                        Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                        if (account_type_selected.equals("0")) {
                            o_cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(selected_days));
                            edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
                        } else if (account_type_selected.equals("1")) {
                            if (months_field.getText().length() != 0) {
                                o_cal.add(Calendar.DAY_OF_YEAR, 30 * Integer.parseInt(months_field.getText().toString()));
                                edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
                            }
                        }

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();


                ;
            }
        });

        edit_c_date.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


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


                ;
            }
        });

        DatabaseReference myref = database.getReference("lastAccountNo");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prefix_account_no.setText((dataSnapshot.getValue(Long.class) + 1) + " - ");
                lastAccountNo = dataSnapshot.getValue(Long.class);
                fields_loaded += 1;
                if (fields_loaded == 2) {
                    fab.setVisibility(View.VISIBLE);
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
                    assert agent1 != null;
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
                    agents.add(each.getName().split(" ")[0] + " (" + each.getId() + ")");
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, agents);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
                fields_loaded += 1;
                if (fields_loaded == 2) {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        payment_duration_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selected_days = "100";
                    Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                    o_cal.add(Calendar.DAY_OF_YEAR, 100);
                    edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
                } else if (position == 1) {
                    selected_days = "200";
                    Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                    o_cal.add(Calendar.DAY_OF_YEAR, 200);
                    edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                o_cal.add(Calendar.DAY_OF_YEAR, 100);
                edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
            }
        });

        account_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    account_type_selected = "0";  //daily basis
                    Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                    o_cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(selected_days));
                    edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
                    loan_grant_roi_til.setVisibility(View.INVISIBLE);
                    loan_grant_duration_til.setVisibility(View.INVISIBLE);
                    payment_duration_spinner.setVisibility(View.VISIBLE);
                    discount_amount_til.setVisibility(View.VISIBLE);
                    discount_amount_field.setText("");
                } else if (position == 1) {
                    account_type_selected = "1"; //monthly
                    if (months_field.getText().toString().length() != 0) {
                        int months = Integer.parseInt(months_field.getText().toString());
                        Calendar o_cal = MainActivity.StringDateToCal(edit_o_date.getText().toString());
                        o_cal.add(Calendar.DAY_OF_YEAR, 30 * months);
                        edit_c_date.setText(MainActivity.CaltoStringDate(o_cal));
                    }
                    loan_grant_roi_til.setVisibility(View.VISIBLE);
                    loan_grant_duration_til.setVisibility(View.VISIBLE);
                    payment_duration_spinner.setVisibility(View.INVISIBLE);

//                    file_amount_field.setVisibility(View.INVISIBLE);
                    discount_amount_til.setVisibility(View.INVISIBLE);

                    file_amount_field.setText(disb_amount_field.getText());
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
        fab.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v) {

                if (disb_amount_field.getText().toString().length() == 0) {
                    disb_amount_field.setError("Disbursement Amount is required!");
                    return;
                } else if (file_duration_field.getText().toString().length() == 0) {
                    if (account_type_selected.equals("1")) {
                        file_duration_field.setError("Duration is required!");
                        return;
                    }
                } else if (edit_o_date.getText().toString().length() == 0) {
                    edit_o_date.setError("Opening date is required!");
                    return;
                } else if (edit_c_date.getText().toString().length() == 0) {
                    edit_c_date.setError("Closing date is required!");
                    return;
                }
                if (account_type_selected.equals("1")) {
                    if (edit_roi.getText().toString().length() == 0) {
                        edit_roi.setError("Rate of interest is required for Monthly basis account!");
                        return;
                    }
                    file_amount_field.setText(disb_amount_field.getText());
                }
                if(account_type_selected.equals("0")) {
                    if (!discount_amount_field.getText().toString().equals("")) {
                        if (Long.parseLong(disb_amount_field.getText().toString()) < Long.parseLong(discount_amount_field.getText().toString())) {
                            discount_amount_til.setError("Must be less than disbursement amt");
                            return;
                        }
                    }
                }
                if (account_no.getText().toString().equals("")) {
                    final_account_no = String.valueOf(lastAccountNo + 1);
                } else {
                    final_account_no = (lastAccountNo + 1) + "-" + account_no.getText().toString();
                }


                DatabaseReference agent_customer = database.getReference("agentAccount");
                assert selected_customer != null;
                agent_customer.child(agent_selected.getId()).child(final_account_no).setValue(selected_customer.getId());

                String o_date = edit_o_date.getText().toString();
                String c_date = edit_c_date.getText().toString();
                String amount = disb_amount_field.getText().toString();
                double roi = 0.0;
                if (edit_roi.getText().toString().length() > 0) {
                    String roi_per_month = edit_roi.getText().toString();
                    roi = Double.parseDouble(roi_per_month) * 12.0;
                }
                String info = additional_info_monthly_grant.getText().toString();
                String lf_number = lf_number_field.getText().toString();
                String file_amount = "0";
                if (file_amount_field.getText().toString().length() != 0) {
                    file_amount = file_amount_field.getText().toString();
                }


                HashMap<String, String> grant_info = new HashMap<>();
                grant_info.put("o_date", o_date);
                grant_info.put("c_date", c_date);
                grant_info.put("last_int_calc", o_date);
                grant_info.put("amount", amount);
                grant_info.put("roi", String.valueOf(roi));
                grant_info.put("info", info);
                grant_info.put("lf_number", lf_number);
                grant_info.put("file_amount", file_amount);
                grant_info.put("r_amt", file_amount);
                grant_info.put("agent", agent_selected.getName());
                grant_info.put("account", final_account_no);
                grant_info.put("customer", selected_customer.getName());
                grant_info.put("customer_id", selected_customer.getId());
                grant_info.put("months", String.valueOf(months_field.getText()));
                grant_info.put("type", account_type_selected);
                grant_info.put("days", selected_days);
                if (account_type_selected.equals("0")) {
                    grant_info.put("duration", selected_days);
                } else {
                    grant_info.put("duration", String.valueOf(months_field.getText()));
                }
                setUpDialog(grant_info);


            }
        });
        return view;
    }

    private void setUpDialog(HashMap<String, String> grant_info) {
        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        GrantLoanDialogFragment dialogFragment = GrantLoanDialogFragment.newInstance(grant_info);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(ft, "dialog");
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


    @Override
    public void onDialogPositiveClick(final HashMap<String, String> grant_info) {
//        Toast.makeText(getContext() , "from host fragment" , Toast.LENGTH_LONG).show();
        DatabaseReference customers = database.getReference("customers").child(grant_info.get("customer_id")).child("accounts");

        Map<String, String> account_number_details = new HashMap<>();

//                account_number_details.put("no", final_account_no);
//                account_number_details.put("customer", selected_customer_to_update.getId());
        account_number_details.put("disb_amt", grant_info.get("amount"));
        account_number_details.put("o_date", grant_info.get("o_date"));
        account_number_details.put("c_date", grant_info.get("c_date"));
        account_number_details.put("info", grant_info.get("info"));
        account_number_details.put("file_amt", grant_info.get("file_amount"));
        account_number_details.put("r_amt", grant_info.get("r_amt"));
        if (account_type_selected.equals("1")) {
            account_number_details.put("last_int_calc", grant_info.get("last_int_calc"));
            account_number_details.put("roi", grant_info.get("roi"));
            account_number_details.put("duration", grant_info.get("months"));
        } else if (account_type_selected.equals("0")) {
            account_number_details.put("duration", selected_days);
        }
        account_number_details.put("type", account_type_selected);
        account_number_details.put("lf_no", grant_info.get("lf_number"));

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
                account_type_map.put(grant_info.get("account"), account_type_selected);
                account_type_db_ref.updateChildren(account_type_map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Log.d("accountType query: ", "data updated.");
                        Toast.makeText(getContext(), "Loan Granted", Toast.LENGTH_LONG).show();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        FragmentSelectCustomer fragmentSelectCustomer = new FragmentSelectCustomer();
                        fragmentTransaction.replace(R.id.fragment_container, fragmentSelectCustomer).addToBackStack(null).
                                commit();
                    }
                });


                //Update accountCustomer
//                        DatabaseReference account_customer_db_ref = database.getReference("accountCustomer");
//                        Map<String, Object> account_customer_map = new HashMap<>();
//                        account_type_map.put(final_account_no, selected_customer_to_update.getId());
//                        account_customer_db_ref.updateChildren(account_customer_map, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                Log.d("accountCustomer query: ", "data updated.");
//                            }
//                        });
            }
        });
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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
