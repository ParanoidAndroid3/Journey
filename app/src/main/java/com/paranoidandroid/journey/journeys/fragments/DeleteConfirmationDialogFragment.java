package com.paranoidandroid.journey.journeys.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.paranoidandroid.journey.R;

/**
 * Asks user to confirm deletion of a journey.
 *
 * Note: this fragment expects to be a child fragment.
 */
public class DeleteConfirmationDialogFragment extends DialogFragment {
    private static final String ARG_JOURNEY_NAME = "com.paranoidandroid.journey.JOURNEY_NAME";
    private static final String ARG_JOURNEY_ID = "com.paranoidandroid.journey.JOURNEY_ID";

    public interface OnDeleteListener {
        void onDelete(String journeyId);
    }

    public static DeleteConfirmationDialogFragment newInstance(
            String journeyName, String journeyObjectId) {
        DeleteConfirmationDialogFragment fragment = new DeleteConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JOURNEY_NAME, journeyName);
        args.putString(ARG_JOURNEY_ID, journeyObjectId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

        String journeyName = getArguments().getString(ARG_JOURNEY_NAME);
        String message = getResources().getString(R.string.confirm_delete, journeyName);
        alertBuilder.setMessage(message);

        final AlertDialog dialog = alertBuilder.create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notifyDelete();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
        return dialog;
    }

    private void notifyDelete() {
        OnDeleteListener listener = (OnDeleteListener) getTargetFragment();
        Bundle args = getArguments();
        String journeyId = args.getString(ARG_JOURNEY_ID);
        listener.onDelete(journeyId);
        dismiss();
    }
}
