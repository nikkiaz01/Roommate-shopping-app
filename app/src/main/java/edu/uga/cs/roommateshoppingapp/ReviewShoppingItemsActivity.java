package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * This is an activity class for listing the current job leads.
 * The current job leads are listed as a RecyclerView.
 */
public class ReviewShoppingItemsActivity
        extends AppCompatActivity
        implements AddShoppingItemDialogFragment.AddShoppingItemDialogListener, EditShoppingItemDialogFragment.EditShoppingItemDialogListener,
        AddToBasketDialog.PurchaseItemDialogListener
{

    public static final String DEBUG_TAG = "ReviewJobLeadsActivity";

    private RecyclerView recyclerView;
    private ShoppingItemRecyclerAdapter recyclerAdapter;

    private List<ShoppingItem> shoppingItemsList;

    private FirebaseDatabase database;

    private String email;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.email = user.getEmail();
        } else {
            // If no one is logged in, send them back to Login
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        Log.d( DEBUG_TAG, "onCreate()" );

        setContentView( R.layout.activity_review_shopping_items );

        recyclerView = findViewById( R.id.recyclerView );

        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddShoppingItemDialogFragment();
                newFragment.show( getSupportFragmentManager(), null);
            }
        });

        // initialize the Shopping Items list
        shoppingItemsList = new ArrayList<ShoppingItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping items is empty at first; it will be updated later
        recyclerAdapter = new ShoppingItemRecyclerAdapter( shoppingItemsList, ReviewShoppingItemsActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingItems");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain shopping items.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our shopping item list.
                shoppingItemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    ShoppingItem shoppingItem = postSnapshot.getValue(ShoppingItem.class);
                    shoppingItem.setKey( postSnapshot.getKey() );
                    shoppingItemsList.add( shoppingItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + shoppingItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }

    // this is our own callback for a AddShoppingItemDialogFragment which adds a new shopping item.
    public void addShoppingItem(ShoppingItem shoppingItem) {
        // add the new shopping item
        // Add a new element (ShoppingItem) to the list of shopping items in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingItems");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new job lead.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain shopping items.
        myRef.push().setValue( shoppingItem )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the ShoppingItem most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post( new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition( shoppingItemsList.size()-1 );
                            }
                        } );

                        Log.d( DEBUG_TAG, "Shopping item saved: " + shoppingItem );
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Shopping item created for " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getApplicationContext(), "Failed to create a shopping item for " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // This is our own callback for a DialogFragment which edits an existing ShoppingItem.
    // The edit may be an update or a deletion of this ShoppingItem.
    // It is called from the EditShoppingItemDialogFragment.
    public void updateShoppingItem( int position, ShoppingItem shoppingItem, int action ) {
        if( action == EditShoppingItemDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );

            // Update the recycler view to show the changes in the updated shopping item in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this shopping item in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "shoppingItems" )
                    .child( shoppingItem.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain shopping items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( shoppingItem ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "Shopping item updated for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditShoppingItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );

            // remove the deleted shopping item from the list (internal list in the App)
            shoppingItemsList.remove( position );

            // Update the recycler view to remove the deleted shopping item from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this shopping item in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "shoppingItems" )
                    .child( shoppingItem.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain shopping items .
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "Shopping item deleted for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void moveItemToBasket(int position, ShoppingItem itemInBasket, int status) {
        DatabaseReference basketRef = database.getReference("baskets");

        basketRef.orderByChild("roommate").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Basket existingBasket = child.getValue(Basket.class);
                                if (existingBasket != null) {
                                    ArrayList<ShoppingItem> basketList = existingBasket.getBasketList();
                                    if (basketList == null) {
                                        basketList = new ArrayList<>();
                                    }

                                    boolean found = false;
                                    for (ShoppingItem s : basketList) {
                                        if (s.getItemName().equalsIgnoreCase(itemInBasket.getItemName())) {
                                            s.setQuantity(s.getQuantity() + itemInBasket.getQuantity());
                                            found = true;
                                            break;
                                        }
                                    }

                                    if (!found) {
                                        basketList.add(itemInBasket);
                                    }

                                    // FIX 2: Ensure this child name matches your Basket class getter
                                    // If your class is getBasketList(), this should be "basketList"
                                    child.getRef().child("basketList").setValue(basketList)
                                            .addOnSuccessListener(aVoid -> finalizeMove(position, itemInBasket, status));
                                }
                                break;
                            }
                        } else { //no basket for this roommate
                            ArrayList<ShoppingItem> newList = new ArrayList<>();
                            newList.add(itemInBasket);
                            Basket newBasket = new Basket(newList, email);

                            basketRef.push().setValue(newBasket)
                                    .addOnSuccessListener(aVoid -> finalizeMove(position, itemInBasket, status));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(DEBUG_TAG, "Basket query failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Helper to handle the UI and Shopping List update after basket logic is done.
     */
    private void finalizeMove(int position, ShoppingItem itemInBasket, int status) {
        Toast.makeText(this, itemInBasket.getItemName() + " moved to basket", Toast.LENGTH_SHORT).show();

        // Calculate remaining quantity for the Shopping List
        ShoppingItem originalItem = shoppingItemsList.get(position);
        int remainingQty = originalItem.getQuantity() - itemInBasket.getQuantity();

        // Create the update object for the Shopping List
        ShoppingItem updateItem = new ShoppingItem(itemInBasket.getItemName(), remainingQty);
        updateItem.setKey(itemInBasket.getKey());

        updateShoppingItem(position, updateItem, status);
    }
}
