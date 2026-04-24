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


// This is a DialogFragment to handle edits to a ShoppingItem.
// The edits are: updates and deletions of existing ShoppingItems.
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

    // A callback listener interface to finish up the editing of a ShoppingItem.
    // ReviewShoppingItemsActivity implements this listener interface, as it will
    // need to update the list of ShoppingItems and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditShoppingItemDialogListener {
        void updateShoppingItem(int position, ShoppingItem shoppingItem, int action);
    }

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
        // The user will be able to modify them.
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
                // close the dialog
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

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String itemName = itemNameView.getText().toString();
            int quantity = parseInt(quantityView.getText().toString());

            ShoppingItem shoppingItem = new ShoppingItem( itemName, quantity );
            shoppingItem.setKey( key );

            // get the Activity's listener to add the new shopping item
            EditShoppingItemDialogListener listener = (EditShoppingItemDialogListener) getActivity();

            // add the new job lead
            listener.updateShoppingItem( position, shoppingItem, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            ShoppingItem shoppingItem = new ShoppingItem( name, quantity );
            shoppingItem.setKey( key );

            // get the Activity's listener to add the new shopping item
            EditShoppingItemDialogListener listener = (EditShoppingItemDialogListener) getActivity();            // add the new shopping item
            listener.updateShoppingItem( position, shoppingItem, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
