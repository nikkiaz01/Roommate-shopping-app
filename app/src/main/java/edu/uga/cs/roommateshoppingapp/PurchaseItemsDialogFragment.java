package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * This dialog allows the user to enter the total price
 * for all items in the basket during checkout.
 */
public class PurchaseItemsDialogFragment extends DialogFragment {

    // stores the total price entered by the user
    private double totalWithTax;

    private PurchaseItemDialogListener mListener;

    /**
     * Listener interface used to send the completed purchase
     * back to the activity.
     */
    public interface PurchaseItemDialogListener {
        void checkOutBasket(PurchaseGroup purchaseItems);
    }

    /**
     * Attaches the listener to the activity.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (PurchaseItemDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PurchaseItemDialogListener");
        }
    }

    /**
     * Creates and displays the dialog UI.
     * The user enters the total price for the basket items.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.purchase_items_dialog, null);

        EditText totalView = layout.findViewById(R.id.editTextNumberDecimal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout).setTitle("Enter total for items in Basket:");

        // Cancel button closes the dialog
        builder.setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss());

        /**
         * Confirm button reads the entered total,
         * creates a PurchaseGroup, and sends it back to the activity.
         */
        builder.setPositiveButton("Confirm", (d, w) -> {
            String totalString = totalView.getText().toString();

            if (!totalString.isEmpty()) {
                totalWithTax = Double.parseDouble(totalString);

                PurchaseGroup purchaseItems = new PurchaseGroup(totalWithTax);

                mListener.checkOutBasket(purchaseItems);
            } else {
                Toast.makeText(getContext(), "Please enter a valid total", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}