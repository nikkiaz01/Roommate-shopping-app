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

import java.util.List;

public class PurchaseItemsDialogFragment extends DialogFragment {
    //a purchase group, removing basketItems completely

    private double totalWithTax;

    private PurchaseItemDialogListener mListener;

    public interface PurchaseItemDialogListener {
        void checkOutBasket(PurchaseGroup purchaseItems);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (PurchaseItemDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PurchaseItemDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.purchase_items_dialog, null);

        EditText totalView = layout.findViewById(R.id.editTextNumberDecimal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout).setTitle("Enter total for items in Basket:");
        builder.setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss());
        builder.setPositiveButton("Confirm", (d, w) -> {
            String totalString = totalView.getText().toString();
            if (!totalString.isEmpty()) {
                totalWithTax = Double.parseDouble((totalView.getText().toString()));
                PurchaseGroup purchaseItems = new PurchaseGroup(totalWithTax);
                mListener.checkOutBasket(purchaseItems);
            } else {
                Toast.makeText(getContext(), "Please enter a valid total", Toast.LENGTH_SHORT).show();
            }


        });

        return builder.create();
    }
}