
package com.namha.expensemanagement.ui.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.namha.expensemanagement.R;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.databinding.HomeFragmentBinding;
import com.namha.expensemanagement.ui.activities.MainActivity;
import com.namha.expensemanagement.viewmodels.DailyLimitViewModel;
import com.namha.expensemanagement.viewmodels.MonthlyLimitViewModel;
import com.namha.expensemanagement.viewmodels.SharedViewModel;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final String PREFS_NAME = "HomeFragmentPrefs";
    private static final String KEY_EXPENSE_PROGRESS = "ExpenseProgress";

    private HomeFragmentBinding binding;
    private TransactionViewModel transactionViewModel;
    private DailyLimitViewModel dailyLimitViewModel;
    private MonthlyLimitViewModel monthlyLimitViewModel;
    private boolean isBalanceVisible = false;
    private EditText etInputMoney;
    private Button btnSave;
    private View.OnClickListener addClickListener;
    private boolean dayWarningShown = false;
    private boolean monthWarningShown = false;

    private SharedViewModel sharedViewModel;
    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding != null ? binding.getRoot() : null; // Kiểm tra binding null
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) {
            Log.e("HomeFragment", "Binding is null in onViewCreated");
            return;
        }

        // Initialize ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        dailyLimitViewModel = new ViewModelProvider(this).get(DailyLimitViewModel.class);
        monthlyLimitViewModel = new ViewModelProvider(this).get(MonthlyLimitViewModel.class);

        // Initialize UI components
        etInputMoney = binding.etInputMoney;
        btnSave = binding.btnSave;

        // Toggle balance visibility
        ImageView imEye = binding.imEye;
        TextView tvBalance = binding.tvBalance;

        imEye.setOnClickListener(v -> {
            if (!isBalanceVisible) {
                transactionViewModel.getTotalBalance().observe(getViewLifecycleOwner(), totalBalance -> {
                    if (totalBalance != null) {
                        tvBalance.setText(String.format("%,.0f VND", totalBalance));
                    } else {
                        Log.e("HomeFragment", "Total balance is null");
                    }
                });
                imEye.setImageResource(R.drawable.icon_eye_24);
            } else {
                tvBalance.setText("*** *** *** VND");
                imEye.setImageResource(R.drawable.eye);
            }
            isBalanceVisible = !isBalanceVisible;
        });

        // Initialize SeekBar and TextView
        AppCompatSeekBar sbExpenses = binding.sbExpenses;
        TextView tvExpenseValue = binding.tvMoney;

        if (getActivity() != null) {
            // Đọc trạng thái của thanh cuộn từ SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int savedProgress = sharedPreferences.getInt(KEY_EXPENSE_PROGRESS, 0);

            // Lấy giá trị money_month_setting từ MonthlyLimit
            monthlyLimitViewModel.getLastMonthLimitSetting().observe(getViewLifecycleOwner(), moneyMonthSetting -> {
                if (moneyMonthSetting != null) {
                    int maxMonthSetting = (int) Math.round(moneyMonthSetting);  // Chuyển đổi money_month_setting từ Double sang int

                    // Cập nhật số tiền chi tiêu từ bản ghi cuối cùng (money_day_setting)
                    dailyLimitViewModel.getLastDailyLimitMoney().observe(getViewLifecycleOwner(), lastMoneyDay -> {
                        if (lastMoneyDay != null) {
                            int maxAmount = (int) lastMoneyDay.doubleValue();
                            sbExpenses.setMax(maxAmount);

                            // Đặt giá trị của SeekBar từ SharedPreferences chỉ khi chưa được thiết lập
                            if (sbExpenses.getProgress() == 0) {
                                sbExpenses.setProgress(savedProgress);
                            }

                            tvExpenseValue.setText(String.format("%,d VND", sbExpenses.getProgress()));
                        } else {
                            Log.e("HomeFragment", "Last daily limit is null");
                        }
                    });

                    sbExpenses.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            tvExpenseValue.setText(String.format("%,d VND", progress));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {}

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            int progress = seekBar.getProgress();

                            // So sánh giá trị của money_day_setting (progress) với money_month_setting
                            if (progress >= maxMonthSetting) {
                                Toast.makeText(getContext(), "Thiết lập ngày phải nhỏ hơn thiết lập tháng", Toast.LENGTH_SHORT).show();
                                seekBar.setProgress(maxMonthSetting - 1);  // Đặt lại SeekBar về giá trị hợp lệ
                            } else {
                                // Cập nhật nếu điều kiện hợp lệ
                                dailyLimitViewModel.updateMoneyDaySetting(progress);

                                // Lưu trạng thái của thanh cuộn vào SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(KEY_EXPENSE_PROGRESS, progress);
                                editor.apply();
                            }
                        }
                    });
                } else {
                    Log.e("HomeFragment", "Last monthly limit setting is null");
                }
            });
        } else {
            Log.e("HomeFragment", "Activity is null, cannot access SharedPreferences");
        }


        btnSave.setOnClickListener(v -> {
            String inputMoneyStr = etInputMoney.getText().toString();
            if (!inputMoneyStr.isEmpty()) {
                try {
                    double inputMoney = Double.parseDouble(inputMoneyStr);

                    // Lấy giá trị money_month_setting từ ViewModel để so sánh
                    monthlyLimitViewModel.getLastMonthLimitSetting().observe(getViewLifecycleOwner(), moneyMonthSetting -> {
                        if (moneyMonthSetting != null) {
                            if (inputMoney >= moneyMonthSetting) {
                                Toast.makeText(getContext(), "Thiết lập ngày phải nhỏ hơn thiết lập tháng", Toast.LENGTH_SHORT).show();
                            } else {
                                // Chèn hoặc cập nhật giá trị nếu hợp lệ
                                dailyLimitViewModel.insertOrUpdateDailyLimit(inputMoney);
                                etInputMoney.setText("");
                                Toast.makeText(getContext(), "Thiết lập thành công", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("HomeFragment", "Last monthly limit setting is null");
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            }
        });


        calculateAndDisplayMonthlyTotal();

        // Gọi hàm cảnh báo khi vượt quá ngân sách
        warningMoney();


        // thay đổi màu nền
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        frameLayout = view.findViewById(R.id.frTrackExpenses);

        sharedViewModel.getSelectedColor().observe(getViewLifecycleOwner(), newColor -> {
            if (newColor != null) {
                frameLayout.setBackgroundColor(newColor);
            }
        });
    }

    // update đẩy thông báo ra ngoài
    double totalExpenseToday = 0.0;
    double totalExpense = 0.0;

    //Chưa đạt giới hạn tiêu ngày hôm nay
    String[] positiveFinances = {
            "Tích lũy cho những ngày sau",
            "Chuyển vào quỹ tiết kiệm",
            "Đầu tư vào kế hoạch dài hạn",
            "Tự thưởng bằng một khoản nhỏ",
            "Xem xét giảm giới hạn chi tiêu ngày"
    };

    // vượt quá giới hạn tiêu ngày hôm nay
    String[] negativeFinances = {
            "Xem lại mục tiêu chi tiêu ngày",
            "Điều chỉnh chi tiêu cho những ngày tới",
            "Phân tích nguyên nhân vượt mức",
            "Hạn chế chi tiêu không cần thiết",
            "Đặt cảnh báo nhắc nhở chi tiêu"
    };

    //    cảnh báo khi số tiền chi tiêu vượt quá
    //    thông báo ra toast và đẩy thông báo ra ngoài app
    private void warningMoney() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "expense_channel";

        // Tạo kênh thông báo nếu cần thiết
        createNotificationChannel(notificationManager, channelId);

        if (!dayWarningShown) {
            dailyLimitViewModel.getLastDailyLimitSetting().observe(getViewLifecycleOwner(), lastDailyLimit -> {
                if (lastDailyLimit != null) {
                    double moneyDaySetting = lastDailyLimit;
                    double sumAmountForToday = totalExpenseToday;
                    String title, message;

                    if (sumAmountForToday > moneyDaySetting) {
                        double warningMoney = sumAmountForToday - moneyDaySetting;
                        title = "Cảnh báo ngân sách ngày";
                        message = String.format("Bạn đã vượt quá giới hạn: %s VND.\nHãy: %s",
                                decimalFormat.format(warningMoney),
                                getRandomSuggestion(negativeFinances));
                    } else {
                        double remainingMoney = moneyDaySetting - sumAmountForToday;
                        title = "Ngân sách ngày còn lại";
                        message = String.format("Bạn còn: %s VND.\nHãy: %s",
                                decimalFormat.format(remainingMoney),
                                getRandomSuggestion(positiveFinances));
                    }

                    showNotification(notificationManager, channelId, 1, title, message);
                    dayWarningShown = true;
                }
            });
        }

        if (!monthWarningShown) {
            monthlyLimitViewModel.getLastMonthLimitSetting().observe(getViewLifecycleOwner(), lastMonthLimit -> {
                if (lastMonthLimit != null) {
                    double moneyMonthSetting = lastMonthLimit;
                    double sumAmountForMonth = totalExpense;
                    String title, message;

                    if (sumAmountForMonth > moneyMonthSetting) {
                        double warningMoney = sumAmountForMonth - moneyMonthSetting;
                        title = "Cảnh báo ngân sách tháng";
                        message = String.format("Bạn đã vượt quá giới hạn: %s VND.\nHãy: %s",
                                decimalFormat.format(warningMoney),
                                getRandomSuggestion(negativeFinances));
                    } else {
                        double remainingMoney = moneyMonthSetting - sumAmountForMonth;
                        title = "Ngân sách tháng còn lại";
                        message = String.format("Bạn còn: %s VND.\nHãy: %s",
                                decimalFormat.format(remainingMoney),
                                getRandomSuggestion(positiveFinances));
                    }

                    showNotification(notificationManager, channelId, 2, title, message);
                    monthWarningShown = true;
                }
            });
        }
    }

    /**
     * Tạo NotificationChannel cho Android 8.0 trở lên.
     */
    private void createNotificationChannel(NotificationManager manager, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Expense Management",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo về ngân sách hàng ngày và hàng tháng.");
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Hiển thị thông báo với tiêu đề và nội dung tùy chỉnh.
     */
    private void showNotification(NotificationManager manager, String channelId, int notificationId, String title, String message) {
        PendingIntent pendingIntent = createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        manager.notify(notificationId, builder.build());
    }

    /**
     * Tạo PendingIntent mở MainActivity, hỗ trợ tất cả phiên bản Android.
     */
    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    /**
     * Chọn ngẫu nhiên một lời khuyên từ danh sách.
     */
    private String getRandomSuggestion(String[] suggestions) {
        Random random = new Random();
        return suggestions[random.nextInt(suggestions.length)];
    }


    // Lấy tháng hiện tại
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    // Lấy năm hiện tại
    private int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    // Lấy ngày hiện tại
    private int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // Tính toán và hiển thị tổng số tiền hàng tháng
    private void calculateAndDisplayMonthlyTotal() {
        // Quan sát tất cả giao dịch từ ViewModel
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            // Kiểm tra xem danh sách giao dịch có null không
            if (transactions != null) {
                // Tính tổng thu nhập trong tháng
                final double totalIncome = sumAmountForCurrentMonth(transactions);
                // Tính tổng chi tiêu trong tháng
                totalExpense = sumAmountForCurrentMonthChiTieu(transactions);
                // Tính tổng chi tiêu trong ngày hôm nay
                totalExpenseToday = sumAmountForToday(transactions);

                // Khởi tạo mảng để lưu tổng chi tiêu bất thường, vì phải sử dụng final
                final double[] totalUnusualExpense = {0};

                // Quan sát giá trị giới hạn chi tiêu trong ngày
                dailyLimitViewModel.getLastDailyLimitSetting().observe(getViewLifecycleOwner(), newDailyLimitSetting -> {
                    // Kiểm tra nếu giá trị giới hạn chi tiêu trong ngày không null
                    if (newDailyLimitSetting != null) {
                        final double dailyLimit = newDailyLimitSetting;

                        // Duyệt qua tất cả giao dịch và kiểm tra chi tiêu vượt quá giới hạn
                        for (Transaction transaction : transactions) {
                            // Nếu giao dịch là chi tiêu (typeId = 1)
                            if (transaction.getTypeId() == 1) {
                                // Nếu số tiền chi tiêu vượt quá giới hạn
                                if (Math.abs(transaction.getAmount()) > dailyLimit) {
                                    // Cộng chi tiêu bất thường vào tổng chi tiêu bất thường
                                    totalUnusualExpense[0] += Math.abs(transaction.getAmount());
                                }
                            }
                        }

                        // Cập nhật giao diện người dùng với các giá trị tính toán
                        if (binding.tv0d != null) {
                            binding.tv0d.setText(String.format("%,.0f VND", totalIncome));
                        }
                        if (binding.tv0d1 != null) {
                            binding.tv0d1.setText(String.format("%,.0f VND", totalExpense));
                        }
                        if (binding.tien != null) {
                            binding.tien.setText(String.format("%,.0f VND", totalExpenseToday));
                        }
                        if (binding.tv0d2 != null) {
                            binding.tv0d2.setText(String.format("%,.0f VND", totalUnusualExpense[0]));
                        }
                    } else {
                        // Ghi log nếu giá trị giới hạn chi tiêu là null
                        Log.e("HomeFragment", "Daily limit setting is null");
                    }
                });
            } else {
                // Ghi log nếu danh sách giao dịch là null
                Log.e("HomeFragment", "Transactions list is null");
            }
        });
    }

    private String cleanDateString(String dateStr) {
        return dateStr != null ? dateStr.replace("Chiều", "").replace("Sáng", "").trim() : "";
    }

    private double sumAmountForToday(List<Transaction> transactions) {
        int currentDay = getCurrentDay();
        int currentMonth = getCurrentMonth();
        int currentYear = getCurrentYear();
        double totalAmount = 0.0;

        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        for (Transaction transaction : transactions) {
            String dateStr = cleanDateString(transaction.getDate());
            Date date = null;

            try {
                date = format24.parse(dateStr);
            } catch (ParseException e) {
                Log.e("HomeFragment", "Error parsing date: " + dateStr, e);
            }

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int recordDay = calendar.get(Calendar.DAY_OF_MONTH);
                int recordMonth = calendar.get(Calendar.MONTH) + 1;
                int recordYear = calendar.get(Calendar.YEAR);

                if (recordDay == currentDay && recordMonth == currentMonth && recordYear == currentYear &&
                        (transaction.getTypeId() == 1 || transaction.getTypeId() == 2)) {
                    totalAmount += transaction.getAmount();
                }
            }
        }

        return totalAmount;
    }

    private double sumAmountForCurrentMonth(List<Transaction> transactions) {
        int currentMonth = getCurrentMonth();
        int currentYear = getCurrentYear();
        double totalAmount = 0.0;

        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        for (Transaction transaction : transactions) {
            String dateStr = cleanDateString(transaction.getDate());
            Date date = null;

            try {
                date = format24.parse(dateStr);
            } catch (ParseException e) {
                Log.e("HomeFragment", "Error parsing date: " + dateStr, e);
            }

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int recordMonth = calendar.get(Calendar.MONTH) + 1;
                int recordYear = calendar.get(Calendar.YEAR);

                if (recordMonth == currentMonth && recordYear == currentYear && transaction.getTypeId() == 3) {
                    totalAmount += transaction.getAmount();
                }
            }
        }

        return totalAmount;
    }

    private double sumAmountForCurrentMonthChiTieu(List<Transaction> transactions) {
        int currentMonth = getCurrentMonth();
        int currentYear = getCurrentYear();
        double totalAmount = 0.0;

        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        for (Transaction transaction : transactions) {
            String dateStr = cleanDateString(transaction.getDate());
            Date date = null;

            try {
                date = format24.parse(dateStr);
            } catch (ParseException e) {
                Log.e("HomeFragment", "Error parsing date: " + dateStr, e);
            }

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int recordMonth = calendar.get(Calendar.MONTH) + 1;
                int recordYear = calendar.get(Calendar.YEAR);

                if (recordMonth == currentMonth && recordYear == currentYear &&
                        (transaction.getTypeId() == 1 || transaction.getTypeId() == 2)) {
                    totalAmount += transaction.getAmount();
                }
            }
        }

        return totalAmount;
    }

    public void setAddClickListener(View.OnClickListener listener) {
        this.addClickListener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}