package com.namha.expensemanagement.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.namha.expensemanagement.R;
import com.namha.expensemanagement.databinding.ActivityMainBinding;
import com.namha.expensemanagement.ui.fragments.AddFragment;
import com.namha.expensemanagement.ui.fragments.ChatbotFragment;
import com.namha.expensemanagement.ui.fragments.HistoryFragment;
import com.namha.expensemanagement.ui.fragments.HomeFragment;
import com.namha.expensemanagement.ui.fragments.ReportFragment;
import com.namha.expensemanagement.ui.fragments.SettingFragment;
import com.namha.expensemanagement.viewmodels.CategoryViewModel;
import com.namha.expensemanagement.viewmodels.DailyLimitViewModel;
import com.namha.expensemanagement.viewmodels.MonthlyLimitViewModel;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;
import com.namha.expensemanagement.viewmodels.TypeViewModel;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TransactionViewModel mTransactionViewModel;
    private TypeViewModel mTypeViewModel;
    private CategoryViewModel mCategoryViewModel;
    private DailyLimitViewModel mDailyLimitViewModel;
    private MonthlyLimitViewModel mMonthlyLimit;
    
    FloatingActionButton fabChatbot;
    // check thông báo
    private static final int REQUEST_CODE_NOTIFICATION = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Kiểm tra xem binding có phải là null không trước khi thiết lập chế độ xem nội dung
        if (binding != null) {
            setContentView(binding.getRoot());
        }
        
        fabChatbot = findViewById(R.id.fabChatbot);

        fabChatbot.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                ChatbotFragment chatbotFragment = new ChatbotFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flHomeContainer, chatbotFragment)
                        .addToBackStack("ChatbotFragment")
                        .commit();
            } else {
                showToast("Vui lòng kiểm tra kết nối Wi-Fi hoặc dữ liệu di động để sử dụng chatbot.");
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flHomeContainer);
            if (currentFragment instanceof ChatbotFragment) {
                fabChatbot.hide();
            } else {
                fabChatbot.show();
            }
        });

        // Khởi tạo ViewModels với kiểm tra null
        mTransactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        if (mTransactionViewModel != null) {
            mTransactionViewModel.getLastTransaction();
        }

        mTypeViewModel = new ViewModelProvider(this).get(TypeViewModel.class);
        if (mTypeViewModel != null) {
            mTypeViewModel.logAllTypes();
        }

        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        if (mCategoryViewModel != null) {
            mCategoryViewModel.logAllCategories();
        }

        mDailyLimitViewModel = new ViewModelProvider(this).get(DailyLimitViewModel.class);
        if (mDailyLimitViewModel != null) {
            mDailyLimitViewModel.logAllDailyLimits();
        }

        mMonthlyLimit = new ViewModelProvider(this).get(MonthlyLimitViewModel.class);
        if (mMonthlyLimit != null) {
            mMonthlyLimit.logAllMonthlyLimits();
        }

        // Hiển thị HomeFragment mặc định
        HomeFragment homeFragment = new HomeFragment();
        if (homeFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, homeFragment).commit();
        }

        // Trình nghe nhấp vào menu với kiểm tra null
        if (binding != null) {
            if (binding.icMenuBottom != null) {
                binding.icMenuBottom.tvHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, new HomeFragment()).commit();
                            updateSelectedItem(binding.icMenuBottom.tvHome);
                    }
                });

                binding.icMenuBottom.tvHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, new HistoryFragment()).commit();
                            updateSelectedItem(binding.icMenuBottom.tvHistory);
                    }
                });

                binding.icMenuBottom.tvUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, new AddFragment()).commit();
                            updateSelectedItem(binding.icMenuBottom.tvUpdate);
                    }
                });

                binding.icMenuBottom.tvReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, new ReportFragment()).commit();
                            updateSelectedItem(binding.icMenuBottom.tvReport);
                    }
                });

                binding.icMenuBottom.tvSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, new SettingFragment()).commit();
                            updateSelectedItem(binding.icMenuBottom.tvSetting);
                    }
                });
            }

        }
        if (binding != null) {
            setContentView(binding.getRoot());
        }
    }

    // thông báo
    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestNotificationPermission();
    }

    // Kiểm tra và yêu cầu quyền nếu cần
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION);
            } else {
                // Quyền đã được cấp, có thể gửi thông báo ngay
                sendNotification();
            }
        } else {
            // Nếu là Android 12 trở xuống, không cần xin quyền -> gửi thông báo ngay
            sendNotification();
        }
    }

    // Xử lý kết quả khi người dùng cấp hoặc từ chối quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền thông báo đã được cấp!", Toast.LENGTH_SHORT).show();
                sendNotification();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để nhận thông báo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hàm gửi thông báo (giữ nguyên logic của bạn)
    private void sendNotification() {
        // Thực hiện logic gửi thông báo của bạn ở đây
        Toast.makeText(this, "Thông báo sẽ được gửi!", Toast.LENGTH_SHORT).show();
    }

    // Hàm cập nhật màu sắc của TextView được chọn
    private void updateSelectedItem(View selectedView) {
        if (binding != null && binding.icMenuBottom != null) {
            int defaultColor = getResources().getColor(R.color.black);
            int selectedColor = getResources().getColor(R.color.color_fd3665);
            int selectedBackgroundColor = getResources().getColor(R.color.white);

            binding.icMenuBottom.tvHome.setTextColor(defaultColor);
            binding.icMenuBottom.tvHome.setTypeface(null, Typeface.NORMAL);
            binding.icMenuBottom.tvHistory.setTextColor(defaultColor);
            binding.icMenuBottom.tvHistory.setTypeface(null, Typeface.NORMAL);
            binding.icMenuBottom.tvUpdate.setBackgroundColor(defaultColor);
            binding.icMenuBottom.tvReport.setTextColor(defaultColor);
            binding.icMenuBottom.tvReport.setTypeface(null, Typeface.NORMAL);
            binding.icMenuBottom.tvSetting.setTextColor(defaultColor);
            binding.icMenuBottom.tvSetting.setTypeface(null, Typeface.NORMAL);

            binding.icMenuBottom.tvHome.setBackgroundColor(Color.TRANSPARENT);
            binding.icMenuBottom.tvHistory.setBackgroundColor(Color.TRANSPARENT);
            binding.icMenuBottom.tvUpdate.setBackgroundColor(Color.TRANSPARENT);
            binding.icMenuBottom.tvReport.setBackgroundColor(Color.TRANSPARENT);
            binding.icMenuBottom.tvSetting.setBackgroundColor(Color.TRANSPARENT);

            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(selectedColor);
                selectedTextView.setTypeface(null, Typeface.BOLD);
            } else if (selectedView instanceof ImageView) {
                selectedView.setBackgroundColor(selectedBackgroundColor);
            }
        }
    }
    // tự tắt ba phím khi click ngoài
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    // Kiểm tra có mạng hay không
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = cm.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
                    return capabilities != null &&
                            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            } else {
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

    // Hàm hiển thị thông báo
    private void showToast(String message) {
        runOnUiThread(() -> android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show());
    }
}
