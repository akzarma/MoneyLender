package com.oxvsys.moneylender;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.oxvsys.moneylender.HomeActivity.database;


public class FragmentDashboard extends Fragment {

    //Admin Dashboard

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Bundle date_bundle;
    Button date_button, from_date_button, to_date_button;
    Long total_daily_amount = 0L;
    Long total_monthly_amount_till_today = 0L;
    Long total_collection = 0L;
    TextView todays_value;
    TextView view_monthly_value_till_today;
    TextView total_collection_value_dashboard;
    RelativeLayout totat_collection_card_layout;
    Calendar sel_calendar, from_calendar;

    private OnFragmentInteractionListener mListener;

    public FragmentDashboard() {

    }

    public static FragmentDashboard newInstance(Calendar sel_cal) {
        FragmentDashboard fragment = new FragmentDashboard();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, sel_cal);
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        final ProgressBar progressBar = view.findViewById(R.id.dashboard_progress);
        progressBar.setVisibility(View.VISIBLE);
        date_button = view.findViewById(R.id.dashboard_select_date);
        from_date_button = view.findViewById(R.id.dashboard_from_button);
        to_date_button = view.findViewById(R.id.dashboard_to_button);
        todays_value = view.findViewById(R.id.todays_collection_value_dashboard);
        view_monthly_value_till_today = view.findViewById(R.id.monthly_collection_value_dashboard);
        total_collection_value_dashboard = view.findViewById(R.id.total_collection_value_dashboard);
        totat_collection_card_layout = view.findViewById(R.id.totat_collection_card_layout);
        sel_calendar = (Calendar) Objects.requireNonNull(getArguments()).getSerializable(ARG_PARAM1);

        int month = sel_calendar.get(Calendar.MONTH) + 1;
        final String sel_date = sel_calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                month + "-" +
                sel_calendar.get(Calendar.YEAR);

        date_button.setText(sel_date);

        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_right_black_24dp));
        fab.setVisibility(View.INVISIBLE);


        DatabaseReference ref = database.getReference("agentCollect");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                final List<AccountAmountCollect> accountAmountCollectList = new ArrayList<>();
                for (DataSnapshot agents : dataSnapshot.getChildren()) {
                    List<AgentCollect> agentCollectList = new ArrayList<>();
                    for (DataSnapshot date : agents.getChildren()) {
                        if (Objects.equals(date.getKey(), sel_date)) {

                            for (DataSnapshot account : date.getChildren()) {
                                Account account_temp = new Account();
                                account_temp.setNo(account.getKey());
                                accountAmountCollectList.add(new AccountAmountCollect(account_temp,
                                        Long.parseLong(Objects.requireNonNull(account.getValue()).toString())));
//                                total_daily_amount += Long.parseLong(account.getValue().toString());
//                                Log.d("------", "onDataChange: " + total_daily_amount);
                            }
                        }
                    }
                }
                DatabaseReference account_type_db_ref = database.getReference("accountType");
                account_type_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (AccountAmountCollect each_account_amount : accountAmountCollectList) {
                            String type = ((HashMap<String, String>) Objects.requireNonNull(dataSnapshot.getValue())).get(each_account_amount.getAccount().getNo());
                            if (type == null) {
                                type = "";
                            }
                            Log.d("accountType query for:", each_account_amount.getAccount().getNo());
                            each_account_amount.getAccount().setType(type);
                            if (type.equals("0")) {
                                total_daily_amount += each_account_amount.getAmount();
                            } else if (type.equals("1")) {
                                total_monthly_amount_till_today += each_account_amount.getAmount();
                            }
                            total_collection = total_monthly_amount_till_today + total_daily_amount;

                        }


                        todays_value.setText("₹ " + String.valueOf(total_daily_amount));
                        view_monthly_value_till_today.setText("₹ " + String.valueOf(total_monthly_amount_till_today));
                        total_collection_value_dashboard.setText("₹ " + String.valueOf(total_collection));
                        total_daily_amount = 0L;
                        total_monthly_amount_till_today = 0L;
                        total_collection = 0L;
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


//        DatabaseReference month_ref = database.getReference("agentCollect").child("monthly");
//        month_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot agents : dataSnapshot.getChildren()){
//                    for (DataSnapshot date : agents.getChildren()){
//                        if(date.getKey().equals(sel_date)){
//                            for (DataSnapshot accounts : date.getChildren()){
//                                total_monthly_amount_till_today += Integer.parseInt(accounts.getValue().toString());
//                                Log.d("------", "onDataChange: " + total_daily_amount);
//                            }
//                        }
//                    }
//                }
//                view_monthly_value_till_today.setText(String.valueOf(total_monthly_amount_till_today));
//                total_monthly_amount_till_today = 0;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear = sel_calendar.get(Calendar.YEAR);
                int mMonth = sel_calendar.get(Calendar.MONTH);
                int mDay = sel_calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar selected_cal = Calendar.getInstance();
                        selected_cal.set(selectedyear, selectedmonth, selectedday);
                        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
                        selected_cal.set(Calendar.MINUTE, 0);
                        selected_cal.set(Calendar.SECOND, 0);
                        selected_cal.set(Calendar.MILLISECOND, 0);
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentDashboard fd = FragmentDashboard.newInstance(selected_cal);
                        ft.replace(R.id.fragment_container, fd).addToBackStack(null).
                                commit();
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        from_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear = sel_calendar.get(Calendar.YEAR);
                int mMonth = sel_calendar.get(Calendar.MONTH);
                int mDay = sel_calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        Calendar from_cal = Calendar.getInstance();
                        from_cal.set(selectedyear, selectedmonth, selectedday);
                        from_cal.set(Calendar.HOUR_OF_DAY, 0);
                        from_cal.set(Calendar.MINUTE, 0);
                        from_cal.set(Calendar.SECOND, 0);
                        from_cal.set(Calendar.MILLISECOND, 0);
                        int month = from_cal.get(Calendar.MONTH) + 1;
                        String from_date = from_cal.get(Calendar.DAY_OF_MONTH) + "-" +
                                month + "-" +
                                from_cal.get(Calendar.YEAR);
                        from_date_button.setText(from_date);
                        from_calendar = from_cal;

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        to_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear = sel_calendar.get(Calendar.YEAR);
                int mMonth = sel_calendar.get(Calendar.MONTH);
                int mDay = sel_calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar selected_cal = Calendar.getInstance();
                        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
                        selected_cal.set(Calendar.MINUTE, 0);
                        selected_cal.set(Calendar.SECOND, 0);
                        selected_cal.set(Calendar.MILLISECOND, 0);

                        Calendar to_cal = (Calendar) selected_cal.clone();
                        to_cal.set(selectedyear, selectedmonth, selectedday);
                        to_cal.set(Calendar.HOUR_OF_DAY, 0);
                        to_cal.set(Calendar.MINUTE, 0);
                        to_cal.set(Calendar.SECOND, 0);
                        to_cal.set(Calendar.MILLISECOND, 0);
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentDateRangeReport fragmentDateRangeReport = FragmentDateRangeReport.newInstance(from_calendar, to_cal);
                        ft.replace(R.id.fragment_container, fragmentDateRangeReport).addToBackStack(null).
                                commit();
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        totat_collection_card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //anish yaha pe daal de
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
