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
import java.util.List;

import static com.oxvsys.moneylender.MainActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDailyInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDailyInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDailyInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private AgentAdapter mAdapter;
    private TextView currentDay_textView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentDailyInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDailyInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDailyInfo newInstance(String param1, String param2) {
        FragmentDailyInfo fragment = new FragmentDailyInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_daily_info, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        currentDay_textView = view.findViewById(R.id.fragment_textV_day);
        currentDay_textView.setText("Daily Info");

        DatabaseReference agentDailyCollects = database.getReference("agentCollects").child("daily");


        agentDailyCollects.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Agent> agents = new ArrayList<>();
                for(DataSnapshot each_agent: dataSnapshot.getChildren()){
                    Agent curr_agent = new Agent();
                    curr_agent.setId(each_agent.getKey());
                    agents.add(curr_agent);

                    Calendar selected_cal = Calendar.getInstance();
                    selected_cal.set(2018, 6 - 1, 28);
                    selected_cal.set(Calendar.HOUR_OF_DAY, 0);
                    selected_cal.set(Calendar.MINUTE, 0);
                    selected_cal.set(Calendar.SECOND, 0);
                    selected_cal.set(Calendar.MILLISECOND, 0);

                    for(DataSnapshot date_snap: each_agent.getChildren()){

                    }


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Agent agent = new Agent();
        agent.setId("0");
        agent.setName("AkshayAgent");
        Agent agent1 = new Agent();
        agent1.setId("1");
        agent1.setName("Anish");

        Account account = new Account();
        account.setAmt(1000);
        account.setNo("230");
        account.setType("daily");

        Account account1 = new Account();
        account1.setAmt(2000);
        account1.setNo("231");
        account1.setType("daily");

        List<AccountAmountCollect> accountAmountCollectList = new ArrayList<>();
        accountAmountCollectList.add(new AccountAmountCollect(account, 100));
        accountAmountCollectList.add(new AccountAmountCollect(account1, 200));

        Calendar collect_date = Calendar.getInstance();
        collect_date.set(2018, 6 - 1, 28);
        collect_date.set(Calendar.HOUR_OF_DAY, 0);
        collect_date.set(Calendar.MINUTE, 0);
        collect_date.set(Calendar.SECOND, 0);
        collect_date.set(Calendar.MILLISECOND, 0);

        List<AgentCollect> agentCollectList = new ArrayList<>();
        AgentCollect agentCollect = new AgentCollect(agent, "daily", collect_date, accountAmountCollectList);
        AgentCollect agentCollect1 = new AgentCollect(agent1, "daily", collect_date, accountAmountCollectList);

        agentCollectList.add(agentCollect);
        agentCollectList.add(agentCollect1);

        Calendar selected_cal = Calendar.getInstance();
        selected_cal.set(2018, 6 - 1, 28);
        selected_cal.set(Calendar.HOUR_OF_DAY, 0);
        selected_cal.set(Calendar.MINUTE, 0);
        selected_cal.set(Calendar.SECOND, 0);
        selected_cal.set(Calendar.MILLISECOND, 0);

        mAdapter = new AgentAdapter(agentCollectList, selected_cal, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

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
