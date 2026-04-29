package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * This dialog allows the user to select how many of an item
 * they want to move from the shopping list to the basket.
 */
public class AddToBasketDialog extends DialogFragment {

    private EditText quantityView;
    private int position;
    private String key;
    private String name;
    private int quantity;

    /**
     * Listener interface used to send the selected item and quantity
     * back to the activity.
     */
    public interface PurchaseItemDialogListener {
        void moveItemToBasket(int position, ShoppingItem item, int status);
    }

    /**
     * Creates a new instance of the dialog and passes item data using a Bundle.
     *
     * @param position position of the item in the list
     * @param key database key of the item
     * @param name name of the item
     * @param quantity current quantity of the item
     * @return a new AddToBasketDialog instance
     */
    public static AddToBasketDialog newInstance(int position, String key, String name, int quantity) {
        AddToBasketDialog dialog = new AddToBasketDialog();
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putInt("position", position);
        args.putString("name", name);
        args.putInt("quantity", quantity);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Creates and displays the dialog UI.
     * It allows the user to enter a quantity and move the item to the basket.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        key = getArguments().getString("key");
        position = getArguments().getInt("position");
        name = getArguments().getString("name");
        quantity = getArguments().getInt("quantity");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_to_basket_dialog, null);

        quantityView = layout.findViewById(R.id.editText2);
        quantityView.setText(String.valueOf(quantity));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout).setTitle("Add " + name + " to Basket");

        // Cancel button closes the dialog
        builder.setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss());

        // Add to Basket button processes the input
        builder.setPositiveButton("Add to Basket", (d, w) -> {
            int status = 1;
            int finalQty;

            try {
                finalQty = Integer.parseInt(quantityView.getText().toString());
            } catch (NumberFormatException e) {
                finalQty = quantity;
            }

            // If user takes all available quantity, mark item for deletion
            if (quantity <= finalQty) {
                status = 2;
                finalQty = quantity;
            }

            // Create the item to be moved to the basket
            ShoppingItem basketItem = new ShoppingItem(name, finalQty);
            basketItem.setKey(key);

            // Send data back to the activity
            ((PurchaseItemDialogListener) getActivity()).moveItemToBasket(position, basketItem, status);
        });

        return builder.create();
    }
}