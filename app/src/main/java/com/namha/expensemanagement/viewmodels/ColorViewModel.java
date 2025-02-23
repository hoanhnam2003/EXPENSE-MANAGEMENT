package com.namha.expensemanagement.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.namha.expensemanagement.database.entities.Color;
import com.namha.expensemanagement.repository.ColorRepository;

import java.util.List;

public class ColorViewModel extends AndroidViewModel {
    private final ColorRepository repository;
    private final LiveData<List<Color>> allColors;
    private static final String TAG = "ColorViewModel";

    public ColorViewModel(@NonNull Application application) {
        super(application);

        // Khởi tạo repository và kiểm tra null
        repository = new ColorRepository(application);
        if (repository != null) {
            allColors = repository.getAllColors();
        } else {
            System.err.println(TAG + ": Repository is null");
            allColors = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng để tránh lỗi null
        }

        logAllColors();
    }

    // Phương thức để log toàn bộ dữ liệu bảng colors, kiểm tra null và log dữ liệu
    public void logAllColors() {
        if (allColors != null) {
            allColors.observeForever(colors -> {
                if (colors == null || colors.isEmpty()) {
                    System.out.println("Không có dữ liệu trong bảng colors");
                } else {
                    for (Color color : colors) {
                        System.out.println("data bảng color: " + "Color ID: " + color.getId() + ", Color Name: " + color.getName_color());
                    }
                }
            });
        } else {
            System.err.println(TAG + ": allColors is null");
        }
    }

    // Trả về tất cả các màu, không cần kiểm tra null vì LiveData không bao giờ null
    public LiveData<List<Color>> getAllColors() {
        return allColors;
    }
}
