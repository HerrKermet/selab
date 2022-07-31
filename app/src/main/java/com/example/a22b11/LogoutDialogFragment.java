package com.example.a22b11;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class LogoutDialogFragment extends DialogFragment {
    final private Runnable okRunnable;

    LogoutDialogFragment(Runnable okRunnable) {
        this.okRunnable = okRunnable;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_logout)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    okRunnable.run();
                })
                .setNegativeButton(R.string.cancel, null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
