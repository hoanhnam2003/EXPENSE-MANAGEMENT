package com.namha.expensemanagement.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.namha.expensemanagement.R;
import com.namha.expensemanagement.databinding.HistoryFragmentBinding;
import com.namha.expensemanagement.dto.History;
import com.namha.expensemanagement.ui.adapters.HistoryAdapter;
import com.namha.expensemanagement.viewmodels.TransactionViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private HistoryFragmentBinding binding;
    private TransactionViewModel transactionViewModel;

    private View.OnClickListener addClickListener;
    private boolean isSearchMode = false;

    private int originalHistoryTopMargin;
    private int originalRvTopMargin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = HistoryFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) return; // Ensure binding is not null

        // Initialize ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Save the original margin values
        ViewGroup.MarginLayoutParams historyParams = (ViewGroup.MarginLayoutParams) binding.history.getLayoutParams();
        originalHistoryTopMargin = historyParams.topMargin;

        ViewGroup.MarginLayoutParams rvParams = (ViewGroup.MarginLayoutParams) binding.rvHistory.getLayoutParams();
        originalRvTopMargin = rvParams.topMargin;


        // Set the click listener for imSearch
        binding.imSearch.setOnClickListener(v -> {
            if (!isSearchMode) {
                // Show the search layout and adjust other views
                togglePopupSearchLayout();
                isSearchMode = true;
            } else {
                // Perform search when clicked again
                performSearch();
            }
        });

        // Initialize RecyclerView with HistoryAdapter
        setupRecyclerView();

        // Observe LiveData from ViewModel
        if (getViewLifecycleOwner() != null) {
            transactionViewModel.getHistoryList().observe(getViewLifecycleOwner(), this::updateHistoryList);
        }
    }

    private void setupRecyclerView() {
        HistoryAdapter adapter = new HistoryAdapter(null, this::showPopupMethod);
        if (requireContext() != null) {
            binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvHistory.setAdapter(adapter);
        } else {
            Log.e("HistoryFragment", "Context is null, cannot setup RecyclerView");
        }
    }

    private void updateHistoryList(List<History> historyList) {
        if (historyList != null) {
            Log.d("HistoryFragment", "Updating history list with " + historyList.size() + " items");

            HistoryAdapter adapter = (HistoryAdapter) binding.rvHistory.getAdapter();
            if (adapter != null) {
                adapter.setItems(historyList);
            } else {
                Log.e("HistoryFragment", "Adapter is null, cannot update history list");
            }
        } else {
            Log.e("HistoryFragment", "History list is null");
        }
    }

    private void showPopupMethod(int transactionId) {
        PopupChooseMethodFragment dialogFragment = new PopupChooseMethodFragment();
        dialogFragment.setCancelable(true);
        dialogFragment.setTransactionId(transactionId);

        if (getChildFragmentManager() != null) {
            dialogFragment.show(getChildFragmentManager(), "PopupChooseMethodFragment");
        } else {
            Log.e("HistoryFragment", "Child FragmentManager is null");
        }
    }

    public void setAddClickListener(View.OnClickListener listener) {
        this.addClickListener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }

    private void togglePopupSearchLayout() {
        if (binding == null) return; // Ensure binding is not null

        if (binding.popupSearchLayout.getVisibility() == View.GONE) {
            binding.popupSearchLayout.setVisibility(View.VISIBLE);

            ViewGroup.MarginLayoutParams historyParams = (ViewGroup.MarginLayoutParams) binding.history.getLayoutParams();
            historyParams.topMargin = getResources().getDimensionPixelSize(R.dimen.popup_search_layout_height);
            binding.history.setLayoutParams(historyParams);

            ViewGroup.MarginLayoutParams rvParams = (ViewGroup.MarginLayoutParams) binding.rvHistory.getLayoutParams();
            rvParams.topMargin = getResources().getDimensionPixelSize(R.dimen.popup_search_layout_height);
            binding.rvHistory.setLayoutParams(rvParams);
        } else {
            // Clear the input in the search field
            binding.etDate.setText("");

            binding.popupSearchLayout.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams historyParams = (ViewGroup.MarginLayoutParams) binding.history.getLayoutParams();
            historyParams.topMargin = originalHistoryTopMargin;
            binding.history.setLayoutParams(historyParams);

            ViewGroup.MarginLayoutParams rvParams = (ViewGroup.MarginLayoutParams) binding.rvHistory.getLayoutParams();
            rvParams.topMargin = originalRvTopMargin;
            binding.rvHistory.setLayoutParams(rvParams);
        }
    }

    private void performSearch() {
        if (binding == null) return; // Ensure binding is not null

        String datePattern = binding.etDate.getText().toString();
        Log.d("HistoryFragment", "Searching for date: " + datePattern);

        if (datePattern.isEmpty()) {
            // If no input is provided, hide the search bar
            if (isSearchMode) {
                togglePopupSearchLayout();
                isSearchMode = false;
            }
            Toast.makeText(requireContext(), "Vui lòng nhập ngày để tìm kiếm.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isValidDate(datePattern)) {
            if (getViewLifecycleOwner() != null) {
                transactionViewModel.searchByDate(datePattern).observe(getViewLifecycleOwner(), historyList -> {
                    if (historyList != null && !historyList.isEmpty()) {
                        updateHistoryList(historyList);
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy giao dịch nào cho ngày này.", Toast.LENGTH_SHORT).show();
                    }

                    // Hide the search layout after search
                    if (isSearchMode) {
                        togglePopupSearchLayout();
                        isSearchMode = false;
                    }
                });
            } else {
                Log.e("HistoryFragment", "getViewLifecycleOwner is null, cannot observe search results");
            }
        } else {
            Toast.makeText(requireContext(), "Ngày tháng năm không hợp lệ. Vui lòng nhập theo định dạng DD/MM/YYYY.", Toast.LENGTH_SHORT).show();
            Log.d("HistoryFragment", "Invalid date format: " + datePattern);
        }
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
