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


public class RegisterActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "RegisterActivity";

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById( R.id.editText );
        passwordEditText = findViewById( R.id.editText5 );

        Button registerButton = findViewById(R.id.button3);
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            if (email.isEmpty()) {
                emailEditText.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Enter a valid email address");
                return;
            }

            if (password.isEmpty()) {
                passwordEditText.setError("Password is required");
                return;
            }

            if (password.length() < 6) {
                passwordEditText.setError("Password must be at least 6 characters");
                return;
            }

            // This is how we can create a new user using an email/password combination.
            // Note that we also add an onComplete listener, which will be invoked once
            // a new user has been created by Firebase.  This is how we will know the
            // new user creation succeeded or failed.
            // If a new user has been created, Firebase already signs in the new user;
            // no separate sign in is needed.
            firebaseAuth.createUserWithEmailAndPassword( email, password )
                    .addOnCompleteListener( RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText( getApplicationContext(),
                                        "Registered user: " + email,
                                        Toast.LENGTH_SHORT ).show();

                                // Sign in success, update UI with the signed-in user's information
                                Log.d( DEBUG_TAG, "createUserWithEmail: success" );

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                Intent intent = new Intent( RegisterActivity.this, MainActivity.class );
                                startActivity( intent );

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                                Exception e = task.getException();

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
