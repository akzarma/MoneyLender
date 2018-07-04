package com.oxvsys.moneylender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        public void onDialogPositiveClick();
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

        View view = inflater.inflate(R.layout.grant_loan_confirm_dialog,null);
        HashMap<String , String> grant_info = new HashMap<>();
        assert getArguments() != null;
        grant_info = (HashMap<String, String>) getArguments().getSerializable(GRANT_INFO);

        TextView account_value = view.findViewById(R.id.grant_loan_account_value);
        account_value.setText(String.valueOf(Objects.requireNonNull(grant_info).get("Account")));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        callback.onDialogPositiveClick();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GrantLoanDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.grant_loan_confirm_dialog, container, false);


        // Do all the stuff to initialize your custom view

        return v;
    }
}
