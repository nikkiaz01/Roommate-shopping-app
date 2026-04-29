package edu.uga.cs.roommateshoppingapp;

import static java.lang.Integer.parseInt;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This dialog allows the user to edit a purchase.
 * The user can update the total price and optionally remove an item.
 */
public class EditDeletePurchaseDialog extends DialogFragment {

    private Spinner spinner;
    private EditText priceView;

    int position;     // the position of purchasedGroup in list
    String key;
    String email;
    double price;
    ArrayList<ShoppingItem> items;

    private EditDeletePurchaseDialog.EditDeletePurchaseDialogListener mListener;

    /**
     * Listener interface used to send updated purchase data back to the activity.
     */
    public interface EditDeletePurchaseDialogListener {
        void onUpdatePurchase(int position, double newPrice, int itemToRemoveIndex);
    }

    /**
     * Creates a new instance of the dialog and passes purchase data using a Bundle.
     *
     * @param position position of the purchase in the list
     * @param key database key of the purchase
     * @param email roommate email
     * @param price total price of the purchase
     * @param items list of items in the purchase
     * @return a new EditDeletePurchaseDialog instance
     */
    public static edu.uga.cs.roommateshoppingapp.EditDeletePurchaseDialog newInstance(int position, String key, String email, double price, ArrayList<ShoppingItem> items) {
        edu.uga.cs.roommateshoppingapp.EditDeletePurchaseDialog dialog = new edu.uga.cs.roommateshoppingapp.EditDeletePurchaseDialog();

        // Supply purchase group item values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putDouble("price", price);
        args.putString("email", email);
        args.putSerializable("items", items);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Attaches the listener to the activity.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (EditDeletePurchaseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditDeletePurchaseDialogListener");
        }
    }

    /**
     * Creates and displays the dialog UI.
     * The user can update the price and select an item to remove.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        email = getArguments().getString( "email" );
        price = getArguments().getDouble("price");
        items = (ArrayList<ShoppingItem>) getArguments().getSerializable("items");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.edit_delete_purchase_dialog, getActivity().findViewById( R.id.root ) );

        spinner = layout.findViewById( R.id.remove_item_spinner );
        priceView = layout.findViewById( R.id.edit_total_price );

        /**
         * Populate spinner with item names and a default "None" option.
         */
        List<String> itemNames = new ArrayList<>();
        itemNames.add("None (Select to remove)");
        for (ShoppingItem item : items) {
            itemNames.add(item.getItemName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, itemNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        priceView.setText(String.valueOf(price));

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Purchase" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "SAVE CHANGES", new edu.uga.cs.roommateshoppingapp.EditDeletePurchaseDialog.SaveButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    /**
     * Handles the save button click.
     * Updates the price and optionally removes an item.
     */
    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            double newPrice = 1.0;
            String stringPrice = priceView.getText().toString();

            if (!stringPrice.isEmpty()) {
                newPrice = Double.valueOf(stringPrice);
            }

            int selectedIndex = spinner.getSelectedItemPosition();

            // If they selected an item (index > 0 because of our placeholder)
            if (selectedIndex > 0) {
                mListener.onUpdatePurchase(position, newPrice, selectedIndex - 1);
            } else {
                mListener.onUpdatePurchase(position, newPrice, -1);
            }

            // close the dialog
            dismiss();
        }
    }
}