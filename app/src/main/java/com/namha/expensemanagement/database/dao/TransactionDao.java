package com.namha.expensemanagement.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.dto.History;
import java.util.List;

@Dao
public interface TransactionDao {

    // Thêm một giao dịch mới vào cơ sở dữ liệu
    @Insert
    void insert(Transaction transaction);

    // Lấy giao dịch cuối cùng trong danh sách, sắp xếp theo ID giảm dần
    @Query("SELECT * FROM transactions ORDER BY id DESC LIMIT 1")
    LiveData<Transaction> getLastTransaction();

    // Lấy số dư cuối cùng từ giao dịch gần nhất
    @Query("SELECT totalBalance FROM transactions ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastTotalBalance();

    // Lấy danh sách tất cả giao dịch trong cơ sở dữ liệu
    @Query("SELECT * FROM transactions")
    LiveData<List<Transaction>> getAllTransactions();

    // Lấy toàn bộ lịch sử giao dịch, bao gồm thông tin danh mục và loại giao dịch
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "ORDER BY t.id DESC")
    LiveData<List<History>> getHistory(); // Sử dụng LiveData để theo dõi dữ liệu

    // Tìm kiếm giao dịch theo ngày và loại giao dịch gần đúng
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "WHERE (:typeName IS NULL OR ty.type_name LIKE '%' || :typeName || '%') " +
            "AND (:datePattern IS NULL OR t.date LIKE '%' || :datePattern || '%') " +
            "ORDER BY t.id DESC")
    LiveData<List<History>> searchByDate(String typeName, String datePattern);

    // Xóa giao dịch khỏi cơ sở dữ liệu theo ID
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    void deleteTransactionById(int transactionId);

    // Cập nhật tổng số dư trong giao dịch mới nhất
    @Query("UPDATE transactions SET totalBalance = :newTotalBalance WHERE id = (SELECT id FROM transactions ORDER BY id DESC LIMIT 1)")
    void updateLatestTotalBalance(double newTotalBalance);

    // Lấy thông tin giao dịch theo ID cụ thể
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "WHERE t.id = :transactionId")
    LiveData<History> getTransactionById(int transactionId);

    // Lấy danh sách giao dịch trong một tháng cụ thể
    @Query("SELECT * FROM transactions WHERE strftime('%m', date) = :month")
    LiveData<List<Transaction>> getTransactionsByMonth(String month);

    // Xóa tất cả các giao dịch trong bảng transactions
    @Query("DELETE FROM transactions")
    void deleteAll();

    // Lấy danh sách giao dịch theo ngày, tháng hoặc năm cụ thể
    @Query("SELECT * FROM transactions WHERE date LIKE '%' || :date || '%'")
    LiveData<List<Transaction>> getTransactionsByDate(String date);

    // Tính tổng thu nhập theo loại giao dịch cụ thể
    @Query("SELECT SUM(amount) FROM transactions WHERE typeId = (SELECT id FROM types WHERE type_name = :typeName LIMIT 2)")
    LiveData<Double> getTotalIncome(String typeName);

    // Lấy toàn bộ lịch sử giao dịch, bao gồm danh mục và loại giao dịch
    @Query("SELECT t.id, c.name AS nameCategory, t.content, t.date, ty.type_name AS typeName, t.amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.categoryId = c.id " +
            "JOIN types ty ON t.typeId = ty.id " +
            "ORDER BY t.id DESC")
    LiveData<List<History>> getAllHistory();
}