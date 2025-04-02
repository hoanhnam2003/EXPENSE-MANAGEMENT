package com.namha.expensemanagement.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.namha.expensemanagement.R;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
    private List<String> suggestions;
    private OnItemClickListener listener;

    // Xác định giao diện cho item
    public interface OnItemClickListener {
        void onItemClick(String suggestion);
    }

    // Constructor
    public SuggestionAdapter(List<String> suggestions, OnItemClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    // Gắn dữ liệu vào item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String suggestion = suggestions.get(position);
        holder.suggestionText.setText(suggestion);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(suggestion));
    }

    // Số lượng item
    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    // ViewHolder cho item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView suggestionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestionText = itemView.findViewById(R.id.suggestionText);
        }
    }
}
