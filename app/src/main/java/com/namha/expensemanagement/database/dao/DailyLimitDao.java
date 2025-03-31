package com.namha.expensemanagement.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.namha.expensemanagement.database.entities.DailyLimit;
import java.util.List;

@Dao
public interface DailyLimitDao {
    // Phương thức chèn một đối tượng DailyLimit vào bảng daily_limits
    @Insert
    void insertDailyLimit(DailyLimit dailyLimit);
    // Phương thức cập nhật một đối tượng DailyLimit trong bảng daily_limits
    @Update
    void updateDailyLimit(DailyLimit dailyLimit);

    // Lấy tất cả dữ liệu bảng daily_limits
    @Query("SELECT * FROM daily_limits")
    LiveData<List<DailyLimit>> getAllDailyLimits();

    // Đếm số bản ghi trong bảng daily_limits
    @Query("SELECT COUNT(*) FROM daily_limits")
    int getDailyLimitCount();  // Sử dụng int thay vì LiveData

    // Lấy ID cuối cùng trong bảng daily_limits
    @Query("SELECT MAX(id) FROM daily_limits")
    int getLastDailyLimitId();

    // Lấy số tiền của bản ghi cuối cùng trong bảng daily_limits
    @Query("SELECT money_day FROM daily_limits ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastDailyLimitMoney();

    // Phương thức để cập nhật money_day_setting
    @Query("UPDATE daily_limits SET money_day_setting = :moneyDaySetting WHERE id = (SELECT MAX(id) FROM daily_limits)")
    void updateMoneyDaySetting(double moneyDaySetting);

    //    xóa toàn bộ dữ liệu trong bảng daily_limits
    @Query("DELETE FROM daily_limits")
    void deleteAll();

    //    lấy id cuối cùng
    @Query("SELECT MAX(id) FROM daily_limits")
    LiveData<Integer> getLastDailyLimitID();

    // Lấy giá trị mới nhất của money_day_setting
    @Query("SELECT money_day_setting FROM daily_limits ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastDailyLimitSetting();
}