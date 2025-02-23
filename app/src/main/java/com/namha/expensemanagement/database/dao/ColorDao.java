package com.namha.expensemanagement.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.namha.expensemanagement.database.entities.Color;

import java.util.List;

@Dao
public interface ColorDao {
    @Insert
    void insert(Color color);

//    lấy tất cả dữ liệu bảng colors
    @Query("SELECT * FROM colors")
    LiveData<List<Color>> getAllColors();

//    xóa tất cả dữ liệu bảng colors
    @Query("DELETE FROM colors")
    void deleteAll();

}
