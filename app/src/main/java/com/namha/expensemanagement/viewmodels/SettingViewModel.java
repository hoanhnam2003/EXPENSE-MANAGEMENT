package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.namha.expensemanagement.database.entities.Setting;
import com.namha.expensemanagement.repository.SettingRepository;

import java.util.List;

public class SettingViewModel extends AndroidViewModel {
    private final SettingRepository repository;
    private final String TAG = "SettingViewModel";
    private final LiveData<List<Setting>> allSettings;

    public SettingViewModel(@NonNull Application application) {
        super(application);

        // Khởi tạo repository và kiểm tra null
        repository = new SettingRepository(application);
        if (repository != null) {
            allSettings = repository.getAllSettings();
        } else {
            Log.e(TAG, "Repository is null");
            allSettings = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng nếu repository null
        }

        logAllSettings();
    }

    // Phương thức log toàn bộ dữ liệu bảng settings, kiểm tra null
    public void logAllSettings() {
        if (allSettings != null) {
            allSettings.observeForever(settings -> {
                if (settings == null || settings.isEmpty()) {
                    System.out.println("Không có dữ liệu trong bảng settings");
                } else {
                    for (Setting setting : settings) {
                        System.out.println("data bảng setting: " + "Setting ID: " + setting.getId() + ", Cỡ chữ: " + setting.getZise_text() + ", id color: " + setting.getId_color());
                    }
                }
            });
        } else {
            Log.e(TAG, "allSettings is null");
        }
    }

    // Phương thức lấy tất cả các cài đặt (settings)
    public LiveData<List<Setting>> getAllSettings() {
        return allSettings;
    }
}
