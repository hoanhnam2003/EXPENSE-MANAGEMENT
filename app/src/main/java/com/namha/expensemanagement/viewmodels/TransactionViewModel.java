package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.dto.History;
import com.namha.expensemanagement.repository.TransactionRepository;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository mRepository;
    private final LiveData<List<Transaction>> mAllTransactions;
    private final LiveData<Double> mTotalBalance;
    private LiveData<List<History>> historyList;
    private static final String TAG = "TransactionViewModel";

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        // Khởi tạo repository và kiểm tra null
        mRepository = new TransactionRepository(application);
        if (mRepository != null) {
            mAllTransactions = mRepository.getAllTransactions();
            mTotalBalance = mRepository.getLastTotalBalance(); // Giả sử phương thức này có trong repository
        } else {
            Log.e(TAG, "Repository is null");
            mAllTransactions = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng nếu repository bị null
            mTotalBalance = new MutableLiveData<>();
        }
    }

    // Phương thức chèn giao dịch, kiểm tra null
    public void insert(Transaction transaction) {
        if (mRepository != null) {
            mRepository.insert(transaction);
        } else {
            Log.e(TAG, "Repository is null, cannot insert transaction");
        }
    }

    // Phương thức lấy tổng số dư
    public LiveData<Double> getTotalBalance() {
        return mTotalBalance;
    }

    // Lấy giao dịch cuối cùng, kiểm tra null
    public LiveData<Transaction> getLastTransaction() {
        if (mRepository != null) {
            return mRepository.getLastTransaction(); // Giả sử phương thức này có trong repository
        } else {
            Log.e(TAG, "Repository is null, cannot get last transaction");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }

    // Lấy danh sách lịch sử, kiểm tra null
    public LiveData<List<History>> getHistoryList() {
        if (historyList == null && mRepository != null) {
            historyList = mRepository.getHistory();
        } else if (mRepository == null) {
            Log.e(TAG, "Repository is null, cannot get history list");
            historyList = new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
        return historyList;
    }

    // Phương thức log toàn bộ dữ liệu bảng transactions
    public void logAllTransactionsOnce() {
        if (mAllTransactions != null) {
            mAllTransactions.observeForever(new Observer<List<Transaction>>() {
                @Override
                public void onChanged(List<Transaction> transactions) {
                    if (transactions != null && !transactions.isEmpty()) {
                        for (Transaction transaction : transactions) {
                            Log.d("kết quả bảng Transaction: ", transaction.toString());
                        }
                    } else {
                        Log.d("TransactionLog", "không có dữ liệu");
                    }
                    mAllTransactions.removeObserver(this);
                }
            });
        } else {
            Log.e(TAG, "mAllTransactions is null");
        }
    }

    // Phương thức tìm kiếm theo ngày và loại, kiểm tra null
    public LiveData<List<History>> searchByTypeAndDate(String typeName, String datePattern) {
        if (mRepository != null) {
            Log.d(TAG, "Searching for type: " + typeName + " and date: " + datePattern);

            LiveData<List<History>> result = mRepository.searchByTypeAndDate(typeName, datePattern);

            result.observeForever(histories -> {
                Log.d(TAG, "Search results: " + (histories != null ? histories.size() : "No results"));
            });

            return result;
        } else {
            Log.e(TAG, "Repository is null, cannot search by type and date");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }


    // Xóa giao dịch theo ID, kiểm tra null
    public void deleteTransactionById(int transactionId) {
        if (mRepository != null) {
            mRepository.deleteTransactionById(transactionId);
        } else {
            Log.e(TAG, "Repository is null, cannot delete transaction");
        }
    }

    // Cập nhật tổng số tiền, kiểm tra null
    public void updateTotalAmount(double newTotalAmount) {
        if (mRepository != null) {
            mRepository.updateTotalAmount(newTotalAmount);
        } else {
            Log.e(TAG, "Repository is null, cannot update total amount");
        }
    }

    // Lấy lịch sử giao dịch theo ID, kiểm tra null
    public LiveData<History> getTransactionById(int transactionId) {
        if (mRepository != null) {
            return mRepository.getTransactionById(transactionId);
        } else {
            Log.e(TAG, "Repository is null, cannot get transaction by ID");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }

    // Lấy danh sách giao dịch theo tháng, kiểm tra null
    public LiveData<List<Transaction>> getTransactionsByMonth(String month) {
        if (mRepository != null) {
            return mRepository.getTransactionsByMonth(month);
        } else {
            Log.e(TAG, "Repository is null, cannot get transactions by month");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }

    // Lấy tất cả giao dịch
    public LiveData<List<Transaction>> getAllTransactions() {
        return mAllTransactions;
    }

    // Lấy danh sách giao dịch theo ngày, kiểm tra null
    public LiveData<List<Transaction>> getTransactionsByDate(String date) {
        if (mRepository != null) {
            return mRepository.getTransactionsByDate(date);
        } else {
            Log.e(TAG, "Repository is null, cannot get transactions by date");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }

    // Giả sử phương thức này lấy tổng thu nhập từ Repository
    // Lấy tổng thu nhập từ loại giao dịch theo tên loại
    public LiveData<Double> getTotalIncome(String typeName) {
        if (mRepository != null) {
            return mRepository.getTotalIncome(typeName);
        } else {
            Log.e(TAG, "Repository is null, cannot get total income");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }

    public LiveData<List<History>> getAllHistory() {
        if (mRepository != null) {
            return mRepository.getAllHistory(); // Đảm bảo phương thức này có trong TransactionRepository
        } else {
            Log.e(TAG, "Repository is null, cannot get all history");
            return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository null
        }
    }

}
