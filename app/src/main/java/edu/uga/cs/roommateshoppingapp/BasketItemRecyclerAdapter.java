package edu.uga.cs.roommateshoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BasketItemRecyclerAdapter extends RecyclerView.Adapter<BasketItemRecyclerAdapter.BasketItemHolder> {

    private ArrayList<ShoppingItem> basketItemList;
    private Context context;

    public BasketItemRecyclerAdapter(ArrayList<ShoppingItem> basketItemList, Context context) {
        this.basketItemList = basketItemList;
        this.context = context;
    }

    class BasketItemHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView quantity;
        Button removeButton;

        public BasketItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            removeButton = itemView.findViewById(R.id.button7);
        }
    }

    @NonNull
    @Override
    public BasketItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new BasketItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketItemHolder holder, int position) {
        ShoppingItem basketItem = basketItemList.get(position);

        holder.itemName.setText(basketItem.getItemName());
        holder.quantity.setText("Qty: " + basketItem.getQuantity());

        MaterialButton materialButton = (MaterialButton) holder.removeButton;
        materialButton.setIcon(null);
        materialButton.setIconPadding(0);
        holder.removeButton.setText("Remove");

        holder.removeButton.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && context instanceof ReviewBasketActivity) {
                ((ReviewBasketActivity) context).moveItemToShoppingList(currentPos, basketItemList.get(currentPos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return basketItemList.size();
    }
}