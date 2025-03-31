package com.namha.expensemanagement.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.namha.expensemanagement.database.entities.Type;

import java.util.List;

@Dao
public interface TypeDao {
    @Insert
    void insertType(Type type);

    @Query("SELECT * FROM types")
    LiveData<List<Type>> getAllTypes();

    @Query("SELECT COUNT(*) > 0 FROM types WHERE type_name = :typeName")
    boolean isTypeExists(String typeName);

    @Query("SELECT id FROM types WHERE type_name = :typeName")
    LiveData<Integer> getTypeIdByName(String typeName);

    // Xóa toàn bộ dữ liệu trong bảng types
    @Query("DELETE FROM types")
    void deleteAll();
}
