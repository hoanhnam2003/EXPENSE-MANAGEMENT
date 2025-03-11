package com.namha.expensemanagement.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.TypeDao;
import com.namha.expensemanagement.database.entities.Type;
import com.namha.expensemanagement.repository.TypeRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class TypeViewModelTest {

    private TypeViewModel viewModel;
    private AppDatabase testDatabase;
    private TypeRepository testRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // Chỉ dùng cho test
                .build();

        TypeDao testDao = testDatabase.typeDao();
        testRepository = new TypeRepository((Application) context);
        viewModel = new TypeViewModel((Application) context);
    }

    @After
    public void tearDown() {
        testDatabase.close();
    }

    @Test
    public void testGetAllTypes() throws InterruptedException {
        LiveData<List<Type>> liveData = viewModel.getAllTypes();
        List<Type> types = getOrAwaitValue(liveData);
        assertNotNull(types);
        assertTrue(types.size() >= 3); // Kiểm tra có ít nhất 3 loại được insert sẵn
    }

    @Test
    public void testGetTypeIdByName() throws InterruptedException {
        LiveData<Integer> typeIdLiveData = viewModel.getTypeIdByName("Thu nhập");
        Integer typeId = getOrAwaitValue(typeIdLiveData);

        assertNotNull(typeId);
        assertEquals(3, (int) typeId);
    }


    @Test
    public void testLogAllTypes() {
        viewModel.logAllTypes();
        // Kiểm tra log bằng cách xem Logcat khi chạy test
        Log.d("TestLogAllTypes", "Đã gọi phương thức logAllTypes()");
    }

    private <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
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
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        return (T) data[0];
    }
}
