package com.namha.expensemanagement.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.namha.expensemanagement.databinding.ItemHistoryBinding;
import com.namha.expensemanagement.dto.History;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

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
            // Tạo DecimalFormatSymbols và đặt dấu phân cách là ','
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setGroupingSeparator(','); // Dùng dấu ',' cho phân cách hàng nghìn

            DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

            binding.tvHistoryBuyContent.setText(item.getNameCategory() != null ? item.getNameCategory() : "N/A");
            binding.tvHistoryBuy1.setText(item.getContent() != null ? item.getContent() : "No content");
            binding.tvTime.setText(item.getDate() != null ? item.getDate() : "No date");
            binding.tvspend.setText(item.getTypeName() != null ? item.getTypeName() : "Unknown type");

            // Format số tiền theo chuẩn mong muốn
            String amount = decimalFormat.format(item.getAmount());
            binding.tvAmount.setText(amount != null ? amount : "0");

            binding.tvThreedots.setOnClickListener(v -> listener.onThreeDotsClick(item.getId()));
        }
    }
}
