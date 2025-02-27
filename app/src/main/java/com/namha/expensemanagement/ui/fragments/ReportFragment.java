
package com.namha.expensemanagement.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.namha.expensemanagement.R;
import com.namha.expensemanagement.database.entities.Transaction;
import com.namha.expensemanagement.databinding.StatisticalFragmentBinding;
import com.namha.expensemanagement.viewmodels.DailyLimitViewModel;
import com.namha.expensemanagement.viewmodels.MonthlyLimitViewModel;
import com.namha.expensemanagement.viewmodels.SharedViewModel;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private StatisticalFragmentBinding binding;
    private View.OnClickListener addClickListener;
    private TransactionViewModel transactionViewModel;
    private MonthlyLimitViewModel monthlyLimitViewModel;
    private DailyLimitViewModel dailyLimitViewModel;

    private SharedViewModel sharedViewModel;
    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = StatisticalFragmentBinding.inflate(inflater, container, false);
        return binding != null ? binding.getRoot() : null; // Kiểm tra binding null
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) {
            Log.e("ReportFragment", "Binding is null in onViewCreated");
            return;
        }

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        monthlyLimitViewModel = new ViewModelProvider(this).get(MonthlyLimitViewModel.class);
        dailyLimitViewModel = new ViewModelProvider(this).get(DailyLimitViewModel.class);

        String todayDate = getTodayDate();

        // Lấy giao dịch của ngày hiện tại
        transactionViewModel.getTransactionsByDate(todayDate).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                updateTodayReport(transactions);
                dailyLimitViewModel.getLastDailyLimitSetting().observe(getViewLifecycleOwner(), moneyDay -> {
                    if (moneyDay != null) {
                        setupPieChart(transactions, moneyDay);
                    } else {
                        Log.e("ReportFragment", "Daily limit is null");
                    }
                });
            } else {
                setupPieChart(transactions, 0.0);
            }
        });

        String monthYear = convertToMonthYear(todayDate);

        // Lấy giao dịch của tháng hiện tại
        transactionViewModel.getTransactionsByDate(monthYear).observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                updateThisMonthReport(transactions);
                monthlyLimitViewModel.getLastMonthLimitSetting().observe(getViewLifecycleOwner(), moneyMonth -> {
                    if (moneyMonth != null) {
                        setupPieChartThisday(transactions, moneyMonth);
                    } else {
                        Log.e("ReportFragment", "Monthly limit is null");
                    }
                });
            } else {
                setupPieChartThisday(transactions, 0.0);
            }
        });

        // thay đổi màu nền
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        frameLayout = view.findViewById(R.id.frStatistical);

        sharedViewModel.getSelectedColor().observe(getViewLifecycleOwner(), newColor -> {
            if (newColor != null) {
                frameLayout.setBackgroundColor(newColor);
            }
        });
    }

    // Thống kê ngày
    private void setupPieChart(List<Transaction> transactions, Double moneyDay) {
        Pair<Double, Double> totalAmountForToday = calculateTotalAmountForToday(transactions);

        double amountSpentToday = totalAmountForToday.first;
        double moneyToDayLimit = moneyDay;

        float monthlySpendingRatio = (float) ((amountSpentToday / moneyToDayLimit) * 100);
        float monthlyBalanceRatio = 100 - monthlySpendingRatio;

        PieChart pieChart = binding.chart;
        if (pieChart == null) {
            Log.e("ReportFragment", "PieChart is null");
            return;
        }

        // Cấu hình giao diện của PieChart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setCenterText("Chi tiêu ngày");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.BLACK);
        ArrayList<PieEntry> entries = new ArrayList<>();
        if(monthlySpendingRatio > 100){
            // Add data to PieChart
            entries.add(new PieEntry(100, "Số tiền đã chi"));
            entries.add(new PieEntry(0, "Số tiền còn lại"));
        }else if(monthlySpendingRatio < 100 && monthlySpendingRatio > 0){
            // Add data to PieChart
            entries.add(new PieEntry(monthlySpendingRatio, "Số tiền đã chi"));
            entries.add(new PieEntry(monthlyBalanceRatio, "Số tiền còn lại"));
        }else{
            // Add data to PieChart
            entries.add(new PieEntry(0, "Số tiền đã chi"));
            entries.add(new PieEntry(100, "Số tiền còn lại"));
        }


        PieDataSet dataSet = new PieDataSet(entries, "");
        int[] colors = {Color.parseColor("#FA3D6A"), Color.parseColor("#FE862F")};
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f%%", value);
            }
        });

        pieChart.setData(data);

        // Cấu hình phần chú thích
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(13f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(30f);
        legend.setYEntrySpace(5f);
        pieChart.setEntryLabelColor(Color.TRANSPARENT);

        pieChart.invalidate();
    }

    // Thống kê tháng
    private void setupPieChartThisday(List<Transaction> transactions, Double moneyMonth) {
        Pair<Double, Double> totalAmountForToday = calculateTotalAmountForToday(transactions);

        double amountSpentToday = totalAmountForToday.first;
        double moneyToDayLimit = moneyMonth;

        float monthlySpendingRatio = (float) ((amountSpentToday / moneyToDayLimit) * 100);
        float monthlyBalanceRatio = 100 - monthlySpendingRatio;

        PieChart pieChart = binding.chartthismonth;
        if (pieChart == null) {
            Log.e("ReportFragment", "PieChart for this month is null");
            return;
        }

        // Cấu hình giao diện của PieChart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setCenterText("Chi tiêu tháng");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.BLACK);

        // Add data to PieChart
        ArrayList<PieEntry> entries = new ArrayList<>();
        if(monthlySpendingRatio > 100){
            // Add data to PieChart
            entries.add(new PieEntry(100, "Số tiền đã chi"));
            entries.add(new PieEntry(0, "Số tiền còn lại"));
        }else if(monthlySpendingRatio < 100 && monthlySpendingRatio > 0){
            // Add data to PieChart
            entries.add(new PieEntry(monthlySpendingRatio, "Số tiền đã chi"));
            entries.add(new PieEntry(monthlyBalanceRatio, "Số tiền còn lại"));
        }else{
            // Add data to PieChart
            entries.add(new PieEntry(0, "Số tiền đã chi"));
            entries.add(new PieEntry(100, "Số tiền còn lại"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        int[] colors = {Color.parseColor("#FA3D6A"), Color.parseColor("#FE862F")};
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f%%", value);
            }
        });

        pieChart.setData(data);

        // Cấu hình phần chú thích
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(13f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(30f);
        legend.setYEntrySpace(5f);
        pieChart.setEntryLabelColor(Color.TRANSPARENT);

        pieChart.invalidate();
    }

    // Hàm cập nhật báo cáo cho ngày hiện tại
    private void updateTodayReport(List<Transaction> transactions) {
        String todayDate = getTodayDate();
        Pair<Double, Double> totalAmountForToday = calculateTotalAmountForToday(transactions);

        double amountSpentToday = totalAmountForToday.first;
        double amountCollectedToday = totalAmountForToday.second;

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        String formattedAmountSpent = decimalFormat.format(amountSpentToday);
        String formattedAmountCollected = decimalFormat.format(amountCollectedToday);

        if (binding != null) {
            binding.todayreport.setText(String.format("Báo cáo hôm nay (%s):", todayDate));
            binding.amountspent.setText(String.format("%sđ", formattedAmountSpent));
            binding.amountcollected.setText(String.format("%sđ", formattedAmountCollected));
        } else {
            Log.e("ReportFragment", "Binding is null in updateTodayReport");
        }
    }

    private void updateThisMonthReport(List<Transaction> transactions) {
        String todayDate = getTodayDate();
        String monthYear = convertToMonthYear(todayDate);

        Pair<Double, Double> totalAmountForToday = calculateTotalAmountForToday(transactions);

        double amountSpentToday = totalAmountForToday.first;
        double amountCollectedToday = totalAmountForToday.second;

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        String formattedAmountSpent = decimalFormat.format(amountSpentToday);
        String formattedAmountCollected = decimalFormat.format(amountCollectedToday);

        if (binding != null) {
            binding.thismonthreport.setText(String.format("Báo cáo tháng này (%s):", monthYear));
            binding.totalexpensesthismonth1.setText(String.format("%sđ", formattedAmountSpent));
            binding.totalrevenuethismonth1.setText(String.format("%sđ", formattedAmountCollected));
        } else {
            Log.e("ReportFragment", "Binding is null in updateThisMonthReport");
        }
    }

    // Hàm tính toán tổng thu chi cho hôm nay
    private Pair<Double, Double> calculateTotalAmountForToday(List<Transaction> transactions) {
        double amountSpentToday = 0.0;
        double amountCollectedToday = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.getTypeId() == 1 || transaction.getTypeId() == 2) {
                amountSpentToday += transaction.getAmount();
            } else if (transaction.getTypeId() == 3) {
                amountCollectedToday += transaction.getAmount();
            }
        }

        return new Pair<>(amountSpentToday, amountCollectedToday);
    }

    // Hàm lấy ngày hiện tại
    private String getTodayDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    // Hàm chuyển đổi từ ngày/tháng/năm sang tháng/năm
    public static String convertToMonthYear(String date) {
        String[] dateParts = date.split("/");
        return dateParts[1] + "/" + dateParts[2];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Tránh memory leak
    }
}
