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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private RecyclerView rvProgressTasks;
    private ProgressTaskAdapter progressTaskAdapter;
    private ImageView ivSearch, ivNotification;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Monthly Preview TextViews
    private TextView tvDoneCount, tvUpcomingCount, tvProgressCount;

    // Handler for UI updates
    private Handler mainHandler;

    // Loading state management with atomic operations
    private volatile boolean isFragmentReady = false;
    private AtomicInteger loadingCounter = new AtomicInteger(0);
    private final int TOTAL_LOADING_OPERATIONS = 4; // We have 4 async operations

    // Data cache to prevent data loss
    private volatile int cachedCompletedCount = 0;
    private volatile int cachedUpcomingCount = 0;
    private volatile int cachedProgressCount = 0;
    private volatile List<Task> cachedProgressTasks = null;

    // Loading flags for each operation
    private volatile boolean completedCountLoaded = false;
    private volatile boolean upcomingCountLoaded = false;
    private volatile boolean progressCountLoaded = false;
    private volatile boolean progressTasksLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupSwipeRefresh();
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
        startLoadingAllData();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvProgressTasks = view.findViewById(R.id.rv_progress_tasks);
        ivSearch = view.findViewById(R.id.iv_search);
        ivNotification = view.findViewById(R.id.iv_notification);

        // Initialize Monthly Preview TextViews
        tvDoneCount = view.findViewById(R.id.tv_done_count);
        tvUpcomingCount = view.findViewById(R.id.tv_upcoming_count);
        tvProgressCount = view.findViewById(R.id.tv_progress_count);

        // Initialize handler
        mainHandler = new Handler(Looper.getMainLooper());

        // Set cached values immediately to prevent 0 display
        updateUIWithCachedData();
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(
                    R.color.purple_primary,
                    R.color.upcoming_blue,
                    R.color.progress_orange
            );

            swipeRefreshLayout.setOnRefreshListener(() -> {
                refreshAllData();
            });
        }
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

        // Set cached tasks if available
        if (cachedProgressTasks != null) {
            progressTaskAdapter.setTasks(cachedProgressTasks);
        }
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
     * Start loading all data with proper synchronization
     */
    private void startLoadingAllData() {
        if (!isFragmentReady || !isAdded() || getContext() == null) {
            return;
        }

        // Reset loading states
        loadingCounter.set(0);
        resetLoadingFlags();

        String currentDate = DateUtils.getCurrentDate();

        // Start all loading operations
        loadCompletedAndOverdueTasksCount();
        loadUpcomingTasksCount(currentDate);
        loadInProgressTasksCount(currentDate);
        loadInProgressTasksList(currentDate);
    }

    /**
     * Refresh all data (called by swipe refresh)
     */
    private void refreshAllData() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        startLoadingAllData();
    }

    private void resetLoadingFlags() {
        completedCountLoaded = false;
        upcomingCountLoaded = false;
        progressCountLoaded = false;
        progressTasksLoaded = false;
    }

    private void loadCompletedAndOverdueTasksCount() {
        if (!isAdded()) return;

        taskViewModel.getCompletedAndOverdueTasksCount(new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                if (isAdded() && isFragmentReady) {
                    cachedCompletedCount = count != null ? count : 0;
                    completedCountLoaded = true;

                    updateUIOnMainThread();
                    checkAllDataLoaded();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    // Keep cached value, don't reset to 0
                    completedCountLoaded = true;

                    updateUIOnMainThread();
                    checkAllDataLoaded();
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
                    cachedUpcomingCount = count != null ? count : 0;
                    upcomingCountLoaded = true;

                    updateUIOnMainThread();
                    checkAllDataLoaded();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    // Keep cached value, don't reset to 0
                    upcomingCountLoaded = true;

                    updateUIOnMainThread();
                    checkAllDataLoaded();
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
                    cachedProgressCount = count != null ? count : 0;
                    progressCountLoaded = true;

                    updateUIOnMainThread();
                    checkAllDataLoaded();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    // Keep cached value, don't reset to 0
                    progressCountLoaded = true;

                    updateUIOnMainThread();
                    checkAllDataLoaded();
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
                    cachedProgressTasks = tasks;
                    progressTasksLoaded = true;

                    updateTasksListOnMainThread();
                    checkAllDataLoaded();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && isFragmentReady) {
                    // Keep cached tasks, don't clear them
                    progressTasksLoaded = true;

                    updateTasksListOnMainThread();
                    checkAllDataLoaded();
                }
            }
        });
    }

    /**
     * Check if all data loading operations are complete
     */
    private void checkAllDataLoaded() {
        if (completedCountLoaded && upcomingCountLoaded &&
                progressCountLoaded && progressTasksLoaded) {

            // All data loaded, hide refresh indicator
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                mainHandler.post(() -> {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }

    /**
     * Update UI with cached data immediately
     */
    private void updateUIWithCachedData() {
        if (tvDoneCount != null) {
            tvDoneCount.setText(String.valueOf(cachedCompletedCount));
        }
        if (tvUpcomingCount != null) {
            tvUpcomingCount.setText(String.valueOf(cachedUpcomingCount));
        }
        if (tvProgressCount != null) {
            tvProgressCount.setText(String.valueOf(cachedProgressCount));
        }
    }

    /**
     * Update monthly preview UI on main thread with cached data
     */
    private void updateUIOnMainThread() {
        if (!isAdded() || !isFragmentReady) return;

        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (isAdded() && isFragmentReady) {
                    updateUIWithCachedData();
                }
            });
        }
    }

    /**
     * Update tasks list on main thread with cached data
     */
    private void updateTasksListOnMainThread() {
        if (!isAdded() || !isFragmentReady) return;

        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (isAdded() && isFragmentReady && progressTaskAdapter != null) {
                    progressTaskAdapter.setTasks(cachedProgressTasks);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Small delay to ensure fragment is fully visible, then refresh
        if (mainHandler != null) {
            mainHandler.postDelayed(() -> {
                if (isAdded() && isFragmentReady) {
                    startLoadingAllData();
                }
            }, 200);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Don't reset data when pausing, keep cache
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentReady = false;

        // Clean up handlers
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        // Don't clear cached data, keep it for next time
    }

    public void refreshData() {
        if (isFragmentReady && mainHandler != null) {
            mainHandler.postDelayed(() -> {
                if (isAdded() && isFragmentReady) {
                    startLoadingAllData();
                }
            }, 100);
        }
    }
}