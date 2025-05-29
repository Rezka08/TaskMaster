package com.example.taskmaster.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.taskmaster.R;
import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.utils.PriorityUtils;
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
    private Task editingTask = null;
    private List<Category> categories = new ArrayList<>();
    private int selectedCategoryId = 1; // Default to first category

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        initViews(view);
        setupViewModel();
        setupClickListeners();
        setDefaultValues();

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
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Load categories
        taskViewModel.getAllCategories(new DatabaseListCallback<Category>() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                categories = categoryList;
                setupCategoryChips();
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void setupCategoryChips() {
        chipGroupCategory.removeAllViews();

        for (Category category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setId(category.getId());

            if (category.getId() == selectedCategoryId) {
                chip.setChecked(true);
            }

            chipGroupCategory.addView(chip);
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
        selectedDate = DateUtils.getCurrentDate();
        selectedStartTime = DateUtils.getCurrentTime();
        selectedEndTime = DateUtils.getCurrentTime();

        tvTaskDate.setText(selectedDate);
        tvStartTime.setText(selectedStartTime);
        tvEndTime.setText(selectedEndTime);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    tvTaskDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    selectedStartTime = String.format("%02d:%02d", hourOfDay, minute);
                    tvStartTime.setText(selectedStartTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    selectedEndTime = String.format("%02d:%02d", hourOfDay, minute);
                    tvEndTime.setText(selectedEndTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void saveTask() {
        String title = etTaskName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTaskName.setError("Nama tugas tidak boleh kosong");
            return;
        }

        if (selectedDate.isEmpty() || selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            return;
        }

        if (editingTask == null) {
            // Create new task
            Task newTask = new Task(title, description, selectedDate, selectedStartTime, selectedEndTime, selectedCategoryId);
            taskViewModel.insert(newTask, new DatabaseCallback<Long>() {
                @Override
                public void onSuccess(Long result) {
                    clearForm();
                    // Navigate back or show success message
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        } else {
            // Update existing task
            editingTask.setTitle(title);
            editingTask.setDescription(description);
            editingTask.setDate(selectedDate);
            editingTask.setStartTime(selectedStartTime);
            editingTask.setEndTime(selectedEndTime);
            editingTask.setCategoryId(selectedCategoryId);

            taskViewModel.update(editingTask, new DatabaseCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    clearForm();
                    // Navigate back or show success message
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        }
    }

    private void clearForm() {
        etTaskName.setText("");
        etDescription.setText("");
        setDefaultValues();
        editingTask = null;
        btnCreateTask.setText("Create Task");
    }

    public void setEditingTask(Task task) {
        this.editingTask = task;
        if (task != null) {
            etTaskName.setText(task.getTitle());
            etDescription.setText(task.getDescription());
            selectedDate = task.getDate();
            selectedStartTime = task.getStartTime();
            selectedEndTime = task.getEndTime();
            selectedCategoryId = task.getCategoryId();

            tvTaskDate.setText(selectedDate);
            tvStartTime.setText(selectedStartTime);
            tvEndTime.setText(selectedEndTime);

            btnCreateTask.setText("Update Task");
        }
    }
}