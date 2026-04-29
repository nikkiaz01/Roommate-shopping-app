package edu.uga.cs.roommateshoppingapp;

import static java.lang.Integer.parseInt;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * This dialog allows the user to add a new shopping item.
 * The user can enter the item name and quantity.
 */
public class AddShoppingItemDialogFragment extends DialogFragment {

    private EditText itemNameView;
    private EditText quantityView;

    /**
     * Listener interface used to send the new shopping item
     * back to the activity.
     */
    public interface AddShoppingItemDialogListener {
        void addShoppingItem(ShoppingItem shoppingItem);
    }

    /**
     * Creates and displays the dialog UI.
     * The user can enter a new item name and quantity.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_shopping_item_dialog,
                getActivity().findViewById(R.id.root));

        // get the view objects in the AlertDialog
        itemNameView = layout.findViewById( R.id.editText1 );
        quantityView = layout.findViewById( R.id.editText2 );

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "New Shopping Item" );

        // Provide the negative button listener
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        // Provide the positive button listener
        builder.setPositiveButton( android.R.string.ok, new AddShoppingItemListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    /**
     * Handles the add button click.
     * Creates a new ShoppingItem and sends it back to the activity.
     */
    private class AddShoppingItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            // get the new shopping item data from the user
            String itemName = itemNameView.getText().toString();
            String quantityInput = quantityView.getText().toString();

            int quantity = 1; // default quantity is 1
            if (!quantityInput.isEmpty()) {
                quantity = parseInt(quantityInput);
            }

            // create a new ShoppingItem object
            ShoppingItem shoppingItem = new ShoppingItem( itemName, quantity );

            // get the Activity's listener to add the new shopping item
            AddShoppingItemDialogListener listener = (AddShoppingItemDialogListener) getActivity();

            // add the new shopping item
            listener.addShoppingItem( shoppingItem );

            // close the dialog
            dismiss();
        }
    }
}