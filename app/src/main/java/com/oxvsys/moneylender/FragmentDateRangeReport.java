package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.oxvsys.moneylender.LoginActivity.database;
import static com.oxvsys.moneylender.MainActivity.CaltoStringDate;
import static com.oxvsys.moneylender.MainActivity.StringDateToCal;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDateRangeReport.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDateRangeReport#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDateRangeReport extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DateRangeAdapter mAdapter;
    private RecyclerView recyclerView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentDateRangeReport() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentDateRangeReport.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDateRangeReport newInstance(Calendar from_date, Calendar to_date) {
        FragmentDateRangeReport fragment = new FragmentDateRangeReport();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, from_date);
        args.putSerializable(ARG_PARAM2, to_date);
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
        View view = inflater.inflate(R.layout.fragment_date_range_report, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        TextView fragment_textV_day = view.findViewById(R.id.fragment_textV_day);


        final Calendar from_cal = (Calendar) getArguments().getSerializable(ARG_PARAM1);
        final Calendar to_cal = (Calendar) getArguments().getSerializable(ARG_PARAM2);

        String from_date = CaltoStringDate(from_cal);
        String to_date = CaltoStringDate(to_cal);

        fragment_textV_day.setText(from_date +" to " + to_date);

        DatabaseReference agent_collect_db_ref = database.getReference("agentCollect");
        agent_collect_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, DateAmount> calendarDateAmountHashMap = new HashMap<>();
                for(DataSnapshot agent:dataSnapshot.getChildren()){
                    for(DataSnapshot date:agent.getChildren()){

                        Calendar date_cal = StringDateToCal(date.getKey());
                        String date_str = CaltoStringDate(date_cal);
                        if(date_cal.after(from_cal) && date_cal.before(to_cal)){
                            for(DataSnapshot account:date.getChildren()){
                                DateAmount dateAmount = new DateAmount();
                                dateAmount.setAmount(Long.parseLong(account.getValue().toString()));
                                dateAmount.setDate(date.getKey());
                                if(calendarDateAmountHashMap.containsKey(date_str)){
                                    Long amount = calendarDateAmountHashMap.get(date_str).getAmount() + dateAmount.getAmount();
                                    dateAmount.setAmount(amount);
                                    calendarDateAmountHashMap.put(date_str, dateAmount);
                                }else{
                                    calendarDateAmountHashMap.put(date_str, dateAmount);
                                }
                            }
                        }
                    }
                }
                mAdapter = new DateRangeAdapter(new ArrayList<>(calendarDateAmountHashMap.values()), getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
