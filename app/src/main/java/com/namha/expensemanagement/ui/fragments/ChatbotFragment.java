package com.namha.expensemanagement.ui.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.server.ChatService;
import com.namha.expensemanagement.ui.activities.MainActivity;
import com.namha.expensemanagement.ui.adapters.MessageAdapter;
import com.namha.expensemanagement.ui.adapters.SuggestionAdapter;
import com.namha.expensemanagement.viewmodels.MessageModel;
import com.namha.expensemanagement.viewmodels.SharedViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChatbotFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageView sendButton;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList;
    private ChatService chatService;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Runnable retryRunnable;
    private String pendingMessage = "";

    private RecyclerView recyclerViewSuggestions;
    private SuggestionAdapter suggestionAdapter;
    private List<String> suggestionList;

    private SharedViewModel sharedViewModel;
    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatbot_frgament, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        inputMessage = view.findViewById(R.id.message);
        sendButton = view.findViewById(R.id.send);
        recyclerViewSuggestions = view.findViewById(R.id.recyclerViewSuggestions);
        progressBar = view.findViewById(R.id.progressBar);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        chatService = new ChatService();

        sendButton.setOnClickListener(v -> sendMessage());

        setupSuggestions();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // thay đổi màu nền
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        frameLayout = view.findViewById(R.id.frChatbot);

        sharedViewModel.getSelectedColor().observe(getViewLifecycleOwner(), newColor -> {
            if (newColor != null) {
                frameLayout.setBackgroundColor(newColor);
            }
        });
    }

    private void setupSuggestions() {
        suggestionList = new ArrayList<>();

        // Hai câu hỏi cố định
        List<String> fixedSuggestions = new ArrayList<>();
        fixedSuggestions.add("Xin chào!");
        fixedSuggestions.add("Bạn có thể giúp tôi không?");

        // Danh sách các gợi ý tài chính khác
        List<String> dynamicSuggestions = new ArrayList<>();

        // Quản lý chi tiêu cá nhân
        dynamicSuggestions.add("Làm thế nào để theo dõi chi tiêu hằng ngày?");
        dynamicSuggestions.add("Những sai lầm thường gặp khi quản lý tài chính cá nhân?");
        dynamicSuggestions.add("Cách lập ngân sách chi tiêu hiệu quả?");
        dynamicSuggestions.add("Các mẹo tiết kiệm tiền mà vẫn đảm bảo chất lượng cuộc sống?");
        dynamicSuggestions.add("Cách giảm chi tiêu không cần thiết?");
        dynamicSuggestions.add("Làm sao để không tiêu tiền hoang phí?");
        dynamicSuggestions.add("Tại sao cần lập kế hoạch tài chính cá nhân?");
        dynamicSuggestions.add("Ứng dụng nào tốt nhất để theo dõi chi tiêu?");

        // Tiết kiệm & Đầu tư
        dynamicSuggestions.add("Nên gửi tiết kiệm ngân hàng hay đầu tư?");
        dynamicSuggestions.add("Chiến lược tiết kiệm để mua nhà/mua xe?");
        dynamicSuggestions.add("Làm thế nào để tiết kiệm tiền mỗi tháng?");
        dynamicSuggestions.add("Các kênh đầu tư phổ biến hiện nay?");
        dynamicSuggestions.add("Nên đầu tư vào chứng khoán, vàng hay bất động sản?");
        dynamicSuggestions.add("Phân bổ thu nhập như thế nào để hợp lý?");
        dynamicSuggestions.add("Tỷ lệ tiết kiệm và đầu tư nên là bao nhiêu?");
        dynamicSuggestions.add("Các quỹ đầu tư an toàn cho người mới bắt đầu?");

        // Thu nhập & Công việc
        dynamicSuggestions.add("Làm thế nào để tăng thu nhập thụ động?");
        dynamicSuggestions.add("Những kỹ năng tài chính cần có trong thời đại số?");
        dynamicSuggestions.add("Làm sao để đàm phán lương hiệu quả?");
        dynamicSuggestions.add("Có nên nghỉ việc để khởi nghiệp không?");
        dynamicSuggestions.add("Công việc nào có tiềm năng thu nhập cao?");
        dynamicSuggestions.add("Cách tìm kiếm các nguồn thu nhập phụ?");
        dynamicSuggestions.add("Làm sao để tự do tài chính sớm?");

        // Nợ & Quản lý tài chính
        dynamicSuggestions.add("Có nên vay tiền để đầu tư không?");
        dynamicSuggestions.add("Làm sao để thoát khỏi nợ nần?");
        dynamicSuggestions.add("Cách xây dựng điểm tín dụng tốt?");
        dynamicSuggestions.add("Thẻ tín dụng có lợi ích và rủi ro gì?");
        dynamicSuggestions.add("Làm sao để kiểm soát chi tiêu bằng thẻ tín dụng?");
        dynamicSuggestions.add("Có nên trả hết nợ trước khi đầu tư không?");

        // Chi tiêu gia đình & Kế hoạch tài chính dài hạn
        dynamicSuggestions.add("Cách quản lý tài chính trong gia đình?");
        dynamicSuggestions.add("Làm sao để dạy con về tài chính từ sớm?");
        dynamicSuggestions.add("Có nên mua bảo hiểm nhân thọ không?");
        dynamicSuggestions.add("Lập kế hoạch tài chính dài hạn như thế nào?");
        dynamicSuggestions.add("Bao nhiêu tiền là đủ để nghỉ hưu?");

        // Sử dụng thời gian thực để tạo số ngẫu nhiên khác nhau mỗi lần mở app
        Random random = new Random(System.currentTimeMillis());

        // Xáo trộn danh sách động
        Collections.shuffle(dynamicSuggestions, random);

        // Lấy 2 câu ngẫu nhiên từ danh sách động
        List<String> selectedDynamicSuggestions = dynamicSuggestions.subList(0, 2);

        // Kết hợp câu cố định và câu ngẫu nhiên
        List<String> dailySuggestions = new ArrayList<>();
        dailySuggestions.addAll(fixedSuggestions);
        dailySuggestions.addAll(selectedDynamicSuggestions);

        suggestionAdapter = new SuggestionAdapter(dailySuggestions, suggestion -> {
            inputMessage.setText(suggestion);
        });

        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSuggestions.setAdapter(suggestionAdapter);
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
        pendingMessage = inputMessage.getText().toString().trim();
        if (pendingMessage.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tin nhắn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isNetworkConnected()) {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Mất kết nối mạng, tự động gửi khi có kết nối...", Toast.LENGTH_SHORT).show();
            retrySendMessage();
            return;
        }

        progressBar.setVisibility(View.GONE);
        performSendMessage(pendingMessage);
    }

    private void performSendMessage(String message) {
        messageList.add(new MessageModel(message, MessageModel.SENT_BY_ME));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
        inputMessage.setText("");

        MessageModel botTypingMessage = new MessageModel("Typing...", MessageModel.SENT_BY_BOT);
        messageList.add(botTypingMessage);
        int botTypingIndex = messageList.size() - 1;
        messageAdapter.notifyItemInserted(botTypingIndex);
        recyclerView.smoothScrollToPosition(botTypingIndex);

        chatService.sendMessage(message, new ChatService.ChatCallback() {
            @Override
            public void onSuccess(String responseText) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
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
                        progressBar.setVisibility(View.GONE);
                        messageList.set(botTypingIndex, new MessageModel("Lỗi: Không thể nhận phản hồi", MessageModel.SENT_BY_BOT));
                        messageAdapter.notifyItemChanged(botTypingIndex);
                        recyclerView.smoothScrollToPosition(botTypingIndex);
                        Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void retrySendMessage() {
        retryRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkConnected()) {
                    progressBar.setVisibility(View.GONE);
                    performSendMessage(pendingMessage);
                } else {
                    handler.postDelayed(this, 3000);
                }
            }
        };
        handler.postDelayed(retryRunnable, 3000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}