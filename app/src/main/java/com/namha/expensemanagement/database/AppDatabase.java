package com.namha.expensemanagement.database;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.namha.expensemanagement.database.dao.CategoryDao;
import com.namha.expensemanagement.database.dao.DailyLimitDao;
import com.namha.expensemanagement.database.dao.MonthlyLimitDao;
import com.namha.expensemanagement.database.dao.TransactionDao;
import com.namha.expensemanagement.database.dao.TypeDao;
import com.namha.expensemanagement.database.entities.Category;
import com.namha.expensemanagement.database.entities.DailyLimit;
import com.namha.expensemanagement.database.entities.MonthlyLimit;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.database.entities.Type;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Type.class, DailyLimit.class, MonthlyLimit.class, Transaction.class}, version = 18)
public abstract class AppDatabase extends RoomDatabase {

    // Biến instance để đảm bảo chỉ có một đối tượng AppDatabase duy nhất được tạo (Singleton Pattern)
    private static volatile AppDatabase instance;

    // ExecutorService để thực hiện các tác vụ ghi dữ liệu vào database trên một luồng riêng biệt
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    // Các phương thức abstract để truy xuất DAO (Data Access Object)
    public abstract CategoryDao categoryDao();
    public abstract TypeDao typeDao();
    public abstract DailyLimitDao dailyLimitDao();
    public abstract MonthlyLimitDao monthlyLimitDao();
    public abstract TransactionDao transactionDao();

    // Phương thức để lấy instance duy nhất của AppDatabase (Singleton với Double-Checked Locking)
    public static AppDatabase getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null."); // Kiểm tra context không được null
        }

        if (instance == null) {
            synchronized (AppDatabase.class) { // Đảm bảo an toàn trong môi trường đa luồng
                if (instance == null) { // Kiểm tra lần nữa trước khi khởi tạo (Double-Checked Locking)
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration() // Cho phép xóa dữ liệu nếu có thay đổi schema
                            .build();
                }
            }
        }
        return instance;
    }

    // Phương thức để lấy ExecutorService cho các tác vụ ghi dữ liệu vào database
    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}
