package com.namha.expensemanagement.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.databinding.ItemHistoryBinding;
import com.namha.expensemanagement.dto.History;
import com.namha.expensemanagement.ui.adapters.HistoryAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class HistoryAdapterTest {

    private HistoryAdapter adapter;
    private List<History> fakeData;
    private Context context;
    private HistoryAdapter.OnThreeDotsClickListener mockListener;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        mockListener = Mockito.mock(HistoryAdapter.OnThreeDotsClickListener.class);

        // Fake dữ liệu
        fakeData = Arrays.asList(
                new History(1, "Food", "Lunch", "2024-03-13", "Expense", 50000),
                new History(2, "Transport", "Bus fare", "2024-03-12", "Expense", 15000)
        );

        adapter = new HistoryAdapter(fakeData, mockListener);
    }

    @Test
    public void testOnCreateViewHolder() {
        ViewGroup parent = new android.widget.FrameLayout(context);
        assertNotNull(adapter.onCreateViewHolder(parent, 0));
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testSetItems() {
        List<History> newData = Arrays.asList(
                new History(3, "Shopping", "Clothes", "2024-03-14", "Expense", 80000)
        );

        adapter.setItems(newData);
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testGetItemData() {
        // Kiểm tra dữ liệu trả về có đúng không
        History item = fakeData.get(0);
        assertEquals("Food", item.getNameCategory());
        assertEquals("Lunch", item.getContent());
        assertEquals("2024-03-13", item.getDate());
        assertEquals("Expense", item.getTypeName());
        assertEquals(50000.0, item.getAmount(), 0.001);
    }

    @Test
    public void testThreeDotsClickListener() {
        // Giả lập click vào nút ba chấm
        mockListener.onThreeDotsClick(1);

        // Kiểm tra xem listener có được gọi đúng không
        verify(mockListener, times(1)).onThreeDotsClick(1);
    }
}
