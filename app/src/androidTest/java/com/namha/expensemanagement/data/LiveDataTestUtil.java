package com.namha.expensemanagement.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataTestUtil {
    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                latch.countDown();
                liveData.removeObserver(this);
            }
        });

        // Chờ tối đa 2 giây để LiveData cập nhật dữ liệu
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new InterruptedException("LiveData value was never set.");
        }

        return (T) data[0];
    }
}

