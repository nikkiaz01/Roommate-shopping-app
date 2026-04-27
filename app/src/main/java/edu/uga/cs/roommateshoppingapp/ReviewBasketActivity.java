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

public class ReviewBasketActivity extends AppCompatActivity implements PurchaseItemsDialogFragment.PurchaseItemDialogListener{

    private RecyclerView recyclerView;
    private BasketItemRecyclerAdapter recyclerAdapter;
    private ArrayList<ShoppingItem> basketList;
    private FirebaseDatabase database;
    private String currentBasketKey;

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
        setContentView(R.layout.activity_review_basket);
        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerView2);
        FloatingActionButton checkoutBtn = findViewById(R.id.floatingActionButton2);
        basketList = new ArrayList<>();
        recyclerAdapter = new BasketItemRecyclerAdapter(basketList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        checkoutBtn.setOnClickListener(v -> {
            if (basketList.isEmpty()) {
                Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show();
            } else {
                DialogFragment newFragment = new PurchaseItemsDialogFragment();
                newFragment.show( getSupportFragmentManager(), null);
            }
        });

        // Sync with Firebase Basket
        // Inside onCreate...
        database.getReference("baskets").orderByChild("roommate").equalTo(email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ShoppingItem> basketItems = new ArrayList<>();

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            currentBasketKey = postSnapshot.getKey();
                            Basket matchingBasket = postSnapshot.getValue(Basket.class);

                            if (matchingBasket != null && matchingBasket.getBasketList() != null) {
                                basketItems.addAll(matchingBasket.getBasketList());
                            }
                        }

                        // Swap the lists and notify
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

    public void moveItemToShoppingList(int position, ShoppingItem itemInBasket) {
        if (currentBasketKey == null) return;

        DatabaseReference shoppingRef = database.getReference("shoppingItems");
        DatabaseReference basketListRef = database.getReference("baskets")
                        .child(currentBasketKey)
                        .child("basketList");

        // Check for duplicates in shopping list to merge before deleting from basket
        shoppingRef.orderByChild("itemName").equalTo(itemInBasket.getItemName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                int currentQty = child.child("quantity").getValue(Integer.class);
                                child.getRef().child("quantity").setValue(currentQty + itemInBasket.getQuantity());
                                break;
                            }
                        } else {
                            shoppingRef.push().setValue(itemInBasket);
                        }
                        if (position < basketList.size()) {
                            basketList.remove(position);
                            // overwrite the Firebase list with the new updated list
                            basketListRef.setValue(basketList).addOnSuccessListener(aVoid ->
                                    Toast.makeText(ReviewBasketActivity.this, "Returned to Shopping List", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void checkOutBasket(PurchaseGroup purchaseGroup) {
        if (currentBasketKey == null|| basketList.isEmpty()) { return;}

        purchaseGroup.setItems(basketList);
        purchaseGroup.setRoommate(email);

        DatabaseReference basketListRef = database.getReference("baskets")
                .child(currentBasketKey);
        DatabaseReference purchaseList = database.getReference("purchaseList");

        String newPurchaseKey = purchaseList.push().getKey();
        purchaseGroup.setKey(newPurchaseKey);

        purchaseList.child(newPurchaseKey).setValue( purchaseGroup )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show a quick confirmation
                        // Swap the lists and notify
                        basketListRef.removeValue().addOnSuccessListener(aVoid2 -> {
                                    // 4. Update local UI
                                    basketList.clear();
                                    recyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "Purchase was a success",
                                    Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getApplicationContext(), "Failed to complete purchase",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
}

