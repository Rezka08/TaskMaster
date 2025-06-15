package com.example.taskmaster.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmaster.R;
import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {
    private ImageView ivBack, ivDelete;
    private TextView tvTaskName, tvTaskDate, tvStartTime, tvEndTime, tvDescription, tvCategory;
    private MaterialButton btnEditTask;

    private Task currentTask;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_task_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupClickListeners();
        loadTaskData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivDelete = findViewById(R.id.iv_delete);
        tvTaskName = findViewById(R.id.tv_task_name);
        tvTaskDate = findViewById(R.id.tv_task_date);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvDescription = findViewById(R.id.tv_description);
        tvCategory = findViewById(R.id.tv_category);
        btnEditTask = findViewById(R.id.btn_edit_task);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivDelete.setOnClickListener(v -> showDeleteConfirmation());

        btnEditTask.setOnClickListener(v -> {
            if (currentTask != null) {
                // Navigate to MainActivity with edit mode
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("edit_task_id", currentTask.getId());
                intent.putExtra("navigate_to", "add_task");
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadTaskData() {
        int taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        taskViewModel.getTaskById(taskId, new DatabaseCallback<Task>() {
            @Override
            public void onSuccess(Task task) {
                runOnUiThread(() -> {
                    if (task != null) {
                        currentTask = task;
                        populateViews(task);
                        loadCategoryName(task.getCategoryId());
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "Task not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(TaskDetailActivity.this, "Error loading task: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void populateViews(Task task) {
        tvTaskName.setText(task.getTitle());
        tvDescription.setText(task.getDescription());

        // Format date display
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(task.getDate());
            tvTaskDate.setText(outputFormat.format(date));
        } catch (Exception e) {
            tvTaskDate.setText(task.getDate());
        }

        // Format time display
        tvStartTime.setText(formatTime(task.getStartTime()));
        tvEndTime.setText(formatTime(task.getEndTime()));
    }

    private String formatTime(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date timeDate = inputFormat.parse(time);
            return outputFormat.format(timeDate);
        } catch (Exception e) {
            return time;
        }
    }

    private void loadCategoryName(int categoryId) {
        taskViewModel.getCategoryById(categoryId, new DatabaseCallback<Category>() {
            @Override
            public void onSuccess(Category category) {
                runOnUiThread(() -> {
                    if (category != null) {
                        tvCategory.setText(category.getName());
                    } else {
                        tvCategory.setText("Unknown Category");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvCategory.setText("Unknown Category");
                });
            }
        });
    }

    private void showDeleteConfirmation() {
        if (currentTask == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete \"" + currentTask.getTitle() + "\"? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteTask() {
        if (currentTask == null) return;

        taskViewModel.delete(currentTask, new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer rowsDeleted) {
                runOnUiThread(() -> {
                    if (rowsDeleted > 0) {
                        Toast.makeText(TaskDetailActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();

                        // Set result to notify calling activity about data change
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("data_changed", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(TaskDetailActivity.this, "Error deleting task: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload task data in case it was updated
        if (currentTask != null) {
            loadTaskData();
        }
    }
}