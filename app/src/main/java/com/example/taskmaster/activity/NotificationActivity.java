package com.example.taskmaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;

import com.example.taskmaster.R;
import com.example.taskmaster.adapter.NotificationTaskAdapter;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.utils.ThemeManager;
import com.example.taskmaster.viewmodel.TaskViewModel;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private RecyclerView rvNotificationTasks;
    private NotificationTaskAdapter notificationTaskAdapter;
    private ImageView ivBack;
    private TextView tvTaskCount;
    private View layoutEmptyState;
    private TabLayout tabLayoutFilter;
    private ThemeManager themeManager;

    private String currentFilter = "upcoming"; // upcoming, progress, completed
    private String currentDate;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notification), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentDate = DateUtils.getCurrentDate();

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        setupTabLayout();

        // Load data with delay to prevent immediate conflicts
        new android.os.Handler().postDelayed(this::loadData, 100);
    }

    private void initViews() {
        rvNotificationTasks = findViewById(R.id.rv_notification_tasks);
        ivBack = findViewById(R.id.iv_back);
        tvTaskCount = findViewById(R.id.tv_task_count);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        tabLayoutFilter = findViewById(R.id.tab_layout_filter);
    }

    private void setupRecyclerView() {
        notificationTaskAdapter = new NotificationTaskAdapter();
        notificationTaskAdapter.setOnItemClickListener(task -> {
            // Navigate to Task Detail
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivityForResult(intent, 1001);
        });

        rvNotificationTasks.setLayoutManager(new LinearLayoutManager(this));
        rvNotificationTasks.setAdapter(notificationTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupTabLayout() {
        tabLayoutFilter.addTab(tabLayoutFilter.newTab().setText("Upcoming"));
        tabLayoutFilter.addTab(tabLayoutFilter.newTab().setText("Progress"));
        tabLayoutFilter.addTab(tabLayoutFilter.newTab().setText("Completed"));

        tabLayoutFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentFilter = "upcoming";
                        break;
                    case 1:
                        currentFilter = "progress";
                        break;
                    case 2:
                        currentFilter = "completed";
                        break;
                }
                // Add delay to prevent rapid switching issues
                new android.os.Handler().postDelayed(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        loadData();
                    }
                }, 150);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private synchronized void loadData() {
        if (isLoading) return; // Prevent multiple simultaneous loads

        isLoading = true;
        switch (currentFilter) {
            case "upcoming":
                loadUpcomingTasks();
                break;
            case "progress":
                loadProgressTasks();
                break;
            case "completed":
                loadCompletedTasks();
                break;
        }
    }

    private void loadUpcomingTasks() {
        // Get upcoming tasks count
        taskViewModel.getUpcomingTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        tvTaskCount.setText(count + " Task" + (count == 1 ? "" : "s") + " Upcoming");
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        tvTaskCount.setText("0 Tasks Upcoming");
                    });
                }
            }
        });

        // Get upcoming tasks list
        taskViewModel.getUpcomingTasks(currentDate, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        if (tasks != null && !tasks.isEmpty()) {
                            notificationTaskAdapter.setTasks(tasks);
                            showEmptyState(false);
                        } else {
                            notificationTaskAdapter.setTasks(new ArrayList<>());
                            showEmptyState(true);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        notificationTaskAdapter.setTasks(new ArrayList<>());
                        showEmptyState(true);
                    });
                }
            }
        });
    }

    private void loadProgressTasks() {
        // Get in progress tasks count
        taskViewModel.getInProgressTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        tvTaskCount.setText(count + " Task" + (count == 1 ? "" : "s") + " in Progress");
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        tvTaskCount.setText("0 Tasks in Progress");
                    });
                }
            }
        });

        // Get in progress tasks list
        taskViewModel.getInProgressTasks(currentDate, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        if (tasks != null && !tasks.isEmpty()) {
                            notificationTaskAdapter.setTasks(tasks);
                            showEmptyState(false);
                        } else {
                            notificationTaskAdapter.setTasks(new ArrayList<>());
                            showEmptyState(true);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        notificationTaskAdapter.setTasks(new ArrayList<>());
                        showEmptyState(true);
                    });
                }
            }
        });
    }

    private void loadCompletedTasks() {
        // Get completed tasks count - this uses the corrected method
        taskViewModel.getCompletedAndOverdueTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        tvTaskCount.setText(count + " Task" + (count == 1 ? "" : "s") + " Completed");
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        tvTaskCount.setText("0 Tasks Completed");
                    });
                }
            }
        });

        // Get completed tasks list
        taskViewModel.getCompletedTasks(new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        if (tasks != null && !tasks.isEmpty()) {
                            notificationTaskAdapter.setTasks(tasks);
                            showEmptyState(false);
                        } else {
                            notificationTaskAdapter.setTasks(new ArrayList<>());
                            showEmptyState(true);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        notificationTaskAdapter.setTasks(new ArrayList<>());
                        showEmptyState(true);
                    });
                }
            }
        });
    }

    private void showEmptyState(boolean show) {
        if (show) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvNotificationTasks.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvNotificationTasks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh data when returning from TaskDetailActivity if data changed
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("data_changed", false)) {
                // Add delay to prevent immediate reload conflicts
                new android.os.Handler().postDelayed(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        loadData();
                    }
                }, 200);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only refresh if not currently loading and activity is visible
        if (!isLoading) {
            new android.os.Handler().postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    loadData();
                }
            }, 150);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isLoading = false; // Reset loading state when activity pauses
    }

    @Override
    public void finish() {
        super.finish();
        // Add smooth transition
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}