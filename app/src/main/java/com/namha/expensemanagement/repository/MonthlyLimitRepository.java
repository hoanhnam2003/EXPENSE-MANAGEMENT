package com.namha.expensemanagement.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.MonthlyLimitDao;
import com.namha.expensemanagement.database.entities.MonthlyLimit;
import java.util.List;

public class MonthlyLimitRepository {

    // Đối tượng DAO để thao tác với bảng MonthlyLimit trong cơ sở dữ liệu
    private final MonthlyLimitDao monthlyLimitDao;

    // LiveData chứa danh sách tất cả các giới hạn chi tiêu hàng tháng, giúp cập nhật UI tự động khi có thay đổi
    private final LiveData<List<MonthlyLimit>> allMonthlyLimits;

    // Constructor khởi tạo repository, lấy instance của database và DAO tương ứng
    public MonthlyLimitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        monthlyLimitDao = db.monthlyLimitDao();
        allMonthlyLimits = monthlyLimitDao.getAllMonthlyLimits();
    }

    // Phương thức lấy danh sách tất cả các giới hạn chi tiêu hàng tháng
    public LiveData<List<MonthlyLimit>> getAllMonthlyLimits() {
        return allMonthlyLimits;
    }

    // Phương thức kiểm tra và chèn hoặc cập nhật monthly_limit trong cơ sở dữ liệu
    public void insertOrUpdateMonthlyLimit(double moneyMonth) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            int count = monthlyLimitDao.getMonthlyLimitCount();
            MonthlyLimit monthlyLimit = new MonthlyLimit(moneyMonth);

            if (count == 0) {
                // Nếu chưa có bản ghi nào, chèn mới
                monthlyLimitDao.insertMonthlyLimit(monthlyLimit);
                System.out.println("Inserted new monthly limit with money_month: " + moneyMonth);
            } else {
                // Nếu đã có bản ghi, cập nhật bản ghi cuối cùng
                Integer lastId = monthlyLimitDao.getLastMonthlyLimitId();
                if (lastId != null) {  // Kiểm tra null cho lastId để tránh lỗi
                    monthlyLimit.setId(lastId);
                    monthlyLimitDao.updateMonthlyLimit(monthlyLimit);
                    System.out.println("Updated monthly limit with ID: " + lastId + " to money_month: " + moneyMonth);
                } else {
                    // Log hoặc xử lý khi lastId là null
                    System.err.println("insertOrUpdateMonthlyLimit: lastId is null");
                }
            }
        });
    }

    // Phương thức cập nhật giá trị money_month_setting
    public void updateMoneyMonthSetting(double moneyMonthSetting) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            Integer lastId = monthlyLimitDao.getLastMonthlyLimitId();
            if (lastId != null) {  // Kiểm tra null cho lastId để tránh lỗi
                monthlyLimitDao.updateMoneyMonthSetting(moneyMonthSetting);
                System.out.println("Updated money_month_setting to: " + moneyMonthSetting);
            } else {
                // Log hoặc xử lý khi lastId là null
                System.err.println("updateMoneyMonthSetting: lastId is null");
            }
        });
    }

    // Phương thức lấy ID của bản ghi cuối cùng trong bảng monthly_limits
    public Integer getLastMonthlyLimitId() {
        Integer lastId = monthlyLimitDao.getLastMonthlyLimitId();
        if (lastId == null) {
            System.err.println("getLastMonthlyLimitId: No record found");
        }
        return lastId;
    }

    // Phương thức để lấy số tiền của bản ghi cuối cùng
    public LiveData<Double> getLastMonthlyLimitMoney() {
        LiveData<Double> lastMoney = monthlyLimitDao.getLastMonthlyLimitMoney();
        if (lastMoney == null) {
            System.err.println("getLastMonthlyLimitMoney: No data found");
        }
        return lastMoney;
    }

    // Phương thức để lấy ID mới nhất dưới dạng LiveData
    public LiveData<Integer> getLastMonthlyLimitID() {
        LiveData<Integer> lastIdLiveData = monthlyLimitDao.getLastMonthlyLimitID();
        if (lastIdLiveData == null) {
            System.err.println("getLastMonthlyLimitID: No LiveData found");
        }
        return lastIdLiveData;
    }

    // Phương thức để lấy giá trị mới nhất của money_month_setting dưới dạng LiveData
    public LiveData<Double> getLastMonthLimitSetting() {
        LiveData<Double> lastSetting = monthlyLimitDao.getLastMonthLimitSetting();
        if (lastSetting == null) {
            System.err.println("getLastMonthLimitSetting: No data found");
        }
        return lastSetting;
    }
}
