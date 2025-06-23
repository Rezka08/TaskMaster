package com.example.taskmaster.fragment;

import android.app.AlertDialog;
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
import com.example.taskmaster.utils.PriorityUtils; // PERBAIKAN: Import PriorityUtils
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTaskFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private Task currentTask;

    // Views
    private EditText etTaskName, etDescription;
    private TextView tvTaskDate, tvStartTime, tvEndTime, tvFragmentTitle;
    private ChipGroup chipGroupCategory;
    private MaterialButton btnUpdateTask, btnDeleteTask;

    // Data
    private String selectedDate = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private int selectedCategoryId = -1;
    private List<Category> categories = new ArrayList<>();
    private int taskId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        loadCategories();

        if (getArguments() != null) {
            taskId = getArguments().getInt("task_id", -1);
            if (taskId != -1) {
                loadTaskDetails(taskId);
            }
        }
        setupClickListeners();
    }

    private void initViews(View view) {
        etTaskName = view.findViewById(R.id.et_task_name);
        etDescription = view.findViewById(R.id.et_description);
        tvTaskDate = view.findViewById(R.id.tv_task_date);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        chipGroupCategory = view.findViewById(R.id.chip_group_category);
        btnUpdateTask = view.findViewById(R.id.btn_update_task);
        btnDeleteTask = view.findViewById(R.id.btn_delete_task);
        tvFragmentTitle = view.findViewById(R.id.tv_fragment_title);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
    }

    private void loadCategories() {
        taskViewModel.getAllCategories(new DatabaseListCallback<Category>() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        categories = categoryList;
                        setupCategoryChips();
                        // If task is already loaded, check the correct chip
                        if (currentTask != null) {
                            chipGroupCategory.check(currentTask.getCategoryId());
                        }
                    });
                }
            }
            @Override
            public void onError(String error) { /* ... */ }
        });
    }

    private void loadTaskDetails(int id) {
        taskViewModel.getTaskById(id, new DatabaseCallback<Task>() {
            @Override
            public void onSuccess(Task task) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        currentTask = task;
                        if (currentTask != null) {
                            populateForm(currentTask);
                        }
                    });
                }
            }
            @Override
            public void onError(String error) { /* ... */ }
        });
    }

    private void populateForm(Task task) {
        etTaskName.setText(task.getTitle());
        etDescription.setText(task.getDescription());
        tvTaskDate.setText(task.getDate());
        tvStartTime.setText(task.getStartTime());
        tvEndTime.setText(task.getEndTime());

        selectedDate = task.getDate();
        selectedStartTime = task.getStartTime();
        selectedEndTime = task.getEndTime();
        selectedCategoryId = task.getCategoryId();

        if (chipGroupCategory.getChildCount() > 0) {
            chipGroupCategory.check(selectedCategoryId);
        }
    }

    private void setupCategoryChips() {
        if (getContext() == null) return;
        chipGroupCategory.removeAllViews();
        for (Category category : categories) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_category_chip, chipGroupCategory, false);
            chip.setText(category.getName());
            chip.setId(category.getId());
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
        btnUpdateTask.setOnClickListener(v -> saveTask());
        btnDeleteTask.setOnClickListener(v -> deleteTask());
    }

    private void showDatePicker() {
        // Gunakan Calendar untuk mendapatkan tanggal saat ini sebagai default
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Buat DatePickerDialog baru
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format tanggal yang dipilih menjadi YYYY-MM-DD
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    // Set teks pada TextView
                    tvTaskDate.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }
    private void showStartTimePicker() {
        // Gunakan Calendar untuk mendapatkan waktu saat ini sebagai default
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Buat TimePickerDialog baru
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    // Format waktu yang dipilih menjadi HH:MM
                    selectedStartTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    // Set teks pada TextView
                    tvStartTime.setText(selectedStartTime);
                },
                hour, minute, true); // true untuk format 24 jam
        timePickerDialog.show();
    }
    private void showEndTimePicker() {
        // Gunakan Calendar untuk mendapatkan waktu saat ini sebagai default
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Buat TimePickerDialog baru
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    // Format waktu yang dipilih menjadi HH:MM
                    selectedEndTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    // Set teks pada TextView
                    tvEndTime.setText(selectedEndTime);
                },
                hour, minute, true); // true untuk format 24 jam
        timePickerDialog.show();
    }

    private void saveTask() {
        String title = etTaskName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        if (title.isEmpty() || currentTask == null) return;

        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setDate(selectedDate);
        currentTask.setStartTime(selectedStartTime);
        currentTask.setEndTime(selectedEndTime);
        currentTask.setCategoryId(selectedCategoryId);
        currentTask.setUpdatedAt(System.currentTimeMillis());

        // PERBAIKAN: Hitung ulang prioritas berdasarkan tanggal yang mungkin baru.
        currentTask.setPriority(PriorityUtils.calculatePriority(selectedDate));

        taskViewModel.update(currentTask, new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Task updated", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).navigateToHome();
                    });
                }
            }
            @Override
            public void onError(String error) { /* ... */ }
        });
    }

    private void deleteTask() {
        if (currentTask == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskViewModel.delete(currentTask, new DatabaseCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer result) {
                            if (isAdded() && getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).navigateToHome();
                                });
                            }
                        }
                        @Override
                        public void onError(String error) { /* ... */ }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}