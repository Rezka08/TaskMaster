package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.utils.DateUtils;
import com.example.taskmaster.utils.PriorityUtils;
import java.util.ArrayList;
import java.util.List;

public class CalendarTaskAdapter extends RecyclerView.Adapter<CalendarTaskAdapter.CalendarTaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();

    @NonNull
    @Override
    public CalendarTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_task, parent, false);
        return new CalendarTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarTaskViewHolder holder, int position) {
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

    class CalendarTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskTime;
        private ImageView ivTask, ivMoreVert;
        private CardView cvTaskItem;

        public CalendarTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskTime = itemView.findViewById(R.id.tv_task_time);
            ivTask = itemView.findViewById(R.id.iv_task);
            ivMoreVert = itemView.findViewById(R.id.iv_more_vert);
            cvTaskItem = itemView.findViewById(R.id.cv_task_item);
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.getTitle());

            // Calculate time left
            long daysLeft = DateUtils.getDaysDifference(task.getDate());
            if (daysLeft == 0) {
                tvTaskTime.setText("Today");
            } else if (daysLeft == 1) {
                tvTaskTime.setText("Tomorrow");
            } else if (daysLeft > 0) {
                tvTaskTime.setText(daysLeft + " Hours Left");
            } else {
                tvTaskTime.setText(Math.abs(daysLeft) + " Days ago");
            }

            // Set background color based on priority
            int priorityColor = PriorityUtils.getPriorityColor(task.getPriority());
            cvTaskItem.setCardBackgroundColor(priorityColor);
        }
    }
}