package com.namha.expensemanagement.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.core.content.ContextCompat;

import com.namha.expensemanagement.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SharedViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule(); // Đảm bảo LiveData cập nhật ngay lập tức

    @Mock
    private Application mockApplication;

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Mock
    private Resources mockResources;

    private SharedViewModel viewModel;
    private final int defaultColor = Color.RED; // Mô phỏng màu mặc định

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockApplication.getApplicationContext()).thenReturn(mockContext);
        when(mockApplication.getBaseContext()).thenReturn(mockContext);
        when(mockApplication.getSharedPreferences("app_settings", Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getColor(R.color.hongthongke, null)).thenReturn(defaultColor); // Mô phỏng ContextCompat.getColor()

        // Giả lập lấy giá trị màu từ SharedPreferences
        when(mockSharedPreferences.getInt("SelectedColor", defaultColor)).thenReturn(defaultColor);

        viewModel = new SharedViewModel(mockApplication);
    }

    @Test
    public void testDefaultColor() {
        // Kiểm tra màu mặc định được thiết lập đúng
        assertEquals(defaultColor, (int) viewModel.getSelectedColor().getValue());
    }

    @Test
    public void testSetSelectedColor() {
        int newColor = Color.BLUE;

        // Gọi setSelectedColor để cập nhật màu
        viewModel.setSelectedColor(newColor);

        // Kiểm tra LiveData đã cập nhật đúng màu
        assertEquals(newColor, (int) viewModel.getSelectedColor().getValue());

        // Kiểm tra SharedPreferences có được lưu đúng màu không
        verify(mockEditor).putInt("SelectedColor", newColor);
        verify(mockEditor).apply();
    }
}
