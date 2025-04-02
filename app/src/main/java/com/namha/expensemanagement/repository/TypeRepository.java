package com.namha.expensemanagement.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.database.dao.TypeDao;
import com.namha.expensemanagement.database.entities.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TypeRepository {
    // Đối tượng DAO để thao tác với bảng Type trong cơ sở dữ liệu
    private final TypeDao typeDao;
    // ExecutorService để thực hiện các thao tác bất đồng bộ
    private final ExecutorService executorService;

    // Constructor khởi tạo repository, lấy instance của database và DAO tương ứng
    public TypeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application); // Lấy instance của cơ sở dữ liệu
        typeDao = db.typeDao(); // Lấy đối tượng DAO từ database
        executorService = AppDatabase.getDatabaseWriteExecutor(); // Lấy ExecutorService để xử lý thao tác bất đồng bộ
    }

    // Phương thức lấy danh sách tất cả các loại chi tiêu từ cơ sở dữ liệu
    public LiveData<List<Type>> getAllTypes() {
        LiveData<List<Type>> allTypes = typeDao.getAllTypes(); // Truy vấn danh sách loại chi tiêu từ database
        if (allTypes == null) { // Kiểm tra nếu danh sách null thì in thông báo lỗi
            System.err.println("TypeRepository: No types data available");
        }
        return allTypes; // Trả về danh sách loại chi tiêu
    }

    // Phương thức kiểm tra xem loại chi tiêu có tồn tại hay không dựa trên tên loại
    public boolean isTypeExists(String typeName) {
        if (typeName != null && !typeName.trim().isEmpty()) { // Kiểm tra xem typeName có hợp lệ không
            return typeDao.isTypeExists(typeName); // Gọi phương thức kiểm tra từ DAO
        } else {
            throw new IllegalArgumentException("Type name must not be null or empty"); // Ném ngoại lệ nếu typeName không hợp lệ
        }
    }

    // Phương thức thêm một loại chi tiêu mới vào cơ sở dữ liệu
    public void insert(Type type) {
        if (type != null) { // Kiểm tra nếu đối tượng type hợp lệ
            executorService.execute(() -> typeDao.insertType(type)); // Thực hiện thao tác chèn dữ liệu vào database trên luồng khác
        } else {
            throw new IllegalArgumentException("Type must not be null"); // Ném ngoại lệ nếu type null
        }
    }
}
