package com.namha.expensemanagement.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.namha.expensemanagement.database.entities.Setting;

import java.util.List;

@Dao
public interface SettingDao {

    // Thêm một mục cài đặt
    @Insert
    void insert(Setting setting);

    // Cập nhật một mục cài đặt
    @Update
    void update(Setting setting);

    // Xóa một mục cài đặt theo ID
    @Query("DELETE FROM settings WHERE id = :id")
    void deleteById(int id);

    // Lấy tất cả các mục cài đặt
    @Query("SELECT * FROM settings")
    LiveData<List<Setting>> getAllSettings();

    // Lấy một mục cài đặt theo ID
    @Query("SELECT * FROM settings WHERE id = :id")
    Setting getSettingById(int id);

    // Lấy cài đặt duy nhất (ví dụ: cài đặt hệ thống chỉ có một bản ghi)
    @Query("SELECT * FROM settings LIMIT 1")
    Setting getSingleSetting();

//    xoá tất cả dữ liệu trong bảng
    @Query("DELETE FROM settings")
    void deleteAll();


}
