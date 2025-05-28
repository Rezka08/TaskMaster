package com.example.taskmaster.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.taskmaster.R;
import com.example.taskmaster.database.TaskDao;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.utils.PriorityUtils;
import java.util.ArrayList;
import java.util.List;

public class TaskWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private List<Task> tasks = new ArrayList<>();
    private TaskDao database;

    public TaskWidgetRemoteViewsFactory(Context context) {
        this.context = context;
        this.database = TaskDao.getDatabase(context);
    }

    @Override
    public void onCreate() {
        // Initialize data
    }

    @Override
    public void onDataSetChanged() {
        // Update tasks data
        // Note: This should be done on background thread in real implementation
        String currentDate = DateUtils.getCurrentDate();
        // Get today's tasks for widget - this is a simplified version
        // In real implementation, you would use proper async handling
    }

    @Override
    public void onDestroy() {
        tasks.clear();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= tasks.size()) {
            return null;
        }

        Task task = tasks.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);

        views.setTextViewText(R.id.widget_task_title, task.getTitle());
        views.setTextViewText(R.id.widget_task_time, task.getStartTime() + " - " + task.getEndTime());

        int priorityColor = PriorityUtils.getPriorityColor(task.getPriority());
        views.setInt(R.id.widget_priority_indicator, "setBackgroundColor", priorityColor);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}