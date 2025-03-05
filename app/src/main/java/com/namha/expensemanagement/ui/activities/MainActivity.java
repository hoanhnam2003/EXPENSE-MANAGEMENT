package com.namha.expensemanagement.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

        // Check if binding is not null before setting the content view
        if (binding != null) {
            setContentView(binding.getRoot());
        }

        //setupUI(findViewById(R.id.main));


        fabChatbot = findViewById(R.id.fabChatbot);

        if (fabChatbot != null) {
            fabChatbot.setOnClickListener(v -> {
                if (isNetworkConnected()) {
                    ChatbotFragment chatbotFragment = new ChatbotFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flHomeContainer, chatbotFragment)
                            .addToBackStack("ChatbotFragment")
                            .commit();
                } else {
                    // Thông báo khi không có mạng
                    showToast("Vui lòng kiểm tra kết nối mạng để sử dụng chatbot.");
                }
            });
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flHomeContainer);
            if (currentFragment instanceof ChatbotFragment) {
                fabChatbot.hide();
            } else {
                fabChatbot.show();
            }
        });

        // Initialize ViewModels with null checks
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

        // Display default HomeFragment
        HomeFragment homeFragment = new HomeFragment();
        if (homeFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, homeFragment).commit();
            setUpFragmentListener(homeFragment);
        }

        // Menu click listeners with null checks
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


//    public void setupUI(View view) {
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener((v, event) -> {
//                hideKeyboard();
//                return false;
//            });
//        }
//
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }
//    }
//
//    private void hideKeyboard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        View view = getCurrentFocus();
//        if (view == null) {
//            view = new View(this);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

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

    public void onAddNewClicked() {
        getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, new AddFragment()).commit();
    }

    private void setUpFragmentListener(Fragment fragment) {
        if (fragment != null) {
            int defaultColor = getResources().getColor(R.color.black);

            if (fragment instanceof HomeFragment) {
                ((HomeFragment) fragment).setAddClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddNewClicked();
                        if (binding != null && binding.icMenuBottom != null) {
                            binding.icMenuBottom.tvHome.setTextColor(defaultColor);
                            binding.icMenuBottom.tvHome.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                });
            } else if (fragment instanceof HistoryFragment) {
                ((HistoryFragment) fragment).setAddClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddNewClicked();
                        if (binding != null && binding.icMenuBottom != null) {
                            binding.icMenuBottom.tvHistory.setTextColor(defaultColor);
                            binding.icMenuBottom.tvHistory.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                });
            } else if (fragment instanceof SettingFragment) {
                ((SettingFragment) fragment).setAddClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddNewClicked();
                        if (binding != null && binding.icMenuBottom != null) {
                            binding.icMenuBottom.tvSetting.setTextColor(defaultColor);
                            binding.icMenuBottom.tvSetting.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                });
            }
        }

    }

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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void showToast(String message) {
        runOnUiThread(() -> android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show());
    }
}
