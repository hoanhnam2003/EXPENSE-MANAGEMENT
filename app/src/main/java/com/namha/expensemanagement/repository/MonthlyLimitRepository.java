package com.namha.expensemanagement.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;


import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.MonthlyLimitDao;
import com.namha.expensemanagement.database.entities.MonthlyLimit;

import java.util.List;

public class MonthlyLimitRepository {
    private final MonthlyLimitDao monthlyLimitDao;
    private final LiveData<List<MonthlyLimit>> allMonthlyLimits;

    public MonthlyLimitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        monthlyLimitDao = db.monthlyLimitDao();
        allMonthlyLimits = monthlyLimitDao.getAllMonthlyLimits();
    }

    public LiveData<List<MonthlyLimit>> getAllMonthlyLimits() {
        return allMonthlyLimits;
    }

    // Kiểm tra và chèn hoặc cập nhật monthly_limit
    public void insertOrUpdateMonthlyLimit(double moneyMonth) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            int count = monthlyLimitDao.getMonthlyLimitCount();
            MonthlyLimit monthlyLimit = new MonthlyLimit(moneyMonth);

            if (count == 0) {
                monthlyLimitDao.insertMonthlyLimit(monthlyLimit);
            } else {
                Integer lastId = monthlyLimitDao.getLastMonthlyLimitId();
                if (lastId != null) {  // Kiểm tra null cho lastId
                    monthlyLimit.setId(lastId);
                    monthlyLimitDao.updateMonthlyLimit(monthlyLimit);
                } else {
                    // Log hoặc xử lý khi lastId là null
                    System.err.println("insertOrUpdateMonthlyLimit: lastId is null");
                }
            }
        });
    }

    public void updateMoneyMonthSetting(double moneyMonthSetting) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            Integer lastId = monthlyLimitDao.getLastMonthlyLimitId();
            if (lastId != null) {  // Kiểm tra null cho lastId
                monthlyLimitDao.updateMoneyMonthSetting(moneyMonthSetting);
            } else {
                // Log hoặc xử lý khi lastId là null
                System.err.println("updateMoneyMonthSetting: lastId is null");
            }
        });
    }

    public Integer getLastMonthlyLimitId() {
        Integer lastId = monthlyLimitDao.getLastMonthlyLimitId();
        if (lastId == null) {
            // Log khi lastId là null
            System.err.println("getLastMonthlyLimitId: No record found");
        }
        return lastId;
    }

    public LiveData<Double> getLastMonthlyLimitMoney() {
        LiveData<Double> lastMoney = monthlyLimitDao.getLastMonthlyLimitMoney();
        if (lastMoney == null) {
            // Log khi không có dữ liệu
            System.err.println("getLastMonthlyLimitMoney: No data found");
        }
        return lastMoney;
    }

    // Phương thức để lấy ID mới nhất
    public LiveData<Integer> getLastMonthlyLimitID() {
        LiveData<Integer> lastIdLiveData = monthlyLimitDao.getLastMonthlyLimitID();
        if (lastIdLiveData == null) {
            // Log khi không có dữ liệu
            System.err.println("getLastMonthlyLimitID: No LiveData found");
        }
        return lastIdLiveData;
    }

    // Phương thức để lấy giá trị mới nhất của money_month_setting
    public LiveData<Double> getLastMonthLimitSetting() {
        LiveData<Double> lastSetting = monthlyLimitDao.getLastMonthLimitSetting();
        if (lastSetting == null) {
            // Log khi không có dữ liệu
            System.err.println("getLastMonthLimitSetting: No data found");
        }
        return lastSetting;
    }

}
