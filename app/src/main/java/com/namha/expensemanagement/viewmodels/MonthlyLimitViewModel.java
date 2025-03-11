    package com.namha.expensemanagement.viewmodels;

    import android.app.Application;
    import android.util.Log;

    import androidx.annotation.NonNull;
    import androidx.lifecycle.AndroidViewModel;
    import androidx.lifecycle.LiveData;
    import androidx.lifecycle.MutableLiveData;


    import com.namha.expensemanagement.database.entities.MonthlyLimit;
    import com.namha.expensemanagement.repository.MonthlyLimitRepository;

    import java.util.List;

    public class MonthlyLimitViewModel extends AndroidViewModel {
        private final MonthlyLimitRepository repository;
        private final LiveData<List<MonthlyLimit>> allMonthlyLimits;
        private static final String TAG = "MonthlyLimitViewModel";

        public MonthlyLimitViewModel(@NonNull Application application) {
            super(application);

            // Khởi tạo repository và kiểm tra null
            repository = new MonthlyLimitRepository(application);
            if (repository != null) {
                allMonthlyLimits = repository.getAllMonthlyLimits();
            } else {
                Log.e(TAG, "Repository is null");
                allMonthlyLimits = new MutableLiveData<>(); // Tạo một MutableLiveData rỗng nếu repository bị null
            }

            checkAndInsertInitialMonthlyLimit();
        }

        // Phương thức log toàn bộ dữ liệu bảng monthly_limits, kiểm tra null và log dữ liệu
        public void logAllMonthlyLimits() {
            if (allMonthlyLimits != null) {
                allMonthlyLimits.observeForever(monthlyLimits -> {
                    if (monthlyLimits == null || monthlyLimits.isEmpty()) {
                        Log.d(TAG, "Không có dữ liệu trong bảng monthly_limits");
                    } else {
                        for (MonthlyLimit monthlyLimit : monthlyLimits) {
                            Log.d(TAG, "Monthly Limit ID: " + monthlyLimit.getId() + ", Monthly Limit Amount: " + monthlyLimit.getMoney_month() + ", Monthly Limit Setting: " + monthlyLimit.getMoney_month_setting());
                        }
                    }
                });
            } else {
                Log.e(TAG, "allMonthlyLimits is null");
            }
        }

        // Phương thức chèn hoặc cập nhật monthly_limit, kiểm tra null
        public void insertOrUpdateMonthlyLimit(double moneyMonth) {
            if (repository != null) {
                repository.insertOrUpdateMonthlyLimit(moneyMonth);
            } else {
                Log.e(TAG, "Repository is null, cannot insert or update");
            }
        }

        // Trả về toàn bộ dữ liệu monthly_limits
        public LiveData<List<MonthlyLimit>> getAllMonthlyLimits() {
            return allMonthlyLimits;
        }

        // Lấy số tiền của bản ghi cuối cùng, kiểm tra null
        public LiveData<Double> getLastMonthlyLimitMoney() {
            if (repository != null) {
                return repository.getLastMonthlyLimitMoney();
            } else {
                Log.e(TAG, "Repository is null, cannot get last monthly limit money");
                return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository bị null
            }
        }

        // Kiểm tra và chèn giá trị ban đầu nếu bảng monthly_limits trống
        private void checkAndInsertInitialMonthlyLimit() {
            if (allMonthlyLimits != null) {
                allMonthlyLimits.observeForever(monthlyLimits -> {
                    if (monthlyLimits == null || monthlyLimits.isEmpty()) {
                        insertOrUpdateMonthlyLimit(0);
                        Log.d(TAG, "Bảng monthly_limits trống, đã chèn giá trị 0");
                    } else {
                        Log.d(TAG, "Bảng monthly_limits đã có dữ liệu");
                    }
                });
            } else {
                Log.e(TAG, "allMonthlyLimits is null, cannot check or insert initial monthly limit");
            }
        }

        // Phương thức cập nhật money_month_setting, kiểm tra null
        public void updateMoneyMonthSetting(double moneyMonthSetting) {
            if (repository != null) {
                repository.updateMoneyMonthSetting(moneyMonthSetting);
            } else {
                Log.e(TAG, "Repository is null, cannot update money month setting");
            }
        }

        // Lấy ID mới nhất, kiểm tra null
        public LiveData<Integer> getLastMonthlyLimitId() {
            if (repository != null) {
                return repository.getLastMonthlyLimitID();
            } else {
                Log.e(TAG, "Repository is null, cannot get last monthly limit ID");
                return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository bị null
            }
        }

        // Lấy giá trị setting mới nhất của money_month, kiểm tra null
        public LiveData<Double> getLastMonthLimitSetting() {
            if (repository != null) {
                return repository.getLastMonthLimitSetting();
            } else {
                Log.e(TAG, "Repository is null, cannot get last month limit setting");
                return new MutableLiveData<>(); // Trả về LiveData rỗng nếu repository bị null
            }
        }
    }
