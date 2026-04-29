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
 * This dialog allows the user to edit or delete a shopping item.
 * The user can update the item name and quantity or remove the item completely.
 */
public class EditShoppingItemDialogFragment extends DialogFragment {

    // indicate the type of the edit
    public static final int SAVE = 1;   // update an existing shopping item
    public static final int DELETE = 2; // delete an existing shopping item

    private EditText itemNameView;
    private EditText quantityView;

    int position;     // the position of the edited ShoppingItem on the list of shopping items
    String key;
    String name;
    int quantity;

    /**
     * Listener interface used to send updated or deleted item data back to the activity.
     */
    public interface EditShoppingItemDialogListener {
        void updateShoppingItem(int position, ShoppingItem shoppingItem, int action);
    }

    /**
     * Creates a new instance of the dialog and passes item data using a Bundle.
     *
     * @param position position of the item in the list
     * @param key database key of the item
     * @param name name of the item
     * @param quantity quantity of the item
     * @return a new EditShoppingItemDialogFragment instance
     */
    public static EditShoppingItemDialogFragment newInstance(int position, String key, String name, int quantity) {
        EditShoppingItemDialogFragment dialog = new EditShoppingItemDialogFragment();

        // Supply shopping item values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("name", name);
        args.putInt("quantity", quantity);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Creates and displays the dialog UI.
     * The user can edit item name and quantity or delete the item.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        name = getArguments().getString( "name" );
        quantity = getArguments().getInt( "quantity" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.add_shopping_item_dialog, getActivity().findViewById( R.id.root ) );

        itemNameView = layout.findViewById( R.id.editText1 );
        quantityView = layout.findViewById( R.id.editText2 );

        // Pre-fill the edit texts with the current values for this shopping item.
        itemNameView.setText( name );
        quantityView.setText(String.valueOf(quantity));

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Shopping Item" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    /**
     * Handles the save button click.
     * Updates the item name and quantity.
     */
    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String itemName = itemNameView.getText().toString();
            int quantity = parseInt(quantityView.getText().toString());

            ShoppingItem shoppingItem = new ShoppingItem( itemName, quantity );
            shoppingItem.setKey( key );

            EditShoppingItemDialogListener listener = (EditShoppingItemDialogListener) getActivity();

            listener.updateShoppingItem( position, shoppingItem, SAVE );

            dismiss();
        }
    }

    /**
     * Handles the delete button click.
     * Removes the item from the list.
     */
    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            ShoppingItem shoppingItem = new ShoppingItem( name, quantity );
            shoppingItem.setKey( key );

            EditShoppingItemDialogListener listener = (EditShoppingItemDialogListener) getActivity();
            listener.updateShoppingItem( position, shoppingItem, DELETE );

            dismiss();
        }
    }
}