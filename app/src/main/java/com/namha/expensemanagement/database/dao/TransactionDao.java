package com.namha.expensemanagement.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.dto.History;

import java.util.List;

// Trong TransactionDao.java
@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

//    lấy giao dịch cuối cùng
    @Query("SELECT * FROM transactions ORDER BY id DESC LIMIT 1")
    LiveData<Transaction> getLastTransaction();

//    lấy số dư cuối cùng
    @Query("SELECT totalBalance FROM transactions ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastTotalBalance();

    @Query("SELECT * FROM transactions")
    LiveData<List<Transaction>> getAllTransactions();

    // Lấy tất cả lịch sử
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "ORDER BY t.id DESC")
    LiveData<List<History>> getHistory(); // Sử dụng LiveData

    // Thêm phương thức tìm kiếm theo ngày và loại gần đúng
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "WHERE (:typeName IS NULL OR ty.type_name LIKE '%' || :typeName || '%') " +
            "AND (:datePattern IS NULL OR t.date LIKE '%' || :datePattern || '%') " +
            "ORDER BY t.id DESC")
    LiveData<List<History>> searchByDate(String typeName, String datePattern);


    // Xóa bản ghi theo id
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    void deleteTransactionById(int transactionId);

    // Cập nhật tổng tiền vào bản ghi mới nhất
    @Query("UPDATE transactions SET totalBalance = :newTotalBalance WHERE id = (SELECT id FROM transactions ORDER BY id DESC LIMIT 1)")
    void updateLatestTotalBalance(double newTotalBalance);

    // Query to get transaction by ID
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "WHERE t.id = :transactionId")

    LiveData<History> getTransactionById(int transactionId);

    // Thêm phương thức lấy giao dịch theo tháng
    @Query("SELECT * FROM transactions WHERE strftime('%m', date) = :month")
    LiveData<List<Transaction>> getTransactionsByMonth(String month);

    // Thêm phương thức xóa toàn bộ giao dịch
    @Query("DELETE FROM transactions")
    void deleteAll();

    // Thêm phương thức lấy giao dịch theo ngày/tháng/năm và tháng năm
    @Query("SELECT * FROM transactions WHERE date LIKE '%' || :date || '%'")
    LiveData<List<Transaction>> getTransactionsByDate(String date);

    @Query("SELECT SUM(amount) FROM transactions WHERE typeId = (SELECT id FROM types WHERE type_name = :typeName LIMIT 2)")
    LiveData<Double> getTotalIncome(String typeName);



    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "ORDER BY t.id DESC")
    LiveData<List<History>> getAllHistory();

}
