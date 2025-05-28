package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class MonthlyPreviewAdapter extends RecyclerView.Adapter<MonthlyPreviewAdapter.MonthViewHolder> {
    private List<MonthlyPreview> monthlyPreviews = new ArrayList<>();

    public static class MonthlyPreview {
        private String monthYear;
        private int taskCount;

        public MonthlyPreview(String monthYear, int taskCount) {
            this.monthYear = monthYear;
            this.taskCount = taskCount;
        }

        public String getMonthYear() { return monthYear; }
        public int getTaskCount() { return taskCount; }
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monthly_preview, parent, false);
        return new MonthViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        MonthlyPreview preview = monthlyPreviews.get(position);
        holder.bind(preview);
    }

    @Override
    public int getItemCount() {
        return monthlyPreviews.size();
    }

    public void setMonthlyPreviews(List<MonthlyPreview> previews) {
        this.monthlyPreviews = previews;
        notifyDataSetChanged();
    }

    class MonthViewHolder extends RecyclerView.ViewHolder {
        private TextView monthTextView;
        private TextView countTextView;
        private TextView labelTextView;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            monthTextView = itemView.findViewById(R.id.tv_month);
            countTextView = itemView.findViewById(R.id.tv_count);
            labelTextView = itemView.findViewById(R.id.tv_label);
        }

        public void bind(MonthlyPreview preview) {
            monthTextView.setText(DateUtils.getMonthName(preview.getMonthYear()));
            countTextView.setText(String.valueOf(preview.getTaskCount()));
            labelTextView.setText("tugas");
        }
    }
}