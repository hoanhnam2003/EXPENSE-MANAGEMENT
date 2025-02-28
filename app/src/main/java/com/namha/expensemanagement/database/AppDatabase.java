package com.namha.expensemanagement.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import com.namha.expensemanagement.database.dao.CategoryDao;
import com.namha.expensemanagement.database.dao.ColorDao;
import com.namha.expensemanagement.database.dao.DailyLimitDao;
import com.namha.expensemanagement.database.dao.MonthlyLimitDao;
import com.namha.expensemanagement.database.dao.SettingDao;
import com.namha.expensemanagement.database.dao.TransactionDao;
import com.namha.expensemanagement.database.dao.TypeDao;
import com.namha.expensemanagement.database.entities.Category;
import com.namha.expensemanagement.database.entities.Color;
import com.namha.expensemanagement.database.entities.DailyLimit;
import com.namha.expensemanagement.database.entities.MonthlyLimit;
import com.namha.expensemanagement.database.entities.Setting;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.database.entities.Type;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Type.class, DailyLimit.class, MonthlyLimit.class, Color.class, Transaction.class, Setting.class}, version = 12)
public abstract class   AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance; // Đảm bảo tính đồng bộ
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract CategoryDao categoryDao();
    public abstract TypeDao typeDao();
    public abstract DailyLimitDao dailyLimitDao();
    public abstract MonthlyLimitDao monthlyLimitDao();
    public abstract TransactionDao transactionDao();
    public abstract ColorDao colorDao();

    public abstract SettingDao settingDao();


    public static AppDatabase getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }

        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) { // Double-checked locking
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}
