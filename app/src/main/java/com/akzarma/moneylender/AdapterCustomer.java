package com.akzarma.moneylender;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterCustomer extends RecyclerView.Adapter<AdapterCustomer.CustomerHolder> {

    private List<Customer> customerList;
    Context context;
    private FragmentManager fragmentManager;

    AdapterCustomer(List<Customer> customerList, Context context, FragmentManager fragmentManager) {
        Collections.sort(customerList, new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        this.customerList = customerList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_customer, parent, false);
        return new CustomerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCustomer.CustomerHolder holder, int position) {
        final Customer customer = customerList.get(position);
        holder.cust_id_field.setText(customer.getId());
        int name_len = customer.getName().split(" ").length;
        String cust_name = "No Name";
        if (name_len > 1) {
            cust_name = customer.getName().split(" ")[0] + ' ' +
                    customer.getName().split(" ")[name_len - 1];
        } else if (name_len == 1) {
            cust_name = customer.getName().split(" ")[0];
        }
        holder.cust_name_field.setText(cust_name);

        holder.customer_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentCustomerInfo far = FragmentCustomerInfo.newInstance(customer);
                fragmentTransaction.replace(R.id.fragment_container, far).addToBackStack(null).
                        commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        TextView cust_name_field;
        TextView cust_id_field;
        CardView customer_card;

        CustomerHolder(View view) {
            super(view);
            cust_name_field = view.findViewById(R.id.cust_name_field);
            cust_id_field = view.findViewById(R.id.cust_id_field);
            customer_card = view.findViewById(R.id.customer_card);


        }
    }
}
