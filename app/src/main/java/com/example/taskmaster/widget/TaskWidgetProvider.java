package com.example.taskmaster.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.view.View;

import com.example.taskmaster.activity.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.repository.TaskRepository;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.utils.PriorityUtils;

import java.util.List;

public class TaskWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_list);

        // Set up click intent to open main activity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set click listener for entire widget
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        // Update widget title
        views.setTextViewText(R.id.widget_title, "TaskMaster");

        // Load today's tasks
        loadTodayTasks(context, views, appWidgetManager, appWidgetId);
    }

    private void loadTodayTasks(Context context, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        TaskRepository repository = new TaskRepository(context);
        String currentDate = DateUtils.getCurrentDate();

        repository.getTodayTasksForWidget(currentDate, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                updateWidgetWithTasks(context, views, appWidgetManager, appWidgetId, tasks);
            }

            @Override
            public void onError(String error) {
                updateWidgetWithEmptyState(context, views, appWidgetManager, appWidgetId);
            }
        });
    }

    private void updateWidgetWithTasks(Context context, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId, List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            updateWidgetWithEmptyState(context, views, appWidgetManager, appWidgetId);
            return;
        }

        // Hide empty state
        views.setViewVisibility(R.id.widget_empty_state, View.GONE);
        views.setViewVisibility(R.id.widget_task_list, View.VISIBLE);

        // Show up to 2 most important tasks
        for (int i = 0; i < Math.min(tasks.size(), 2); i++) {
            Task task = tasks.get(i);
            updateTaskView(views, task, i + 1);
        }

        // Hide unused task views
        for (int i = tasks.size(); i < 2; i++) {
            hideTaskView(views, i + 1);
        }

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateWidgetWithEmptyState(Context context, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Hide task list
        views.setViewVisibility(R.id.widget_task_list, View.GONE);

        // Show empty state
        views.setViewVisibility(R.id.widget_empty_state, View.VISIBLE);
        views.setTextViewText(R.id.widget_empty_title, "No tasks today");
        views.setTextViewText(R.id.widget_empty_subtitle, "Tap to add a new task");

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateTaskView(RemoteViews views, Task task, int taskNumber) {
        String taskLayoutId = "widget_task_" + taskNumber;
        String titleId = "widget_task_title_" + taskNumber;
        String timeId = "widget_task_time_" + taskNumber;

        int taskLayoutResId = getResourceId(taskLayoutId);
        int titleResId = getResourceId(titleId);
        int timeResId = getResourceId(timeId);

        if (taskLayoutResId != 0 && titleResId != 0 && timeResId != 0) {
            views.setViewVisibility(taskLayoutResId, View.VISIBLE);
            views.setTextViewText(titleResId, task.getTitle());

            // Calculate time display
            long daysDiff = DateUtils.getDaysDifference(task.getDate());
            String timeText;
            if (daysDiff == 0) {
                timeText = task.getStartTime();
            } else if (daysDiff == 1) {
                timeText = "Tomorrow";
            } else if (daysDiff > 0) {
                timeText = daysDiff + " days left";
            } else {
                timeText = "Overdue";
            }

            views.setTextViewText(timeResId, timeText);

            // Set background based on priority
            int backgroundColor = PriorityUtils.getPriorityColor(task.getPriority());
            views.setInt(taskLayoutResId, "setBackgroundColor", backgroundColor);
        }
    }

    private void hideTaskView(RemoteViews views, int taskNumber) {
        String taskLayoutId = "widget_task_" + taskNumber;
        int taskLayoutResId = getResourceId(taskLayoutId);

        if (taskLayoutResId != 0) {
            views.setViewVisibility(taskLayoutResId, View.GONE);
        }
    }

    private int getResourceId(String resourceName) {
        try {
            return R.id.class.getField(resourceName).getInt(null);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Called when first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Called when last widget is removed
    }
}