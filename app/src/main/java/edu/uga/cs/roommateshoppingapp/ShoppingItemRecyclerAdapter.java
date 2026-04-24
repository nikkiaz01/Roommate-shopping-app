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
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {

    public static final String DEBUG_TAG = "ShoppingItemRecyclerAdapter";

    private List<ShoppingItem> shoppingItemList;
    private Context context;

    public ShoppingItemRecyclerAdapter(List<ShoppingItem> shoppingItemList, Context context ) {
        this.shoppingItemList = shoppingItemList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ShoppingItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView quantity;
        Button purchaseButton;

        public ShoppingItemHolder(View itemView ) {
            super(itemView);

            itemName = itemView.findViewById( R.id.itemName );
            quantity = itemView.findViewById( R.id.quantity );
            purchaseButton = itemView.findViewById(R.id.button7);
        }
    }

    @NonNull
    @Override
    public ShoppingItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.shopping_item, parent, false );
        return new ShoppingItemHolder( view );
    }

    // This method fills in the values of the Views to show a JobLead
    @Override
    public void onBindViewHolder( ShoppingItemHolder holder, int position ) {
        ShoppingItem shoppingItem = shoppingItemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + shoppingItem );

        String key = shoppingItem.getKey();
        String name = shoppingItem.getItemName();
        int quantity = shoppingItem.getQuantity();

        holder.itemName.setText( shoppingItem.getItemName());
        holder.quantity.setText( "Qty: " + shoppingItem.getQuantity() );


        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the shopping items shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditShoppingItemDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                EditShoppingItemDialogFragment editShoppingItemFragment =
                        EditShoppingItemDialogFragment.newInstance( holder.getAdapterPosition(), key, name, quantity );
                editShoppingItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });

        holder.purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToBasketDialog purchaseItemDialogFragment =
                        AddToBasketDialog.newInstance( holder.getAdapterPosition(), key, name, quantity );
                purchaseItemDialogFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }
}
