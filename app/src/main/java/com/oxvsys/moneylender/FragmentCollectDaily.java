package com.oxvsys.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import static com.oxvsys.moneylender.MainActivity.database;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCollectDaily.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCollectDaily#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCollectDaily extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentCollectDaily() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCollectDaily.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCollectDaily newInstance(String param1, String param2) {
        FragmentCollectDaily fragment = new FragmentCollectDaily();
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
        View view = inflater.inflate(R.layout.fragment_collect_daily, container, false);
        //==================HARD CODED=============================================================================
        final String agent_id = "0"; //get logged in id and check type of user == "agent" and also customer in next line
//        Customer selected_customer = new Customer();
//        selected_customer.setId("A1");
        final EditText amount_field = view.findViewById(R.id.amount_field);
        Button deposit_button = view.findViewById(R.id.deposit_button);
        deposit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount_recieved = Integer.parseInt(String.valueOf(amount_field.getText()));
                Account selected_account = new Account();
                selected_account.setNo("1");
                selected_account.setAmt(1000);
                selected_account.setType("daily");
                //=========================================================================================================

                final Calendar curr_cal = Calendar.getInstance();
                curr_cal.set(2018, 6 - 1, 28);
                curr_cal.set(Calendar.HOUR_OF_DAY, 0);
                curr_cal.set(Calendar.MINUTE, 0);
                curr_cal.set(Calendar.SECOND, 0);
                curr_cal.set(Calendar.MILLISECOND, 0);
                final String curr_date = String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                        String.valueOf(curr_cal.get(Calendar.MONTH)) + "-" +
                        String.valueOf(curr_cal.get(Calendar.YEAR));

                final DatabaseReference agent = database.getReference("agents/");

//        agent.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(agent_id)){
//                    for(DataSnapshot dates:dataSnapshot.getChildren()){
//
//                    }
//                }else{
//                    HashMap<String, Object> agent_id_key_value = new HashMap<>();
//                    HashMap<String, Object> date_key_value = new HashMap<>();
//                    HashMap<String, Integer> account_key_value = new HashMap<>();
//                    account_key_value.put(selected_account.getNo(), amount_recieved);
//                    date_key_value.put(curr_date, account_key_value);
//                    agent_id_key_value.put(agent_id, date_key_value);
//                    agent.setValue(agent_id_key_value)
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
                agent.child("daily").child(agent_id).child(String.valueOf(curr_cal.get(Calendar.DAY_OF_MONTH)) + "-" +
                        String.valueOf(curr_cal.get(Calendar.MONTH) + 1) + "-" +
                        String.valueOf(curr_cal.get(Calendar.YEAR))).child(selected_account.getNo()).setValue(amount_recieved);

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
