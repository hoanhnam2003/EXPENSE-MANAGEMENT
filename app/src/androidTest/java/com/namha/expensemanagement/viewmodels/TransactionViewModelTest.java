package com.namha.expensemanagement.viewmodels;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.repository.TransactionRepository;
import com.namha.expensemanagement.data.LiveDataTestUtil;
import com.namha.expensemanagement.dto.History;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TransactionViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TransactionViewModel viewModel;
    private TransactionRepository repository;
    private Application application;

    @Before
    public void setup() {
        application = ApplicationProvider.getApplicationContext();
        repository = new TransactionRepository(application);
        viewModel = new TransactionViewModel(application);
    }

    @Test
    public void testGetAllTransactions() throws InterruptedException {
        LiveData<List<Transaction>> transactions = viewModel.getAllTransactions();
        List<Transaction> result = LiveDataTestUtil.getValue(transactions);
        assertNotNull(result);
    }

    @Test
    public void testInsertTransaction() throws InterruptedException {
        Transaction transaction = new Transaction(
                100.0, // amount
                1, // typeId (Food)
                "Lunch", // content
                "2024-03-11", // date
                1, // categoryId (Food)
                500.0, // totalBalance (giả sử 500.0)
                null, // idDailyLimit
                null  // idMonthlyLimit
        );

        viewModel.insert(transaction);

        // Chờ database cập nhật (nếu cần)
        Thread.sleep(500);

        List<Transaction> transactions = LiveDataTestUtil.getValue(viewModel.getAllTransactions());
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());

        // So sánh dữ liệu bằng cách duyệt danh sách thay vì dùng contains()
        boolean found = false;
        for (Transaction t : transactions) {
            if (t.getAmount() == transaction.getAmount() &&
                    t.getTypeId() == transaction.getTypeId() &&
                    t.getContent().equals(transaction.getContent()) &&
                    t.getDate().equals(transaction.getDate()) &&
                    t.getCategoryId() == transaction.getCategoryId() &&
                    t.getTotalBalance() == transaction.getTotalBalance()) {
                found = true;
                break;
            }
        }

        assertTrue("Transaction không tồn tại trong danh sách!", found);
    }

    @Test
    public void testGetTotalBalance() throws InterruptedException {
        LiveData<Double> totalBalance = viewModel.getTotalBalance();
        Double balance = LiveDataTestUtil.getValue(totalBalance);
        assertNotNull(balance);
    }

    @Test
    public void testGetLastTransaction() throws InterruptedException {
        LiveData<Transaction> lastTransaction = viewModel.getLastTransaction();
        Transaction transaction = LiveDataTestUtil.getValue(lastTransaction);
        assertNotNull(transaction);
    }

    @Test
    public void testSearchByTypeAndDate() throws InterruptedException {
        LiveData<List<History>> historyLiveData = viewModel.searchByTypeAndDate("Food", "2024-03-11");
        List<History> historyList = LiveDataTestUtil.getValue(historyLiveData);
        assertNotNull(historyList);
    }

    @Test
    public void testDeleteTransactionById() throws InterruptedException {
        Transaction transaction = new Transaction(
                50.0, // amount
                2, // typeId (Transport)
                "Bus ticket", // content
                "2024-03-11", // date
                2, // categoryId (Transport)
                500.0, // totalBalance (giả sử 500.0)
                null, // idDailyLimit
                null  // idMonthlyLimit
        );

        viewModel.insert(transaction);

        // Chờ database cập nhật
        Thread.sleep(500);

        List<Transaction> transactionsBefore = LiveDataTestUtil.getValue(viewModel.getAllTransactions());
        assertNotNull(transactionsBefore);
        assertFalse(transactionsBefore.isEmpty());

        // Lấy transaction vừa insert (vì ID có thể được SQLite tự động tạo)
        Transaction insertedTransaction = null;
        for (Transaction t : transactionsBefore) {
            if (t.getAmount() == transaction.getAmount() &&
                    t.getTypeId() == transaction.getTypeId() &&
                    t.getContent().equals(transaction.getContent()) &&
                    t.getDate().equals(transaction.getDate()) &&
                    t.getCategoryId() == transaction.getCategoryId() &&
                    t.getTotalBalance() == transaction.getTotalBalance()) {
                insertedTransaction = t;
                break;
            }
        }

        assertNotNull("Không tìm thấy transaction sau khi insert!", insertedTransaction);
        int insertedId = insertedTransaction.getId(); // Lấy ID thực tế sau khi insert

        // Xóa transaction bằng ID thực tế
        viewModel.deleteTransactionById(insertedId);

        // Chờ database cập nhật
        Thread.sleep(500);

        List<Transaction> transactionsAfter = LiveDataTestUtil.getValue(viewModel.getAllTransactions());

        // Kiểm tra xem transaction có còn tồn tại không
        boolean found = false;
        for (Transaction t : transactionsAfter) {
            if (t.getId() == insertedId) {
                found = true;
                break;
            }
        }

        assertFalse("Transaction vẫn tồn tại sau khi delete!", found);
    }

    @Test
    public void testUpdateTotalAmount() throws InterruptedException {
        viewModel.updateTotalAmount(500.0);
        Double updatedBalance = LiveDataTestUtil.getValue(viewModel.getTotalBalance());
        assertEquals(500.0, updatedBalance, 0.01);
    }
}
