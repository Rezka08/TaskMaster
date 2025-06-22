package com.example.taskmaster.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.taskmaster.R;
import com.example.taskmaster.activity.MainActivity;
import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTaskFragment extends Fragment {
    private TaskViewModel taskViewModel;

    // Views
    private EditText etTaskName;
    private EditText etDescription;
    private TextView tvTaskDate;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private ChipGroup chipGroupCategory;
    private MaterialButton btnCreateTask;

    // Data
    private String selectedDate = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private List<Category> categories = new ArrayList<>();
    private int selectedCategoryId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        initViews(view);
        setupViewModel();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        etTaskName = view.findViewById(R.id.et_task_name);
        etDescription = view.findViewById(R.id.et_description);
        tvTaskDate = view.findViewById(R.id.tv_task_date);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        chipGroupCategory = view.findViewById(R.id.chip_group_category);
        btnCreateTask = view.findViewById(R.id.btn_create_task);
        btnCreateTask.setText("Create Task"); // Pastikan teks tombol benar
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Load categories
        taskViewModel.getAllCategories(new DatabaseListCallback<Category>() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        categories = categoryList;
                        if (!categories.isEmpty()) {
                            selectedCategoryId = categories.get(0).getId(); // Default ke kategori pertama
                        }
                        setupCategoryChips();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading categories: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Selalu set form ke nilai default saat fragment ini ditampilkan
        setDefaultValues();
    }

    private void setupCategoryChips() {
        if (getContext() == null) return;
        chipGroupCategory.removeAllViews();

        // Buat file layout baru `res/layout/item_category_chip.xml`
        // <com.google.android.material.chip.Chip xmlns:android="http://schemas.android.com/apk/res/android"
        //    style="@style/Widget.MaterialComponents.Chip.Choice"
        //    android:layout_width="wrap_content"
        //    android:layout_height="wrap_content" />

        for (Category category : categories) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_category_chip, chipGroupCategory, false);
            chip.setText(category.getName());
            chip.setId(category.getId());
            chipGroupCategory.addView(chip);
        }

        if (!categories.isEmpty()) {
            chipGroupCategory.check(categories.get(0).getId());
        }

        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                selectedCategoryId = checkedIds.get(0);
            }
        });
    }

    private void setupClickListeners() {
        tvTaskDate.setOnClickListener(v -> showDatePicker());
        tvStartTime.setOnClickListener(v -> showStartTimePicker());
        tvEndTime.setOnClickListener(v -> showEndTimePicker());
        btnCreateTask.setOnClickListener(v -> saveTask());
    }

    private void setDefaultValues() {
        etTaskName.setText("");
        etDescription.setText("");

        selectedDate = DateUtils.getCurrentDate();
        selectedStartTime = DateUtils.getCurrentTime();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        selectedEndTime = String.format("%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));

        updateTimeDisplays();

        if (chipGroupCategory.getChildCount() > 0) {
            chipGroupCategory.check(chipGroupCategory.getChildAt(0).getId());
        }
    }

    private void updateTimeDisplays() {
        tvTaskDate.setText(selectedDate);
        tvStartTime.setText(selectedStartTime);
        tvEndTime.setText(selectedEndTime);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvTaskDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            selectedStartTime = String.format("%02d:%02d", hourOfDay, minute);
            tvStartTime.setText(selectedStartTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            selectedEndTime = String.format("%02d:%02d", hourOfDay, minute);
            tvEndTime.setText(selectedEndTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void saveTask() {
        String title = etTaskName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTaskName.setError("Task name cannot be empty");
            return;
        }
        if (selectedCategoryId == -1) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreateTask.setEnabled(false);
        btnCreateTask.setText("Creating...");

        Task newTask = new Task(title, description, selectedDate, selectedStartTime, selectedEndTime, selectedCategoryId);
        taskViewModel.insert(newTask, new DatabaseCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Task created successfully", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).navigateToHome();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error creating task: " + error, Toast.LENGTH_SHORT).show();
                        btnCreateTask.setEnabled(true);
                        btnCreateTask.setText("Create Task");
                    });
                }
            }
        });
    }
}