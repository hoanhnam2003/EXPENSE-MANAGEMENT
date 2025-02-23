package com.namha.expensemanagement.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;


import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.ColorDao;
import com.namha.expensemanagement.database.entities.Color;

import java.util.List;

public class ColorRepository {
    private final ColorDao colorDao;
    private final LiveData<List<Color>> allColors;

    public ColorRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        colorDao = db.colorDao();
        allColors = colorDao.getAllColors();
    }

    public LiveData<List<Color>> getAllColors() {
        return allColors;
    }
}
