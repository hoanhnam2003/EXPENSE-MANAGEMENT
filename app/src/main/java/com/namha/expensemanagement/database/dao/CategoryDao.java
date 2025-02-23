package com.namha.expensemanagement.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.namha.expensemanagement.database.entities.Category;

import java.util.List;

@Dao
public interface   CategoryDao {
    @Insert
    void insertCategory(Category category);

//    lấy tất cả dữ liệu bảng categories
    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Query("DELETE FROM categories WHERE name = :categoryName")
    void deleteCategoryByName(String categoryName);

    // Xóa toàn bộ dữ liệu bảng categories
    @Query("DELETE FROM categories")
    void deleteAll();
}
