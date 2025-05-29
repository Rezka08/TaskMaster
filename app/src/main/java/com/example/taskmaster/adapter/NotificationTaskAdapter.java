package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class NotificationTaskAdapter extends RecyclerView.Adapter<NotificationTaskAdapter.NotificationTaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();

    @NonNull
    @Override
    public NotificationTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_task, parent, false);
        return new NotificationTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationTaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.bind(currentTask);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    class NotificationTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskTime;

        public NotificationTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskTime = itemView.findViewById(R.id.tv_task_time);
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.getTitle());

            // Calculate days left
            long daysLeft = DateUtils.getDaysDifference(task.getDate());
            if (daysLeft == 0) {
                tvTaskTime.setText("Today");
            } else if (daysLeft == 1) {
                tvTaskTime.setText("Tomorrow");
            } else if (daysLeft > 0) {
                tvTaskTime.setText(daysLeft + " Days left");
            } else {
                tvTaskTime.setText(Math.abs(daysLeft) + " Days ago");
            }
        }
    }
}