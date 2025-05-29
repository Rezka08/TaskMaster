package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import java.util.ArrayList;
import java.util.List;

public class ProgressTaskAdapter extends RecyclerView.Adapter<ProgressTaskAdapter.ProgressTaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();

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
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    class ProgressTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskTime;

        public ProgressTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskTime = itemView.findViewById(R.id.tv_task_time);
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.getTitle());
            tvTaskTime.setText("2 Days ago"); // You can make this dynamic
        }
    }
}