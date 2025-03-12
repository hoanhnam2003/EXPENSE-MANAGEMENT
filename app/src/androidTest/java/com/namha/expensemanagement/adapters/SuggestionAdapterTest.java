package com.namha.expensemanagement.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.ui.adapters.SuggestionAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SuggestionAdapterTest {

    private SuggestionAdapter adapter;
    private List<String> mockSuggestions;

    @Mock
    private SuggestionAdapter.OnItemClickListener mockListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockSuggestions = Arrays.asList("Suggestion 1", "Suggestion 2", "Suggestion 3");
        adapter = new SuggestionAdapter(mockSuggestions, mockListener);
    }

    @Test
    public void testItemCount() {
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testBindViewHolder() {
        // Tạo một ViewGroup giả lập (FrameLayout)
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // Inflate layout vào parent
        View itemView = LayoutInflater.from(ApplicationProvider.getApplicationContext())
                .inflate(R.layout.item_suggestion, parent, false);

        // Tạo ViewHolder
        SuggestionAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        adapter.onBindViewHolder(viewHolder, 1);

        // Kiểm tra nội dung của TextView có đúng không
        TextView textView = viewHolder.itemView.findViewById(R.id.suggestionText);
        assertEquals("Suggestion 2", textView.getText().toString());
    }

    @Test
    public void testItemClick() {
        RecyclerView recyclerView = new RecyclerView(ApplicationProvider.getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(ApplicationProvider.getApplicationContext())); // Cần thiết!

        // Inflate layout
        ViewGroup parent = recyclerView;
        SuggestionAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        adapter.onBindViewHolder(viewHolder, 0);

        // Giả lập sự kiện click
        viewHolder.itemView.performClick();

        // Kiểm tra xem phương thức onItemClick() có được gọi không
        verify(mockListener).onItemClick("Suggestion 1");
    }
}
