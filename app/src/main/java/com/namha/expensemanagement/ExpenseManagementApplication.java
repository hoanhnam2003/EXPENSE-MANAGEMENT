package com.namha.expensemanagement;

import android.app.Application;

import com.namha.expensemanagement.database.AppDatabase;

public class ExpenseManagementApplication extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
    }

    public static AppDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Database has not been initialized yet.");
        }
        return database;
    }
}
