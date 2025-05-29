package com.example.taskmaster.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.activity.NotificationActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.ProgressTaskAdapter;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import java.util.List;

public class HomeFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private RecyclerView rvProgressTasks;
    private ProgressTaskAdapter progressTaskAdapter;
    private ImageView ivSearch, ivNotification;

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

    private void initViews(View view) {
        rvProgressTasks = view.findViewById(R.id.rv_progress_tasks);
        ivSearch = view.findViewById(R.id.iv_search);
        ivNotification = view.findViewById(R.id.iv_notification);
    }

    private void setupRecyclerView() {
        progressTaskAdapter = new ProgressTaskAdapter();
        rvProgressTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProgressTasks.setAdapter(progressTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        loadProgressTasks();
    }

    private void setupClickListeners() {
        ivSearch.setOnClickListener(v -> {
            // Navigate to search or show search dialog
        });

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void loadProgressTasks() {
        taskViewModel.getInProgressTasks(DateUtils.getCurrentDate(), new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                progressTaskAdapter.setTasks(tasks);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}