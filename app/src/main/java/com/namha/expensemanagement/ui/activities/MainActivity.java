package com.namha.expensemanagement.ui.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.namha.expensemanagement.R;
import com.namha.expensemanagement.databinding.ActivityMainBinding;
import com.namha.expensemanagement.ui.fragments.AddFragment;
import com.namha.expensemanagement.ui.fragments.HistoryFragment;
import com.namha.expensemanagement.ui.fragments.HomeFragment;
import com.namha.expensemanagement.ui.fragments.ReportFragment;
import com.namha.expensemanagement.ui.fragments.SettingFragment;
import com.namha.expensemanagement.viewmodels.CategoryViewModel;
import com.namha.expensemanagement.viewmodels.ColorViewModel;
import com.namha.expensemanagement.viewmodels.DailyLimitViewModel;
import com.namha.expensemanagement.viewmodels.MonthlyLimitViewModel;
import com.namha.expensemanagement.viewmodels.SettingViewModel;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;
import com.namha.expensemanagement.viewmodels.TypeViewModel;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TransactionViewModel mTransactionViewModel;
    private TypeViewModel mTypeViewModel;
    private CategoryViewModel mCategoryViewModel;
    private DailyLimitViewModel mDailyLimitViewModel;
    private MonthlyLimitViewModel mMonthlyLimit;
    private SettingViewModel mSettingViewModel;
    private ColorViewModel mColorViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Check if binding is not null before setting the content view
        if (binding != null) {
            setContentView(binding.getRoot());
        }

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

        mSettingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        if (mSettingViewModel != null) {
            mSettingViewModel.logAllSettings();
        }

        mColorViewModel = new ViewModelProvider(this).get(ColorViewModel.class);
        if (mColorViewModel != null) {
            mColorViewModel.logAllColors();
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
                        HomeFragment homeFragment = new HomeFragment();
                        if (homeFragment != null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, homeFragment).commit();
                            setUpFragmentListener(homeFragment);
                            updateSelectedItem(binding.icMenuBottom.tvHome);
                        }
                    }
                });

                binding.icMenuBottom.tvHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HistoryFragment historyFragment = new HistoryFragment();
                        if (historyFragment != null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, historyFragment).commit();
                            setUpFragmentListener(historyFragment);
                            updateSelectedItem(binding.icMenuBottom.tvHistory);
                        }
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
                        ReportFragment reportFragment = new ReportFragment();
                        if (reportFragment != null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, reportFragment).commit();
                            setUpFragmentListener(reportFragment);
                            updateSelectedItem(binding.icMenuBottom.tvReport);
                        }
                    }
                });

                binding.icMenuBottom.tvSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingFragment settingFragment = new SettingFragment();
                        if (settingFragment != null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.flHomeContainer, settingFragment).commit();
                            setUpFragmentListener(settingFragment);
                            updateSelectedItem(binding.icMenuBottom.tvSetting);
                        }
                    }
                });
            }
        }
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
}
