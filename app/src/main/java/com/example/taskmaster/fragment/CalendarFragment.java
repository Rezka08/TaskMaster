package com.example.taskmaster.fragment;

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
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.CalendarTaskAdapter;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class CalendarFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private RecyclerView rvTasks;
    private CalendarTaskAdapter taskAdapter;
    private TextView tvMonthYear;
    private ImageView ivBack, ivSearch;
    private MaterialButton btnAddTask;

    private String selectedDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        rvTasks = view.findViewById(R.id.rv_tasks);
        tvMonthYear = view.findViewById(R.id.tv_month_year);
        ivBack = view.findViewById(R.id.iv_back);
        ivSearch = view.findViewById(R.id.iv_search);
        btnAddTask = view.findViewById(R.id.btn_add_task);

        selectedDate = DateUtils.getCurrentDate();
        tvMonthYear.setText("May, 2025"); // You can make this dynamic
    }

    private void setupRecyclerView() {
        taskAdapter = new CalendarTaskAdapter();
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTasks.setAdapter(taskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        loadTasksForDate(selectedDate);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            // Navigate back
        });

        ivSearch.setOnClickListener(v -> {
            // Show search
        });

        btnAddTask.setOnClickListener(v -> {
            // Navigate to add task
        });
    }

    private void loadTasksForDate(String date) {
        taskViewModel.getTasksByDate(date, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskAdapter.setTasks(tasks);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}