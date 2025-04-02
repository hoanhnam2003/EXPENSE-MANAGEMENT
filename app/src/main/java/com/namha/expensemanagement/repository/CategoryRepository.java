package com.namha.expensemanagement.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.CategoryDao;
import com.namha.expensemanagement.database.entities.Category;
import java.util.List;

public class CategoryRepository {

    // Đối tượng DAO để thao tác với bảng Category trong cơ sở dữ liệu
    private final CategoryDao categoryDao;

    // LiveData chứa danh sách tất cả các danh mục (categories), giúp cập nhật UI tự động khi có thay đổi
    private final LiveData<List<Category>> allCategories;

    // Constructor khởi tạo repository, lấy instance của database và DAO tương ứng
    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    // Phương thức lấy danh sách tất cả các danh mục
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    // Phương thức chèn một danh mục mới vào cơ sở dữ liệu
    public void insert(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null"); // Kiểm tra category không được null
        }
        // Sử dụng Executor để chạy thao tác chèn trên một luồng riêng biệt, tránh chặn UI thread
        AppDatabase.getDatabaseWriteExecutor().execute(() -> categoryDao.insertCategory(category));
    }

    // Phương thức xóa danh mục khỏi cơ sở dữ liệu dựa trên tên danh mục
    public void deleteCategoryByName(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            throw new IllegalArgumentException("Category name must not be null or empty"); // Kiểm tra tên danh mục hợp lệ
        }
        // Sử dụng Executor để chạy thao tác xóa trên một luồng riêng biệt
        AppDatabase.getDatabaseWriteExecutor().execute(() -> categoryDao.deleteCategoryByName(categoryName));
    }
}
