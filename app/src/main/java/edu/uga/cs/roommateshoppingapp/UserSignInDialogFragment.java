package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * This dialog allows the user to sign in.
 * It collects the user's email and password and sends them to the activity.
 */
public class UserSignInDialogFragment extends DialogFragment {

    private EditText emailView;
    private EditText passwordView;

    /**
     * Listener interface used to send login credentials
     * back to the activity.
     */
    public interface SignInDialogListener {
        void signIn(String email, String password);
    }

    /**
     * Creates and displays the sign-in dialog UI.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate dialog layout
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.signin_dialog, null);

        // Get input fields
        emailView = layout.findViewById(R.id.editTextText);
        passwordView = layout.findViewById(R.id.editTextTextPassword);

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout);

        builder.setTitle("Sign In");

        // Cancel button → closes dialog
        builder.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss());

        // OK button → sends data back to activity
        builder.setPositiveButton(android.R.string.ok, new SignInListener());

        return builder.create();
    }

    /**
     * Handles sign-in button click.
     * Sends email and password to the activity.
     */
    private class SignInListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            // Get user input
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();

            // Send data to activity
            SignInDialogListener listener = (SignInDialogListener) getActivity();
            listener.signIn(email, password);

            // Close dialog
            dismiss();
        }
    }
}