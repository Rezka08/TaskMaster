package com.example.taskmaster.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.utils.PriorityUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTaskFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView dateTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private Spinner categorySpinner;
    private TextView priorityTextView;
    private Button saveButton;

    private String selectedDate = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";

    private Task editingTask = null; // For edit mode
    private List<Category> categories = new ArrayList<>();

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
        titleEditText = view.findViewById(R.id.et_task_title);
        descriptionEditText = view.findViewById(R.id.et_task_description);
        dateTextView = view.findViewById(R.id.tv_task_date);
        startTimeTextView = view.findViewById(R.id.tv_start_time);
        endTimeTextView = view.findViewById(R.id.tv_end_time);
        categorySpinner = view.findViewById(R.id.spinner_category);
        priorityTextView = view.findViewById(R.id.tv_priority_auto);
        saveButton = view.findViewById(R.id.btn_save_task);

        // Set default values
        selectedDate = DateUtils.getCurrentDate();
        selectedStartTime = DateUtils.getCurrentTime();
        selectedEndTime = DateUtils.getCurrentTime();

        dateTextView.setText(selectedDate);
        startTimeTextView.setText(selectedStartTime);
        endTimeTextView.setText(selectedEndTime);

        updatePriorityDisplay();
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Load categories for spinner
        taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categoryList -> {
            this.categories = categoryList;
            setupCategorySpinner();
        });
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        dateTextView.setOnClickListener(v -> showDatePicker());
        startTimeTextView.setOnClickListener(v -> showStartTimePicker());
        endTimeTextView.setOnClickListener(v -> showEndTimePicker());
        saveButton.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    dateTextView.setText(selectedDate);
                    updatePriorityDisplay();
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
                    startTimeTextView.setText(selectedStartTime);
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
                    endTimeTextView.setText(selectedEndTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void updatePriorityDisplay() {
        int priority = PriorityUtils.calculatePriority(selectedDate);
        String priorityText = PriorityUtils.getPriorityText(priority);
        int priorityColor = PriorityUtils.getPriorityColor(priority);

        priorityTextView.setText("Prioritas: " + priorityText + " (otomatis)");
        priorityTextView.setTextColor(priorityColor);
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Nama tugas tidak boleh kosong");
            return;
        }

        if (selectedDate.isEmpty() || selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            // Show error message
            return;
        }

        int selectedCategoryPosition = categorySpinner.getSelectedItemPosition();
        if (selectedCategoryPosition < 0 || selectedCategoryPosition >= categories.size()) {
            // Show error message
            return;
        }

        int categoryId = categories.get(selectedCategoryPosition).getId();

        if (editingTask == null) {
            // Create new task
            Task newTask = new Task(title, description, selectedDate, selectedStartTime, selectedEndTime, categoryId);
            taskViewModel.insert(newTask);
        } else {
            // Update existing task
            editingTask.setTitle(title);
            editingTask.setDescription(description);
            editingTask.setDate(selectedDate);
            editingTask.setStartTime(selectedStartTime);
            editingTask.setEndTime(selectedEndTime);
            editingTask.setCategoryId(categoryId);
            taskViewModel.update(editingTask);
        }

        // Navigate back or clear form
        clearForm();
    }

    private void clearForm() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        selectedDate = DateUtils.getCurrentDate();
        selectedStartTime = DateUtils.getCurrentTime();
        selectedEndTime = DateUtils.getCurrentTime();
        dateTextView.setText(selectedDate);
        startTimeTextView.setText(selectedStartTime);
        endTimeTextView.setText(selectedEndTime);
        categorySpinner.setSelection(0);
        updatePriorityDisplay();
        editingTask = null;
    }

    public void setEditingTask(Task task) {
        this.editingTask = task;
        if (task != null) {
            titleEditText.setText(task.getTitle());
            descriptionEditText.setText(task.getDescription());
            selectedDate = task.getDate();
            selectedStartTime = task.getStartTime();
            selectedEndTime = task.getEndTime();
            dateTextView.setText(selectedDate);
            startTimeTextView.setText(selectedStartTime);
            endTimeTextView.setText(selectedEndTime);

            // Set category spinner selection
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == task.getCategoryId()) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }

            updatePriorityDisplay();
            saveButton.setText("Update Tugas");
        }
    }
}