package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.oxvsys.moneylender.HomeActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAgentsDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAgentsDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAgentsDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    AdapterAgentPassword mAdapter;
    ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentAgentsDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAgentsDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAgentsDetail newInstance(String param1, String param2) {
        FragmentAgentsDetail fragment = new FragmentAgentsDetail();
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
        View view = inflater.inflate(R.layout.fragment_agent_detail, container, false);
        progressBar = view.findViewById(R.id.show_cust_progress);
        progressBar.setVisibility(View.VISIBLE);
        FloatingActionButton fab = (android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_save_black_24dp));
        fab.setVisibility(View.INVISIBLE);
        final RecyclerView recyclerView = view.findViewById(R.id.agent_pass_recycler);
        DatabaseReference user_db_ref = database.getReference("users");
        user_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ArrayList<String>> agentPassList = new ArrayList<>();
                for(DataSnapshot each:dataSnapshot.getChildren()){
                    ArrayList<String> agentPass = new ArrayList<>();
                    agentPass.add(each.getKey());
                    agentPass.add(((HashMap<String, String>)each.getValue()).get("pwd"));
                    agentPassList.add(agentPass);
                }
                mAdapter = new AdapterAgentPassword(agentPassList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
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
