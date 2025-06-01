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

public class ProgressTaskAdapter extends RecyclerView.Adapter<ProgressTaskAdapter.ProgressTaskViewHolder> {
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
    public ProgressTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress_task, parent, false);
        return new ProgressTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressTaskViewHolder holder, int position) {
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

    class ProgressTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskTime;

        public ProgressTaskViewHolder(@NonNull View itemView) {
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

            // Calculate days left or since
            long daysDiff = DateUtils.getDaysDifference(task.getDate());
            if (daysDiff == 0) {
                tvTaskTime.setText("Today");
            } else if (daysDiff == 1) {
                tvTaskTime.setText("Tomorrow");
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