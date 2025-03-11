package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.data.LiveDataTestUtil;
import com.namha.expensemanagement.database.entities.DailyLimit;
import com.namha.expensemanagement.repository.DailyLimitRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DailyLimitViewModelTest {

    private DailyLimitViewModel viewModel;
    private DailyLimitRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        Application application = ApplicationProvider.getApplicationContext();
        viewModel = new DailyLimitViewModel(application);
    }

    @Test
    public void testInsertOrUpdateDailyLimit() throws InterruptedException {
        viewModel.insertOrUpdateDailyLimit(1000.0);

        Thread.sleep(1000); // Chờ database cập nhật xong

        LiveData<List<DailyLimit>> allDailyLimitsLiveData = viewModel.getAllDailyLimits();
        List<DailyLimit> allDailyLimits = LiveDataTestUtil.getValue(allDailyLimitsLiveData);

        assertNotNull(allDailyLimits);
        assertFalse(allDailyLimits.isEmpty());
        assertEquals(1000.0, allDailyLimits.get(allDailyLimits.size() - 1).getMoney_day(), 0.001);
    }

    @Test
    public void testGetLastDailyLimitMoney() throws InterruptedException {
        viewModel.insertOrUpdateDailyLimit(500.0);

        // Chờ Room cập nhật
        Thread.sleep(1000); // Chờ 1 giây để đảm bảo dữ liệu được lưu

        Double lastMoney = LiveDataTestUtil.getValue(viewModel.getLastDailyLimitMoney());

        assertNotNull(lastMoney);
        assertEquals(500.0, lastMoney, 0.001);
    }


    @Test
    public void testUpdateMoneyDaySetting() throws InterruptedException {
        viewModel.updateMoneyDaySetting(2000.0);

        // Chờ database cập nhật (tạm thời dùng Thread.sleep, có thể tối ưu bằng coroutines)
        Thread.sleep(1000);

        Double lastSetting = LiveDataTestUtil.getValue(viewModel.getLastDailyLimitSetting());

        assertNotNull(lastSetting);
        assertEquals(2000.0, lastSetting, 0.001);
    }


    @Test
    public void testGetLastDailyLimitId() throws InterruptedException {
        Integer lastId = LiveDataTestUtil.getValue(viewModel.getLastDailyLimitId());

        assertNotNull(lastId);
        assertTrue(lastId >= 0);
    }
}
