package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.namha.expensemanagement.R;

public class SharedViewModel extends AndroidViewModel {
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_SELECTED_COLOR = "SelectedColor";

    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;

    public SharedViewModel(Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);

        // Lấy màu đã lưu khi khởi động app
        int savedColor = sharedPreferences.getInt(KEY_SELECTED_COLOR, ContextCompat.getColor(application.getBaseContext(), R.color.hongthongke)); // Mặc định là màu trắng
        selectedColor.setValue(savedColor);
    }

    public void setSelectedColor(int color) {
        selectedColor.setValue(color);
        // Lưu màu vào SharedPreferences
        sharedPreferences.edit().putInt(KEY_SELECTED_COLOR, color).apply();
    }

    public LiveData<Integer> getSelectedColor() {
        return selectedColor;
    }

    public void resetSelectedColor() {
        int defaultColor = ContextCompat.getColor(getApplication(), R.color.hongthongke);

        // Xóa màu sắc đã lưu khỏi SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SELECTED_COLOR);
        editor.apply();

        // Cập nhật LiveData để giao diện thay đổi ngay lập tức
        selectedColor.setValue(defaultColor);
    }
}
