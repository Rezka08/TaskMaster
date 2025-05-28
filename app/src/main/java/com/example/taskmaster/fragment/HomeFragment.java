package com.example.taskmaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.MonthlyPreviewAdapter;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements TaskAdapter.OnTaskClickListener {
    private TaskViewModel taskViewModel;
    private RecyclerView monthlyPreviewRecyclerView;
    private RecyclerView tasksRecyclerView;
    private MonthlyPreviewAdapter monthlyPreviewAdapter;
    private TaskAdapter taskAdapter;
    private TabLayout filterTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerViews();
        setupViewModel();
        setupTabLayout();

        return view;
    }

    private void initViews(View view) {
        monthlyPreviewRecyclerView = view.findViewById(R.id.rv_monthly_preview);
        tasksRecyclerView = view.findViewById(R.id.rv_tasks);
        filterTabLayout = view.findViewById(R.id.tab_layout_filter);
    }

    private void setupRecyclerViews() {
        // Monthly Preview RecyclerView
        monthlyPreviewAdapter = new MonthlyPreviewAdapter();
        monthlyPreviewRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        monthlyPreviewRecyclerView.setAdapter(monthlyPreviewAdapter);

        // Tasks RecyclerView
        taskAdapter = new TaskAdapter();
        taskAdapter.setOnTaskClickListener(this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Load monthly preview data
        loadMonthlyPreview();

        // Load upcoming tasks by default
        loadUpcomingTasks();
    }

    private void setupTabLayout() {
        filterTabLayout.addTab(filterTabLayout.newTab().setText("Upcoming"));
        filterTabLayout.addTab(filterTabLayout.newTab().setText("In Progress"));
        filterTabLayout.addTab(filterTabLayout.newTab().setText("Completed"));

        filterTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void loadMonthlyPreview() {
        List<MonthlyPreviewAdapter.MonthlyPreview> previews = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String monthYear = DateUtils.getMonthYear(i);
            taskViewModel.getTaskCountByMonth(monthYear, new DatabaseCountCallback() {
                @Override
                public void onSuccess(Integer count) {
                    previews.add(new MonthlyPreviewAdapter.MonthlyPreview(monthYear, count));
                    if (previews.size() == 4) {
                        monthlyPreviewAdapter.setMonthlyPreviews(previews);
                    }
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        }
    }

    private void loadUpcomingTasks() {
        taskViewModel.getUpcomingTasks(DateUtils.getCurrentDate(), new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskAdapter.setTasks(tasks);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading tasks: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInProgressTasks() {
        taskViewModel.getInProgressTasks(DateUtils.getCurrentDate(), new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskAdapter.setTasks(tasks);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading tasks: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCompletedTasks() {
        taskViewModel.getCompletedTasks(new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskAdapter.setTasks(tasks);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading tasks: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskClick(Task task) {
        // Handle task click - navigate to detail or edit
    }

    @Override
    public void onTaskToggle(Task task) {
        task.setCompleted(!task.isCompleted());
        taskViewModel.update(task, new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                // Refresh current tab
                TabLayout.Tab selectedTab = filterTabLayout.getTabAt(filterTabLayout.getSelectedTabPosition());
                if (selectedTab != null) {
                    selectedTab.select();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error updating task: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskEdit(Task task) {
        // Navigate to edit task fragment
    }
}
