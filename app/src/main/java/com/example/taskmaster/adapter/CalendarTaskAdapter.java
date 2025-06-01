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
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

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
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();

        // Show/hide empty state in parent if needed
        // This can be handled by the fragment
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

            // Set click listener for entire item
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(tasks.get(getAdapterPosition()));
                }
            });

            // Set click listener for more options
            ivMoreVert.setOnClickListener(v -> {
                // Can be used for showing popup menu with edit/delete options
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(tasks.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.getTitle());

            // Calculate time display
            long daysDiff = DateUtils.getDaysDifference(task.getDate());
            if (daysDiff == 0) {
                // Check if task is in progress (today's date)
                tvTaskTime.setText("Today - " + task.getStartTime());
            } else if (daysDiff == 1) {
                tvTaskTime.setText("Tomorrow");
            } else if (daysDiff > 0) {
                if (daysDiff < 7) {
                    tvTaskTime.setText(daysDiff + " Days Left");
                } else {
                    long hoursLeft = daysDiff * 24;
                    tvTaskTime.setText(hoursLeft + " Hours Left");
                }
            } else if (daysDiff == -1) {
                tvTaskTime.setText("Yesterday");
            } else {
                tvTaskTime.setText(Math.abs(daysDiff) + " Days ago");
            }

            // Set background color based on priority
            int priorityColor = PriorityUtils.getPriorityColor(task.getPriority());
            cvTaskItem.setCardBackgroundColor(priorityColor);

            // Set text colors based on priority
            if (task.getPriority() == PriorityUtils.PRIORITY_HIGH) {
                tvTaskTitle.setTextColor(android.graphics.Color.WHITE);
                tvTaskTime.setTextColor(android.graphics.Color.parseColor("#CCFFFFFF"));
                ivTask.setColorFilter(android.graphics.Color.WHITE);
                ivMoreVert.setColorFilter(android.graphics.Color.WHITE);
            } else {
                tvTaskTitle.setTextColor(android.graphics.Color.parseColor("#2C3E50"));
                tvTaskTime.setTextColor(android.graphics.Color.parseColor("#7F8C8D"));
                ivTask.setColorFilter(android.graphics.Color.parseColor("#2C3E50"));
                ivMoreVert.setColorFilter(android.graphics.Color.parseColor("#2C3E50"));
            }
        }
    }
}