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
    private static final String API_URL = "https://mentoroid-api.geniam.com/client/chatGPT/test"; // URL API để gửi câu hỏi
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8"); // Kiểu dữ liệu JSON cho request
    private final OkHttpClient client = new OkHttpClient(); // Đối tượng OkHttpClient để gửi request HTTP

    // Interface để xử lý kết quả phản hồi từ API
    public interface ChatCallback {
        void onSuccess(String responseText); // Phương thức callback khi nhận được phản hồi thành công
        void onError(String errorMessage); // Phương thức callback khi có lỗi xảy ra
    }

    // Phương thức gửi tin nhắn đến API
    public void sendMessage(String question, ChatCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(); // Tạo đối tượng JSON để gửi lên API
            jsonObject.put("question", question); // Thêm câu hỏi vào JSON
            jsonObject.put("model", "gpt-4o-2024-05-13"); // Chỉ định model AI sử dụng

            RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON); // Chuyển đổi JSON thành request body
            Request request = new Request.Builder()
                    .url(API_URL) // Thiết lập URL API
                    .post(requestBody) // Gửi request dạng POST
                    .addHeader("Content-Type", "application/json") // Thiết lập header cho request
                    .build();

            // Gửi request bất đồng bộ đến API
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Lỗi kết nối: " + e.getMessage()); // Xử lý lỗi kết nối
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) { // Kiểm tra nếu phản hồi không thành công
                        callback.onError("API lỗi: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string(); // Lấy nội dung phản hồi từ API
                    try {
                        JSONObject responseJson = new JSONObject(responseBody); // Chuyển đổi phản hồi thành JSON
                        if (responseJson.has("data") && responseJson.getJSONObject("data").has("choices")) {
                            JSONArray choices = responseJson.getJSONObject("data").getJSONArray("choices"); // Lấy danh sách lựa chọn từ API
                            if (choices.length() > 0) {
                                String text = choices.getJSONObject(0).getJSONObject("message").getString("content"); // Lấy nội dung phản hồi
                                callback.onSuccess(text);
                                return;
                            }
                        }
                        callback.onError("API không trả về dữ liệu hợp lệ!"); // Xử lý khi không có dữ liệu hợp lệ
                    } catch (JSONException e) {
                        callback.onError("Lỗi phân tích JSON: " + e.getMessage()); // Xử lý lỗi khi phân tích JSON thất bại
                    }
                }
            });

        } catch (JSONException e) {
            callback.onError("Lỗi tạo JSON: " + e.getMessage()); // Xử lý lỗi khi tạo JSON thất bại
        }
    }
}
