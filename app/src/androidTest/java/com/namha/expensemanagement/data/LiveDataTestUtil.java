package com.namha.expensemanagement.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LiveDataTestUtil {
    public static <T> T getValue(final LiveData<T> liveData, long timeout, TimeUnit timeUnit) throws InterruptedException {
        AtomicReference<T> data = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data.set(t);
                latch.countDown();
                liveData.removeObserver(this);
            }
        });

        if (!latch.await(timeout, timeUnit)) {
            throw new InterruptedException("LiveData value was never set within the given timeout.");
        }

        return data.get();
    }

    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        return getValue(liveData, 2, TimeUnit.SECONDS);
    }
}
