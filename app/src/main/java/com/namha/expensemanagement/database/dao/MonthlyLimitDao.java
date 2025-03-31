package com.namha.expensemanagement.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.namha.expensemanagement.database.entities.MonthlyLimit;
import java.util.List;

@Dao
public interface MonthlyLimitDao {
    // Phương thức chèn một đối tượng MonthlyLimit vào bảng monthly_limits
    @Insert
    void insertMonthlyLimit(MonthlyLimit monthlyLimit);

    // Phương thức cập nhật một đối tượng MonthlyLimit trong bảng monthly_limits
    @Update
    void updateMonthlyLimit(MonthlyLimit monthlyLimit);

    // Xóa toàn bộ dữ liệu trong bảng monthly_limits
    @Query("DELETE FROM monthly_limits")
    void deleteAll();

    // Lấy tất cả dữ liệu từ bảng monthly_limits
    @Query("SELECT * FROM monthly_limits")
    LiveData<List<MonthlyLimit>> getAllMonthlyLimits();

    // Đếm số bản ghi trong bảng monthly_limits
    @Query("SELECT COUNT(*) FROM monthly_limits")
    int getMonthlyLimitCount();

    // Lấy ID cuối cùng trong bảng monthly_limits
    @Query("SELECT MAX(id) FROM monthly_limits")
    Integer getLastMonthlyLimitId();

    // Lấy số tiền của bản ghi cuối cùng trong bảng monthly_limits
    @Query("SELECT money_month FROM monthly_limits ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastMonthlyLimitMoney();

    // Cập nhật giá trị money_month_setting cho bản ghi có ID lớn nhất
    @Query("UPDATE monthly_limits SET money_month_setting = :moneyMonthSetting WHERE id = (SELECT MAX(id) FROM monthly_limits)")
    void updateMoneyMonthSetting(double moneyMonthSetting);

    // Lấy ID cuối cùng dưới dạng LiveData từ bảng monthly_limits
    @Query("SELECT MAX(id) FROM monthly_limits")
    LiveData<Integer> getLastMonthlyLimitID();

    // Lấy giá trị mới nhất của money_month_setting từ bảng monthly_limits
    @Query("SELECT money_month_setting FROM monthly_limits ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastMonthLimitSetting();
}
