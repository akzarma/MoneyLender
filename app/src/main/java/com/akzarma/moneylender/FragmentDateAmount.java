package com.akzarma.moneylender;

import android.annotation.SuppressLint;
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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDateAmount.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDateAmount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDateAmount extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    AdapterAgentAccountPayment mAdapter;


    private OnFragmentInteractionListener mListener;

    public FragmentDateAmount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentDateAmount.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDateAmount newInstance(List<DateAmount> dateAmountList, String agent, CustomerAmount customerAmount) {
        FragmentDateAmount fragment = new FragmentDateAmount();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) dateAmountList);
        args.putSerializable(ARG_PARAM2, customerAmount);
        args.putSerializable(ARG_PARAM3, agent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_amount_recycler, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.date_amount_recycler);
        TextView date_amount_heading_field = view.findViewById(R.id.date_amount_heading_field);
        TextView date_amount_heading1_field = view.findViewById(R.id.date_amount_heading1_field);
        assert getArguments() != null;
        date_amount_heading1_field.setText("Agent ID: " + getArguments().getString(ARG_PARAM3));
        List<DateAmount> dateAmountList = (List<DateAmount>) getArguments().getSerializable(ARG_PARAM1);
        CustomerAmount customerAmount = (CustomerAmount) getArguments().getSerializable(ARG_PARAM2);

        assert dateAmountList != null;
        Collections.sort(dateAmountList, new Comparator<DateAmount>() {
            @Override
            public int compare(DateAmount o1, DateAmount o2) {
                return MainActivity.StringDateToCal(o1.getDate()).compareTo(MainActivity.StringDateToCal(o2.getDate()));
            }
        });

        assert customerAmount != null;
        date_amount_heading_field.setText(customerAmount.getCustomer().getName().split(" ")[0] + " (A/C: " +
                customerAmount.getCustomer().getAccounts1().get(0).getNo() + ")");


        mAdapter = new AdapterAgentAccountPayment(dateAmountList);
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
