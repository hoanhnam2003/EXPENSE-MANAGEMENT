package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SharedViewModelTest {
    private SharedViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Application application;
    private Context context;

    // Đảm bảo LiveData chạy trên main thread trong test
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        // Lấy context của ứng dụng trong môi trường test
        application = ApplicationProvider.getApplicationContext();
        context = application;

        // Mock SharedPreferences
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);

        // Giả lập SharedPreferences
        int defaultColor = ContextCompat.getColor(context, R.color.hongthongke);
        when(sharedPreferences.getInt(eq("selected_color"), anyInt())).thenReturn(defaultColor);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);

        // Tạo ViewModel
        viewModel = new SharedViewModel(application);
    }

    @Test
    public void testInitialColor() {
        int expectedColor = ContextCompat.getColor(context, R.color.hongthongke);
        assertEquals(expectedColor, (int) viewModel.getSelectedColor().getValue());
    }

    @Test
    public void testSetSelectedColor() throws InterruptedException {
        int newColor = Color.RED;

        // Quan sát LiveData để kiểm tra cập nhật
        Observer<Integer> observer = mock(Observer.class);
        viewModel.getSelectedColor().observeForever(observer);

        viewModel.setSelectedColor(newColor);

        // Đợi LiveData cập nhật (chỉ cần trong môi trường test)
        Thread.sleep(100); // Đợi 100ms để đảm bảo LiveData được cập nhật

        // Kiểm tra LiveData có nhận đúng giá trị mới không
        verify(observer).onChanged(newColor);
        assertEquals(newColor, (int) viewModel.getSelectedColor().getValue());

        // Xác nhận SharedPreferences đã lưu giá trị mới
        verify(editor, times(1)).putInt(eq("selected_color"), eq(newColor));
        verify(editor, times(1)).apply();

        // Bỏ quan sát để tránh memory leak
        viewModel.getSelectedColor().removeObserver(observer);
    }
}
