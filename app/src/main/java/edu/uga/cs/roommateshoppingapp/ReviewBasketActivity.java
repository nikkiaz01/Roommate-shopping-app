package edu.uga.cs.roommateshoppingapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ReviewBasketActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BasketItemRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> basketList;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                // Requirement 10: Trigger Checkout Dialog here
                Toast.makeText(this, "Checkout coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        // Sync with Firebase Basket
        database.getReference("basketItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                basketList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = postSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        item.setKey(postSnapshot.getKey());
                        basketList.add(item);
                    }
                }
                recyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void moveItemToShoppingList(int position, ShoppingItem itemInBasket) {
        DatabaseReference shoppingRef = database.getReference("shoppingItems");
        DatabaseReference basketRef = database.getReference("basketItems").child(itemInBasket.getKey());

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
                        // Requirement 9: Remove from basket
                        basketRef.removeValue().addOnSuccessListener(aVoid ->
                                Toast.makeText(ReviewBasketActivity.this, "Returned to Shopping List", Toast.LENGTH_SHORT).show()
                        );
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}