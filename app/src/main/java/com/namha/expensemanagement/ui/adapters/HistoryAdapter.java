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

    // Constructor chấp nhận OnThreeDotsClickListener
    public HistoryAdapter(List<History> items, OnThreeDotsClickListener listener) {
        this.items = items;
        this.threeDotsClickListener = listener;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    // Đảm bảo mục không phải là null
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = items.get(position);
        if (item != null) {
            holder.bind(item, position, threeDotsClickListener);
        }
    }

    // Xử lý trường hợp khi các mục là null
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Thông báo adapter để làm mới giao diện
    public void setItems(List<History> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // Xác định giao diện cho trình lắng nghe nhấn vào nút ba chấm
    public interface OnThreeDotsClickListener {
        void onThreeDotsClick(int position);
    }

    // ViewHolder cho item
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryBinding binding;

        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Gắn dữ liệu vào item
        public void bind(History item, int position, OnThreeDotsClickListener listener) {
            // Tạo DecimalFormatSymbols và đặt dấu phân cách là ','
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setGroupingSeparator(','); // Dùng dấu ',' cho phân cách hàng nghìn

            DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

            binding.tvHistoryBuyContent.setText(item.getNameCategory() != null ? item.getNameCategory() : "N/A");
            binding.tvHistoryBuy1.setText(item.getContent() != null ? item.getContent() : "No content");
            binding.tvTime.setText(item.getDate() != null ? item.getDate() : "No date");
            binding.tvspend.setText(item.getTypeName() != null ? item.getTypeName() : "Unknown type");

            // Format số tiền theo chuẩn mong muốn và thêm đơn vị VND
            String amount = decimalFormat.format(item.getAmount()) + " VND";
            binding.tvAmount.setText(amount);

            binding.tvThreedots.setOnClickListener(v -> listener.onThreeDotsClick(item.getId()));
        }
    }
}
