
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
        String channelId = "2";
        String channelName = "Track Expenses";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("This is your channel description");
            notificationManager.createNotificationChannel(channel);
        }

        if (!dayWarningShown) {
            dailyLimitViewModel.getLastDailyLimitSetting().observe(getViewLifecycleOwner(), lastDailyLimit -> {
                if (lastDailyLimit != null) {
                    double moneyDaySetting = lastDailyLimit;
                    double sumAmountForToday = totalExpenseToday;
                    if (sumAmountForToday > moneyDaySetting) {
                        double warningMoney = sumAmountForToday - moneyDaySetting;
                        String formattedWarningMoney = decimalFormat.format(warningMoney);

                        // Tạo một Intent để mở MainActivity
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Đối với Android 12 (API 31) hoặc cao hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Sử dụng FLAG_IMMUTABLE
                            );
                        } else {
                            // Đối với các phiên bản Android thấp hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT // Không sử dụng FLAG_IMMUTABLE cho Android dưới API 31
                            );
                        }

                        // Chọn ngẫu nhiên một gợi ý từ mảng negativeFinances
                        Random random = new Random();
                        String randomSuggestion = negativeFinances[random.nextInt(negativeFinances.length)];

                        // Tạo thông báo
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Ngân sách ngày")
                                .setContentText("Bạn đã vượt quá ngân sách ngày: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                // Thêm BigTextStyle để hiển thị đầy đủ nội dung
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Bạn đã vượt quá ngân sách ngày: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion));

                        notificationManager.notify(1, builder.build());
                        dayWarningShown = true;
                        Toast toast = Toast.makeText(getContext(), "Bạn đã vượt quá ngân sách ngày: " + formattedWarningMoney + " VND", Toast.LENGTH_SHORT);
                        View toastView = toast.getView();
                        if (toastView != null) {
                            TextView toastMessage = toastView.findViewById(android.R.id.message);
                            if (toastMessage != null) {
                                toastMessage.setTextColor(Color.RED);
                            }
                        }
                        toast.show();
                    }else{
                        double warningMoney = moneyDaySetting - sumAmountForToday;
                        String formattedWarningMoney = decimalFormat.format(warningMoney);

                        // Tạo một Intent để mở MainActivity
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Đối với Android 12 (API 31) hoặc cao hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Sử dụng FLAG_IMMUTABLE
                            );
                        } else {
                            // Đối với các phiên bản Android thấp hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT // Không sử dụng FLAG_IMMUTABLE cho Android dưới API 31
                            );
                        }

                        // Chọn ngẫu nhiên một gợi ý từ mảng negativeFinances
                        Random random = new Random();
                        String randomSuggestion = positiveFinances[random.nextInt(negativeFinances.length)];

                        // Tạo thông báo
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Ngân sách ngày")
                                .setContentText("Ngân sách hiện tại của bạn còn: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                // Thêm BigTextStyle để hiển thị đầy đủ nội dung
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Ngân sách hiện tại của bạn còn: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion));

                        notificationManager.notify(1, builder.build());
                        dayWarningShown = true;
                    }
                } else {
                    Log.e("HomeFragment", "Last daily limit setting is null");
                }
            });
        }

        if (!monthWarningShown) {
            monthlyLimitViewModel.getLastMonthLimitSetting().observe(getViewLifecycleOwner(), lastMonthLimitSetting -> {
                if (lastMonthLimitSetting != null) {
                    double moneyMonthSetting = lastMonthLimitSetting;
                    double sumAmountForCurrentMonth = totalExpense;

                    if (sumAmountForCurrentMonth > moneyMonthSetting) {
                        double warningMoney = sumAmountForCurrentMonth - moneyMonthSetting;
                        String formattedWarningMoney = decimalFormat.format(warningMoney);

                        // Tạo một Intent để mở MainActivity
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Đối với Android 12 (API 31) hoặc cao hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Sử dụng FLAG_IMMUTABLE
                            );
                        } else {
                            // Đối với các phiên bản Android thấp hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT // Không sử dụng FLAG_IMMUTABLE cho Android dưới API 31
                            );
                        }


                        // Chọn ngẫu nhiên một gợi ý từ mảng negativeFinances
                        Random random = new Random();
                        String randomSuggestion = negativeFinances[random.nextInt(negativeFinances.length)];

                        // Tạo thông báo
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Ngân sách tháng")
                                .setContentText("Bạn đã vượt quá ngân sách tháng: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                // Thêm BigTextStyle để hiển thị đầy đủ nội dung
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Bạn đã vượt quá ngân sách tháng: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion));


                        notificationManager.notify(2, builder.build());
                        monthWarningShown = true;
                        Toast toast = Toast.makeText(getContext(), "Bạn đã vượt quá ngân sách tháng: " + formattedWarningMoney + " VND", Toast.LENGTH_SHORT);
                        View toastView = toast.getView();
                        if (toastView != null) {
                            TextView toastMessage = toastView.findViewById(android.R.id.message);
                            if (toastMessage != null) {
                                toastMessage.setTextColor(Color.RED);
                            }
                        }
                        toast.show();
                    }else{
                        double warningMoney = moneyMonthSetting - sumAmountForCurrentMonth;
                        String formattedWarningMoney = decimalFormat.format(warningMoney);

                        // Tạo một Intent để mở MainActivity
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Đối với Android 12 (API 31) hoặc cao hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Sử dụng FLAG_IMMUTABLE
                            );
                        } else {
                            // Đối với các phiên bản Android thấp hơn
                            pendingIntent = PendingIntent.getActivity(
                                    getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT // Không sử dụng FLAG_IMMUTABLE cho Android dưới API 31
                            );
                        }
                        // Chọn ngẫu nhiên một gợi ý từ mảng negativeFinances
                        Random random = new Random();
                        String randomSuggestion = negativeFinances[random.nextInt(negativeFinances.length)];

                        // Tạo thông báo
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Ngân sách tháng")
                                .setContentText("Ngân sách hiện tại của bạn còn: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                // Thêm BigTextStyle để hiển thị đầy đủ nội dung
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Ngân sách hiện tại của bạn còn: " + formattedWarningMoney + " VND.\nHãy: " + randomSuggestion));

                        notificationManager.notify(2, builder.build());
                        monthWarningShown = true;
                    }
                } else {
                    Log.e("HomeFragment", "Last monthly limit setting is null");
                }
            });
        }
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
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                double totalIncome = sumAmountForCurrentMonth(transactions);
                totalExpense = sumAmountForCurrentMonthChiTieu(transactions);
                totalExpenseToday = sumAmountForToday(transactions);

                Log.d("totalExpenseToday", "totalExpenseToday: " + totalExpenseToday);

                if (binding.tv0d != null) {
                    binding.tv0d.setText(String.format("%,.0f VND", totalIncome));
                }
                if (binding.tv0d1 != null) {
                    binding.tv0d1.setText(String.format("%,.0f VND", totalExpense));
                }
                if (binding.tien != null) {
                    binding.tien.setText(String.format("%,.0f VND", totalExpenseToday));
                }
            } else {
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

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showFabChatbot();
    }
}
