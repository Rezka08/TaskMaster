package com.example.taskmaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalenderFragment extends Fragment implements TaskAdapter.OnTaskClickListener {
    private TaskViewModel taskViewModel;
    private CalendarView calendarView;
    private SearchView searchView;
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;

    private String selectedDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupCalendarView();
        setupSearchView();

        return view;
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        searchView = view.findViewById(R.id.search_view);
        tasksRecyclerView = view.findViewById(R.id.rv_tasks);

        selectedDate = DateUtils.getCurrentDate();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        taskAdapter.setOnTaskClickListener(this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Load tasks for current date by default
        loadTasksForDate(selectedDate);
    }

    private void setupCalendarView() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadTasksForDate(selectedDate);
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTasks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadTasksForDate(selectedDate);
                } else {
                    searchTasks(newText);
                }
                return true;
            }
        });
    }

    private void loadTasksForDate(String date) {
        taskViewModel.getTasksByDate(date).observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.setTasks(tasks);
        });
    }

    private void searchTasks(String query) {
        taskViewModel.searchTasks(query).observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.setTasks(tasks);
        });
    }

    @Override
    public void onTaskClick(Task task) {
        // Handle task click - show details
    }

    @Override
    public void onTaskToggle(Task task) {
        task.setCompleted(!task.isCompleted());
        taskViewModel.update(task);
    }

    @Override
    public void onTaskEdit(Task task) {
        // Navigate to edit mode
    }
}
