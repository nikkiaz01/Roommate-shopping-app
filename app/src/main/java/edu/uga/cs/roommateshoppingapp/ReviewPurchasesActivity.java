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
import java.util.Map;

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

        checkoutBtn.setOnClickListener(v -> {
            if (purchasesList.isEmpty()) {
                Toast.makeText(this, "No purchases to settle!", Toast.LENGTH_SHORT).show();
            } else {

                HashMap<String, Double> roommateMap = calculateSettleUp(purchasesList);

                DialogFragment newFragment = SettleCostDialog.newInstance(totalSpent, roommateMap);
                newFragment.show( getSupportFragmentManager(), null);
            }
        });

        // Sync with Firebase Basket
        // Inside onCreate...
        database.getReference("purchaseList").orderByChild("roommate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<PurchaseGroup> purchaseItems = new ArrayList<>();

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            PurchaseGroup indivPurchaseGroup = postSnapshot.getValue(PurchaseGroup.class);

                            if (indivPurchaseGroup != null) {
                                if (indivPurchaseGroup.getItems() == null) {
                                    indivPurchaseGroup.setItems(new ArrayList<>());
                                }
                                purchaseItems.add(indivPurchaseGroup);
                            }
                        }

                        // Swap the lists and notify
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

    public void onUpdatePurchase(int position, double newPrice, int itemToRemoveIndex) {

        PurchaseGroup editedGroup = purchasesList.get(position);
        editedGroup.setTotalWithTax(newPrice);
        DatabaseReference shoppingRef = database.getReference("shoppingItems");

        final ShoppingItem removedItem;
        if (itemToRemoveIndex != -1) {
            removedItem = editedGroup.getItems().get(itemToRemoveIndex);
            editedGroup.getItems().remove(itemToRemoveIndex);
            if (editedGroup.getItems().isEmpty()) {
                if (removedItem != null) {
                    shoppingRef.orderByChild("itemName").equalTo(removedItem.getItemName())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            Integer currentQty = child.child("quantity").getValue(Integer.class);
                                            if (currentQty == null) {
                                                currentQty = 0;
                                            }
                                            child.getRef().child("quantity").setValue(currentQty + removedItem.getQuantity());
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

        // 3. Sync to Firebase
        database.getReference("purchaseList")
                .child(editedGroup.getKey())
                .setValue(editedGroup)
                .addOnSuccessListener(aVoid -> {
                    if (removedItem != null) {
                        shoppingRef.orderByChild("itemName").equalTo(removedItem.getItemName())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                Integer currentQty = child.child("quantity").getValue(Integer.class);
                                                if (currentQty == null) {
                                                    currentQty = 0;
                                                }
                                                child.getRef().child("quantity").setValue(currentQty + removedItem.getQuantity());
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
     * Calculates total spending and the net balance for each roommate.
     * Positive value = Roommate is owed money (they overpaid).
     * Negative value = Roommate owes money (they underpaid).
     */
    public HashMap<String, Double> calculateSettleUp(ArrayList<PurchaseGroup> purchases) {
        totalSpent = 0;
        // Map to aggregate totals: Key = Roommate, Value = Sum of all their purchases
        HashMap<String, Double> roommatePaidMap = new HashMap<>();

        // 1. Aggregate totals (This handles the "Duplicates")
        for (PurchaseGroup pg : purchases) {
            String roommate = pg.getRoommate();
            double amount = pg.getTotalWithTax();

            totalSpent += amount;

            // if roommate exists, add to their total or start at 0.0
            double currentTotal = roommatePaidMap.getOrDefault(roommate, 0.0);
            roommatePaidMap.put(roommate, currentTotal + amount); //update amt in map
        }
        return roommatePaidMap;
    }
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