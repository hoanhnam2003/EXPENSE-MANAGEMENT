package com.namha.expensemanagement.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;


import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.DailyLimitDao;
import com.namha.expensemanagement.database.entities.DailyLimit;

import java.util.List;

public class DailyLimitRepository {
    private final DailyLimitDao dailyLimitDao;
    private final LiveData<List<DailyLimit>> allDailyLimits;

    public DailyLimitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        dailyLimitDao = db.dailyLimitDao();
        allDailyLimits = dailyLimitDao.getAllDailyLimits();
    }

    public LiveData<List<DailyLimit>> getAllDailyLimits() {
        return allDailyLimits;
    }

    // Kiểm tra và chèn hoặc cập nhật daily_limit
    public void insertOrUpdateDailyLimit(double moneyDay) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            int count = dailyLimitDao.getDailyLimitCount();
            Log.d("DailyLimitRepository", "Count in daily_limits: " + count);

            if (count == 0) {
                DailyLimit dailyLimit = new DailyLimit(moneyDay);
                dailyLimitDao.insertDailyLimit(dailyLimit);
                Log.d("DailyLimitRepository", "Inserted new daily limit with money_day: " + moneyDay);
            } else {
                Integer lastId = dailyLimitDao.getLastDailyLimitId();
                if (lastId != null) {  // Kiểm tra null cho lastId
                    DailyLimit dailyLimit = new DailyLimit(moneyDay);
                    dailyLimit.setId(lastId);
                    dailyLimitDao.updateDailyLimit(dailyLimit);
                    Log.d("DailyLimitRepository", "Updated daily limit with ID: " + lastId + " to money_day: " + moneyDay);
                } else {
                    Log.d("DailyLimitRepository", "Failed to update: lastId is null");
                }
            }
        });
    }

    // Lấy id của bản ghi cuối cùng
    public Integer getLastDailyLimitId() {
        Integer lastId = dailyLimitDao.getLastDailyLimitId();
        if (lastId == null) {
            Log.d("DailyLimitRepository", "getLastDailyLimitId: No records found");
        }
        return lastId;
    }

    // Phương thức để lấy số tiền của bản ghi cuối cùng
    public LiveData<Double> getLastDailyLimitMoney() {
        LiveData<Double> lastMoney = dailyLimitDao.getLastDailyLimitMoney();
        if (lastMoney == null) {
            Log.d("DailyLimitRepository", "getLastDailyLimitMoney: No data available");
        }
        return lastMoney;
    }

    public void updateMoneyDaySetting(double moneyDaySetting) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            dailyLimitDao.updateMoneyDaySetting(moneyDaySetting);
        });
    }

    // Phương thức để lấy ID mới nhất
    public LiveData<Integer> getLastDailyLimitID() {
        LiveData<Integer> lastIdLiveData = dailyLimitDao.getLastDailyLimitID();
        if (lastIdLiveData == null) {
            Log.d("DailyLimitRepository", "getLastDailyLimitID: No LiveData found");
        }
        return lastIdLiveData;
    }

    // Phương thức để lấy giá trị mới nhất của money_day_setting
    public LiveData<Double> getLastDailyLimitMoneySync() {
        LiveData<Double> lastSetting = dailyLimitDao.getLastDailyLimitSetting();
        if (lastSetting == null) {
            Log.d("DailyLimitRepository", "getLastDailyLimitMoneySync: No data available");
        }
        return lastSetting;
    }

}
