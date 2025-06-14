package com.namha.expensemanagement.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatService {
    // Key API
    private static final String API_KEY = "AIzaSyBV0Mj1zRhvsK4GsqIMaHTkZ-QLgNpL9uw";
    // URL API
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    // Kiểu dữ liệu gửi đi và nhận về
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    // Đối tượng OkHttpClient để thực hiện các yêu cầu HTTP
    private final OkHttpClient client = new OkHttpClient();

    // Interface để xử lý phản hồi bất đồng bộ
    public interface ChatCallback {
        void onSuccess(String responseText);
        void onError(String errorMessage);
    }
    // Hàm gửi tin nhắn đến API
    public void sendMessage(String question, ChatCallback callback) {
        try {
            // Tạo đối tượng chứa văn bản câu hỏi
            JSONObject textObject = new JSONObject();
            textObject.put("text", question);

            // Đóng gói vào mảng "parts"
            JSONObject partObject = new JSONObject();
            partObject.put("parts", new JSONArray().put(textObject));

            // Đóng gói vào mảng "contents"
            JSONArray contentsArray = new JSONArray().put(partObject);

            // Tạo body JSON gửi đến API
            JSONObject body = new JSONObject();
            body.put("contents", contentsArray);

            // Tạo request HTTP POST với body JSON
            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Gửi yêu cầu bất đồng bộ
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Gọi callback khi kết nối thất bại
                    callback.onError("Lỗi kết nối: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Kiểm tra phản hồi có thành công không
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi API: " + response.code() + " - " + response.message());
                        return;
                    }
                    // Đọc nội dung phản hồi
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        // Lấy danh sách "candidates" từ JSON
                        JSONArray candidates = json.optJSONArray("candidates");
                        if (candidates != null && candidates.length() > 0) {
                            // Lấy đối tượng "content" đầu tiên
                            JSONObject message = candidates.getJSONObject(0).optJSONObject("content");
                            if (message != null) {
                                // Lấy phần text từ "parts"
                                JSONArray parts = message.optJSONArray("parts");
                                if (parts != null && parts.length() > 0) {
                                    // Gửi phản hồi thành công về callback
                                    String text = parts.getJSONObject(0).optString("text");
                                    callback.onSuccess(text);
                                    return;
                                }
                            }
                        }
                        // Trường hợp không lấy được dữ liệu đúng định dạng
                        callback.onError("Phản hồi không hợp lệ từ API.");
                    } catch (JSONException e) {
                        // Lỗi khi phân tích JSON
                        callback.onError("Lỗi phân tích JSON: " + e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {
            // Lỗi khi tạo JSON ban đầu
            callback.onError("Lỗi tạo JSON: " + e.getMessage());
        }
    }
}
