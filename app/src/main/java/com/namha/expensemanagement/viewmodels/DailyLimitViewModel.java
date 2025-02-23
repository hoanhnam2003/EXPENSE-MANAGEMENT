package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.namha.expensemanagement.database.entities.DailyLimit;
import com.namha.expensemanagement.repository.DailyLimitRepository;

import java.util.List;

public class DailyLimitViewModel extends AndroidViewModel {
    private final DailyLimitRepository repository;
    private final LiveData<List<DailyLimit>> allDailyLimits;

    public DailyLimitViewModel(@NonNull Application application) {
        super(application);

        // Khởi tạo repository và kiểm tra null
        repository = new DailyLimitRepository(application);
        if (repository != null) {
            allDailyLimits = repository.getAllDailyLimits();
        } else {
            Log.e("DailyLimitViewModel", "Repository is null");
            allDailyLimits = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng nếu repository bị null
        }

        checkAndInsertInitialDailyLimit();
    }

    // Phương thức để log toàn bộ dữ liệu trong bảng daily_limits
    public void logAllDailyLimits() {
        if (allDailyLimits != null) {
            allDailyLimits.observeForever(dailyLimits -> {
                if (dailyLimits == null || dailyLimits.isEmpty()) {
                    Log.d("DailyLimitViewModel", "Không có dữ liệu trong bảng daily_limits");
                } else {
                    for (DailyLimit dailyLimit : dailyLimits) {
                        Log.d("DailyLimitViewModel", "Daily Limit ID: " + dailyLimit.getId() + ", Daily Limit Amount: " + dailyLimit.getMoney_day() + ", Daily Limit Setting: " + dailyLimit.getMoney_day_setting());
                    }
                }
            });
        } else {
            Log.e("DailyLimitViewModel", "allDailyLimits is null");
        }
    }

    // Phương thức để chèn hoặc cập nhật daily_limit, kiểm tra null
    public void insertOrUpdateDailyLimit(double moneyDay) {
        if (repository != null) {
            repository.insertOrUpdateDailyLimit(moneyDay);
        } else {
            Log.e("DailyLimitViewModel", "Repository is null, cannot insert or update");
        }
    }

    // Phương thức lấy tất cả dữ liệu daily_limits
    public LiveData<List<DailyLimit>> getAllDailyLimits() {
        return allDailyLimits;
    }

    // Phương thức lấy số tiền của bản ghi cuối cùng, kiểm tra null
    public LiveData<Double> getLastDailyLimitMoney() {
        if (repository != null) {
            return repository.getLastDailyLimitMoney();
        } else {
            Log.e("DailyLimitViewModel", "Repository is null, cannot get last daily limit money");
            return new MutableLiveData<>(); // Trả về một LiveData rỗng nếu repository null
        }
    }

    // Kiểm tra và chèn giá trị 0 nếu bảng daily_limits trống
    private void checkAndInsertInitialDailyLimit() {
        if (allDailyLimits != null) {
            allDailyLimits.observeForever(dailyLimits -> {
                if (dailyLimits == null || dailyLimits.isEmpty()) {
                    insertOrUpdateDailyLimit(0);
                    Log.d("DailyLimitViewModel", "Bảng daily_limits trống, đã chèn giá trị 0");
                } else {
                    Log.d("DailyLimitViewModel", "Bảng daily_limits đã có dữ liệu");
                }
            });
        } else {
            Log.e("DailyLimitViewModel", "allDailyLimits is null, cannot check or insert initial daily limit");
        }
    }

    // Phương thức để cập nhật money_day_setting, kiểm tra null
    public void updateMoneyDaySetting(double moneyDaySetting) {
        if (repository != null) {
            repository.updateMoneyDaySetting(moneyDaySetting);
        } else {
            Log.e("DailyLimitViewModel", "Repository is null, cannot update money day setting");
        }
    }

    // Phương thức lấy ID mới nhất, kiểm tra null
    public LiveData<Integer> getLastDailyLimitId() {
        if (repository != null) {
            return repository.getLastDailyLimitID();
        } else {
            Log.e("DailyLimitViewModel", "Repository is null, cannot get last daily limit ID");
            return new MutableLiveData<>(); // Trả về một LiveData rỗng nếu repository null
        }
    }

    // Phương thức lấy giá trị mới nhất của money_day_setting, kiểm tra null
    public LiveData<Double> getLastDailyLimitSetting() {
        if (repository != null) {
            return repository.getLastDailyLimitMoneySync();
        } else {
            Log.e("DailyLimitViewModel", "Repository is null, cannot get last daily limit setting");
            return new MutableLiveData<>(); // Trả về một LiveData rỗng nếu repository null
        }
    }

}
