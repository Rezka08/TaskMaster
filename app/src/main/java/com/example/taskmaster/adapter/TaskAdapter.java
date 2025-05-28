package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.PriorityUtils;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskToggle(Task task);
        void onTaskEdit(Task task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
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

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView timeTextView;
        private TextView priorityTextView;
        private CheckBox completedCheckBox;
        private View priorityIndicator;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_task_title);
            timeTextView = itemView.findViewById(R.id.tv_task_time);
            priorityTextView = itemView.findViewById(R.id.tv_task_priority);
            completedCheckBox = itemView.findViewById(R.id.cb_task_completed);
            priorityIndicator = itemView.findViewById(R.id.view_priority_indicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaskClick(tasks.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaskEdit(tasks.get(position));
                }
                return true;
            });

            completedCheckBox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaskToggle(tasks.get(position));
                }
            });
        }

        public void bind(Task task) {
            titleTextView.setText(task.getTitle());
            timeTextView.setText(task.getStartTime() + " - " + task.getEndTime());
            priorityTextView.setText(PriorityUtils.getPriorityText(task.getPriority()));
            completedCheckBox.setChecked(task.isCompleted());

            int priorityColor = PriorityUtils.getPriorityColor(task.getPriority());
            priorityIndicator.setBackgroundColor(priorityColor);
            priorityTextView.setTextColor(priorityColor);
        }
    }
}