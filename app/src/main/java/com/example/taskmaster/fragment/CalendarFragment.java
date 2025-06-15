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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.activity.SearchActivity;
import com.example.taskmaster.activity.SettingsActivity;
import com.example.taskmaster.adapter.CalendarTaskAdapter;
import com.example.taskmaster.adapter.CalendarDayAdapter;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.CalendarDay;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private TaskViewModel taskViewModel;
    private RecyclerView rvTasks, rvCalendarDays;
    private CalendarTaskAdapter taskAdapter;
    private CalendarDayAdapter calendarDayAdapter;
    private TextView tvMonthYear;
    private ImageView ivBack, ivSearch, ivForward;
    private MaterialButton btnSettings;

    private String selectedDate = "";
    private Calendar currentCalendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initViews(view);
        setupRecyclerViews();
        setupViewModel();
        setupClickListeners();
        setupCalendar();

        return view;
    }

    private void initViews(View view) {
        rvTasks = view.findViewById(R.id.rv_tasks);
        rvCalendarDays = view.findViewById(R.id.rv_calendar_days);
        tvMonthYear = view.findViewById(R.id.tv_month_year);
        ivBack = view.findViewById(R.id.iv_back);
        ivSearch = view.findViewById(R.id.iv_search);
        ivForward = view.findViewById(R.id.iv_forward);
        btnSettings = view.findViewById(R.id.btn_settings);

        currentCalendar = Calendar.getInstance();
        selectedDate = DateUtils.getCurrentDate();
    }

    private void setupRecyclerViews() {
        // Setup calendar days RecyclerView
        calendarDayAdapter = new CalendarDayAdapter();
        calendarDayAdapter.setOnDateClickListener(date -> {
            selectedDate = date;
            loadTasksForDate(selectedDate);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 7);
        rvCalendarDays.setLayoutManager(gridLayoutManager);

        // Add spacing decoration
        int spacingInPixels = (int) (4 * getResources().getDisplayMetrics().density); // 4dp in pixels
        rvCalendarDays.addItemDecoration(new com.example.taskmaster.utils.GridSpacingItemDecoration(7, spacingInPixels, true));

        rvCalendarDays.setAdapter(calendarDayAdapter);

        // Setup tasks RecyclerView
        taskAdapter = new CalendarTaskAdapter();
        taskAdapter.setOnItemClickListener(task -> {
            // Navigate to Task Detail
            Intent intent = new Intent(getContext(), com.example.taskmaster.activity.TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivity(intent);
        });
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTasks.setAdapter(taskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            // Navigate to previous month
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarDisplay();
        });

        ivForward.setOnClickListener(v -> {
            // Navigate to next month
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarDisplay();
        });

        ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        // NEW: Settings button click listener
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);

            // Add smooth transition
            if (getActivity() != null) {
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    private void setupCalendar() {
        updateCalendarDisplay();
        loadTasksForDate(selectedDate);
    }

    private void updateCalendarDisplay() {
        // Update month/year display
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        tvMonthYear.setText(monthYearFormat.format(currentCalendar.getTime()));

        // Generate calendar days
        List<CalendarDay> calendarDays = generateCalendarDays();
        calendarDayAdapter.setCalendarDays(calendarDays, selectedDate);
    }

    private List<CalendarDay> generateCalendarDays() {
        List<CalendarDay> days = new ArrayList<>();

        // Create a calendar for the first day of the month
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Adjust for Monday as first day of week
        // Calendar.SUNDAY = 1, MONDAY = 2, ..., SATURDAY = 7
        // We want: Monday = 0, Tuesday = 1, ..., Sunday = 6
        int adjustedFirstDay = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - 2;

        // Add empty days for the previous month
        for (int i = 0; i < adjustedFirstDay; i++) {
            days.add(new CalendarDay("", false, false));
        }

        // Add days of current month
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = DateUtils.getCurrentDate();

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String dateString = dateFormat.format(calendar.getTime());

            boolean isToday = dateString.equals(todayDate);
            boolean hasTask = false; // Will be updated when we load tasks for the month

            days.add(new CalendarDay(String.valueOf(day), isToday, hasTask, dateString));
        }

        return days;
    }

    private void loadTasksForDate(String date) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        taskViewModel.getTasksByDate(date, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        taskAdapter.setTasks(tasks);
                        toggleEmptyState(tasks == null || tasks.isEmpty());

                        // Update calendar to show which days have tasks
                        updateCalendarWithTasks();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        taskAdapter.setTasks(new ArrayList<>());
                        toggleEmptyState(true);
                    });
                }
            }
        });
    }

    private void updateCalendarWithTasks() {
        // Load all tasks for the current month to mark days with tasks
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = monthFormat.format(currentCalendar.getTime());

        taskViewModel.getAllTasks(new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> allTasks) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Create a list of dates that have tasks in current month
                        List<String> datesWithTasks = new ArrayList<>();
                        for (Task task : allTasks) {
                            if (task.getDate().startsWith(currentMonth)) {
                                datesWithTasks.add(task.getDate());
                            }
                        }

                        // Update calendar days with task indicators
                        List<CalendarDay> updatedDays = generateCalendarDaysWithTasks(datesWithTasks);
                        calendarDayAdapter.setCalendarDays(updatedDays, selectedDate);
                    });
                }
            }

            @Override
            public void onError(String error) {
                // Handle error silently, calendar will still work without task indicators
            }
        });
    }

    private List<CalendarDay> generateCalendarDaysWithTasks(List<String> datesWithTasks) {
        List<CalendarDay> days = new ArrayList<>();

        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int adjustedFirstDay = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - 2;

        // Add empty days for the previous month
        for (int i = 0; i < adjustedFirstDay; i++) {
            days.add(new CalendarDay("", false, false));
        }

        // Add days of current month with task indicators
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = DateUtils.getCurrentDate();

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String dateString = dateFormat.format(calendar.getTime());

            boolean isToday = dateString.equals(todayDate);
            boolean hasTask = datesWithTasks.contains(dateString);

            days.add(new CalendarDay(String.valueOf(day), isToday, hasTask, dateString));
        }

        return days;
    }

    private void toggleEmptyState(boolean isEmpty) {
        View emptyLayout = getView() != null ? getView().findViewById(R.id.layout_empty_tasks) : null;
        if (emptyLayout != null) {
            emptyLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        rvTasks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh tasks for selected date
        if (!selectedDate.isEmpty()) {
            loadTasksForDate(selectedDate);
        }
    }

    /**
     * Public method to refresh data from other components
     */
    public void refreshData() {
        if (isAdded() && getContext() != null) {
            updateCalendarDisplay();
            if (!selectedDate.isEmpty()) {
                loadTasksForDate(selectedDate);
            }
        }
    }
}