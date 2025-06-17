package com.example.taskmaster.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.taskmaster.R;
import com.example.taskmaster.activity.NotificationActivity;
import com.example.taskmaster.activity.SearchActivity;
import com.example.taskmaster.adapter.ProgressTaskAdapter;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;

import java.util.List;

public class HomeFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvProgressTasks;
    private ProgressTaskAdapter progressTaskAdapter;

    // Header Views
    private ImageView ivSearch, ivNotification;

    // Monthly Preview Views
    private TextView tvDoneCount, tvUpcomingCount, tvProgressCount;

    private String currentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        currentDate = DateUtils.getCurrentDate();

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        setupSwipeRefresh();

        loadData();

        return view;
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvProgressTasks = view.findViewById(R.id.rv_progress_tasks);
        ivSearch = view.findViewById(R.id.iv_search);
        ivNotification = view.findViewById(R.id.iv_notification);
        tvDoneCount = view.findViewById(R.id.tv_done_count);
        tvUpcomingCount = view.findViewById(R.id.tv_upcoming_count);
        tvProgressCount = view.findViewById(R.id.tv_progress_count);
    }

    private void setupRecyclerView() {
        progressTaskAdapter = new ProgressTaskAdapter();
        progressTaskAdapter.setOnItemClickListener(task -> {
            // Navigate to Task Detail
            Intent intent = new Intent(getContext(), com.example.taskmaster.activity.TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivityForResult(intent, 1001);
        });

        rvProgressTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProgressTasks.setAdapter(progressTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupClickListeners() {
        ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.purple_primary,
                R.color.purple_primary_dark,
                R.color.purple_primary_light
        );

        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        loadMonthlyPreview();
        loadProgressTasks();
    }

    private void loadMonthlyPreview() {
        // Load Done Count (Completed + Overdue tasks)
        taskViewModel.getCompletedAndOverdueTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        tvDoneCount.setText(String.valueOf(count));
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        tvDoneCount.setText("0");
                    });
                }
            }
        });

        // Load Upcoming Count
        taskViewModel.getUpcomingTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        tvUpcomingCount.setText(String.valueOf(count));
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        tvUpcomingCount.setText("0");
                    });
                }
            }
        });

        // Load Progress Count (Today's tasks)
        taskViewModel.getInProgressTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        tvProgressCount.setText(String.valueOf(count));
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        tvProgressCount.setText("0");
                    });
                }
            }
        });
    }

    private void loadProgressTasks() {
        taskViewModel.getInProgressTasks(currentDate, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        progressTaskAdapter.setTasks(tasks);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        progressTaskAdapter.setTasks(null);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh data when returning from TaskDetailActivity if data changed
        if (requestCode == 1001 && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getBooleanExtra("data_changed", false)) {
                loadData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment resumes
        loadData();
    }

    /**
     * Public method to refresh data - can be called from MainActivity
     */
    public void refreshData() {
        loadData();
    }

    /**
     * Interface for MainActivity to handle data changes
     */
    public interface OnDataChangedListener {
        void onDataChanged();
    }
}