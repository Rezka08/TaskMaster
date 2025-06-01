package com.example.taskmaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.adapter.NotificationTaskAdapter;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private RecyclerView rvNotificationTasks;
    private NotificationTaskAdapter notificationTaskAdapter;
    private TabLayout tabLayoutFilter;
    private TextView tvTaskCount;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupTabLayout();
        setupClickListeners();
    }

    private void initViews() {
        rvNotificationTasks = findViewById(R.id.rv_notification_tasks);
        tabLayoutFilter = findViewById(R.id.tab_layout_filter);
        tvTaskCount = findViewById(R.id.tv_task_count);
        ivBack = findViewById(R.id.iv_back);
    }

    private void setupRecyclerView() {
        notificationTaskAdapter = new NotificationTaskAdapter();
        notificationTaskAdapter.setOnItemClickListener(task -> {
            // Navigate to Task Detail
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivityForResult(intent, 1001); // Use startActivityForResult to detect changes
        });
        rvNotificationTasks.setLayoutManager(new LinearLayoutManager(this));
        rvNotificationTasks.setAdapter(notificationTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        loadUpcomingTasks(); // Default tab
    }

    private void setupTabLayout() {
        tabLayoutFilter.addTab(tabLayoutFilter.newTab().setText("Upcoming"));
        tabLayoutFilter.addTab(tabLayoutFilter.newTab().setText("In-progress"));
        tabLayoutFilter.addTab(tabLayoutFilter.newTab().setText("Completed"));

        tabLayoutFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadUpcomingTasks();
                        break;
                    case 1:
                        loadInProgressTasks();
                        break;
                    case 2:
                        loadCompletedTasks();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadUpcomingTasks() {
        taskViewModel.getUpcomingTasks(DateUtils.getCurrentDate(), new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    notificationTaskAdapter.setTasks(tasks);
                    tvTaskCount.setText(tasks.size() + " Task" + (tasks.size() == 1 ? "" : "s") + " Upcoming");
                    toggleEmptyState(tasks.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    notificationTaskAdapter.setTasks(null);
                    tvTaskCount.setText("0 Tasks Upcoming");
                    toggleEmptyState(true);
                });
            }
        });
    }

    private void loadInProgressTasks() {
        taskViewModel.getInProgressTasks(DateUtils.getCurrentDate(), new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    notificationTaskAdapter.setTasks(tasks);
                    tvTaskCount.setText(tasks.size() + " Task" + (tasks.size() == 1 ? "" : "s") + " In Progress");
                    toggleEmptyState(tasks.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    notificationTaskAdapter.setTasks(null);
                    tvTaskCount.setText("0 Tasks In Progress");
                    toggleEmptyState(true);
                });
            }
        });
    }

    private void loadCompletedTasks() {
        taskViewModel.getCompletedTasks(new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    notificationTaskAdapter.setTasks(tasks);
                    tvTaskCount.setText(tasks.size() + " Task" + (tasks.size() == 1 ? "" : "s") + " Completed");
                    toggleEmptyState(tasks.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    notificationTaskAdapter.setTasks(null);
                    tvTaskCount.setText("0 Tasks Completed");
                    toggleEmptyState(true);
                });
            }
        });
    }

    private void toggleEmptyState(boolean isEmpty) {
        findViewById(R.id.layout_empty_state).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvNotificationTasks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh data when returning from TaskDetailActivity if data changed
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("data_changed", false)) {
                // Refresh current tab
                TabLayout.Tab selectedTab = tabLayoutFilter.getTabAt(tabLayoutFilter.getSelectedTabPosition());
                if (selectedTab != null) {
                    switch (selectedTab.getPosition()) {
                        case 0:
                            loadUpcomingTasks();
                            break;
                        case 1:
                            loadInProgressTasks();
                            break;
                        case 2:
                            loadCompletedTasks();
                            break;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh current tab
        TabLayout.Tab selectedTab = tabLayoutFilter.getTabAt(tabLayoutFilter.getSelectedTabPosition());
        if (selectedTab != null) {
            switch (selectedTab.getPosition()) {
                case 0:
                    loadUpcomingTasks();
                    break;
                case 1:
                    loadInProgressTasks();
                    break;
                case 2:
                    loadCompletedTasks();
                    break;
            }
        }
    }
}