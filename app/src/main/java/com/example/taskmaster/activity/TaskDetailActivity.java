package com.example.taskmaster.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import com.google.android.material.button.MaterialButton;

public class TaskDetailActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextView tvTaskName, tvTaskDate, tvStartTime, tvEndTime, tvDescription, tvCategory;
    private MaterialButton btnEditTask;

    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_task_detail);

        initViews();
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

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnEditTask.setOnClickListener(v -> {
            // Navigate to edit mode
        });
    }

    private void loadTaskData() {
        // Get task data from intent
        // Populate views with task data
    }
}