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
    private TransactionDao mTransactionDao;
    private LiveData<List<Transaction>> mAllTransactions;
    private LiveData<Double> mLastTotalBalance;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mTransactionDao = db.transactionDao();
        mAllTransactions = mTransactionDao.getAllTransactions();
        mLastTotalBalance = mTransactionDao.getLastTotalBalance();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        if (mAllTransactions == null) {
            Log.e("TransactionRepository", "getAllTransactions: No transactions data available");
        }
        return mAllTransactions;
    }

    public LiveData<Transaction> getLastTransaction() {
        LiveData<Transaction> lastTransaction = mTransactionDao.getLastTransaction();
        if (lastTransaction == null) {
            Log.e("TransactionRepository", "getLastTransaction: No last transaction found");
        }
        return lastTransaction;
    }

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

    // Phương thức lấy lịch sử
    public LiveData<List<History>> getHistory() {
        LiveData<List<History>> history = mTransactionDao.getHistory();
        if (history == null) {
            Log.e("TransactionRepository", "getHistory: No history data available");
        }
        return history;
    }

    // Tìm kiếm lịch sử
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

    // Cập nhật tổng tiền
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

    // Lấy giao dịch theo tháng
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

    // Lấy giao dịch theo ngày
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

    // Phương thức lấy tổng thu nhập từ loại giao dịch theo tên loại
    public LiveData<Double> getTotalIncome(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            Log.e("TransactionRepository", "getTotalIncome: Invalid typeName");
            return null;
        }

        // Gọi phương thức trong Dao để lấy tổng thu nhập từ loại thu nhập theo tên
        LiveData<Double> totalIncome = mTransactionDao.getTotalIncome(typeName);

        if (totalIncome == null) {
            Log.e("TransactionRepository", "getTotalIncome: No total income found for typeName: " + typeName);
        }

        return totalIncome;
    }

    public LiveData<List<History>> getAllHistory() {
        return mTransactionDao.getAllHistory(); // Giả sử TransactionDao có phương thức này
    }
}