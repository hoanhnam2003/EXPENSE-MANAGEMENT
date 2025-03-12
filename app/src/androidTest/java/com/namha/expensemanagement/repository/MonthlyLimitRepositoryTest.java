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
import com.namha.expensemanagement.database.dao.MonthlyLimitDao;
import com.namha.expensemanagement.database.entities.MonthlyLimit;
import com.namha.expensemanagement.repository.MonthlyLimitRepository;

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
public class MonthlyLimitRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private MonthlyLimitDao monthlyLimitDao;
    private MonthlyLimitRepository repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        monthlyLimitDao = database.monthlyLimitDao();
        repository = new MonthlyLimitRepository(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertOrUpdateMonthlyLimit() throws InterruptedException {
        repository.insertOrUpdateMonthlyLimit(1000.0);

        // Chờ một chút để dữ liệu cập nhật vào database
        Thread.sleep(500);

        // Dùng getValue để lấy danh sách giới hạn tháng
        List<MonthlyLimit> limits = getValue(repository.getAllMonthlyLimits());

        assertNotNull(limits);

        // Kiểm tra số lượng bản ghi có thể lớn hơn hoặc bằng 1
        assertTrue("Số lượng bản ghi không hợp lệ", limits.size() >= 1);

        // Kiểm tra giá trị của bản ghi cuối cùng
        assertEquals(1000.0, limits.get(limits.size() - 1).getMoney_month(), 0.01);
    }

    @Test
    public void testUpdateMoneyMonthSetting() throws InterruptedException {
        repository.insertOrUpdateMonthlyLimit(1000.0);
        Thread.sleep(500);
        repository.updateMoneyMonthSetting(1500.0);
        Thread.sleep(500);

        Double updatedValue = getValue(repository.getLastMonthLimitSetting());
        assertNotNull(updatedValue);
        assertEquals(1500.0, updatedValue, 0.01);
    }

    @Test
    public void testGetLastMonthlyLimitId() throws InterruptedException {
        repository.insertOrUpdateMonthlyLimit(1000.0);
        Thread.sleep(500);

        Integer lastId = repository.getLastMonthlyLimitId();
        assertNotNull(lastId);
        assertTrue(lastId > 0);
    }

    @Test
    public void testGetLastMonthlyLimitMoney() throws InterruptedException {
        repository.insertOrUpdateMonthlyLimit(2000.0);
        Thread.sleep(500);

        Double lastMoney = getValue(repository.getLastMonthlyLimitMoney());
        assertNotNull(lastMoney);
        assertEquals(2000.0, lastMoney, 0.01);
    }

    @Test
    public void testGetLastMonthlyLimitID() throws InterruptedException {
        repository.insertOrUpdateMonthlyLimit(1000.0);
        Thread.sleep(500);

        Integer lastId = getValue(repository.getLastMonthlyLimitID());
        assertNotNull(lastId);
        assertTrue(lastId > 0);
    }

    // Helper method to get LiveData value
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

