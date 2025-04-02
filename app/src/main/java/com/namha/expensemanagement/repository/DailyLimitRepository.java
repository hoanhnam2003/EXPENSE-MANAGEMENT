package com.namha.expensemanagement.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.DailyLimitDao;
import com.namha.expensemanagement.database.entities.DailyLimit;
import java.util.List;

public class DailyLimitRepository {

    // Đối tượng DAO để thao tác với bảng DailyLimit trong cơ sở dữ liệu
    private final DailyLimitDao dailyLimitDao;

    // LiveData chứa danh sách tất cả các giới hạn chi tiêu hàng ngày, giúp cập nhật UI tự động khi có thay đổi
    private final LiveData<List<DailyLimit>> allDailyLimits;

    // Constructor khởi tạo repository, lấy instance của database và DAO tương ứng
    public DailyLimitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        dailyLimitDao = db.dailyLimitDao();
        allDailyLimits = dailyLimitDao.getAllDailyLimits();
    }

    // Phương thức lấy danh sách tất cả các giới hạn chi tiêu hàng ngày
    public LiveData<List<DailyLimit>> getAllDailyLimits() {
        return allDailyLimits;
    }

    // Phương thức kiểm tra và chèn hoặc cập nhật daily_limit trong cơ sở dữ liệu
    public void insertOrUpdateDailyLimit(double moneyDay) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            int count = dailyLimitDao.getDailyLimitCount();
            Log.d("DailyLimitRepository", "Count in daily_limits: " + count);

            if (count == 0) {
                // Nếu chưa có bản ghi nào, chèn mới
                DailyLimit dailyLimit = new DailyLimit(moneyDay);
                dailyLimitDao.insertDailyLimit(dailyLimit);
                Log.d("DailyLimitRepository", "Inserted new daily limit with money_day: " + moneyDay);
            } else {
                // Nếu đã có bản ghi, cập nhật bản ghi cuối cùng
                Integer lastId = dailyLimitDao.getLastDailyLimitId();
                if (lastId != null) {  // Kiểm tra null cho lastId để tránh lỗi
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

    // Phương thức lấy ID của bản ghi cuối cùng trong bảng daily_limits
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

    // Phương thức cập nhật giá trị money_day_setting
    public void updateMoneyDaySetting(double moneyDaySetting) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            dailyLimitDao.updateMoneyDaySetting(moneyDaySetting);
        });
    }

    // Phương thức để lấy ID mới nhất dưới dạng LiveData
    public LiveData<Integer> getLastDailyLimitID() {
        LiveData<Integer> lastIdLiveData = dailyLimitDao.getLastDailyLimitID();
        if (lastIdLiveData == null) {
            Log.d("DailyLimitRepository", "getLastDailyLimitID: No LiveData found");
        }
        return lastIdLiveData;
    }

    // Phương thức để lấy giá trị mới nhất của money_day_setting dưới dạng LiveData
    public LiveData<Double> getLastDailyLimitMoneySync() {
        LiveData<Double> lastSetting = dailyLimitDao.getLastDailyLimitSetting();
        if (lastSetting == null) {
            Log.d("DailyLimitRepository", "getLastDailyLimitMoneySync: No data available");
        }
        return lastSetting;
    }
}
