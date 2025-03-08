package com.namha.expensemanagement.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.namha.expensemanagement.database.entities.Type;
import com.namha.expensemanagement.repository.TypeRepository;

import java.util.List;

public class  TypeViewModel extends AndroidViewModel {
    private final TypeRepository repository;
    private final LiveData<List<Type>> allTypes;
    private static final String TAG = "TypeViewModel";

    public TypeViewModel(@NonNull Application application) {
        super(application);

        // Khởi tạo repository và kiểm tra null
        repository = new TypeRepository(application);
        if (repository != null) {
            allTypes = repository.getAllTypes();
        } else {
            Log.e(TAG, "Repository is null");
            allTypes = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng nếu repository null
        }

        insertSampleData();
        logAllTypes();
    }

    // Chèn các dữ liệu mẫu nếu chưa tồn tại
    private void insertSampleData() {
        insertTypeIfNotExists(new Type(1, "Tiền chi"));
        insertTypeIfNotExists(new Type(2, "Cho vay"));
        insertTypeIfNotExists(new Type(3, "Thu nhập"));
    }

    // Chèn dữ liệu nếu không tồn tại, kiểm tra null cho repository
    private void insertTypeIfNotExists(Type type) {
        if (repository != null) {
            new Thread(() -> {
                if (!repository.isTypeExists(type.getType_name())) {
                    repository.insert(type);
                } else {
                    Log.d(TAG, "insertTypeIfNotExists: Type already exists - " + type.getType_name());
                }
            }).start();
        } else {
            Log.e(TAG, "Repository is null, cannot insert type");
        }
    }

    // Lấy tất cả các loại, không cần kiểm tra null cho LiveData
    public LiveData<List<Type>> getAllTypes() {
        return allTypes;
    }

    // Lấy ID loại theo tên, kiểm tra null cho danh sách types
    public LiveData<Integer> getTypeIdByName(String typeName) {
        MediatorLiveData<Integer> typeIdLiveData = new MediatorLiveData<>();
        if (allTypes != null) {
            typeIdLiveData.addSource(allTypes, types -> {
                if (types != null && !types.isEmpty()) {
                    for (Type type : types) {
                        if (type.getType_name().equals(typeName)) {
                            Log.d(TAG, "getTypeIdByName: Found type ID - " + type.getId() + " for type name - " + typeName);
                            typeIdLiveData.setValue(type.getId());
                            return;
                        }
                    }
                }
                Log.d(TAG, "getTypeIdByName: No match found for type name - " + typeName);
                typeIdLiveData.setValue(null);
            });
        } else {
            Log.e(TAG, "allTypes is null");
            typeIdLiveData.setValue(null);
        }
        return typeIdLiveData;
    }

    // Phương thức log toàn bộ dữ liệu bảng types
    public void logAllTypes() {
        if (allTypes != null) {
            allTypes.observeForever(new Observer<List<Type>>() {
                @Override
                public void onChanged(List<Type> types) {
                    if (types != null && !types.isEmpty()) {
                        for (Type type : types) {
                            Log.d("Data bảng type: ", "Type ID: " + type.getId() + ", Type Name: " + type.getType_name());
                        }
                    } else {
                        Log.d(TAG, "No data available in table types");
                    }
                }
            });
        } else {
            Log.e(TAG, "allTypes is null");
        }
    }
}