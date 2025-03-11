package com.namha.expensemanagement.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.entities.Category;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CategoryRepositoryTest {
    private AppDatabase db;
    private CategoryRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();

        // Khởi tạo database in-memory
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        // Cung cấp database test vào AppDatabase để CategoryRepository sử dụng
        setTestDatabase(db);

        // Tạo repository từ Application giả lập
        repository = new CategoryRepository((Application) context);
    }

    @After
    public void closeDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Test
    public void testInsertAndGetAllCategories() throws InterruptedException {
        Category category = new Category("Food");
        repository.insert(category);

        Thread.sleep(500);

        LiveData<List<Category>> liveData = repository.getAllCategories();
        List<Category> categories = getOrAwaitValue(liveData);

        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Food", categories.get(0).getName());
    }

    @Test
    public void testDeleteCategoryByName() throws InterruptedException {
        Category category = new Category("Travel");
        repository.insert(category);
        Thread.sleep(500);

        repository.deleteCategoryByName("Travel");
        Thread.sleep(500);

        LiveData<List<Category>> liveData = repository.getAllCategories();
        List<Category> categories = getOrAwaitValue(liveData);

        assertNotNull(categories);
        assertTrue(categories.isEmpty());
    }

    // Hàm hỗ trợ để chờ LiveData có giá trị trả về
    private <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Object[] data = new Object[1];

        liveData.observeForever(o -> {
            data[0] = o;
            latch.countDown();
        });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }

        return (T) data[0];
    }

    // Thiết lập database test cho AppDatabase
    private static void setTestDatabase(AppDatabase testDb) {
        try {
            java.lang.reflect.Field field = AppDatabase.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, testDb);
        } catch (Exception e) {
            throw new RuntimeException("Không thể thiết lập database test", e);
        }
    }
}
