package com.example.taskmaster.activity;

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
                notificationTaskAdapter.setTasks(tasks);
                tvTaskCount.setText(tasks.size() + " Task Upcoming");
                toggleEmptyState(tasks.isEmpty());
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void loadInProgressTasks() {
        taskViewModel.getInProgressTasks(DateUtils.getCurrentDate(), new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                notificationTaskAdapter.setTasks(tasks);
                tvTaskCount.setText(tasks.size() + " Task In Progress");
                toggleEmptyState(tasks.isEmpty());
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void loadCompletedTasks() {
        taskViewModel.getCompletedTasks(new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                notificationTaskAdapter.setTasks(tasks);
                tvTaskCount.setText(tasks.size() + " Task Completed");
                toggleEmptyState(tasks.isEmpty());
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void toggleEmptyState(boolean isEmpty) {
        findViewById(R.id.layout_empty_state).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvNotificationTasks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}