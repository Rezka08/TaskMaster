package com.example.taskmaster.adapter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import java.util.Locale;

public class SearchTaskAdapter extends RecyclerView.Adapter<SearchTaskAdapter.SearchTaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private String searchQuery = "";

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SearchTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_task, parent, false);
        return new SearchTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTaskViewHolder holder, int position) {
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

    public void setSearchQuery(String query) {
        this.searchQuery = query;
    }

    class SearchTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskDescription, tvTaskDate, tvTaskTime;

        public SearchTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskDate = itemView.findViewById(R.id.tv_task_date);
            tvTaskTime = itemView.findViewById(R.id.tv_task_time);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(tasks.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Task task) {
            // Highlight search query in title
            tvTaskTitle.setText(highlightSearchQuery(task.getTitle(), searchQuery));

            // Show description if available
            if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
                tvTaskDescription.setText(highlightSearchQuery(task.getDescription(), searchQuery));
                tvTaskDescription.setVisibility(View.VISIBLE);
            } else {
                tvTaskDescription.setVisibility(View.GONE);
            }

            // Format and display date
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
                java.util.Date date = inputFormat.parse(task.getDate());
                tvTaskDate.setText(outputFormat.format(date));
            } catch (Exception e) {
                tvTaskDate.setText(task.getDate());
            }

            // Calculate time display
            long daysDiff = DateUtils.getDaysDifference(task.getDate());
            if (daysDiff == 0) {
                tvTaskTime.setText("Today • " + task.getStartTime());
            } else if (daysDiff == 1) {
                tvTaskTime.setText("Tomorrow • " + task.getStartTime());
            } else if (daysDiff > 0) {
                tvTaskTime.setText(daysDiff + " days left • " + task.getStartTime());
            } else if (daysDiff == -1) {
                tvTaskTime.setText("Yesterday • " + task.getStartTime());
            } else {
                tvTaskTime.setText(Math.abs(daysDiff) + " days ago • " + task.getStartTime());
            }
        }

        private SpannableString highlightSearchQuery(String text, String query) {
            SpannableString spannableString = new SpannableString(text);

            if (query != null && !query.trim().isEmpty()) {
                String lowercaseText = text.toLowerCase(Locale.getDefault());
                String lowercaseQuery = query.toLowerCase(Locale.getDefault());

                int startIndex = lowercaseText.indexOf(lowercaseQuery);
                while (startIndex >= 0) {
                    int endIndex = startIndex + query.length();
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startIndex = lowercaseText.indexOf(lowercaseQuery, endIndex);
                }
            }

            return spannableString;
        }
    }
}