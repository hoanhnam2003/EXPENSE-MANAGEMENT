package com.namha.expensemanagement.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.namha.expensemanagement.databinding.ItemHistoryBinding;
import com.namha.expensemanagement.dto.History;

import java.text.DecimalFormat;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> items;
    private final OnThreeDotsClickListener threeDotsClickListener;

    // Constructor accepting the OnThreeDotsClickListener
    public HistoryAdapter(List<History> items, OnThreeDotsClickListener listener) {
        this.items = items;
        this.threeDotsClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = items.get(position);
        if (item != null) {
            holder.bind(item, position, threeDotsClickListener); // Ensure item is not null
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0; // Handle case when items is null
    }

    public void setItems(List<History> items) {
        this.items = items;
        notifyDataSetChanged(); // Thông báo adapter để làm mới giao diện
    }

    // Define the interface for the click listener
    public interface OnThreeDotsClickListener {
        void onThreeDotsClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryBinding binding;

        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(History item, int position, OnThreeDotsClickListener listener) {
            // Khởi tạo DecimalFormat với định dạng mong muốn
            DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

            // Bind data to views with null checks
            binding.tvHistoryBuyContent.setText(item.getNameCategory() != null ? item.getNameCategory() : "N/A"); // Handle null for nameCategory
            binding.tvHistoryBuy1.setText(item.getContent() != null ? item.getContent() : "No content"); // Handle null for content
            binding.tvTime.setText(item.getDate() != null ? item.getDate() : "No date"); // Handle null for date
            binding.tvspend.setText(item.getTypeName() != null ? item.getTypeName() : "Unknown type"); // Handle null for type_name

            // Format số tiền và gán vào TextView
            String amount = decimalFormat.format(item.getAmount());
            binding.tvAmount.setText(amount != null ? amount : "0.00"); // Bind amount, handle null case

            // Set the click listener for tvThreedots, check if listener is not null
            binding.tvThreedots.setOnClickListener(v -> listener.onThreeDotsClick(item.getId())); // Pass the transaction ID
        }
    }
}
