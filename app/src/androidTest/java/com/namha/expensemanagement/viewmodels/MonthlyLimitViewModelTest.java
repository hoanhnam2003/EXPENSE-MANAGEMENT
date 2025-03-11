package com.namha.expensemanagement.viewmodels;

import static org.junit.Assert.*;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.namha.expensemanagement.database.entities.MonthlyLimit;
import com.namha.expensemanagement.repository.MonthlyLimitRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class MonthlyLimitViewModelTest {
    private MonthlyLimitViewModel viewModel;
    private MonthlyLimitRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        Application application = getApplicationContext();
        repository = new MonthlyLimitRepository(application);
        viewModel = new MonthlyLimitViewModel(application);
    }

    @After
    public void tearDown() {
        viewModel = null;
        repository = null;
    }

    // Test kiểm tra xem danh sách MonthlyLimits có thể được truy xuất không
    @Test
    public void testGetAllMonthlyLimits() throws InterruptedException {
        List<MonthlyLimit> limits = getOrAwaitValue(viewModel.getAllMonthlyLimits());
        assertNotNull(limits);
        Log.d("Test", "Số lượng monthly limits: " + limits.size());
    }

    // Test kiểm tra phương thức insertOrUpdateMonthlyLimit hoạt động đúng không
    @Test
    public void testInsertOrUpdateMonthlyLimit() throws InterruptedException {
        viewModel.insertOrUpdateMonthlyLimit(5000);
        TimeUnit.SECONDS.sleep(1); // Chờ một khoảng thời gian để DB cập nhật

        Double lastLimit = getOrAwaitValue(viewModel.getLastMonthlyLimitMoney());
        assertNotNull(lastLimit);
        assertEquals(5000, lastLimit, 0.01);
    }

    // Test kiểm tra giá trị của MonthlyLimit mới nhất
    @Test
    public void testGetLastMonthlyLimitMoney() throws InterruptedException {
        Double lastLimit = getOrAwaitValue(viewModel.getLastMonthlyLimitMoney());
        assertNotNull(lastLimit);
        Log.d("Test", "Số tiền monthly limit mới nhất: " + lastLimit);
    }

    // Test kiểm tra cập nhật giá trị money_month_setting
    @Test
    public void testUpdateMoneyMonthSetting() throws InterruptedException {
        viewModel.updateMoneyMonthSetting(10000);
        TimeUnit.SECONDS.sleep(1);

        Double lastSetting = getOrAwaitValue(viewModel.getLastMonthLimitSetting());
        assertNotNull(lastSetting);
        assertEquals(10000, lastSetting, 0.01);
    }

    // Test kiểm tra lấy ID mới nhất
    @Test
    public void testGetLastMonthlyLimitId() throws InterruptedException {
        Integer lastId = getOrAwaitValue(viewModel.getLastMonthlyLimitId());
        assertNotNull(lastId);
        Log.d("Test", "ID monthly limit mới nhất: " + lastId);
    }

    private <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        return (T) data[0];
    }
}
