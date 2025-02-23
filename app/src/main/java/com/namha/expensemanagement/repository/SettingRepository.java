package com.namha.expensemanagement.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;


import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.SettingDao;
import com.namha.expensemanagement.database.entities.Setting;

import java.util.List;

public class SettingRepository {
    private final SettingDao settingDao;
    private final LiveData<List<Setting>> allSettings;

    public SettingRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        settingDao = db.settingDao();
        allSettings = settingDao.getAllSettings();
    }

    public LiveData<List<Setting>> getAllSettings() {
        return allSettings;
    }
}
