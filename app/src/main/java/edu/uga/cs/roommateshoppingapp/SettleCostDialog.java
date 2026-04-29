package edu.uga.cs.roommateshoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * This dialog shows how much each roommate paid and how much they owe.
 * It calculates the average cost per roommate and determines balances.
 */
public class SettleCostDialog extends DialogFragment {

    private TextView totalPriceView;
    private TextView roommatePaidView;
    private TextView averageRoommateView;
    private TextView roommateOwedView;

    HashMap<String, Double> roommateTotals;

    Double totalOwed;
    Double avgPerRoommate;

    private SettleCostDialogListener mListener;

    /**
     * Listener used to clear all purchases after settlement.
     */
    public interface SettleCostDialogListener {
        void clearPurchases();
    }

    /**
     * Creates a new dialog instance with total spent and roommate totals.
     *
     * @param totalOwed total amount spent by all roommates
     * @param roommateTotals map of each roommate and how much they paid
     * @return dialog instance
     */
    public static SettleCostDialog newInstance(Double totalOwed, HashMap<String, Double> roommateTotals) {
        SettleCostDialog dialog = new SettleCostDialog();

        Bundle args = new Bundle();
        args.putDouble("totalOwed", totalOwed);
        args.putSerializable("roommateTotals", roommateTotals);
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
            mListener = (SettleCostDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SettleCostDialogListener");
        }
    }

    /**
     * Creates and displays the dialog UI.
     * Performs all calculations for splitting costs.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        totalOwed = getArguments().getDouble("totalOwed");
        roommateTotals = (HashMap<String, Double>) getArguments().getSerializable("roommateTotals");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.settle_cost_dialog, getActivity().findViewById(R.id.root));

        totalPriceView = layout.findViewById(R.id.text2);
        roommateOwedView = layout.findViewById(R.id.text8);
        roommatePaidView = layout.findViewById(R.id.text4);
        averageRoommateView = layout.findViewById(R.id.text6);

        // Show total spent
        totalPriceView.setText(String.format("$%.2f", totalOwed));

        int numberRoommates = 0;
        String roommatePaid = "";

        // Build string showing how much each roommate paid
        for (Map.Entry<String, Double> entry : roommateTotals.entrySet()) {
            String name = entry.getKey();
            Double total = entry.getValue();

            roommatePaid = roommatePaid + name + ":\n Paid: " + String.format("$%.2f", total) + "\n";

            numberRoommates++;
        }

        roommatePaidView.setText(roommatePaid);

        // Calculate average cost per roommate
        avgPerRoommate = Math.round((totalOwed / numberRoommates) * 100.0) / 100.0;

        averageRoommateView.setText(String.format("$%.2f", avgPerRoommate));

        String roommateOwed = "";

        // Calculate how much each roommate owes
        for (Map.Entry<String, Double> entry : roommateTotals.entrySet()) {

            String name = entry.getKey();
            Double total = entry.getValue();

            // If they paid less than average → they owe money
            // If they paid more than average → they owe $0
            Double owe = Math.round((avgPerRoommate - total) * 100.0) / 100.0;

            if (owe <= 0.0){
                roommateOwed = roommateOwed + name + ":\n Owes: $0.00\n";
            } else {
                roommateOwed = roommateOwed + name + ":\n Owes: " + String.format("$%.2f", owe) + "\n";
            }
        }

        roommateOwedView.setText(roommateOwed);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout);

        builder.setTitle("Settle Purchases");

        // Cancel button
        builder.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss());

        // Confirm button → clears all purchases
        builder.setPositiveButton("CONFIRM", new SaveButtonClickListener());

        return builder.create();
    }

    /**
     * Handles confirm button click.
     * Clears all purchases after settlement.
     */
    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            mListener.clearPurchases();

            dismiss();
        }
    }
}