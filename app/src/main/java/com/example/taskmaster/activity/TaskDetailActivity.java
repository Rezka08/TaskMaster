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
import com.example.taskmaster.utils.ThemeManager;
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
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
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

        btnEditTask.setOnClickListener(v -> {
            if (currentTask != null) {
                // KIRIM FLAG BARU UNTUK EDIT
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("navigate_to", "edit_task"); // Flag baru
                intent.putExtra("task_id", currentTask.getId());
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

    // ... (sisa method seperti populateViews, formatTime, loadCategoryName tetap sama)

    private void populateViews(Task task) {
        tvTaskName.setText(task.getTitle());
        tvDescription.setText(task.getDescription());
        tvTaskDate.setText(task.getDate());
        tvStartTime.setText(task.getStartTime());
        tvEndTime.setText(task.getEndTime());
    }

    private void loadCategoryName(int categoryId) {
        taskViewModel.getCategoryById(categoryId, new DatabaseCallback<Category>() {
            @Override
            public void onSuccess(Category category) {
                if (category != null) {
                    tvCategory.setText(category.getName());
                } else {
                    tvCategory.setText("Uncategorized");
                }
            }
            @Override
            public void onError(String error) {
                tvCategory.setText("Uncategorized");
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}