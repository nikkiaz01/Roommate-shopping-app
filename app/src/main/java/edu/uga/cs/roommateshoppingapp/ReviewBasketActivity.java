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

/**
 * This activity shows all items currently in the user's basket.
 * It allows users to remove items or check out and create a purchase.
 */
public class ReviewBasketActivity extends AppCompatActivity implements PurchaseItemsDialogFragment.PurchaseItemDialogListener {

    private RecyclerView recyclerView;
    private BasketItemRecyclerAdapter recyclerAdapter;
    private ArrayList<ShoppingItem> basketList;
    private FirebaseDatabase database;
    private String currentBasketKey;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in, otherwise send back to login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.email = user.getEmail();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        setContentView(R.layout.activity_review_basket);

        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerView2);
        FloatingActionButton checkoutBtn = findViewById(R.id.floatingActionButton2);

        basketList = new ArrayList<>();
        recyclerAdapter = new BasketItemRecyclerAdapter(basketList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        // If basket is empty, don't allow checkout
        checkoutBtn.setOnClickListener(v -> {
            if (basketList.isEmpty()) {
                Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show();
            } else {
                DialogFragment newFragment = new PurchaseItemsDialogFragment();
                newFragment.show(getSupportFragmentManager(), null);
            }
        });

        // Listen to Firebase and load the current user's basket
        database.getReference("baskets").orderByChild("roommate").equalTo(email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ShoppingItem> basketItems = new ArrayList<>();

                        // Loop through all baskets that match this user
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            currentBasketKey = postSnapshot.getKey(); // save basket key for later updates
                            Basket matchingBasket = postSnapshot.getValue(Basket.class);

                            // Only add items if basket exists and is not null
                            if (matchingBasket != null && matchingBasket.getBasketList() != null) {
                                basketItems.addAll(matchingBasket.getBasketList());
                            }
                        }

                        // Update UI list
                        basketList.clear();
                        basketList.addAll(basketItems);
                        recyclerAdapter.notifyDataSetChanged();

                        if (basketList.isEmpty()) {
                            Log.d("BASKET_SYNC", "Basket is empty in DB for email: " + email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BASKET_SYNC", "Database error: " + error.getMessage());
                    }
                });
    }

    /**
     * Moves an item from the basket back to the shopping list.
     */
    public void moveItemToShoppingList(int position, ShoppingItem itemInBasket) {
        if (currentBasketKey == null) return;

        DatabaseReference shoppingRef = database.getReference("shoppingItems");
        DatabaseReference basketListRef = database.getReference("baskets")
                .child(currentBasketKey)
                .child("basketList");

        // Check if item already exists in shopping list
        shoppingRef.orderByChild("itemName").equalTo(itemInBasket.getItemName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            // If item exists, increase its quantity
                            for (DataSnapshot child : snapshot.getChildren()) {
                                int currentQty = child.child("quantity").getValue(Integer.class);
                                child.getRef().child("quantity")
                                        .setValue(currentQty + itemInBasket.getQuantity());
                                break;
                            }
                        } else {
                            // If not, add it as a new item
                            shoppingRef.push().setValue(itemInBasket);
                        }

                        // Remove item from basket locally and update Firebase
                        if (position < basketList.size()) {
                            basketList.remove(position);

                            // overwrite entire basket list in DB with updated version
                            basketListRef.setValue(basketList).addOnSuccessListener(aVoid ->
                                    Toast.makeText(ReviewBasketActivity.this, "Returned to Shopping List", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Handles checkout of the basket.
     */
    public void checkOutBasket(PurchaseGroup purchaseGroup) {
        if (currentBasketKey == null || basketList.isEmpty()) return;

        // Attach basket items and user info to purchase
        purchaseGroup.setItems(basketList);
        purchaseGroup.setRoommate(email);

        DatabaseReference basketListRef = database.getReference("baskets")
                .child(currentBasketKey);
        DatabaseReference purchaseList = database.getReference("purchaseList");

        // Generate a new key for the purchase
        String newPurchaseKey = purchaseList.push().getKey();
        purchaseGroup.setKey(newPurchaseKey);

        // Save purchase to Firebase
        purchaseList.child(newPurchaseKey).setValue(purchaseGroup)
                .addOnSuccessListener(aVoid -> {

                    // After purchase, remove the basket from DB
                    basketListRef.removeValue().addOnSuccessListener(aVoid2 -> {

                        // Clear UI list after checkout
                        basketList.clear();
                        recyclerAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), "Purchase was a success",
                                Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to complete purchase",
                            Toast.LENGTH_SHORT).show();
                });
    }
}