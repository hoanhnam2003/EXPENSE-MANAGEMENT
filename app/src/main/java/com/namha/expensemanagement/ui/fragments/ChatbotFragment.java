package com.namha.expensemanagement.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.server.ChatService;
import com.namha.expensemanagement.ui.activities.MainActivity;
import com.namha.expensemanagement.ui.adapters.MessageAdapter;
import com.namha.expensemanagement.viewmodels.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageView sendButton;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList;
    private ChatService chatService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatbot_frgament, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        inputMessage = view.findViewById(R.id.message);
        sendButton = view.findViewById(R.id.send);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        chatService = new ChatService();

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).findViewById(R.id.fabChatbot).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).findViewById(R.id.fabChatbot).setVisibility(View.VISIBLE);
    }


    private void sendMessage() {
        String userMessage = inputMessage.getText().toString().trim();
        if (userMessage.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tin nhắn!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị tin nhắn của người dùng
        messageList.add(new MessageModel(userMessage, MessageModel.SENT_BY_ME));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
        inputMessage.setText("");

        // Thêm tin nhắn "Đang trả lời..." từ bot
        MessageModel botTypingMessage = new MessageModel("Typing...", MessageModel.SENT_BY_BOT);
        messageList.add(botTypingMessage);
        int botTypingIndex = messageList.size() - 1;
        messageAdapter.notifyItemInserted(botTypingIndex);
        recyclerView.smoothScrollToPosition(botTypingIndex);

        // Gửi tin nhắn đến server
        chatService.sendMessage(userMessage, new ChatService.ChatCallback() {
            @Override
            public void onSuccess(String responseText) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Cập nhật tin nhắn "Đang trả lời..." thành câu trả lời từ bot
                        messageList.set(botTypingIndex, new MessageModel(responseText, MessageModel.SENT_BY_BOT));
                        messageAdapter.notifyItemChanged(botTypingIndex);
                        recyclerView.smoothScrollToPosition(botTypingIndex);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Cập nhật tin nhắn "Đang trả lời..." thành thông báo lỗi
                        messageList.set(botTypingIndex, new MessageModel("Lỗi: Không thể nhận phản hồi", MessageModel.SENT_BY_BOT));
                        messageAdapter.notifyItemChanged(botTypingIndex);
                        recyclerView.smoothScrollToPosition(botTypingIndex);

                        Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

}
