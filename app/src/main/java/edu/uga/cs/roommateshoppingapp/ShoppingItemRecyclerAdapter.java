package edu.uga.cs.roommateshoppingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter used to display shopping items.
 * Each item shows its name, quantity, and options to edit or move to basket.
 */
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {

    public static final String DEBUG_TAG = "ShoppingItemRecyclerAdapter";

    private List<ShoppingItem> shoppingItemList;
    private Context context;

    /**
     * Constructor for the adapter.
     *
     * @param shoppingItemList list of shopping items
     * @param context activity context
     */
    public ShoppingItemRecyclerAdapter(List<ShoppingItem> shoppingItemList, Context context ) {
        this.shoppingItemList = shoppingItemList;
        this.context = context;
    }

    /**
     * ViewHolder class that holds references to each item view.
     */
    class ShoppingItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView quantity;
        Button purchaseButton;

        public ShoppingItemHolder(View itemView ) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            purchaseButton = itemView.findViewById(R.id.button7);
        }
    }

    /**
     * Creates a new ViewHolder when needed.
     */
    @NonNull
    @Override
    public ShoppingItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item, parent, false);
        return new ShoppingItemHolder(view);
    }

    /**
     * Binds data to each shopping item in the list.
     * Displays item info and sets click behaviors.
     */
    @Override
    public void onBindViewHolder(ShoppingItemHolder holder, int position) {

        ShoppingItem shoppingItem = shoppingItemList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String name = shoppingItem.getItemName();
        int quantity = shoppingItem.getQuantity();

        holder.itemName.setText(name);
        holder.quantity.setText("Qty: " + quantity);

        /**
         * Clicking the whole item opens edit/delete dialog.
         */
        holder.itemView.setOnClickListener(v -> {
            EditShoppingItemDialogFragment editShoppingItemFragment =
                    EditShoppingItemDialogFragment.newInstance(
                            holder.getAdapterPosition(),
                            key,
                            name,
                            quantity
                    );

            editShoppingItemFragment.show(
                    ((AppCompatActivity)context).getSupportFragmentManager(),
                    null
            );
        });

        /**
         * Clicking the button opens dialog to move item to basket.
         */
        holder.purchaseButton.setOnClickListener(v -> {
            AddToBasketDialog purchaseItemDialogFragment =
                    AddToBasketDialog.newInstance(
                            holder.getAdapterPosition(),
                            key,
                            name,
                            quantity
                    );

            purchaseItemDialogFragment.show(
                    ((AppCompatActivity)context).getSupportFragmentManager(),
                    null
            );
        });
    }

    /**
     * Returns the number of items in the list.
     */
    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }
}