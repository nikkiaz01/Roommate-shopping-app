package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NavigationActivity extends AppCompatActivity
         {

    public static final String DEBUG_TAG = "NavigationActivity";

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onCreate()" );

        Button shoppingListButton = findViewById( R.id.button4 );
        Button basketButton = findViewById( R.id.button5 );
        Button purchasesButton = findViewById(R.id.button6);
        Button logoutButton = findViewById(R.id.button8);

        shoppingListButton.setOnClickListener( new shoppingListClickListener() );
        basketButton.setOnClickListener( new basketClickListener() );
        purchasesButton.setOnClickListener( new purchasesClickListener() );
        logoutButton.setOnClickListener(new logoutClickListener());
    }


    private class shoppingListClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            Intent intent = new Intent( NavigationActivity.this, ReviewShoppingItemsActivity.class );
            startActivity( intent );
        }
    }

     private class basketClickListener implements View.OnClickListener {
         @Override
         public void onClick( View v ) {
             Intent intent = new Intent( NavigationActivity.this, ReviewBasketActivity.class );
             startActivity( intent );
         }
     }

     private class purchasesClickListener implements View.OnClickListener {
         @Override
         public void onClick( View v ) {
             Intent intent = new Intent( NavigationActivity.this, ReviewPurchasesActivity.class );
             startActivity( intent );
         }
     }
     private class logoutClickListener implements View.OnClickListener {
         @Override
         public void onClick( View v ) {
             Intent intent = new Intent( NavigationActivity.this, MainActivity.class );
             startActivity( intent );
         }
     }


    // These activity callback methods are not needed and are for edational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "JobLead: NavigationActivity.onRestart()" );
        super.onRestart();
    }
}
