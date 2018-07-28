package com.oxvsys.moneylender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;

public class GrantLoanDialogFragment extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }


    NoticeDialogListener callback;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(HashMap<String, String> finalGrant_info);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private static final String GRANT_INFO = "grant_info";

    public static GrantLoanDialogFragment newInstance(HashMap<String, String> grant_info) {
        GrantLoanDialogFragment fragment = new GrantLoanDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(GRANT_INFO, grant_info);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        View view = inflater.inflate(R.layout.grant_loan_confirm_dialog, null);
        HashMap<String, String> grant_info = new HashMap<>();
        assert getArguments() != null;
        grant_info = (HashMap<String, String>) getArguments().getSerializable(GRANT_INFO);

        TextView account_value_field = view.findViewById(R.id.grant_loan_account_value);
        TextView customer_field = view.findViewById(R.id.grant_loan_customer_value);
        TextView disbursement_field = view.findViewById(R.id.grant_loan_dis_amount_value);
        TextView file_field = view.findViewById(R.id.grant_loan_file_value);
        TextView type_field = view.findViewById(R.id.grant_loan_type_value);
        TextView duration_field = view.findViewById(R.id.grant_loan_duration_value);

        account_value_field.setText(String.valueOf(Objects.requireNonNull(grant_info).get("account")));
        customer_field.setText(String.valueOf(Objects.requireNonNull(grant_info).get("customer")));
        disbursement_field.setText(String.valueOf(Objects.requireNonNull(grant_info).get("amount")));
        file_field.setText(String.valueOf(Objects.requireNonNull(grant_info).get("file_amount")));

        switch (grant_info.get("type")) {
            case "0":
                duration_field.setText(MessageFormat.format("{0} days", grant_info.get("duration")));
                type_field.setText("Daily basis");
                break;
            case "1":
                duration_field.setText(MessageFormat.format("{0} month(s)", grant_info.get("duration")));
                type_field.setText("Monthly basis");
                break;
            default:
                duration_field.setText(grant_info.get("duration"));
                type_field.setText("Unknown");

                break;
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final HashMap<String, String> finalGrant_info = grant_info;
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        callback.onDialogPositiveClick(finalGrant_info);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GrantLoanDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
