package com.example.taskmaster.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment untuk mengedit dan menghapus task yang sudah ada.
 */
public class EditTaskFragment extends Fragment {

    // Konstanta
    private static final String ARG_TASK_ID = "task_id";

    // Komponen UI
    private EditText etTaskName, etDescription;
    private TextView tvTaskDate, tvStartTime, tvEndTime, tvFragmentTitle;
    private ChipGroup chipGroupCategory;
    private MaterialButton btnUpdateTask, btnDeleteTask;

    // ViewModel & Data
    private TaskViewModel taskViewModel;
    private List<Category> categories = new ArrayList<>();
    private Task currentTask;
    private int taskIdToEdit = -1;

    // Variabel untuk menyimpan data sementara dari UI
    private String selectedDate = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private int selectedCategoryId = -1;

    //================================================================================
    // Lifecycle Methods
    //================================================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskIdToEdit = getArguments().getInt(ARG_TASK_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan layout terpisah untuk Edit Fragment
        return inflater.inflate(R.layout.fragment_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupClickListeners();

        if (taskIdToEdit == -1) {
            handleInvalidId();
            return;
        }

        loadAndSetupCategories();
    }

    //================================================================================
    // Initialization
    //================================================================================

    private void initViews(View view) {
        etTaskName = view.findViewById(R.id.et_task_name);
        etDescription = view.findViewById(R.id.et_description);
        tvTaskDate = view.findViewById(R.id.tv_task_date);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        chipGroupCategory = view.findViewById(R.id.chip_group_category);
        btnUpdateTask = view.findViewById(R.id.btn_update_task);
        btnDeleteTask = view.findViewById(R.id.btn_delete_task);
        tvFragmentTitle = view.findViewById(R.id.tv_fragment_title); // Asumsi ID ini ada di XML

        if (tvFragmentTitle != null) {
            tvFragmentTitle.setText("Edit Task");
        }
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupClickListeners() {
        tvTaskDate.setOnClickListener(v -> showDatePicker());
        tvStartTime.setOnClickListener(v -> showTimePicker(true));
        tvEndTime.setOnClickListener(v -> showTimePicker(false));
        btnUpdateTask.setOnClickListener(v -> updateTask());
        btnDeleteTask.setOnClickListener(v -> showDeleteConfirmation());
    }

    //================================================================================
    // Data Loading & UI Population
    //================================================================================

    private void loadAndSetupCategories() {
        taskViewModel.getAllCategories(new DatabaseListCallback<Category>() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        categories = categoryList;
                        setupCategoryChips();
                        loadTaskDetails(); // Lanjutkan memuat detail task
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error loading categories: " + error, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void loadTaskDetails() {
        taskViewModel.getTaskById(taskIdToEdit, new DatabaseCallback<Task>() {
            @Override
            public void onSuccess(Task task) {
                if (isAdded() && getActivity() != null) {
                    if (task != null) {
                        currentTask = task;
                        getActivity().runOnUiThread(() -> populateUiWithTaskData(task));
                    } else {
                        handleInvalidId();
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to load task: " + error, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
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

    private void populateUiWithTaskData(Task task) {
        etTaskName.setText(task.getTitle());
        etDescription.setText(task.getDescription());
        selectedDate = task.getDate();
        selectedStartTime = task.getStartTime();
        selectedEndTime = task.getEndTime();
        selectedCategoryId = task.getCategoryId();

        tvTaskDate.setText(selectedDate);
        tvStartTime.setText(selectedStartTime);
        tvEndTime.setText(selectedEndTime);

        chipGroupCategory.clearCheck();
        View chipToSelect = chipGroupCategory.findViewById(selectedCategoryId);
        if (chipToSelect instanceof Chip) {
            ((Chip) chipToSelect).setChecked(true);
        }
    }

    //================================================================================
    // User Actions
    //================================================================================

    private void updateTask() {
        // ... (kode validasi sama)
        String title = etTaskName.getText().toString().trim();
        if (title.isEmpty()) {
            etTaskName.setError("Task name is required");
            return;
        }

        // ... (update objek currentTask sama)
        currentTask.setTitle(title);
        currentTask.setDescription(etDescription.getText().toString().trim());
        currentTask.setDate(selectedDate);
        currentTask.setStartTime(selectedStartTime);
        currentTask.setEndTime(selectedEndTime);
        currentTask.setCategoryId(selectedCategoryId);

        btnUpdateTask.setEnabled(false);
        btnUpdateTask.setText("Updating...");

        taskViewModel.update(currentTask, new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> { // PERBAIKAN DI SINI
                        Toast.makeText(getContext(), "Update failed: " + error, Toast.LENGTH_SHORT).show();
                        btnUpdateTask.setEnabled(true);
                        btnUpdateTask.setText("Update Task");
                    });
                }
            }
        });
    }

    private void showDeleteConfirmation() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask() {
        if (currentTask == null) return;

        taskViewModel.delete(currentTask, new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && getActivity() != null) { // PERBAIKAN DI SINI
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to delete task: " + error, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    // ... (showDatePicker dan showTimePicker tetap sama)
    private void showDatePicker() {
        if (getContext() == null) return;
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, y, m, d) -> {
            selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
            tvTaskDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(boolean isStart) {
        if (getContext() == null) return;
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hour, minute) -> {
            String time = String.format("%02d:%02d", hour, minute);
            if(isStart) {
                selectedStartTime = time;
                tvStartTime.setText(time);
            } else {
                selectedEndTime = time;
                tvEndTime.setText(time);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }


    //================================================================================
    // Helpers & Navigation
    //================================================================================

    private void handleInvalidId() {
        if (isAdded()) {
            Toast.makeText(getContext(), "Invalid Task ID.", Toast.LENGTH_SHORT).show();
            navigateToHome();
        }
    }

    private void navigateToHome() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToHome();
        }
    }
}