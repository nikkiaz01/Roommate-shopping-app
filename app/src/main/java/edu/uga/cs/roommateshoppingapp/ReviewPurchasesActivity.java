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
import java.util.HashMap;

/**
 * This activity shows all completed purchases.
 * Users can edit purchases, remove items, and settle costs between roommates.
 */
public class ReviewPurchasesActivity extends AppCompatActivity implements EditDeletePurchaseDialog.EditDeletePurchaseDialogListener, SettleCostDialog.SettleCostDialogListener {

    private RecyclerView recyclerView;
    private PurchaseItemsRecyclerAdapter recyclerAdapter;
    private ArrayList<PurchaseGroup> purchasesList;
    private FirebaseDatabase database;

    private double totalSpent = 0.0;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in, otherwise redirect to login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.email = user.getEmail();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        setContentView(R.layout.activity_review_purchases);

        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerView2);
        FloatingActionButton checkoutBtn = findViewById(R.id.floatingActionButton2);

        purchasesList = new ArrayList<>();
        recyclerAdapter = new PurchaseItemsRecyclerAdapter(purchasesList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        // When user clicks settle button
        checkoutBtn.setOnClickListener(v -> {
            if (purchasesList.isEmpty()) {
                Toast.makeText(this, "No purchases to settle!", Toast.LENGTH_SHORT).show();
            } else {

                // Calculate how much each roommate paid
                HashMap<String, Double> roommateMap = calculateSettleUp(purchasesList);

                // Open dialog to show results
                DialogFragment newFragment = SettleCostDialog.newInstance(totalSpent, roommateMap);
                newFragment.show(getSupportFragmentManager(), null);
            }
        });

        // Load purchases from Firebase
        database.getReference("purchaseList").orderByChild("roommate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<PurchaseGroup> purchaseItems = new ArrayList<>();

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            PurchaseGroup indivPurchaseGroup = postSnapshot.getValue(PurchaseGroup.class);

                            if (indivPurchaseGroup != null) {

                                // Prevent crash if Firebase returns null list
                                if (indivPurchaseGroup.getItems() == null) {
                                    indivPurchaseGroup.setItems(new ArrayList<>());
                                }

                                purchaseItems.add(indivPurchaseGroup);
                            }
                        }

                        // Update UI
                        purchasesList.clear();
                        purchasesList.addAll(purchaseItems);
                        recyclerAdapter.notifyDataSetChanged();

                        if (purchasesList.isEmpty()) {
                            Log.d("BASKET_SYNC", "Purchases is empty in DB");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PURCHASES_SYNC", "Database error: " + error.getMessage());
                    }
                });
    }

    /**
     * Updates a purchase when user edits or removes an item.
     */
    public void onUpdatePurchase(int position, double newPrice, int itemToRemoveIndex) {

        PurchaseGroup editedGroup = purchasesList.get(position);
        editedGroup.setTotalWithTax(newPrice);

        DatabaseReference shoppingRef = database.getReference("shoppingItems");

        final ShoppingItem removedItem;

        // If user chose to remove an item
        if (itemToRemoveIndex != -1) {

            removedItem = editedGroup.getItems().get(itemToRemoveIndex);
            editedGroup.getItems().remove(itemToRemoveIndex);

            // If no items left → delete entire purchase
            if (editedGroup.getItems().isEmpty()) {

                // Add item back to shopping list before deleting purchase
                if (removedItem != null) {
                    shoppingRef.orderByChild("itemName").equalTo(removedItem.getItemName())
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists()) {
                                        // Merge quantities if item already exists
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            Integer currentQty = child.child("quantity").getValue(Integer.class);
                                            if (currentQty == null) currentQty = 0;

                                            child.getRef().child("quantity")
                                                    .setValue(currentQty + removedItem.getQuantity());
                                            break;
                                        }
                                    } else {
                                        // Otherwise create new item
                                        shoppingRef.push().setValue(removedItem);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                }

                // Delete purchase from Firebase
                database.getReference("purchaseList")
                        .child(editedGroup.getKey())
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Purchase removed (no items left)", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIREBASE_DELETE", "Failed: " + e.getMessage());
                        });

                return;
            }

        } else {
            removedItem = null;
        }

        // Update purchase in Firebase
        database.getReference("purchaseList")
                .child(editedGroup.getKey())
                .setValue(editedGroup)
                .addOnSuccessListener(aVoid -> {

                    // If item was removed, add it back to shopping list
                    if (removedItem != null) {
                        shoppingRef.orderByChild("itemName").equalTo(removedItem.getItemName())
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                Integer currentQty = child.child("quantity").getValue(Integer.class);
                                                if (currentQty == null) currentQty = 0;

                                                child.getRef().child("quantity")
                                                        .setValue(currentQty + removedItem.getQuantity());
                                                break;
                                            }
                                        } else {
                                            shoppingRef.push().setValue(removedItem);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                    }

                    Toast.makeText(this, "Purchase updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_UPDATE", "Failed: " + e.getMessage());
                });
    }

    /**
     * Calculates how much each roommate paid.
     */
    public HashMap<String, Double> calculateSettleUp(ArrayList<PurchaseGroup> purchases) {

        totalSpent = 0;

        HashMap<String, Double> roommatePaidMap = new HashMap<>();

        // Loop through all purchases
        for (PurchaseGroup pg : purchases) {

            String roommate = pg.getRoommate();
            double amount = pg.getTotalWithTax();

            totalSpent += amount;

            // Add amount to that roommate's total
            double currentTotal = roommatePaidMap.getOrDefault(roommate, 0.0);
            roommatePaidMap.put(roommate, currentTotal + amount);
        }

        return roommatePaidMap;
    }

    /**
     * Clears all purchases after settling.
     */
    public void clearPurchases() {

        database.getReference("purchaseList")
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    purchasesList.clear();
                    Toast.makeText(this, "Recently Purchased Settled Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_UPDATE", "Failed: " + e.getMessage());
                });
    }
}