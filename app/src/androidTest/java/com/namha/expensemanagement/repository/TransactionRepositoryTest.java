package com.namha.expensemanagement.repository;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.data.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TransactionRepositoryTest {

    private AppDatabase mAppDatabase;
    private TransactionRepository mTransactionRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        // Khởi tạo database trong bộ nhớ (in-memory database)
        mAppDatabase = Room.inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        AppDatabase.class
                ).allowMainThreadQueries() // Cho phép chạy trên main thread (chỉ dùng cho test)
                .build();

        // Khởi tạo repository
        mTransactionRepository = new TransactionRepository(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testInsertTransaction() throws InterruptedException {
        // Tạo đối tượng Transaction
        Transaction transaction = new Transaction(100.0, 1, "Test Transaction", "2025-03-12", 2, 500.0, 1, 2);

        // Chèn giao dịch vào cơ sở dữ liệu
        mTransactionRepository.insert(transaction);

        // Lấy danh sách giao dịch
        LiveData<List<Transaction>> transactions = mTransactionRepository.getAllTransactions();

        // Lấy dữ liệu từ LiveData bằng LiveDataTestUtil
        List<Transaction> transactionList = LiveDataTestUtil.getValue(transactions);

        // Kiểm tra dữ liệu
        assertNotNull(transactionList);
        assertEquals(1, transactionList.size());
        assertEquals("Test Transaction", transactionList.get(0).getContent());
        assertEquals(100.0, transactionList.get(0).getAmount(), 0);
        assertEquals("2025-03-12", transactionList.get(0).getDate());
        assertEquals(500.0, transactionList.get(0).getTotalBalance(), 0);
    }

    @Test
    public void testGetAllTransactions() throws InterruptedException {
        // Lấy danh sách giao dịch hiện có
        LiveData<List<Transaction>> transactionsLiveData = mTransactionRepository.getAllTransactions();
        List<Transaction> transactionList = LiveDataTestUtil.getValue(transactionsLiveData);

        // Kiểm tra nếu danh sách null hoặc rỗng thì chèn dữ liệu test vào
        if (transactionList == null || transactionList.isEmpty()) {
            Transaction transaction1 = new Transaction(100.0, 1, "Test Transaction 1", "2025-03-12", 2, 500.0, 1, 2);
            Transaction transaction2 = new Transaction(150.0, 1, "Test Transaction 2", "2025-03-13", 3, 600.0, 1, 2);

            mTransactionRepository.insert(transaction1);
            mTransactionRepository.insert(transaction2);

            // Chờ dữ liệu cập nhật vào database
            Thread.sleep(500);

            // Lấy lại danh sách sau khi chèn dữ liệu test
            transactionsLiveData = mTransactionRepository.getAllTransactions();
            transactionList = LiveDataTestUtil.getValue(transactionsLiveData);
        }

        // Kiểm tra danh sách không null và có ít nhất một giao dịch
        assertNotNull(transactionList);
        assertFalse(transactionList.isEmpty());

        // In ra log để kiểm tra dữ liệu
        for (Transaction t : transactionList) {
            Log.d("Test", "Transaction: " + t.getContent() + ", Amount: " + t.getAmount());
        }
    }

    @Test
    public void testDeleteTransactionById() throws InterruptedException {
        // Tạo giao dịch
        Transaction transaction = new Transaction(100.0, 1, "Test Transaction", "2025-03-12", 2, 500.0, 1, 2);

        // Chèn giao dịch vào database
        mTransactionRepository.insert(transaction);
        Thread.sleep(500);  // Đợi database cập nhật

        // Lấy danh sách sau khi chèn để kiểm tra
        List<Transaction> transactionListBeforeDelete = LiveDataTestUtil.getValue(mTransactionRepository.getAllTransactions());
        assertNotNull(transactionListBeforeDelete);
        assertFalse(transactionListBeforeDelete.isEmpty());

        // Lấy ID của giao dịch vừa chèn
        Transaction lastTransaction = transactionListBeforeDelete.get(transactionListBeforeDelete.size() - 1);
        int insertedId = lastTransaction.getId();

        // Kiểm tra ID có hợp lệ không
        assertTrue(insertedId > 0);

        // Xóa giao dịch theo ID
        mTransactionRepository.deleteTransactionById(insertedId);
        Thread.sleep(500);  // Đợi database cập nhật

        // Lấy danh sách sau khi xóa
        List<Transaction> transactionListAfterDelete = LiveDataTestUtil.getValue(mTransactionRepository.getAllTransactions());

        // Kiểm tra danh sách không chứa giao dịch đã xóa
        assertNotNull(transactionListAfterDelete);
        assertFalse(transactionListAfterDelete.contains(lastTransaction));
    }

    @After
    public void tearDown() {
        // Đóng cơ sở dữ liệu sau khi test
        mAppDatabase.close();
    }
}
