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
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

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
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    class NotificationTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskTime;

        public NotificationTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskTime = itemView.findViewById(R.id.tv_task_time);

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(tasks.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.getTitle());

            // Calculate days left
            long daysDiff = DateUtils.getDaysDifference(task.getDate());
            if (daysDiff == 0) {
                tvTaskTime.setText("Today • " + task.getStartTime());
            } else if (daysDiff == 1) {
                tvTaskTime.setText("Tomorrow • " + task.getStartTime());
            } else if (daysDiff > 0) {
                tvTaskTime.setText(daysDiff + " Days left");
            } else if (daysDiff == -1) {
                tvTaskTime.setText("Yesterday");
            } else {
                tvTaskTime.setText(Math.abs(daysDiff) + " Days ago");
            }
        }
    }
}