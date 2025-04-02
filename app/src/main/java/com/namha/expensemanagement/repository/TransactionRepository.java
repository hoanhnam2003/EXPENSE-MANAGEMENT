package com.namha.expensemanagement.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.TransactionDao;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.dto.History;
import java.util.List;

public class TransactionRepository {
    // DAO để thao tác với bảng Transaction trong cơ sở dữ liệu
    private final TransactionDao mTransactionDao;

    // LiveData chứa danh sách tất cả giao dịch
    private final LiveData<List<Transaction>> mAllTransactions;

    // LiveData chứa tổng số dư cuối cùng
    private final LiveData<Double> mLastTotalBalance;

    // Constructor khởi tạo repository, lấy instance của database và DAO tương ứng
    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mTransactionDao = db.transactionDao();
        mAllTransactions = mTransactionDao.getAllTransactions();
        mLastTotalBalance = mTransactionDao.getLastTotalBalance();
    }

    // Phương thức lấy tất cả giao dịch
    public LiveData<List<Transaction>> getAllTransactions() {
        if (mAllTransactions == null) {
            Log.e("TransactionRepository", "getAllTransactions: No transactions data available");
        }
        return mAllTransactions;
    }

    // Lấy giao dịch cuối cùng
    public LiveData<Transaction> getLastTransaction() {
        LiveData<Transaction> lastTransaction = mTransactionDao.getLastTransaction();
        if (lastTransaction == null) {
            Log.e("TransactionRepository", "getLastTransaction: No last transaction found");
        }
        return lastTransaction;
    }

    // Thêm một giao dịch mới vào cơ sở dữ liệu
    public void insert(Transaction transaction) {
        if (transaction != null) {
            AppDatabase.getDatabaseWriteExecutor().execute(() -> mTransactionDao.insert(transaction));
        } else {
            Log.e("TransactionRepository", "insert: Transaction is null");
        }
    }

    // Lấy tổng số dư cuối cùng
    public LiveData<Double> getLastTotalBalance() {
        if (mLastTotalBalance == null) {
            Log.e("TransactionRepository", "getLastTotalBalance: No total balance available");
        }
        return mLastTotalBalance;
    }

    // Lấy lịch sử giao dịch
    public LiveData<List<History>> getHistory() {
        LiveData<List<History>> history = mTransactionDao.getHistory();
        if (history == null) {
            Log.e("TransactionRepository", "getHistory: No history data available");
        }
        return history;
    }

    // Tìm kiếm giao dịch theo loại và ngày
    public LiveData<List<History>> searchByTypeAndDate(String typeName, String datePattern) {
        if ((datePattern == null || datePattern.isEmpty()) && (typeName == null || typeName.isEmpty())) {
            Log.e("TransactionRepository", "searchByTypeAndDate: Both parameters are null or empty");
            return null;
        }

        Log.d("TransactionRepository", "Searching for type: " + typeName + " and date: " + datePattern);

        LiveData<List<History>> searchResult = mTransactionDao.searchByDate(
                typeName == null || typeName.trim().isEmpty() ? null : typeName,
                datePattern == null || datePattern.trim().isEmpty() ? null : datePattern
        );

        if (searchResult == null) {
            Log.e("TransactionRepository", "searchByTypeAndDate: No search results found for type " + typeName + " and date " + datePattern);
        }
        return searchResult;
    }

    // Xóa giao dịch theo ID
    public void deleteTransactionById(int transactionId) {
        if (transactionId > 0) {
            AppDatabase.getDatabaseWriteExecutor().execute(() -> mTransactionDao.deleteTransactionById(transactionId));
        } else {
            Log.e("TransactionRepository", "deleteTransactionById: Invalid transaction ID");
        }
    }

    // Cập nhật tổng số dư
    public void updateTotalAmount(double newTotalAmount) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            if (newTotalAmount >= 0) {
                mTransactionDao.updateLatestTotalBalance(newTotalAmount);
            } else {
                Log.e("TransactionRepository", "updateTotalAmount: Invalid total amount");
            }
        });
    }

    // Lấy giao dịch theo ID
    public LiveData<History> getTransactionById(int transactionId) {
        if (transactionId <= 0) {
            Log.e("TransactionRepository", "getTransactionById: Invalid transaction ID");
            return null;
        }
        LiveData<History> result = mTransactionDao.getTransactionById(transactionId);
        if (result == null) {
            Log.e("TransactionRepository", "getTransactionById: No transaction found with ID: " + transactionId);
        }
        return result;
    }

    // Lấy danh sách giao dịch theo tháng
    public LiveData<List<Transaction>> getTransactionsByMonth(String month) {
        if (month == null || month.isEmpty()) {
            Log.e("TransactionRepository", "getTransactionsByMonth: Month is null or empty");
            return null;
        }
        LiveData<List<Transaction>> transactionsByMonth = mTransactionDao.getTransactionsByMonth(month);
        if (transactionsByMonth == null) {
            Log.e("TransactionRepository", "getTransactionsByMonth: No transactions found for month " + month);
        }
        return transactionsByMonth;
    }

    // Lấy danh sách giao dịch theo ngày
    public LiveData<List<Transaction>> getTransactionsByDate(String date) {
        if (date == null || date.isEmpty()) {
            Log.e("TransactionRepository", "getTransactionsByDate: Date is null or empty");
            return null;
        }
        LiveData<List<Transaction>> transactionsByDate = mTransactionDao.getTransactionsByDate(date);
        if (transactionsByDate == null) {
            Log.e("TransactionRepository", "getTransactionsByDate: No transactions found for date " + date);
        }
        return transactionsByDate;
    }

    // Lấy tổng thu nhập theo loại giao dịch
    public LiveData<Double> getTotalIncome(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            Log.e("TransactionRepository", "getTotalIncome: Invalid typeName");
            return null;
        }

        LiveData<Double> totalIncome = mTransactionDao.getTotalIncome(typeName);

        if (totalIncome == null) {
            Log.e("TransactionRepository", "getTotalIncome: No total income found for typeName: " + typeName);
        }

        return totalIncome;
    }

    // Lấy tất cả lịch sử giao dịch
    public LiveData<List<History>> getAllHistory() {
        return mTransactionDao.getAllHistory(); // Giả sử TransactionDao có phương thức này
    }
}