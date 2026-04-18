package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class UserSignInDialogFragment extends DialogFragment {
    private EditText emailView;
    private EditText passwordView;

    public interface SignInDialogListener {
        void signIn( String email, String password );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.signin_dialog, null);

        // get the view objects in the AlertDialog
        emailView = layout.findViewById( R.id.editTextText );
        passwordView = layout.findViewById( R.id.editTextTextPassword );


        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        // Set its view (inflated above).
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Sign In" );
        // Provide the negative button listener
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton( android.R.string.ok, new SignInListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SignInListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();


            UserSignInDialogFragment.SignInDialogListener listener = (UserSignInDialogFragment.SignInDialogListener) getActivity();

            listener.signIn( email, password );

            // close the dialog
            dismiss();
        }
    }

}
