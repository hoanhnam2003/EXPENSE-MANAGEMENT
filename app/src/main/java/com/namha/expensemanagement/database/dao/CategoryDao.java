package com.namha.expensemanagement.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.namha.expensemanagement.database.entities.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    // Phương thức chèn một đối tượng Category vào bảng categories
    @Insert
    void insertCategory(Category category);

    // Phương thức lấy tất cả các bản ghi trong bảng categories dưới dạng LiveData.
    // LiveData giúp theo dõi sự thay đổi trong dữ liệu và tự động cập nhật giao diện khi dữ liệu thay đổi.
    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    // Phương thức xóa một Category trong bảng categories dựa trên tên của nó
    @Query("DELETE FROM categories WHERE name = :categoryName")
    void deleteCategoryByName(String categoryName);

    // Xóa toàn bộ dữ liệu bảng categories
    @Query("DELETE FROM categories")
    void deleteAll();
}
