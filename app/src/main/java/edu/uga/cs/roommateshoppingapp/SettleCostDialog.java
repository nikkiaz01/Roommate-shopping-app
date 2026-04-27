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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettleCostDialog extends DialogFragment {


    private TextView totalPriceView;
    private TextView roommatePaidView;
    private TextView averageRoommateView;
    private TextView roommateOwedView;

    HashMap<String, Double> roommateTotals;

    Double totalOwed;

    Double avgPerRoommate;

    private SettleCostDialog.SettleCostDialogListener mListener;

    // A callback listener interface to finish up the editing of a ShoppingItem.
    // ReviewShoppingItemsActivity implements this listener interface, as it will
    // need to update the list of ShoppingItems and also update the RecyclerAdapter to reflect the
    // changes.
    public interface SettleCostDialogListener {
        void clearPurchases();
    }

    public static edu.uga.cs.roommateshoppingapp.SettleCostDialog newInstance( Double totalOwed, HashMap<String, Double> roommateTotals) {
        edu.uga.cs.roommateshoppingapp.SettleCostDialog dialog = new edu.uga.cs.roommateshoppingapp.SettleCostDialog();

        // Supply purchase group item values as an argument.
        Bundle args = new Bundle();
        args.putDouble("totalOwed", totalOwed);
        args.putSerializable("roommateTotals", roommateTotals);
        dialog.setArguments(args);

        return dialog;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (SettleCostDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SettleCostDialogListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        totalOwed = getArguments().getDouble("totalOwed");
        roommateTotals = (HashMap<String, Double>) getArguments().getSerializable("roommateTotals");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.settle_cost_dialog, getActivity().findViewById( R.id.root ) );

        totalPriceView = layout.findViewById( R.id.text2);
        roommateOwedView = layout.findViewById( R.id.text8);
        roommatePaidView = layout.findViewById( R.id.text4);
        averageRoommateView = layout.findViewById( R.id.text6);

        totalPriceView.setText(String.format("$%.2f", totalOwed));
        int numberRoommates = 0;
        String roommatePaid = "";
        for (Map.Entry<String, Double> entry : roommateTotals.entrySet()) {
            String name = entry.getKey();
            Double total = entry.getValue();
            roommatePaid = roommatePaid + name + ":\n Paid: " + String.format("$%.2f", total) + "\n";
            numberRoommates ++;
        }
        roommatePaidView.setText(roommatePaid);

        avgPerRoommate = Math.round((totalOwed / numberRoommates) * 100.0) / 100.0;
        averageRoommateView.setText(String.format("$%.2f", avgPerRoommate));
        String roommateOwed = "";
        for (Map.Entry<String, Double> entry : roommateTotals.entrySet()) {
            String name = entry.getKey();
            Double total = entry.getValue();
            Double owe = Math.round((avgPerRoommate - total) * 100.0) / 100.0;

            if (owe <= 0.0){
                roommateOwed = roommateOwed + name + ":\n Owes: $0.00\n";
            } else {
                roommateOwed = roommateOwed + name + ":\n Owes: " + String.format("$%.2f", owe) + "\n";
            }
        }
        roommateOwedView.setText(roommateOwed);


        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Settle Purchases" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "CONFIRM", new edu.uga.cs.roommateshoppingapp.SettleCostDialog.SaveButtonClickListener() );


        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {


                mListener.clearPurchases();



            // close the dialog
            dismiss();
        }
    }

}
