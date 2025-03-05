package com.namha.expensemanagement.ui.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import com.namha.expensemanagement.viewmodels.SharedViewModel;
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

    private SharedViewModel sharedViewModel;

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


        binding.etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textInput = s.toString();
                if (textInput.isEmpty()) binding.imClear.setVisibility(View.GONE);
                else binding.imClear.setVisibility(View.VISIBLE);
                performSearch(textInput);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.imClear.setOnClickListener(v -> {
            binding.etDate.setText("");
            binding.rvHistory.setVisibility(View.VISIBLE);
        });

        // Initialize RecyclerView with HistoryAdapter
        setupRecyclerView();

        // Observe LiveData from ViewModel
        if (getViewLifecycleOwner() != null) {
            transactionViewModel.getHistoryList().observe(getViewLifecycleOwner(), this::updateHistoryList);
        }

        // thay đổi màu nền
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getSelectedColor().observe(getViewLifecycleOwner(), newColor -> {
            if (newColor != null) {
                binding.frHistory.setBackgroundColor(newColor);
            }
        });
    }

    private void setupRecyclerView() {
        HistoryAdapter adapter = new HistoryAdapter(null,
                position -> {
                    showPopupMethod(position);
                }
        );
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvHistory.setAdapter(adapter);
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

            binding.popupSearchLayout.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams historyParams = (ViewGroup.MarginLayoutParams) binding.history.getLayoutParams();
            historyParams.topMargin = originalHistoryTopMargin;
            binding.history.setLayoutParams(historyParams);

            ViewGroup.MarginLayoutParams rvParams = (ViewGroup.MarginLayoutParams) binding.rvHistory.getLayoutParams();
            rvParams.topMargin = originalRvTopMargin;
            binding.rvHistory.setLayoutParams(rvParams);
        }
    }

    private void performSearch(String keySearch) {
        if (binding == null) return; // Đảm bảo binding không null

        Log.d("HistoryFragment", "User input: " + keySearch);

        if (keySearch.isEmpty()) {
            // Nếu ô tìm kiếm trống, báo lỗi và ẩn thanh tìm kiếm
            if (isSearchMode) {
                togglePopupSearchLayout();
                isSearchMode = false;
            }
            return;
        }

        String datePattern = "";
        String typeName = "";

        if (isValidDate(keySearch)) {
            datePattern = keySearch; // Nếu nhập đúng định dạng ngày, gán vào datePattern
        } else {
            typeName = keySearch; // Nếu không phải ngày, coi như loại giao dịch
        }

        Log.d("HistoryFragment", "Searching for date: " + datePattern + ", type: " + typeName);

        if (getViewLifecycleOwner() != null) {
            transactionViewModel.searchByTypeAndDate(typeName, datePattern).observe(getViewLifecycleOwner(), historyList -> {
                if (historyList != null && !historyList.isEmpty()) {
                    binding.rvHistory.setVisibility(View.VISIBLE);
                    updateHistoryList(historyList);
                } else {
                    binding.rvHistory.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Không tìm thấy giao dịch nào.", Toast.LENGTH_SHORT).show();
                }

                // Ẩn popup tìm kiếm sau khi tìm
                if (isSearchMode) {
                    togglePopupSearchLayout();
                    isSearchMode = false;
                }
            });
        } else {
            Log.e("HistoryFragment", "getViewLifecycleOwner is null, cannot observe search results");
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
