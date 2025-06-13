package com.example.taskmaster.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.taskmaster.activity.NotificationActivity;
import com.example.taskmaster.activity.SearchActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.ProgressTaskAdapter;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import java.util.List;

public class HomeFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private RecyclerView rvProgressTasks;
    private ProgressTaskAdapter progressTaskAdapter;
    private ImageView ivSearch, ivNotification;

    // Monthly Preview TextViews
    private TextView tvDoneCount, tvUpcomingCount, tvProgressCount;

    // Handler for UI updates
    private Handler mainHandler;

    // Loading state management
    private volatile boolean isDataLoading = false;
    private volatile boolean isFragmentReady = false;

    // Data storage for reliable display
    private int completedCount = 0;
    private int upcomingCount = 0;
    private int progressCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isFragmentReady = true;

        // Load data immediately when view is created
        loadAllData();
    }

    private void initViews(View view) {
        rvProgressTasks = view.findViewById(R.id.rv_progress_tasks);
        ivSearch = view.findViewById(R.id.iv_search);
        ivNotification = view.findViewById(R.id.iv_notification);

        // Initialize Monthly Preview TextViews
        tvDoneCount = view.findViewById(R.id.tv_done_count);
        tvUpcomingCount = view.findViewById(R.id.tv_upcoming_count);
        tvProgressCount = view.findViewById(R.id.tv_progress_count);

        // Initialize handler
        mainHandler = new Handler(Looper.getMainLooper());

        // Set default values immediately
        updateMonthlyPreviewUI();
    }

    private void setupRecyclerView() {
        progressTaskAdapter = new ProgressTaskAdapter();
        progressTaskAdapter.setOnItemClickListener(task -> {
            // Navigate to Task Detail
            Intent intent = new Intent(getContext(), com.example.taskmaster.activity.TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivity(intent);
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

    /**
     * Load all data with proper synchronization
     */
    private void loadAllData() {
        if (isDataLoading || !isFragmentReady || !isAdded() || getContext() == null) {
            return;
        }

        isDataLoading = true;
        String currentDate = DateUtils.getCurrentDate();

        // Load all data in sequence to avoid race conditions
        loadCompletedTasksCount();
        loadUpcomingTasksCount(currentDate);
        loadInProgressTasksCount(currentDate);
        loadInProgressTasksList(currentDate);
    }

    private void loadCompletedTasksCount() {
        if (!isAdded()) return;

        taskViewModel.getCompletedTasksCount(new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (isAdded() && isFragmentReady) {
                    completedCount = count != null ? count : 0;
                    updateMonthlyPreviewUI();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    completedCount = 0;
                    updateMonthlyPreviewUI();
                }
            }
        });
    }

    private void loadUpcomingTasksCount(String currentDate) {
        if (!isAdded()) return;

        taskViewModel.getUpcomingTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (isAdded() && isFragmentReady) {
                    upcomingCount = count != null ? count : 0;
                    updateMonthlyPreviewUI();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    upcomingCount = 0;
                    updateMonthlyPreviewUI();
                }
            }
        });
    }

    private void loadInProgressTasksCount(String currentDate) {
        if (!isAdded()) return;

        taskViewModel.getInProgressTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (isAdded() && isFragmentReady) {
                    progressCount = count != null ? count : 0;
                    updateMonthlyPreviewUI();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    progressCount = 0;
                    updateMonthlyPreviewUI();
                }
            }
        });
    }

    private void loadInProgressTasksList(String currentDate) {
        if (!isAdded()) return;

        taskViewModel.getInProgressTasks(currentDate, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (isAdded() && isFragmentReady) {
                    updateTasksList(tasks);
                    isDataLoading = false;
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    updateTasksList(null);
                    isDataLoading = false;
                }
            }
        });
    }

    /**
     * Update monthly preview UI on main thread
     */
    private void updateMonthlyPreviewUI() {
        if (!isAdded() || !isFragmentReady) return;

        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (isAdded() && isFragmentReady) {
                    if (tvDoneCount != null) {
                        tvDoneCount.setText(String.valueOf(completedCount));
                    }
                    if (tvUpcomingCount != null) {
                        tvUpcomingCount.setText(String.valueOf(upcomingCount));
                    }
                    if (tvProgressCount != null) {
                        tvProgressCount.setText(String.valueOf(progressCount));
                    }
                }
            });
        }
    }

    /**
     * Update tasks list on main thread
     */
    private void updateTasksList(List<Task> tasks) {
        if (!isAdded() || !isFragmentReady) return;

        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (isAdded() && isFragmentReady && progressTaskAdapter != null) {
                    progressTaskAdapter.setTasks(tasks);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Wait a bit before loading to ensure fragment is fully visible
        if (mainHandler != null) {
            mainHandler.postDelayed(() -> {
                if (isAdded() && isFragmentReady) {
                    loadAllData();
                }
            }, 300);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isDataLoading = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentReady = false;
        isDataLoading = false;

        // Clean up handlers
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        // Clear adapters
        if (progressTaskAdapter != null) {
            progressTaskAdapter.setTasks(null);
        }
    }

    /**
     * Public method to refresh data from other components
     */
    public void refreshData() {
        if (isFragmentReady && !isDataLoading && mainHandler != null) {
            mainHandler.postDelayed(() -> {
                if (isAdded() && isFragmentReady) {
                    loadAllData();
                }
            }, 200);
        }
    }
}