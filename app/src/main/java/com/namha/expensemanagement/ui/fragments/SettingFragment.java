package com.namha.expensemanagement.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.database.AppDatabase;
import com.namha.expensemanagement.databinding.SettingFragmentBinding;
import com.namha.expensemanagement.viewmodels.DailyLimitViewModel;
import com.namha.expensemanagement.viewmodels.MonthlyLimitViewModel;
import com.namha.expensemanagement.viewmodels.SharedViewModel;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class SettingFragment extends Fragment {

    private static final String PREFS_NAME = "SettingFragmentPrefs";
    private static final String KEY_EXPENSE_PROGRESS = "ExpenseProgress";

    private SettingFragmentBinding binding;
    private MonthlyLimitViewModel monthlyLimitViewModel;
    private DailyLimitViewModel dailyLimitViewModel;
    private SharedPreferences sharedPreferences;
    private View.OnClickListener addClickListener;
    private AppDatabase appDatabase;
    private SeekBar seekBar;
    private FrameLayout frameLayout;
    private SharedViewModel sharedViewModel;

    TransactionViewModel transactionViewModel;

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

    DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SettingFragmentBinding.inflate(inflater, container, false);
        return binding != null ? binding.getRoot() : null; // Kiểm tra binding null


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) {
            return; // Đảm bảo binding không null trước khi tiếp tục
        }
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        monthlyLimitViewModel = new ViewModelProvider(this).get(MonthlyLimitViewModel.class);
        dailyLimitViewModel = new ViewModelProvider(this).get(DailyLimitViewModel.class);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        appDatabase = AppDatabase.getInstance(requireContext());
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        seekBar = view.findViewById(R.id.seekBar);
        frameLayout = view.findViewById(R.id.frStatistical);


        setupUI();

        // Đọc giá trị đã lưu trước đó
        int savedColor = sharedPreferences.getInt("SelectedColor", ContextCompat.getColor(requireContext(), R.color.hongthongke));
        int savedProgress = sharedPreferences.getInt("SeekBarProgress", 0);

        // Cập nhật SeekBar và màu của FrameLayout với giá trị đã lưu
        seekBar.setProgress(savedProgress);
        frameLayout.setBackgroundColor(savedColor);
        sharedViewModel.setSelectedColor(savedColor); // Cập nhật ViewModel

        // Xử lý sự kiện SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int selectedColor;
                Context context = seekBar.getContext();

                switch (progress) {
                    case 0:
                        selectedColor = ContextCompat.getColor(context, R.color.hongthongke);
                        break;
                    case 1:
                        selectedColor = ContextCompat.getColor(context, R.color.color_red);
                        break;
                    case 2:
                        selectedColor = ContextCompat.getColor(context, R.color.color_green);
                        break;
                    case 3:
                        selectedColor = ContextCompat.getColor(context, R.color.color_blue);
                        break;
                    default:
                        selectedColor = ContextCompat.getColor(context, R.color.hongthongke); // Mặc định
                }

                frameLayout.setBackgroundColor(selectedColor);
                sharedViewModel.setSelectedColor(selectedColor); // Cập nhật vào ViewModel
                saveSelectedColor(selectedColor);

                // Lưu trạng thái màu vào SharedPreferences ngay lập tức
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("SeekBarProgress", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        binding.gmailsupport.setOnClickListener(v -> openEmailSupport());

    }

    private void openEmailSupport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hotro.quanlychitieu@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Yêu cầu hỗ trợ");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng gửi email"));
        } else {
            Toast.makeText(getContext(), "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupUI() {
        // Kiểm tra và thiết lập dữ liệu từ ViewModel
        monthlyLimitViewModel.getLastMonthlyLimitMoney().observe(getViewLifecycleOwner(), lastMoneyDay -> {
            if (lastMoneyDay != null) {
                int maxAmount = (int) lastMoneyDay.doubleValue();
                binding.sbExpenses1.setMax(maxAmount);

                int savedProgress = sharedPreferences.getInt(KEY_EXPENSE_PROGRESS, 0);
                symbols.setGroupingSeparator(',');

                binding.sbExpenses1.setProgress(savedProgress);
                binding.money.setText(String.format("%s VND", decimalFormat.format(savedProgress)));
            } else {
                binding.sbExpenses1.setMax(0); // Set max khi không có dữ liệu
                binding.sbExpenses1.setProgress(0);
                binding.money.setText("0 VND");
            }
        });

        // Sự kiện SeekBar - Kiểm tra trước khi cập nhật tiền tháng
        binding.sbExpenses1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.money.setText(String.format("%s VND", decimalFormat.format(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                // Lấy giá trị money_day_setting từ DailyLimit
                dailyLimitViewModel.getLastDailyLimitSetting().observe(getViewLifecycleOwner(), moneyDaySetting -> {
                    if (moneyDaySetting != null) {
                        // Chuyển đổi Double thành int để so sánh
                        int moneyDaySettingInt = (int) Math.round(moneyDaySetting);

                        if (progress < moneyDaySettingInt) {
                            Toast.makeText(getContext(), "Thiết lập tháng phải lớn hơn thiết lập ngày", Toast.LENGTH_SHORT).show();
                            seekBar.setProgress(moneyDaySettingInt);  // Đặt lại SeekBar
                        } else {
                            // Cập nhật nếu điều kiện hợp lệ
                            monthlyLimitViewModel.updateMoneyMonthSetting(progress);

                            // Lưu trạng thái của SeekBar vào SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(KEY_EXPENSE_PROGRESS, progress);
                            editor.apply();
                        }
                    }
                });
            }

        });

        binding.btnSave4.setOnClickListener(v -> {
            String inputMoneyStr = binding.edtMonthlySpending.getText().toString().trim();

            // Kiểm tra nếu nhập vào rỗng
            if (inputMoneyStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Loại bỏ dấu phẩy trước khi chuyển đổi thành số
                String cleanInputMoneyStr = inputMoneyStr.replace(",", "");
                double inputMoney = Double.parseDouble(cleanInputMoneyStr);

                // Kiểm tra nếu số tiền phải lớn hơn 0
                if (inputMoney <= 0) {
                    Toast.makeText(getContext(), "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thiết lập typeName dựa trên ID loại thu nhập
                String incomeTypeName = "Thu nhập";  // Có thể thay đổi theo logic của bạn

                // Quan sát tổng thu nhập từ các giao dịch đã thêm
                transactionViewModel.getTotalIncome(incomeTypeName).observe(getViewLifecycleOwner(), totalIncome -> {
                    if (totalIncome != null) {
                        double totalIncomeValue = totalIncome;

                        // Kiểm tra nếu số tiền nhập vào vượt quá tổng thu nhập
                        if (inputMoney > totalIncomeValue) {
                            Toast.makeText(getContext(),
                                    "Thiết lập tháng không được vượt quá tổng thu nhập",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Cập nhật hoặc thêm mới giới hạn thu nhập vào ViewModel
                            monthlyLimitViewModel.insertOrUpdateMonthlyLimit(inputMoney);

                            // Xóa trường nhập và hiển thị thông báo thành công
                            binding.edtMonthlySpending.setText("");
                            Toast.makeText(getContext(), "Thiết lập thành công", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("IncomeError", "Không thể lấy tổng thu nhập.");
                        Toast.makeText(getContext(), "Vui lòng thêm thu nhập trước khi thiết lập chi tiêu tháng", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("UnexpectedError", e.getMessage(), e);
                Toast.makeText(getContext(), "Đã xảy ra lỗi. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });

        // Bổ sung TextWatcher để tự động thêm dấu phẩy khi nhập
        binding.edtMonthlySpending.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;

                isEditing = true;
                String rawText = s.toString().replaceAll(",", ""); // Xóa dấu phẩy cũ

                try {
                    // Chuyển thành số và định dạng lại
                    double parsed = Double.parseDouble(rawText);
                    String formatted = formatCurrency(parsed);

                    // Cập nhật lại EditText với giá trị đã định dạng
                    binding.edtMonthlySpending.setText(formatted);
                    binding.edtMonthlySpending.setSelection(formatted.length()); // Đặt con trỏ về cuối
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                isEditing = false;
            }
        });

        binding.deletedata.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // Hàm định dạng số tiền với dấu phẩy phân tách hàng nghìn
    private String formatCurrency(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');  // Đặt dấu phẩy làm phân cách nghìn
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount);
    }

    // Hiển thị hộp thoại xác nhận xóa
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ dữ liệu không?")
                .setPositiveButton("Xóa", (dialog, which) -> clearAllData())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Xóa toàn bộ dữ liệu khỏi cơ sở dữ liệu và SharedPreferences
    private void clearAllData() {
        if (appDatabase == null) {
            Log.e("SettingFragment", "AppDatabase is null. Cannot delete data.");
            return;
        }

        // Xóa toàn bộ dữ liệu trong database
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            appDatabase.categoryDao().deleteAll();
            appDatabase.dailyLimitDao().deleteAll();
            appDatabase.monthlyLimitDao().deleteAll();
            appDatabase.transactionDao().deleteAll();
        });
        // Xóa dữ liệu SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Xóa màu đã lưu trong SharedPreferences thông qua SharedViewModel
        sharedViewModel.resetSelectedColor();

        // Cập nhật giao diện sau khi xóa
        requireActivity().runOnUiThread(() -> {
            monthlyLimitViewModel.updateMoneyMonthSetting(0);
            binding.sbExpenses1.setProgress(0);
            binding.money.setText("0 VND");
            Toast.makeText(getContext(), "Toàn bộ dữ liệu đã được xóa", Toast.LENGTH_SHORT).show();
        });
    }

    public void setAddClickListener(View.OnClickListener listener) {
        this.addClickListener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding để tránh memory leak
    }

    private void saveSelectedColor(int color) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SelectedColor", color);
        editor.apply();
    }

}