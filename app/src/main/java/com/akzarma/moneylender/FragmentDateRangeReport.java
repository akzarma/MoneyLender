package com.akzarma.moneylender;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.akzarma.moneylender.HomeActivity.database;
import static com.akzarma.moneylender.MainActivity.CaltoStringDate;
import static com.akzarma.moneylender.MainActivity.StringDateToCal;
import static com.akzarma.moneylender.MainActivity.logged_agent;
import static com.akzarma.moneylender.MainActivity.logged_type;


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
    private AdapterDateRange mAdapter;
    private RecyclerView recyclerView;
    private List<DateAmount> dateAmountList;
    private HashMap<String, Account> accountHashMap = new HashMap<>();
    private HashSet<String> account_no_list = new HashSet<>();

    final HashMap<String, DateAmount> calendarDateAmountHashMap = new HashMap<>();
    private Calendar from_cal, to_cal;
    private String from_date, to_date;

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
        final Button get_both_excel_button = view.findViewById(R.id.get_both_excel_button);
        final Button get_daily_excel_button = view.findViewById(R.id.get_daily_excel_button);
        final Button get_monthly_excel_button = view.findViewById(R.id.get_monthly_excel_button);
        get_both_excel_button.setVisibility(View.INVISIBLE);
        get_daily_excel_button.setVisibility(View.INVISIBLE);
        get_monthly_excel_button.setVisibility(View.INVISIBLE);

        TextView fragment_textV_day = view.findViewById(R.id.fragment_textV_day);


        from_cal = (Calendar) getArguments().getSerializable(ARG_PARAM1);
        to_cal = (Calendar) getArguments().getSerializable(ARG_PARAM2);

        from_date = CaltoStringDate(from_cal);
        to_date = CaltoStringDate(to_cal);

        fragment_textV_day.setText(from_date + " to " + to_date);

        if (MainActivity.logged_type.equals("admin")) {
            DatabaseReference agent_collect_db_ref = database.getReference("agentCollect");
            agent_collect_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot agent : dataSnapshot.getChildren())
                        for (DataSnapshot date : agent.getChildren()) {
                            Calendar date_cal = StringDateToCal(date.getKey());
                            String date_str = CaltoStringDate(date_cal);
                            if ((date_cal.after(from_cal) && date_cal.before(to_cal)) ||
                                    (date_cal.equals(from_cal) || date_cal.equals(to_cal))) {
                                for (DataSnapshot account : date.getChildren()) {
                                    account_no_list.add(account.getKey());
                                    DateAmount dateAmount = new DateAmount();
                                    dateAmount.setAllAmounts(String.valueOf(account.getValue()));
                                    dateAmount.setDate(date.getKey());
                                    if (calendarDateAmountHashMap.containsKey(date_str)) {
                                        Long amount_principal = calendarDateAmountHashMap.get(date_str).getAmount_principal() + dateAmount.getAmount_principal();
                                        Long amount_interest = calendarDateAmountHashMap.get(date_str).getAmount_interest() + dateAmount.getAmount_interest();
                                        dateAmount.setAmount_interest(amount_interest);
                                        dateAmount.setAmount_principal(amount_principal);
                                        calendarDateAmountHashMap.put(date_str, dateAmount);
                                    } else {
                                        calendarDateAmountHashMap.put(date_str, dateAmount);
                                    }
                                }
                            }
                        }
                    dateAmountList = new ArrayList<>(calendarDateAmountHashMap.values());
                    mAdapter = new AdapterDateRange(dateAmountList, getContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);

                    DatabaseReference agent_account_db_ref = database.getReference("agentAccount");
                    agent_account_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> account_no_to_remove = new ArrayList<>();
                            for (DataSnapshot agent : dataSnapshot.getChildren()) {
                                account_no_to_remove.clear();
                                for (String each_account : account_no_list) {
                                    if (agent.hasChild(each_account)) {
                                        Account account = new Account();
                                        account.setNo(each_account);
                                        Customer customer = new Customer();
                                        customer.setId(String.valueOf(agent.child(each_account).getValue()));
                                        account.setCustomer(customer);
                                        accountHashMap.put(each_account, account);
                                        account_no_to_remove.add(each_account);
                                    }
                                }
                                account_no_list.removeAll(account_no_to_remove);
                            }

                            final DatabaseReference cust_db_ref = database.getReference("customers");
                            cust_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (Map.Entry<String, Account> each_account : accountHashMap.entrySet()) {
                                        String cust_id = each_account.getValue().getCustomer().getId();
                                        if (dataSnapshot.hasChild(cust_id)) {
                                            Customer customer = dataSnapshot.child(cust_id).getValue(Customer.class);
                                            customer.setId(cust_id);
                                            each_account.getValue().setCustomer(customer);
                                        }
                                    }

                                    final DatabaseReference cust_db_ref = database.getReference("accountType");
                                    cust_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (Map.Entry<String, Account> each_account : accountHashMap.entrySet()) {
                                                String acc_id = each_account.getKey();
                                                each_account.getValue().setType(dataSnapshot.child(acc_id).getValue(String.class));
                                            }

                                            get_both_excel_button.setVisibility(View.VISIBLE);
                                            get_daily_excel_button.setVisibility(View.VISIBLE);
                                            get_monthly_excel_button.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (logged_type.equals("agent")) {
            get_daily_excel_button.setVisibility(View.GONE);
            get_monthly_excel_button.setVisibility(View.GONE);

            DatabaseReference agent_collect_db_ref = database.getReference("agentCollect").child(logged_agent);
            agent_collect_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot date : dataSnapshot.getChildren()) {

                        Calendar date_cal = StringDateToCal(Objects.requireNonNull(date.getKey()));
                        String date_str = CaltoStringDate(date_cal);
                        if ((date_cal.after(from_cal) && date_cal.before(to_cal)) ||
                                (date_cal.equals(from_cal) || date_cal.equals(to_cal))) {
                            for (DataSnapshot account : date.getChildren()) {
                                DateAmount dateAmount = new DateAmount();
                                dateAmount.setAllAmounts(String.valueOf(account.getValue()));
                                dateAmount.setDate(date.getKey());
                                if (calendarDateAmountHashMap.containsKey(date_str)) {
                                    Long amount_principal = calendarDateAmountHashMap.get(date_str).getAmount_principal() + dateAmount.getAmount_principal();
                                    Long amount_interest = calendarDateAmountHashMap.get(date_str).getAmount_interest() + dateAmount.getAmount_interest();
                                    dateAmount.setAmount_interest(amount_interest);
                                    dateAmount.setAmount_principal(amount_principal);
                                    calendarDateAmountHashMap.put(date_str, dateAmount);
                                } else {
                                    calendarDateAmountHashMap.put(date_str, dateAmount);
                                }
                            }
                        }
                    }
                    dateAmountList = new ArrayList<>(calendarDateAmountHashMap.values());
                    mAdapter = new AdapterDateRange(dateAmountList, getContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    get_both_excel_button.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        get_daily_excel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generat_excel(0);
            }
        });

        get_monthly_excel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generat_excel(1);
            }
        });

        get_both_excel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generat_excel(-1);
            }


        });


        return view;
    }


    void generat_excel(int account_type) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("Frag date range", "onCreate: " + "true file");
        } else Log.d("Frag date range", "onCreate: " + "not writable");
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MoneyLender");
        File DocsDirectory = new File(root.getAbsolutePath(), "Monthly Reports");
        DocsDirectory.mkdirs();
        File actualDoc = new File(DocsDirectory.getAbsolutePath(), from_date + "_to_" + to_date + ".xls");
        int row = 0;
        int serialCol = 0;
        int dateCol = 1;
        int nameCol = 2;
        int collectionCol = 3;
        int intCollectionCol = 4;
        int intTotalCol = 5;
        try {

            final WritableWorkbook workbook = Workbook.createWorkbook(actualDoc);
            final WritableSheet sheet = workbook.createSheet("Test Sheet", 0);
            Label heading = new Label(0, row, "Monthly Report from " + from_date + " to " + to_date);
            row++;
            Label account_label = new Label(dateCol, row, "Date");
            Label cust_name_label = new Label(dateCol, row, "Customer Name");
            Label amount_label = new Label(collectionCol, row, "Disbursement Collection");
            Label int_amount_label = new Label(intCollectionCol, row, "Interest Collection");
            Label int_total_label = new Label(intTotalCol, row, "Total Collection");
            Label sr_number = new Label(serialCol, row, "Sr.No");
            row++;

            try {
                sheet.addCell(heading);
                sheet.addCell(account_label);
                sheet.addCell(amount_label);
                sheet.addCell(int_amount_label);
                sheet.addCell(int_total_label);
                sheet.addCell(sr_number);


                long total_amount = 0;
                long total_amount_int = 0;
                Number ser_no, amount_collected, int_amount_collected, int_total_collected;
                Label date_label;
                Calendar curr_cal = (Calendar) from_cal.clone();
                Calendar to_cal_copy = (Calendar) to_cal.clone();
                to_cal_copy.add(Calendar.DAY_OF_MONTH, 1);
                while (curr_cal.before(to_cal_copy)) {
                    String curr_date = MainActivity.CaltoStringDate(curr_cal);


                    ser_no = new Number(serialCol, row, row - 1);
                    date_label = new Label(dateCol, row, curr_date);
                    if (calendarDateAmountHashMap.containsKey(curr_date)) {
                        total_amount += calendarDateAmountHashMap.get(curr_date).getAmount_principal();
                        total_amount_int += calendarDateAmountHashMap.get(curr_date).getAmount_interest();
                        amount_collected = new Number(collectionCol, row, calendarDateAmountHashMap.get(curr_date).getAmount_principal());
                        int_amount_collected = new Number(intCollectionCol, row, calendarDateAmountHashMap.get(curr_date).getAmount_interest());
                        int_total_collected = new Number(intTotalCol, row, calendarDateAmountHashMap.get(curr_date).getAmount_interest() +
                                calendarDateAmountHashMap.get(curr_date).getAmount_principal());
                    } else {
                        amount_collected = new Number(collectionCol, row, 0);
                        int_amount_collected = new Number(intCollectionCol, row, 0);
                        int_total_collected = new Number(intTotalCol, row, 0);
                    }
                    row++;
                    sheet.addCell(date_label);
                    sheet.addCell(amount_collected);
                    sheet.addCell(int_amount_collected);
                    sheet.addCell(int_total_collected);
                    sheet.addCell(ser_no);
                    curr_cal.add(Calendar.DAY_OF_MONTH, 1);
                }

                row++;
                date_label = new Label(dateCol, row, "Total Collection");
                amount_collected = new Number(collectionCol, row, total_amount);
                int_amount_collected = new Number(intCollectionCol, row, total_amount_int);
                int_total_collected = new Number(intTotalCol, row, total_amount + total_amount_int);
                sheet.addCell(date_label);
                sheet.addCell(int_total_collected);
                sheet.addCell(amount_collected);
                sheet.addCell(int_amount_collected);

                try {
                    workbook.write();
                    workbook.close();
                    Toast.makeText(getContext(), "File saved in Documents folder", Toast.LENGTH_LONG).show();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
