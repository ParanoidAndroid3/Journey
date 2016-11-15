package com.paranoidandroid.journey.myjourneys.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.paranoidandroid.journey.R;

/**
 * Asks user to confirm log out.
 */
public class LogoutConfirmationDialogFragment extends DialogFragment {
    public interface OnLogoutListener {
        void onLogout();
    }

    private OnLogoutListener listener;

    // Keep this method in case we need to add args.
    public static LogoutConfirmationDialogFragment newInstance() {
        return new LogoutConfirmationDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setMessage(R.string.confirm_logout);

        final AlertDialog dialog = alertBuilder.create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onLogout();
                        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutListener) {
            listener = (OnLogoutListener) context;
        } else {
            throw new IllegalArgumentException(
                    "context must be instance of " + OnLogoutListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}