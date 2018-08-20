package com.akzarma.moneylender;


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

/**
 * Created by Anish on 20/8/18.
 */

// TODO AKSHAY   Ignore all but onCreateDialog(). Extract data from the received HashMap there
public class CollectConfirmDialogFragment extends DialogFragment{
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
        //Both functions implemented in FragmentCollect
        public void onDialogPositiveClick(HashMap<String, String> finalGrant_info);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private static final String COLLECT_INFO = "collect_info";

    public static CollectConfirmDialogFragment newInstance(HashMap<String, Long> collect_info) {
        CollectConfirmDialogFragment fragment = new CollectConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(COLLECT_INFO, collect_info);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        View view = inflater.inflate(R.layout.collect_loan_confirm_dialog, null);
        HashMap<String, String> collect_info = new HashMap<>();
        assert getArguments() != null;

        //Get data from HashMap here
        collect_info = (HashMap<String, String>) getArguments().getSerializable(COLLECT_INFO);

        //Set text
        TextView collect_amount = view.findViewById(R.id.collect_loan_amount_value);
        collect_amount.setText(Objects.requireNonNull(collect_info).get("collect_amount"));



        final HashMap<String, String> finalGrant_info = collect_info;
        builder.setView(view)

                .setPositiveButton("Collect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Once positive button is clicked, collect loan
                        callback.onDialogPositiveClick(finalGrant_info);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Simply dismiss the fragment
                        CollectConfirmDialogFragment.this.getDialog().cancel();

                        //or execute callback.onNegativeButton();
                    }
                });
        return builder.create();
    }
}
