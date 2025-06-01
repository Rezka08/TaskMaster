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
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private RecyclerView rvProgressTasks;
    private ProgressTaskAdapter progressTaskAdapter;
    private ImageView ivSearch, ivNotification;

    // Monthly Preview TextViews
    private TextView tvDoneCount, tvUpcomingCount, tvProgressCount;

    // Synchronization variables
    private boolean isDataLoading = false;
    private Handler mainHandler;
    private final Object loadingLock = new Object();

    // Data tracking
    private AtomicInteger loadingCounter = new AtomicInteger(0);
    private volatile boolean isFragmentActive = false;

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
        isFragmentActive = true;
        // Load data with delay to ensure fragment is ready
        if (mainHandler != null) {
            mainHandler.postDelayed(this::loadAllDataSynchronized, 200);
        }
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
        resetMonthlyPreview();
    }

    private void resetMonthlyPreview() {
        if (tvDoneCount != null) tvDoneCount.setText("0");
        if (tvUpcomingCount != null) tvUpcomingCount.setText("0");
        if (tvProgressCount != null) tvProgressCount.setText("0");
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
     * Synchronized data loading to prevent race conditions
     */
    private void loadAllDataSynchronized() {
        synchronized (loadingLock) {
            if (isDataLoading || !isFragmentActive || !isAdded() || getContext() == null) {
                return;
            }

            isDataLoading = true;
            loadingCounter.set(4); // We're loading 4 pieces of data

            // Reset UI first
            if (mainHandler != null) {
                mainHandler.post(() -> {
                    resetMonthlyPreview();
                    progressTaskAdapter.setTasks(null);
                });
            }
        }

        String currentDate = DateUtils.getCurrentDate();

        // Load all data simultaneously but track completion
        loadCompletedTasksCount();
        loadUpcomingTasksCount(currentDate);
        loadInProgressTasksCount(currentDate);
        loadInProgressTasksList(currentDate);
    }

    private void loadCompletedTasksCount() {
        taskViewModel.getCompletedTasksCount(new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                updateUIOnMainThread(() -> {
                    if (tvDoneCount != null && isFragmentActive) {
                        tvDoneCount.setText(String.valueOf(count != null ? count : 0));
                    }
                    onDataLoadComplete();
                });
            }

            @Override
            public void onError(String error) {
                updateUIOnMainThread(() -> {
                    if (tvDoneCount != null && isFragmentActive) {
                        tvDoneCount.setText("0");
                    }
                    onDataLoadComplete();
                });
            }
        });
    }

    private void loadUpcomingTasksCount(String currentDate) {
        taskViewModel.getUpcomingTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                updateUIOnMainThread(() -> {
                    if (tvUpcomingCount != null && isFragmentActive) {
                        tvUpcomingCount.setText(String.valueOf(count != null ? count : 0));
                    }
                    onDataLoadComplete();
                });
            }

            @Override
            public void onError(String error) {
                updateUIOnMainThread(() -> {
                    if (tvUpcomingCount != null && isFragmentActive) {
                        tvUpcomingCount.setText("0");
                    }
                    onDataLoadComplete();
                });
            }
        });
    }

    private void loadInProgressTasksCount(String currentDate) {
        taskViewModel.getInProgressTasksCount(currentDate, new DatabaseCountCallback() {
            @Override
            public void onSuccess(Integer count) {
                updateUIOnMainThread(() -> {
                    if (tvProgressCount != null && isFragmentActive) {
                        tvProgressCount.setText(String.valueOf(count != null ? count : 0));
                    }
                    onDataLoadComplete();
                });
            }

            @Override
            public void onError(String error) {
                updateUIOnMainThread(() -> {
                    if (tvProgressCount != null && isFragmentActive) {
                        tvProgressCount.setText("0");
                    }
                    onDataLoadComplete();
                });
            }
        });
    }

    private void loadInProgressTasksList(String currentDate) {
        taskViewModel.getInProgressTasks(currentDate, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                updateUIOnMainThread(() -> {
                    if (isFragmentActive) {
                        progressTaskAdapter.setTasks(tasks);
                    }
                    onDataLoadComplete();
                });
            }

            @Override
            public void onError(String error) {
                updateUIOnMainThread(() -> {
                    if (isFragmentActive) {
                        progressTaskAdapter.setTasks(null);
                    }
                    onDataLoadComplete();
                });
            }
        });
    }

    private void updateUIOnMainThread(Runnable runnable) {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        } else if (mainHandler != null) {
            mainHandler.post(runnable);
        }
    }

    private void onDataLoadComplete() {
        int remaining = loadingCounter.decrementAndGet();
        if (remaining <= 0) {
            synchronized (loadingLock) {
                isDataLoading = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentActive = true;

        // Wait a bit before loading to ensure fragment is fully visible
        if (mainHandler != null) {
            mainHandler.postDelayed(() -> {
                synchronized (loadingLock) {
                    if (!isDataLoading && isFragmentActive) {
                        loadAllDataSynchronized();
                    }
                }
            }, 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentActive = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentActive = false;

        // Clean up handlers and reset loading state
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        synchronized (loadingLock) {
            isDataLoading = false;
            loadingCounter.set(0);
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
        if (isFragmentActive && mainHandler != null) {
            mainHandler.postDelayed(this::loadAllDataSynchronized, 100);
        }
    }
}