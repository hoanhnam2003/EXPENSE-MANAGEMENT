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
        private static final String API_URL = "https://mentoroid-api.geniam.com/client/chatGPT/test";
        private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        private final OkHttpClient client = new OkHttpClient();

        public interface ChatCallback {
            void onSuccess(String responseText);
            void onError(String errorMessage);
        }

        public void sendMessage(String question, ChatCallback callback) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("question", question);
                jsonObject.put("model", "gpt-4o-2024-05-13");

                RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(requestBody)
                        .addHeader("Content-Type", "application/json")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onError("Lỗi kết nối: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            callback.onError("API lỗi: " + response.code());
                            return;
                        }

                        String responseBody = response.body().string();
                        try {
                            JSONObject responseJson = new JSONObject(responseBody);
                            if (responseJson.has("data") && responseJson.getJSONObject("data").has("choices")) {
                                JSONArray choices = responseJson.getJSONObject("data").getJSONArray("choices");
                                if (choices.length() > 0) {
                                    String text = choices.getJSONObject(0).getJSONObject("message").getString("content");
                                    callback.onSuccess(text);
                                    return;
                                }
                            }
                            callback.onError("API không trả về dữ liệu hợp lệ!");
                        } catch (JSONException e) {
                            callback.onError("Lỗi phân tích JSON: " + e.getMessage());
                        }
                    }
                });

            } catch (JSONException e) {
                callback.onError("Lỗi tạo JSON: " + e.getMessage());
            }
        }
    }
