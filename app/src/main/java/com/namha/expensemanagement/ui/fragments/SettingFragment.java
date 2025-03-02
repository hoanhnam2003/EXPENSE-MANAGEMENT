package com.namha.expensemanagement.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
    private TextView setmonthlyspending, money, setcolor, setfontsize, datamanagement;
    private SeekBar sbExpenses2;

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

        monthlyLimitViewModel = new ViewModelProvider(this).get(MonthlyLimitViewModel.class);
        dailyLimitViewModel = new ViewModelProvider(this).get(DailyLimitViewModel.class);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        appDatabase = AppDatabase.getInstance(requireContext());
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        seekBar = view.findViewById(R.id.seekBar);
        frameLayout = view.findViewById(R.id.frStatistical);
        // Find the views
        setmonthlyspending = view.findViewById(R.id.setmonthlyspending);
        money = view.findViewById(R.id.money);
        setcolor = view.findViewById(R.id.setcolor);
        setfontsize = view.findViewById(R.id.setfontsize);
        datamanagement = view.findViewById(R.id.datamanagement);
        sbExpenses2 = view.findViewById(R.id.sbExpenses2);


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
                editor.putInt("SelectedColor", selectedColor);
                editor.putInt("SeekBarProgress", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        binding.gmailsupport.setOnClickListener(v -> openEmailSupport());

        // Set an OnSeekBarChangeListener to change the font size
        sbExpenses2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Adjust text size based on SeekBar progress
                float textSize = 12 + (progress * 0.5f); // Adjust the multiplier as needed
                setmonthlyspending.setTextSize(textSize);
                money.setTextSize(textSize);
                setcolor.setTextSize(textSize);
                setfontsize.setTextSize(textSize);
                datamanagement.setTextSize(textSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optionally handle when the user starts tracking
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optionally handle when the user stops tracking
            }
        });
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
                binding.sbExpenses1.setProgress(savedProgress);
                binding.money.setText(String.format("%,d VND", savedProgress));
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
                binding.money.setText(String.format("%,d VND", progress));
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
            if (inputMoneyStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double inputMoney = Double.parseDouble(inputMoneyStr);
                if (inputMoney <= 0) {
                    Toast.makeText(getContext(), "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy giá trị money_day_setting từ DailyLimit
                dailyLimitViewModel.getLastDailyLimitSetting().observe(getViewLifecycleOwner(), moneyDaySetting -> {
                    if (moneyDaySetting != null && inputMoney < moneyDaySetting) {
                        Toast.makeText(getContext(), "Thiết lập tháng phải lớn hơn thiết lập ngày", Toast.LENGTH_SHORT).show();
                    } else {
                        // Chèn hoặc cập nhật nếu điều kiện hợp lệ
                        monthlyLimitViewModel.insertOrUpdateMonthlyLimit(inputMoney);
                        binding.edtMonthlySpending.setText("");
                        Toast.makeText(getContext(), "Thiết lập thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        binding.deletedata.setOnClickListener(v -> showDeleteConfirmationDialog());
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
