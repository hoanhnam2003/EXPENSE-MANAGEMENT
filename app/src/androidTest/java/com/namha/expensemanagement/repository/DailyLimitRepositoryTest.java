package com.namha.expensemanagement.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.DailyLimitDao;
import com.namha.expensemanagement.database.entities.DailyLimit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class DailyLimitRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private DailyLimitDao dailyLimitDao;
    private DailyLimitRepository dailyLimitRepository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dailyLimitDao = database.dailyLimitDao();
        dailyLimitRepository = new DailyLimitRepository(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertOrUpdateDailyLimit() throws InterruptedException {
        double initialLimit = 50000.0;
        dailyLimitRepository.insertOrUpdateDailyLimit(initialLimit);

        // Đợi LiveData cập nhật
        TimeUnit.SECONDS.sleep(1);

        Integer lastId = getValue(dailyLimitRepository.getLastDailyLimitID());
        assertNotNull(lastId);

        Double lastMoney = getValue(dailyLimitRepository.getLastDailyLimitMoney());
        assertEquals(initialLimit, lastMoney, 0.01);
    }

    @Test
    public void testUpdateDailyLimit() throws InterruptedException {
        double initialLimit = 50000.0;
        double updatedLimit = 75000.0;

        // Chèn giới hạn ban đầu
        dailyLimitRepository.insertOrUpdateDailyLimit(initialLimit);
        TimeUnit.SECONDS.sleep(1);

        // Cập nhật giới hạn
        dailyLimitRepository.insertOrUpdateDailyLimit(updatedLimit);
        TimeUnit.SECONDS.sleep(1);

        Double lastMoney = getValue(dailyLimitRepository.getLastDailyLimitMoney());
        assertEquals(updatedLimit, lastMoney, 0.01);
    }

    @Test
    public void testGetLastDailyLimitMoneySync() throws InterruptedException {
        double limit = 60000.0;

        // Tạo đối tượng DailyLimit với giá trị chính xác
        DailyLimit dailyLimit = new DailyLimit(limit);
        dailyLimit.setMoney_day_setting(limit); // Đảm bảo set cả money_day_setting

        // Chèn dữ liệu vào Database
        dailyLimitDao.insertDailyLimit(dailyLimit); // Thay bằng phương thức insert/update đúng của DAO

        // Đợi dữ liệu cập nhật vào DB
        TimeUnit.MILLISECONDS.sleep(500); // Chờ để đảm bảo Room DB đã ghi nhận

        // Lấy giá trị từ DB
        LiveData<Double> lastInsertedLiveData = dailyLimitDao.getLastDailyLimitMoney();

        // Dùng LiveData Observer để lấy giá trị
        final Double[] lastSetting = new Double[1];
        Observer<Double> observer = new Observer<Double>() {
            @Override
            public void onChanged(Double value) {
                lastSetting[0] = value;
            }
        };

        // Thêm observer vào LiveData
        lastInsertedLiveData.observeForever(observer);

        // Đợi một thời gian ngắn để lấy giá trị từ LiveData
        TimeUnit.MILLISECONDS.sleep(500);

        // Kiểm tra giá trị
        assertNotNull("Giá trị lấy về không được null", lastSetting[0]);
        assertEquals("Giá trị cuối cùng không đúng", limit, lastSetting[0], 0.01);
    }

    // Hàm trợ giúp để lấy giá trị từ LiveData trong test
    private <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Object[] data = new Object[1];
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
