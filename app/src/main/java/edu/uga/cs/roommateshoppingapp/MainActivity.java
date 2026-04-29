package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Main activity of the app.
 * Handles user sign-in and navigation to registration.
 */
public class MainActivity extends AppCompatActivity
        implements UserSignInDialogFragment.SignInDialogListener {

    public static final String DEBUG_TAG = "MainActivity";

    private FirebaseAuth mAuth;

    /**
     * Called when the activity is created.
     * Sets up UI and button listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d( DEBUG_TAG, "JobLead: MainActivity.onCreate()" );

        Button signInButton = findViewById( R.id.button1 );
        Button registerButton = findViewById( R.id.button2 );

        signInButton.setOnClickListener( new SignInButtonClickListener() );
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }

    /**
     * Handles user sign-in using Firebase authentication.
     *
     * @param email user email
     * @param password user password
     */
    @Override
    public void signIn( String email, String password )
    {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword( email, password )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d( DEBUG_TAG, "signInWithEmail:success" );

                            // Go to navigation screen after successful login
                            Intent intent = new Intent( MainActivity.this, NavigationActivity.class );
                            intent.putExtra("email", email);
                            startActivity( intent );
                        }
                        else {
                            Log.d( DEBUG_TAG, "signInWithEmail:failure", task.getException() );
                            Toast.makeText( MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Opens the sign-in dialog when sign-in button is clicked.
     */
    private class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            DialogFragment newFragment = new UserSignInDialogFragment();
            newFragment.show( getSupportFragmentManager(), null );
        }
    }

    /**
     * Opens the registration screen when register button is clicked.
     */
    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    // Lifecycle methods for logging (educational purposes)

    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onRestart()" );
        super.onRestart();
    }
}