package com.namha.expensemanagement.adapters;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.ui.adapters.MessageAdapter;
import com.namha.expensemanagement.viewmodels.MessageModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MessageAdapterTest {

    private MessageAdapter adapter;
    private List<MessageModel> fakeMessages;
    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();

        // Tạo dữ liệu giả lập
        fakeMessages = Arrays.asList(
                new MessageModel("Hello!", MessageModel.SENT_BY_ME),
                new MessageModel("Hi! How can I help you?", MessageModel.SENT_BY_BOT)
        );

        adapter = new MessageAdapter(fakeMessages);
    }

    @Test
    public void testOnCreateViewHolder() {
        ViewGroup parent = new android.widget.FrameLayout(context);
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
    }

    @Test
    public void testOnBindViewHolder() {
        ViewGroup parent = new android.widget.FrameLayout(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.item_message, parent, false);

        MessageAdapter.viewHolder viewHolder = adapter.new viewHolder(itemView);

        adapter.onBindViewHolder(viewHolder, 0);
        assertEquals("Hello!", ((TextView) itemView.findViewById(R.id.right_text)).getText().toString());

        adapter.onBindViewHolder(viewHolder, 1);
        assertEquals("Hi! How can I help you?", ((TextView) itemView.findViewById(R.id.left_text)).getText().toString());
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }
}
