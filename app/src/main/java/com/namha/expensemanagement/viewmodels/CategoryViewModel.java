package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.namha.expensemanagement.database.entities.Category;
import com.namha.expensemanagement.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    private final LiveData<List<Category>> allCategories;
    private static final String TAG = "CategoryViewModel";

    public CategoryViewModel(@NonNull Application application) {
        super(application);

        // Khởi tạo repository và kiểm tra null
        repository = new CategoryRepository(application);
        if (repository != null) {
            allCategories = repository.getAllCategories();
        } else {
            Log.e(TAG, "Repository is null");
            allCategories = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng để tránh lỗi null
        }
    }

    // Phương thức trả về danh sách tất cả categories, không cần kiểm tra null vì LiveData không bao giờ null
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    // Phương thức chèn Category, kiểm tra null trước khi chèn
    public void insert(Category category) {
        if (category != null) {
            repository.insert(category);
        } else {
            Log.e(TAG, "Attempted to insert a null category");
        }
    }

    // Phương thức xóa Category theo tên, kiểm tra null hoặc chuỗi rỗng trước khi xóa
    public void deleteCategoryByName(String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            repository.deleteCategoryByName(categoryName);
        } else {
            Log.e(TAG, "Attempted to delete category with null or empty name");
        }
    }

    // Phương thức tìm ID của Category theo tên, kiểm tra danh sách null hoặc rỗng
    public LiveData<Integer> getCategoryIdByName(String categoryName) {
        MediatorLiveData<Integer> categoryIdLiveData = new MediatorLiveData<>();
        categoryIdLiveData.addSource(allCategories, categories -> {
            if (categories != null && !categories.isEmpty()) {
                for (Category category : categories) {
                    if (category.getName().equals(categoryName)) {
                        categoryIdLiveData.setValue(category.getId());
                        return;
                    }
                }
                // Nếu không tìm thấy Category phù hợp
                categoryIdLiveData.setValue(null);
            } else {
                // Danh sách categories là null hoặc rỗng
                categoryIdLiveData.setValue(null);
            }
        });
        return categoryIdLiveData;
    }

    // Phương thức mới để log toàn bộ dữ liệu bảng categories, kiểm tra null và log dữ liệu
    public void logAllCategories() {
        allCategories.observeForever(new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    for (Category category : categories) {
                        Log.d("data bảng category: ", "Category ID: " + category.getId() + ", Category Name: " + category.getName());
                    }
                } else {
                    Log.d("data bảng category: ", "No categories found or list is empty");
                }
            }
        });
    }
}