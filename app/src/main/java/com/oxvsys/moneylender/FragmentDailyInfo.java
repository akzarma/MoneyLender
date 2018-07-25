package com.oxvsys.moneylender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.oxvsys.moneylender.HomeActivity.database;

public class FragmentDailyInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private AdapterAgent mAdapter;
//    private TextView currentDay_textView;
    Calendar sel_calendar;


    private OnFragmentInteractionListener mListener;

    public FragmentDailyInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentDailyInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDailyInfo newInstance(Calendar sel_cal) {
        FragmentDailyInfo fragment = new FragmentDailyInfo();
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
        View view = inflater.inflate(R.layout.fragment_daily_info, container, false);
        recyclerView = view.findViewById(R.id.recycler);
//        currentDay_textView = view.findViewById(R.id.fragment_textV_day);
//        currentDay_textView.setText("Daily Info");
        Button date_button = (Button) view.findViewById(R.id.date_button);


        sel_calendar = (Calendar) getArguments().getSerializable(ARG_PARAM1);

//        int month = sel_calendar.get(Calendar.MONTH) + 1;
        final String sel_date = MainActivity.CaltoStringDate(sel_calendar);
        date_button.setText(sel_date);

        DatabaseReference agentDailyCollects = database.getReference("agentCollect");


        agentDailyCollects.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<AgentCollect> agentCollectList = new ArrayList<>();
//                List<Agent> agents = new ArrayList<>();
                for (DataSnapshot each_agent : dataSnapshot.getChildren()) {
                    Agent curr_agent = new Agent();
                    curr_agent.setId(each_agent.getKey());
//                    agents.add(curr_agent);


                    for (DataSnapshot date : each_agent.getChildren()) {
                        if (date.getKey().equals(sel_date)) {
                            List<AccountAmountCollect> accountAmountCollectList = new ArrayList<>();
                            for (DataSnapshot each_account : date.getChildren()) {
                                Account account = new Account();
                                account.setType("");
                                account.setNo(each_account.getKey());

                                accountAmountCollectList.add(new AccountAmountCollect(account,
                                        Long.parseLong(each_account.getValue().toString())));
                            }
                            agentCollectList.add(new AgentCollect(curr_agent, "", sel_calendar, accountAmountCollectList));

                        }
                    }


                }

                mAdapter = new AdapterAgent(agentCollectList, sel_calendar, getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Agent agent = new Agent();
//        agent.setId("0");
//        agent.setName("AkshayAgent");
//        Agent agent1 = new Agent();
//        agent1.setId("1");
//        agent1.setName("Anish");
//
//        Account account = new Account();
//        account.setDisb_amt(1000);
//        account.setNo("230");
//        account.setType("daily");
//
//        Account account1 = new Account();
//        account1.setDisb_amt(2000);
//        account1.setNo("231");
//        account1.setType("daily");
//
//        List<AccountAmountCollect> accountAmountCollectList = new ArrayList<>();
//        accountAmountCollectList.add(new AccountAmountCollect(account, 100));
//        accountAmountCollectList.add(new AccountAmountCollect(account1, 200));
//
//        Calendar collect_date = Calendar.getInstance();
//        collect_date.set(2018, 6 - 1, 28);
//        collect_date.set(Calendar.HOUR_OF_DAY, 0);
//        collect_date.set(Calendar.MINUTE, 0);
//        collect_date.set(Calendar.SECOND, 0);
//        collect_date.set(Calendar.MILLISECOND, 0);
//
//        List<AgentCollect> agentCollectList = new ArrayList<>();
//        AgentCollect agentCollect = new AgentCollect(agent, "daily", collect_date, accountAmountCollectList);
//        AgentCollect agentCollect1 = new AgentCollect(agent1, "daily", collect_date, accountAmountCollectList);
//
//        agentCollectList.add(agentCollect);
//        agentCollectList.add(agentCollect1);
//
//        Calendar selected_cal = Calendar.getInstance();
//        selected_cal.set(2018, 6 - 1, 28);
//        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
//        selected_cal.set(Calendar.MINUTE, 0);
//        selected_cal.set(Calendar.SECOND, 0);
//        selected_cal.set(Calendar.MILLISECOND, 0);
//
//        mAdapter = new AdapterAgent(agentCollectList, selected_cal, getContext());
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
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
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentDailyInfo fd = FragmentDailyInfo.newInstance(selected_cal);
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
