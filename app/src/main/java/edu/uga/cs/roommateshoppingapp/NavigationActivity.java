package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * This activity is the main navigation screen after login.
 * It allows the user to go to the shopping list, basket, purchases, or logout.
 */
public class NavigationActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "NavigationActivity";

    private FirebaseAuth mAuth;

    /**
     * Called when the activity is created.
     * Sets up buttons and their click listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onCreate()" );

        Button shoppingListButton = findViewById( R.id.button4 );
        Button basketButton = findViewById( R.id.button5 );
        Button purchasesButton = findViewById(R.id.button6);
        Button logoutButton = findViewById(R.id.button8);

        shoppingListButton.setOnClickListener( new shoppingListClickListener() );
        basketButton.setOnClickListener( new basketClickListener() );
        purchasesButton.setOnClickListener( new purchasesClickListener() );
        logoutButton.setOnClickListener(new logoutClickListener());
    }

    /**
     * Opens the shopping list screen.
     */
    private class shoppingListClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            Intent intent = new Intent( NavigationActivity.this, ReviewShoppingItemsActivity.class );
            startActivity( intent );
        }
    }

    /**
     * Opens the basket screen.
     */
    private class basketClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            Intent intent = new Intent( NavigationActivity.this, ReviewBasketActivity.class );
            startActivity( intent );
        }
    }

    /**
     * Opens the purchases screen.
     */
    private class purchasesClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            Intent intent = new Intent( NavigationActivity.this, ReviewPurchasesActivity.class );
            startActivity( intent );
        }
    }

    /**
     * Logs the user out and returns to the main screen.
     * Clears the back stack so the user cannot go back.
     */
    private class logoutClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    // Lifecycle methods for logging (educational purposes)

    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "ShoppingItem: NavigationActivity.onRestart()" );
        super.onRestart();
    }
}