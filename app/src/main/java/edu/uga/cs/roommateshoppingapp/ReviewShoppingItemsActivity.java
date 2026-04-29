package edu.uga.cs.roommateshoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * This activity displays the shopping list.
 * Users can add, update, delete items, and move items to the basket.
 */
public class ReviewShoppingItemsActivity
        extends AppCompatActivity
        implements AddShoppingItemDialogFragment.AddShoppingItemDialogListener,
        EditShoppingItemDialogFragment.EditShoppingItemDialogListener,
        AddToBasketDialog.PurchaseItemDialogListener {

    public static final String DEBUG_TAG = "ReviewJobLeadsActivity";

    private RecyclerView recyclerView;
    private ShoppingItemRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> shoppingItemsList;
    private FirebaseDatabase database;
    private String email;

    /**
     * Called when the activity is created.
     * Initializes UI, checks login, and loads shopping items from Firebase.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.email = user.getEmail();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        Log.d(DEBUG_TAG, "onCreate()");
        setContentView(R.layout.activity_review_shopping_items);

        recyclerView = findViewById(R.id.recyclerView);

        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(v -> {
            DialogFragment newFragment = new AddShoppingItemDialogFragment();
            newFragment.show(getSupportFragmentManager(), null);
        });

        shoppingItemsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerAdapter = new ShoppingItemRecyclerAdapter(shoppingItemsList, this);
        recyclerView.setAdapter(recyclerAdapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingItems");

        // Load items from Firebase and update UI
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                shoppingItemsList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ShoppingItem shoppingItem = postSnapshot.getValue(ShoppingItem.class);

                    // Save key so we can update/delete later
                    shoppingItem.setKey(postSnapshot.getKey());

                    shoppingItemsList.add(shoppingItem);
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Failed: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Adds a new shopping item to Firebase.
     *
     * @param shoppingItem the item to be added
     */
    public void addShoppingItem(ShoppingItem shoppingItem) {

        DatabaseReference myRef = database.getReference("shoppingItems");

        // push() creates a unique item in Firebase
        myRef.push().setValue(shoppingItem)
                .addOnSuccessListener(aVoid -> {

                    // Scroll to show newly added item
                    recyclerView.post(() ->
                            recyclerView.smoothScrollToPosition(shoppingItemsList.size() - 1)
                    );

                    Toast.makeText(getApplicationContext(),
                            "Shopping item created for " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Failed to create item",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates or deletes a shopping item in Firebase.
     *
     * @param position position of the item in the list
     * @param shoppingItem updated item
     * @param action indicates SAVE or DELETE
     */
    public void updateShoppingItem(int position, ShoppingItem shoppingItem, int action) {

        if (action == EditShoppingItemDialogFragment.SAVE) {

            recyclerAdapter.notifyItemChanged(position);

            // Update item using its key
            DatabaseReference ref = database
                    .getReference()
                    .child("shoppingItems")
                    .child(shoppingItem.getKey());

            ref.setValue(shoppingItem);

        } else if (action == EditShoppingItemDialogFragment.DELETE) {

            shoppingItemsList.remove(position);
            recyclerAdapter.notifyItemRemoved(position);

            // Remove item from Firebase
            DatabaseReference ref = database
                    .getReference()
                    .child("shoppingItems")
                    .child(shoppingItem.getKey());

            ref.removeValue();
        }
    }

    /**
     * Moves an item from the shopping list to the basket.
     * Handles merging quantities if item already exists.
     *
     * @param position position in shopping list
     * @param itemInBasket item being moved
     * @param status indicates whether to delete or update original item
     */
    public void moveItemToBasket(int position, ShoppingItem itemInBasket, int status) {

        DatabaseReference basketRef = database.getReference("baskets");

        basketRef.orderByChild("roommate").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            for (DataSnapshot child : snapshot.getChildren()) {

                                Basket existingBasket = child.getValue(Basket.class);
                                ArrayList<ShoppingItem> basketList = existingBasket.getBasketList();

                                if (basketList == null) {
                                    basketList = new ArrayList<>();
                                }

                                boolean found = false;

                                // Merge quantities if item exists
                                for (ShoppingItem s : basketList) {
                                    if (s.getItemName().equalsIgnoreCase(itemInBasket.getItemName())) {
                                        s.setQuantity(s.getQuantity() + itemInBasket.getQuantity());
                                        found = true;
                                        break;
                                    }
                                }

                                // Add new item if not found
                                if (!found) {
                                    basketList.add(itemInBasket);
                                }

                                child.getRef().child("basketList").setValue(basketList)
                                        .addOnSuccessListener(aVoid ->
                                                finalizeMove(position, itemInBasket, status));

                                break;
                            }

                        } else {
                            // Create new basket if none exists
                            ArrayList<ShoppingItem> newList = new ArrayList<>();
                            newList.add(itemInBasket);

                            Basket newBasket = new Basket(newList, email);

                            basketRef.push().setValue(newBasket)
                                    .addOnSuccessListener(aVoid ->
                                            finalizeMove(position, itemInBasket, status));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(DEBUG_TAG, "Basket query failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Updates the shopping list after moving an item to the basket.
     * Adjusts remaining quantity or deletes the item if needed.
     *
     * @param position position in list
     * @param itemInBasket item moved
     * @param status indicates update or delete
     */
    private void finalizeMove(int position, ShoppingItem itemInBasket, int status) {

        Toast.makeText(this,
                itemInBasket.getItemName() + " moved to basket",
                Toast.LENGTH_SHORT).show();

        ShoppingItem originalItem = shoppingItemsList.get(position);
        int remainingQty = originalItem.getQuantity() - itemInBasket.getQuantity();

        ShoppingItem updateItem = new ShoppingItem(itemInBasket.getItemName(), remainingQty);
        updateItem.setKey(itemInBasket.getKey());

        updateShoppingItem(position, updateItem, status);
    }
}