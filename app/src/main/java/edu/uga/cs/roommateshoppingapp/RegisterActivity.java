package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This activity allows a user to register for a new account.
 * It collects email and password, validates input, and creates a Firebase user.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "RegisterActivity";

    private EditText emailEditText;
    private EditText passwordEditText;

    /**
     * Called when the activity is created.
     * Initializes UI components and button listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById( R.id.editText );
        passwordEditText = findViewById( R.id.editText5 );

        Button registerButton = findViewById(R.id.button3);
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }

    /**
     * Handles register button click.
     * Validates input and creates a new user in Firebase.
     */
    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            // Validate email input
            if (email.isEmpty()) {
                emailEditText.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Enter a valid email address");
                return;
            }

            // Validate password input
            if (password.isEmpty()) {
                passwordEditText.setError("Password is required");
                return;
            }

            if (password.length() < 6) {
                passwordEditText.setError("Password must be at least 6 characters");
                return;
            }

            /**
             * Creates a new user using Firebase Authentication.
             * If successful, the user is automatically signed in.
             */
            firebaseAuth.createUserWithEmailAndPassword( email, password )
                    .addOnCompleteListener( RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText( getApplicationContext(),
                                        "Registered user: " + email,
                                        Toast.LENGTH_SHORT ).show();

                                Log.d( DEBUG_TAG, "createUserWithEmail: success" );

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                // Go back to main screen after registration
                                Intent intent = new Intent( RegisterActivity.this, MainActivity.class );
                                startActivity( intent );

                            } else {

                                Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                                Exception e = task.getException();

                                // Handle duplicate email case
                                if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                    emailEditText.setError("This email already exists. Try logging in instead.");
                                } else {
                                    Toast.makeText(RegisterActivity.this,
                                            "Registration failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }
}