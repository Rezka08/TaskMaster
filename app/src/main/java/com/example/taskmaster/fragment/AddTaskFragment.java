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
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        categories = categoryList;
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

    private void setupCategoryChips() {
        chipGroupCategory.removeAllViews();

        for (Category category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setId(category.getId());

            // Apply custom style for consistent theming
            chip.setChipBackgroundColorResource(R.color.chip_background_color_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_color_selector));
            chip.setChipStrokeColorResource(R.color.chip_stroke_color_selector);
            chip.setChipStrokeWidth(2f);
            chip.setChipCornerRadius(16f);
            chip.setTextSize(14f);
            chip.setMinHeight((int) (40 * getResources().getDisplayMetrics().density));

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
        // Clear all fields first
        etTaskName.setText("");
        etDescription.setText("");

        // Set current date and time as defaults
        selectedDate = DateUtils.getCurrentDate();
        selectedStartTime = DateUtils.getCurrentTime();

        // Set end time 1 hour later
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        selectedEndTime = String.format("%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));

        updateTimeDisplays();
    }

    private void updateTimeDisplays() {
        tvTaskDate.setText(selectedDate);
        tvStartTime.setText(selectedStartTime);
        tvEndTime.setText(selectedEndTime);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                R.style.CustomDatePickerTheme, // Use custom theme for consistent styling
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    tvTaskDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Don't allow past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                R.style.CustomTimePickerTheme, // Use custom theme for consistent styling
                (view, hourOfDay, minute) -> {
                    selectedStartTime = String.format("%02d:%02d", hourOfDay, minute);
                    tvStartTime.setText(selectedStartTime);

                    // Auto-set end time to 1 hour later
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endCalendar.set(Calendar.MINUTE, minute);
                    endCalendar.add(Calendar.HOUR_OF_DAY, 1);

                    selectedEndTime = String.format("%02d:%02d",
                            endCalendar.get(Calendar.HOUR_OF_DAY),
                            endCalendar.get(Calendar.MINUTE));
                    tvEndTime.setText(selectedEndTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                R.style.CustomTimePickerTheme, // Use custom theme for consistent styling
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
            etTaskName.setError("Task name cannot be empty");
            etTaskName.requestFocus();
            return;
        }

        if (selectedDate.isEmpty() || selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if end time is after start time
        if (!isEndTimeAfterStartTime()) {
            Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent multiple clicks
        btnCreateTask.setEnabled(false);
        btnCreateTask.setText("Creating...");

        if (editingTask == null) {
            // Create new task
            Task newTask = new Task(title, description, selectedDate, selectedStartTime, selectedEndTime, selectedCategoryId);
            taskViewModel.insert(newTask, new DatabaseCallback<Long>() {
                @Override
                public void onSuccess(Long result) {
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Task created successfully", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error creating task: " + error, Toast.LENGTH_SHORT).show();
                            resetCreateButton();
                        });
                    }
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
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error updating task: " + error, Toast.LENGTH_SHORT).show();
                            resetCreateButton();
                        });
                    }
                }
            });
        }
    }

    private void resetCreateButton() {
        btnCreateTask.setEnabled(true);
        btnCreateTask.setText(editingTask == null ? "Create Task" : "Update Task");
    }

    private void navigateToHome() {
        // Simple and safe navigation approach
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            // Use a delayed approach to avoid fragment conflicts
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    try {
                        mainActivity.onDataChanged();

                        // Simple bottom navigation switch
                        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                                getActivity().findViewById(R.id.bottom_navigation);
                        if (bottomNav != null) {
                            bottomNav.setSelectedItemId(R.id.nav_home);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 300); // Slightly longer delay
        }
    }

    private boolean isEndTimeAfterStartTime() {
        try {
            String[] startParts = selectedStartTime.split(":");
            String[] endParts = selectedEndTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            return (endHour > startHour) || (endHour == startHour && endMinute > startMinute);
        } catch (Exception e) {
            return false;
        }
    }

    private void clearForm() {
        etTaskName.setText("");
        etDescription.setText("");
        setDefaultValues();
        editingTask = null;
        btnCreateTask.setText("Create Task");
        selectedCategoryId = categories.size() > 0 ? categories.get(0).getId() : 1;

        // Reset chip selection
        if (chipGroupCategory.getChildCount() > 0) {
            chipGroupCategory.clearCheck();
            ((Chip) chipGroupCategory.getChildAt(0)).setChecked(true);
        }

        // Re-enable button
        btnCreateTask.setEnabled(true);
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

            updateTimeDisplays();
            btnCreateTask.setText("Update Task");

            // Update chip selection
            for (int i = 0; i < chipGroupCategory.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupCategory.getChildAt(i);
                chip.setChecked(chip.getId() == selectedCategoryId);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (editingTask == null) {
            // Only clear form if not editing
            clearForm();
        }
    }

    // Interface for MainActivity communication
    public interface MainActivity {
        void navigateToHome();
        void onDataChanged();
    }
}