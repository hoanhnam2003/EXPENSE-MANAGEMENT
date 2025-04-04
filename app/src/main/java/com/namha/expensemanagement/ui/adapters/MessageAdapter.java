package com.namha.expensemanagement.ui.adapters;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.viewmodels.MessageModel;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {
    List<MessageModel>modelList;

    // Constructor
    public MessageAdapter(List<MessageModel> modelList) {
        this.modelList = modelList;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null);
        return new viewHolder(view);
    }

    // Gắn dữ liệu vào item
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MessageModel model = modelList.get(position);

        if (model.getSentBy().equals(MessageModel.SENT_BY_ME)) {
            // Người dùng gửi tin nhắn
            holder.leftChat.setVisibility(View.GONE);
            holder.rightChat.setVisibility(View.VISIBLE);
            holder.rightText.setText(model.getMessage());

            // Sao chép khi nhấn giữ tin nhắn của người dùng
            holder.rightText.setOnLongClickListener(v -> {
                copyToClipboard(v, model.getMessage());
                return true;
            });

        } else {
            // Chatbot trả lời
            holder.rightChat.setVisibility(View.GONE);
            holder.leftChat.setVisibility(View.VISIBLE);
            holder.leftText.setText(model.getMessage());

            // Sao chép khi nhấn giữ tin nhắn của chatbot
            holder.leftText.setOnLongClickListener(v -> {
                copyToClipboard(v, model.getMessage());
                return true;
            });
        }
    }

    // Số lượng item
    @Override
    public int getItemCount() {
        return modelList.size();
    }

    // Tạo ViewHolder
    public class viewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout leftChat, rightChat;
        TextView leftText, rightText;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            leftChat = itemView.findViewById(R.id.left_chat);
            rightChat = itemView.findViewById(R.id.right_chat);
            leftText = itemView.findViewById(R.id.left_text);
            rightText = itemView.findViewById(R.id.right_text);
        }
    }
    // sao chép câu trả lời và câu hỏi
    private void copyToClipboard(View v, String text) {
        ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(v.getContext(), "Đã sao chép tin nhắn", Toast.LENGTH_SHORT).show();
    }
}
