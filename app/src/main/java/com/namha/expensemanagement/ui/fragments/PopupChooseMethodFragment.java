package com.namha.expensemanagement.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;


public class PopupChooseMethodFragment extends DialogFragment {

    private TransactionCallback callback;

    public void setTransactionCallback(TransactionCallback callback) {
        this.callback = callback;
    }

    private int transactionId; // Store the transaction ID
    private TransactionViewModel transactionViewModel;

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.popup_choose_method);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize ViewModel
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        Button btnDelete = dialog.findViewById(R.id.btnDeleteRecord);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        btnDelete.setOnClickListener(v -> {
            if (transactionId != 0 && getActivity() != null && getActivity().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                handleDeleteAction(); // Thực hiện hành động xóa
            }
            dismiss(); // Đóng dialog sau khi thực hiện
        });

        btnClose.setOnClickListener(v -> dismiss()); // Close the popup

        return dialog;
    }

    // Method to set transaction ID
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    private void handleDeleteAction() {
        // Biến tạm để lưu trữ dữ liệu từ LiveData
        double[] amount = new double[1];
        String[] typeName = new String[1];
        // Get the transaction to be deleted
        transactionViewModel.getTransactionById(transactionId).observe(this, transaction -> {
            if (transaction != null) {
                amount[0] = transaction.getAmount();
                typeName[0] = transaction.getTypeName();

                // Gọi callback để trả dữ liệu ra ngoài
                if (callback != null) {
                    callback.onTransactionFetched(amount[0], typeName[0]);
                }
            } else {
                Log.d("PopupChooseMethodFragment", "Transaction not found with ID: " + transactionId);
            }
        });

        transactionViewModel.getTotalBalance().observe(this, currentTotalAmount -> {
            setTransactionCallback(new TransactionCallback() {

                @Override
                public void onTransactionFetched(final double amount, final String typeName) {
                    if(currentTotalAmount != 0.0){
                        double newTotalAmount = currentTotalAmount;
                        switch (typeName) {
                            case "Tiền chi":
                            case "Cho vay":
                                newTotalAmount += amount;
                                break;
                            case "Thu nhập":
                                newTotalAmount -= amount;
                                break;
                            default:
                                Log.d("PopupChooseMethodFragment", "Invalid type name: " + typeName);
                                return;
                        }

                        // Delete the transaction and update the total balance
                        transactionViewModel.deleteTransactionById(transactionId);
                        transactionViewModel.updateTotalAmount(newTotalAmount);
                    }
                    // Hiển thị thông báo thành công
                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                }
            });

        });


    }


    // Trong PopupChooseMethodFragment
    @Override
    public void onDestroy() {
        super.onDestroy();
        transactionViewModel.getTransactionById(transactionId).removeObservers(this);
        transactionViewModel.getLastTransaction().removeObservers(this);
    }

}

interface TransactionCallback {
    void onTransactionFetched(double amount, String typeName);
}