package com.namha.expensemanagement.repository;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.TypeDao;
import com.namha.expensemanagement.database.entities.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class TypeRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private TypeDao typeDao;
    private TypeRepository repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        typeDao = database.typeDao();
        repository = new TypeRepository(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetAllTypes() throws InterruptedException {
        typeDao.deleteAll(); // Xóa tất cả dữ liệu trước khi kiểm tra
        Thread.sleep(200);   // Đợi DB xóa xong

        Type type = new Type(100, "Food");
        repository.insert(type);
        Thread.sleep(500); // Chờ dữ liệu cập nhật

        List<Type> types = getValue(repository.getAllTypes());

        assertNotNull(types);
        assertFalse(types.isEmpty());

        // Kiểm tra xem danh sách có chứa "Food" không
        boolean found = types.stream().anyMatch(t -> "Food".equals(t.getType_name()));
        assertTrue(found);
    }


    @Test
    public void testIsTypeExists() throws InterruptedException {
        Type type = new Type(101, "Transport");
        type.setType_name("Transport");

        repository.insert(type);
        Thread.sleep(500);

        boolean exists = repository.isTypeExists("Transport");
        assertTrue(exists);

        boolean notExists = repository.isTypeExists("Entertainment");
        assertFalse(notExists);
    }

    // Helper method để lấy dữ liệu từ LiveData
    private <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<T> data = new AtomicReference<>();
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data.set(t);
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        return data.get();
    }
}
