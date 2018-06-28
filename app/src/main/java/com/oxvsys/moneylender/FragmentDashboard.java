package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.oxvsys.moneylender.MainActivity.database;


public class FragmentDashboard extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Bundle date_bundle;
    Button date_button;
    int total_daily_amount = 0;
    int total_monthly_amount_till_today = 0;
    TextView todays_value;
    TextView view_monthly_value_till_today;
    Calendar sel_calendar;

    private OnFragmentInteractionListener mListener;

    public FragmentDashboard() {
        // Required empty public constructor
    }

    public static FragmentDashboard newInstance(Calendar sel_cal) {
        FragmentDashboard fragment = new FragmentDashboard();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,sel_cal);
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        date_button = (Button) view.findViewById(R.id.dashboard_title_button);

        todays_value = (TextView) view.findViewById(R.id.todays_collection_value_dashboard);
        view_monthly_value_till_today = (TextView) view.findViewById(R.id.monthly_collection_value_dashboard);

        sel_calendar = (Calendar) getArguments().getSerializable(ARG_PARAM1);

        int month = sel_calendar.get(Calendar.MONTH) + 1;
        final String sel_date = sel_calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                month + "-" +
                sel_calendar.get(Calendar.YEAR);

        date_button.setText(sel_date);
        DatabaseReference ref = database.getReference("agentCollect").child("daily");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot agents : dataSnapshot.getChildren()){
                    for (DataSnapshot date : agents.getChildren()){
                        if(date.getKey().equals(sel_date)){
                            for (DataSnapshot accounts : date.getChildren()){
                                total_daily_amount += Long.parseLong(accounts.getValue().toString());
                                Log.d("------", "onDataChange: " + total_daily_amount);
                            }
                        }
                    }
                }
                todays_value.setText(String.valueOf(total_daily_amount));
                total_daily_amount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference month_ref = database.getReference("agentCollect").child("monthly");
        month_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot agents : dataSnapshot.getChildren()){
                    for (DataSnapshot date : agents.getChildren()){
                        if(date.getKey().equals(sel_date)){
                            for (DataSnapshot accounts : date.getChildren()){
                                total_monthly_amount_till_today += Integer.parseInt(accounts.getValue().toString());
                                Log.d("------", "onDataChange: " + total_daily_amount);
                            }
                        }
                    }
                }
                view_monthly_value_till_today.setText(String.valueOf(total_monthly_amount_till_today));
                total_monthly_amount_till_today = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = sel_calendar.get(Calendar.YEAR);
                int mMonth = sel_calendar.get(Calendar.MONTH);
                int mDay = sel_calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker = new DatePickerDialog(
                            getActivity(), new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            Calendar selected_cal = Calendar.getInstance();
                            selected_cal.set(selectedyear,selectedmonth,selectedday);
                            selected_cal.set(Calendar.HOUR_OF_DAY, 0);
                            selected_cal.set(Calendar.MINUTE, 0);
                            selected_cal.set(Calendar.SECOND, 0);
                            selected_cal.set(Calendar.MILLISECOND, 0);
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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
