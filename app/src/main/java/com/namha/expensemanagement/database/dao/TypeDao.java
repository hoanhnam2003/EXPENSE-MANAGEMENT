package com.namha.expensemanagement.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.namha.expensemanagement.database.entities.Type;

import java.util.List;

@Dao
public interface TypeDao {

    // Chèn một loại chi tiêu mới vào bảng types
    @Insert
    void insertType(Type type);

    // Lấy toàn bộ danh sách loại chi tiêu từ bảng types
    @Query("SELECT * FROM types")
    LiveData<List<Type>> getAllTypes();

    // Kiểm tra xem loại chi tiêu có tồn tại trong bảng types hay không
    @Query("SELECT COUNT(*) > 0 FROM types WHERE type_name = :typeName")
    boolean isTypeExists(String typeName);

    // Lấy ID của một loại chi tiêu dựa vào tên loại
    @Query("SELECT id FROM types WHERE type_name = :typeName")
    LiveData<Integer> getTypeIdByName(String typeName);

    // Xóa toàn bộ dữ liệu trong bảng types
    @Query("DELETE FROM types")
    void deleteAll();
}
