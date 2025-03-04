package com.namha.expensemanagement.ui.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.database.entities.Category;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.database.entities.Type;
import com.namha.expensemanagement.databinding.AddFragmentBinding;
import com.namha.expensemanagement.viewmodels.CategoryViewModel;
import com.namha.expensemanagement.viewmodels.DailyLimitViewModel;
import com.namha.expensemanagement.viewmodels.MonthlyLimitViewModel;
import com.namha.expensemanagement.viewmodels.SharedViewModel;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;
import com.namha.expensemanagement.viewmodels.TypeViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddFragment extends Fragment {

    private AddFragmentBinding binding;
    private CategoryViewModel categoryViewModel;
    private TypeViewModel typeViewModel;
    private TransactionViewModel transactionViewModel;
    private ArrayAdapter<String> spinnerCategoryAdapter;
    private ArrayAdapter<Type> spinnerTypeAdapter;
    private List<String> categoriesList = new ArrayList<>();
    private List<Type> typesList = new ArrayList<>();
    private DailyLimitViewModel dailyLimitViewModel;
    private MonthlyLimitViewModel monthlyLimitViewModel;
    private SharedViewModel sharedViewModel;
    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel và kiểm tra null
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        typeViewModel = new ViewModelProvider(this).get(TypeViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        dailyLimitViewModel = new ViewModelProvider(this).get(DailyLimitViewModel.class);
        monthlyLimitViewModel = new ViewModelProvider(this).get(MonthlyLimitViewModel.class);

        setupSpinners();

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoriesList.clear();
                for (Category category : categories) {
                    categoriesList.add(category.getName());
                }
                spinnerCategoryAdapter.notifyDataSetChanged();
            } else {
                Log.e("AddFragment", "categories is null");
            }
        });

        // thay đổi màu nền
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        frameLayout = view.findViewById(R.id.frAdd);

        sharedViewModel.getSelectedColor().observe(getViewLifecycleOwner(), newColor -> {
            if (newColor != null) {
                frameLayout.setBackgroundColor(newColor);
            }
        });

        typeViewModel.getAllTypes().observe(getViewLifecycleOwner(), types -> {
            if (types != null) {
                typesList.clear();
                typesList.addAll(types);
                spinnerTypeAdapter.notifyDataSetChanged();
            } else {
                Log.e("AddFragment", "types is null");
            }
        });

        setupDateTimePicker();

        // Xử lý lưu danh mục
        binding.btnSave2.setOnClickListener(v -> {
            String categoryName = binding.editTextCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                Category category = new Category(categoryName);
                categoryViewModel.insert(category);
                binding.editTextCategoryName.setText("");
                Toast.makeText(getContext(), "Danh mục đã được thêm", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý xóa danh mục
        binding.btnDeleteCategory.setOnClickListener(v -> {
            if (binding.spinnerCatalog.getSelectedItem() != null) {
                String categoryNameToDelete = binding.spinnerCatalog.getSelectedItem().toString();
                if (!categoryNameToDelete.isEmpty()) {
                    categoryViewModel.deleteCategoryByName(categoryNameToDelete);
                    Toast.makeText(getContext(), "Danh mục đã được xóa", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Vui lòng chọn danh mục để xóa", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("AddFragment", "spinnerCatalog is null");
                Toast.makeText(getContext(), "Vui lòng chọn danh mục để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý lưu loại giao dịch
        binding.btnSave3.setOnClickListener(v -> {
            String amountText = binding.editTextAmount.getText().toString().trim();
            String cleanedAmountText = amountText.replaceAll("[^0-9]", "");

            if (TextUtils.isEmpty(cleanedAmountText) || !isValidDecimal(cleanedAmountText)) {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền hợp lệ (ví dụ: 10.300)", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(cleanedAmountText);
            String formattedAmount = formatCurrency(amount);
            binding.editTextAmount.setText(formattedAmount);

            String selectedCategory = binding.spinnerCatalog.getSelectedItem() != null
                    ? binding.spinnerCatalog.getSelectedItem().toString()
                    : "Unknown";

            Type selectedType = (Type) binding.spinnerType.getSelectedItem();
            String selectedTime = binding.tvTime1.getText().toString().trim();
            String content = binding.content.getText().toString().trim();

            if (!selectedCategory.isEmpty() && selectedType != null && !selectedTime.isEmpty() && !content.isEmpty()) {
                observeOnce(categoryViewModel.getCategoryIdByName(selectedCategory), getViewLifecycleOwner(), categoryId -> {
                    if (categoryId != null) {
                        observeOnce(typeViewModel.getTypeIdByName(selectedType.getType_name()), getViewLifecycleOwner(), typeId -> {
                            if (typeId != null) {
                                observeOnce(dailyLimitViewModel.getLastDailyLimitId(), getViewLifecycleOwner(), idDailyLimit -> {
                                    observeOnce(monthlyLimitViewModel.getLastMonthlyLimitId(), getViewLifecycleOwner(), idMonthyLimit -> {
                                        int dailyLimitId = idDailyLimit != null ? idDailyLimit : 0;
                                        int monthlyLimitId = idMonthyLimit != null ? idMonthyLimit : 0;

                                        // Gọi phương thức để cập nhật số dư và lưu giao dịch
                                        updateTotalBalanceAndSaveTransaction(amount, typeId, content, selectedTime, categoryId, dailyLimitId, monthlyLimitId);
                                    });
                                });
                            } else {
                                Toast.makeText(getContext(), "Loại giao dịch không tồn tại.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Danh mục không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm observeOnce để quan sát LiveData một lần
    public static <T> void observeOnce(LiveData<T> liveData, LifecycleOwner owner, Observer<T> observer) {
        if (liveData != null) {
            liveData.observe(owner, new Observer<T>() {
                @Override
                public void onChanged(T t) {
                    observer.onChanged(t);
                    liveData.removeObserver(this);
                }
            });
        } else {
            Log.e("AddFragment", "LiveData is null in observeOnce");
        }
    }

    // Hàm updateTotalBalanceAndSaveTransaction để cập nhật số dư và lưu giao dịch
    private void updateTotalBalanceAndSaveTransaction(double amount, int typeId, String content, String date, int categoryId, int idDailyLimit, int idMonthlyLimit) {
        observeOnce(transactionViewModel.getLastTransaction(), getViewLifecycleOwner(), lastTransaction -> {
            double totalBalance = lastTransaction != null ? lastTransaction.getTotalBalance() : 0;

            Log.d("UpdateTotalBalance", "Tổng tiền hiện tại: " + totalBalance);

            Type selectedType = (Type) binding.spinnerType.getSelectedItem();
            if (selectedType != null) {
                if (selectedType.getType_name().equals("Tiền chi") || selectedType.getType_name().equals("Cho vay")) {
                    totalBalance -= amount;
                } else if (selectedType.getType_name().equals("Thu nhập")) {
                    totalBalance += amount;
                }

                Log.d("UpdateTotalBalance", "Cập nhật tổng tiền: " + totalBalance);
            } else {
                Log.e("UpdateTotalBalance", "selectedType bị null");
            }

            saveTransaction(amount, typeId, content, date, categoryId, totalBalance, idDailyLimit, idMonthlyLimit);
        });
    }

    // Hàm saveTransaction để lưu giao dịch
    private void saveTransaction(double amount, int typeId, String content, String date, int categoryId, double totalBalance, int idDailyLimit, int idMonthlyLimit) {
        if (typeId <= 0 || categoryId <= 0) {
            Toast.makeText(getContext(), "Danh mục hoặc loại giao dịch không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d("SaveTransaction", "Lưu giao dịch với số tiền: " + amount +
                    ", typeId: " + typeId +
                    ", content: " + content +
                    ", date: " + date +
                    ", categoryId: " + categoryId +
                    ", totalBalance: " + totalBalance +
                    ", idDailyLimit: " + idDailyLimit +
                    ", idMonthlyLimit: " + idMonthlyLimit
            );

            Transaction transaction = new Transaction(amount, typeId, content, date, categoryId, totalBalance, null, null);
            transactionViewModel.insert(transaction);

            // Dọn dẹp trường nhập liệu sau khi lưu
            binding.editTextAmount.setText("");
            binding.content.setText("");
            Toast.makeText(getContext(), "Dữ liệu đã được lưu", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SaveTransaction", "Lỗi khi lưu giao dịch", e);
            Toast.makeText(getContext(), "Lỗi khi lưu dữ liệu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm kiểm tra số tiền nhập vào có hợp lệ không
    private boolean isValidDecimal(String amountText) {
        try {
            Double.parseDouble(amountText);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Hàm định dạng số tiền
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        formatter.setGroupingUsed(true);
        return formatter.format(amount);
    }

    private void setupSpinners() {
        if (getContext() != null) {
            if (binding != null) {
                // Setup spinner for categories
                if (categoriesList != null) {
                    spinnerCategoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoriesList);
                    spinnerCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerCatalog.setAdapter(spinnerCategoryAdapter);
                } else {
                    Log.e("AddFragment", "categoriesList is null, cannot setup spinnerCategoryAdapter");
                }

                // Setup spinner for types
                if (typesList != null) {
                    spinnerTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typesList);
                    spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerType.setAdapter(spinnerTypeAdapter);
                } else {
                    Log.e("AddFragment", "typesList is null, cannot setup spinnerTypeAdapter");
                }
            } else {
                Log.e("AddFragment", "Binding is null, cannot setup spinners");
            }
        } else {
            Log.e("AddFragment", "Context is null, cannot setup spinners");
        }
    }


    private void setupDateTimePicker() {
        if (binding.tvTime1 != null) {
            binding.tvTime1.setOnClickListener(v -> showDateTimePicker());
        } else {
            Log.e("AddFragment", "tvTime1 is null. Check if it is correctly included in the layout.");
        }
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute1) -> {
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                binding.tvTime1.setText(selectedTime + " " + selectedDate);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }
}
