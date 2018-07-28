package com.oxvsys.moneylender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;
import java.util.Objects;

import static com.oxvsys.moneylender.HomeActivity.database;

public class AdapterAccount extends RecyclerView.Adapter<AdapterAccount.AccountHolder> {

    private List<Account> accountList;
    private Customer customer;
    private Context context;
    private FragmentManager fragmentManager;
    private String curr_agent;

    AdapterAccount(Customer customer, FragmentManager fragmentManager, Context context) {
        this.accountList = customer.getAccounts1();
        this.customer = customer;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_account, parent, false);
        return new AccountHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AdapterAccount.AccountHolder holder, int position) {
        final Account account = accountList.get(position);

        holder.lf_field.setText(account.getLf_no());
        holder.info_field.setText(account.getInfo());
        holder.customer_loan_amount_field.setText("₹ " + (String.valueOf(account.getFile_amt())));
        holder.loan_duration_field.setText(String.valueOf(account.getDuration()));
        holder.start_date_field.setText(MainActivity.CaltoStringDate(account.getO_date()));
        holder.end_date_field.setText(MainActivity.CaltoStringDate(account.getC_date()));
        holder.customer_deposited_field.setText("₹ " + String.valueOf(account.getDeposited()));
        if (account.getType().equals("0")) {
            holder.customer_account_type_field.setText("Daily Basis");
            holder.customer_account_field.setText(account.getNo() + " (Daily Basis)");
            Calendar c_date_cal = Calendar.getInstance();
            c_date_cal.setTimeInMillis(account.getC_date().getTimeInMillis());
            holder.loan_duration_field.setText(String.valueOf(account.getDuration()) + " days");
        } else if (account.getType().equals("1")) {
            holder.account_info_card_layout.setBackgroundColor(context.getResources().getColor(R.color.colorIndigo));
            holder.customer_account_field.setText(account.getNo() + " (Monthly Basis)");
            holder.customer_account_type_field.setText("Monthly basis (" + account.getRoi() + "% annual interest)");
            holder.loan_duration_field.setText(String.valueOf(account.getDuration()) + " months");
        }


        final List<DateAmount> dateAmountList = new ArrayList<>();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cardView.setEnabled(false);
                final Calendar o_date = account.getO_date();
                final Calendar c_date = account.getC_date();

                DatabaseReference agent_acc_db_ref = database.getReference("agentAccount");
                agent_acc_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot agent : dataSnapshot.getChildren()) {
                            HashMap<String, String> accountCustIDMap = (HashMap<String, String>) (agent.getValue());
                            if (accountCustIDMap != null) {
                                for (Map.Entry<String, String> each_acc_cust : accountCustIDMap.entrySet()) {
                                    if (each_acc_cust.getKey().equals(account.getNo())) {
                                        curr_agent = agent.getKey();
                                        DatabaseReference date_amount_db_ref = database.getReference("agentCollect").child(curr_agent);
                                        date_amount_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot date : dataSnapshot.getChildren()) {
                                                    Calendar curr_date = MainActivity.StringDateToCal(Objects.requireNonNull(date.getKey()));
                                                    if ((curr_date.after(o_date) && curr_date.before(c_date)) ||
                                                            (curr_date.equals(o_date) || curr_date.equals(c_date))) {
                                                        DateAmount dateAmount = new DateAmount();
                                                        dateAmount.setDate(date.getKey());
                                                        Long curr_amount = ((HashMap<String, Long>) Objects.requireNonNull(date.getValue())).get(account.getNo());

                                                        if (curr_amount != null) {
                                                            dateAmount.setAmount(curr_amount);
                                                            dateAmountList.add(dateAmount);

                                                        }
                                                    }
                                                }
                                                CustomerAmount dummy_cust_amount = new CustomerAmount();
                                                List<Account> accountList1 = new ArrayList<>();
                                                accountList1.add(account);
                                                Customer dummy_customer = (Customer) customer.clone();
                                                dummy_customer.setAccounts1(accountList1);
                                                dummy_cust_amount.setCustomer(dummy_customer);
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                FragmentDateAmount fragmentDateAmount = FragmentDateAmount.newInstance(dateAmountList, curr_agent, dummy_cust_amount);
                                                fragmentTransaction.replace(R.id.fragment_container, fragmentDateAmount).addToBackStack(null).
                                                        commit();
                                                holder.cardView.setEnabled(true);
                                            }


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                holder.cardView.setEnabled(true);
                                            }
                                        });

                                        return;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
    }


    @Override
    public int getItemCount() {
        return accountList.size();
    }

    class AccountHolder extends RecyclerView.ViewHolder {
        TextView lf_field;
        TextView info_field;
        TextView customer_loan_amount_field;
        TextView customer_deposited_field;
        TextView customer_account_type_field;
        TextView customer_account_field;
        TextView loan_duration_field;
        TextView start_date_field;
        TextView end_date_field;
        CardView cardView;
        RelativeLayout account_info_card_layout;

        AccountHolder(View view) {
            super(view);
            lf_field = view.findViewById(R.id.lf_field);
            info_field = view.findViewById(R.id.info_field);
            customer_loan_amount_field = view.findViewById(R.id.customer_loan_amount_field);
            customer_deposited_field = view.findViewById(R.id.customer_deposited_field);
            customer_account_type_field = view.findViewById(R.id.customer_account_type_field);
            customer_account_field = view.findViewById(R.id.customer_account_field);
            loan_duration_field = view.findViewById(R.id.loan_duration_field);
            end_date_field = view.findViewById(R.id.last_date_field);
            start_date_field = view.findViewById(R.id.start_date_field);
            cardView = view.findViewById(R.id.account_info_card);
            account_info_card_layout = view.findViewById(R.id.account_info_card_layout);

        }
    }
}
