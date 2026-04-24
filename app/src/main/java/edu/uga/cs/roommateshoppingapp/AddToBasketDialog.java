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

public class AddToBasketDialog extends DialogFragment {

    private EditText quantityView;
    private int position;
    private String key;
    private String name;
    private int quantity;

    public interface PurchaseItemDialogListener {
        void moveItemToBasket(int position, ShoppingItem item, int status);
    }

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
        builder.setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss());
        builder.setPositiveButton("Add to Basket", (d, w) -> {
                int status = 1;
                int finalQty;

                try {
                    finalQty = Integer.parseInt(quantityView.getText().toString());
                } catch (NumberFormatException e) {
                    finalQty = quantity;
                }

                if (quantity <= finalQty) {
                    status = 2; // DELETE
                    finalQty = quantity;
                }

                ShoppingItem basketItem = new ShoppingItem(name, finalQty);
                basketItem.setKey(key);
                ((PurchaseItemDialogListener) getActivity()).moveItemToBasket(position, basketItem, status);
            });

        return builder.create();
    }
}