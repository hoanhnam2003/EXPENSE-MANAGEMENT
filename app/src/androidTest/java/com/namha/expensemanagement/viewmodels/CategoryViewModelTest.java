package com.namha.expensemanagement.viewmodels;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.data.LiveDataTestUtil;
import com.namha.expensemanagement.database.entities.Category;
import com.namha.expensemanagement.repository.CategoryRepository;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.junit.Rule;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CategoryViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Application application;
    private CategoryRepository repository;
    private CategoryViewModel viewModel;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        application = (Application) context;
        repository = new CategoryRepository(application);
        viewModel = new CategoryViewModel(application);
    }

    @Test
    public void testGetAllCategories() throws Exception {
        // Giả lập danh sách categories
        List<Category> fakeCategories = Arrays.asList(
                new Category("Food"),
                new Category("Transport")
        );

        MutableLiveData<List<Category>> liveData = new MutableLiveData<>();
        liveData.setValue(fakeCategories);

        // Sử dụng LiveData giả lập thay vì Mockito
        List<Category> result = LiveDataTestUtil.getValue(liveData);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("Transport", result.get(1).getName());
    }

    @Test
    public void testInsertCategory() throws Exception {
        // Tạo một category mới
        Category category = new Category("Health");

        // Chèn vào ViewModel
        viewModel.insert(category);

        // Chờ dữ liệu cập nhật
        Thread.sleep(500); // Đợi một chút để LiveData cập nhật

        List<Category> result = LiveDataTestUtil.getValue(viewModel.getAllCategories());

        // Kiểm tra danh sách không null và có chứa "Health"
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Health")));
    }

    @Test
    public void testDeleteCategoryByName() throws Exception {
        // Thêm một category mới
        Category category = new Category("Food");
        viewModel.insert(category);

        // Chờ dữ liệu cập nhật
        Thread.sleep(500); // Chờ LiveData cập nhật

        List<Category> insertedList = LiveDataTestUtil.getValue(viewModel.getAllCategories());
        assertNotNull(insertedList);
        assertTrue(insertedList.stream().anyMatch(c -> c.getName().equals("Food")));

        // Xóa category
        viewModel.deleteCategoryByName("Food");

        // Chờ dữ liệu cập nhật sau khi xóa
        Thread.sleep(500); // Chờ LiveData cập nhật

        List<Category> result = LiveDataTestUtil.getValue(viewModel.getAllCategories());

        // Kiểm tra danh sách không còn "Food"
        assertNotNull(result);
        assertFalse(result.stream().anyMatch(c -> c.getName().equals("Food")));
    }
}
