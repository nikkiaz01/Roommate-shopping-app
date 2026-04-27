package edu.uga.cs.roommateshoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

    public class PurchaseItemsRecyclerAdapter extends RecyclerView.Adapter<edu.uga.cs.roommateshoppingapp.PurchaseItemsRecyclerAdapter.PurchaseItemHolder> {

        private ArrayList<PurchaseGroup> purchaseGroupsList;
        private Context context;

        public PurchaseItemsRecyclerAdapter(ArrayList<PurchaseGroup> purchaseGroupsList, Context context) {
            this.purchaseGroupsList = purchaseGroupsList;
            this.context = context;
        }

        class PurchaseItemHolder extends RecyclerView.ViewHolder {
            TextView purchaseName;
            TextView price;
            TextView roommate;
            TextView items;
            Button removeButton;

            public PurchaseItemHolder(View itemView) {
                super(itemView);
                purchaseName = itemView.findViewById(R.id.purchaseName);
                price = itemView.findViewById(R.id.price);
                roommate = itemView.findViewById(R.id.textView5);
                items = itemView.findViewById(R.id.items);
                removeButton = itemView.findViewById(R.id.button7);
            }
        }

        @NonNull
        @Override
        public edu.uga.cs.roommateshoppingapp.PurchaseItemsRecyclerAdapter.PurchaseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_group_card, parent, false);
            return new edu.uga.cs.roommateshoppingapp.PurchaseItemsRecyclerAdapter.PurchaseItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull edu.uga.cs.roommateshoppingapp.PurchaseItemsRecyclerAdapter.PurchaseItemHolder holder, int position) {
            PurchaseGroup purchaseGroupItem = purchaseGroupsList.get(position);

            holder.purchaseName.setText("Purchase made on " + purchaseGroupItem.getTimestamp());
            holder.roommate.setText("By: " + purchaseGroupItem.getRoommate());
            holder.price.setText("Total: $" + purchaseGroupItem.getTotalWithTax());
            ArrayList<ShoppingItem> boughtItems = purchaseGroupItem.getItems();
            String itemsString = "Items:\n";
            for (ShoppingItem s : boughtItems){
                    itemsString = itemsString + s.getItemName() + " - Qty: " + s.getQuantity() + "\n";
            }
            holder.items.setText(itemsString);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditDeletePurchaseDialog editDeletePurchaseDialogFrag =
                            EditDeletePurchaseDialog.newInstance( holder.getAdapterPosition(), purchaseGroupItem.getKey(), purchaseGroupItem.getRoommate(), purchaseGroupItem.getTotalWithTax(), purchaseGroupItem.getItems() );
                    editDeletePurchaseDialogFrag.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
                }
            });
        }

        @Override
        public int getItemCount() {
            return purchaseGroupsList.size();
        }
    }

