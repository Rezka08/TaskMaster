package com.example.taskmaster.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.model.CalendarDay;
import java.util.ArrayList;
import java.util.List;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.CalendarDayViewHolder> {
    private List<CalendarDay> calendarDays = new ArrayList<>();
    private String selectedDate = "";
    private OnDateClickListener onDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public CalendarDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarDayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarDayViewHolder holder, int position) {
        CalendarDay calendarDay = calendarDays.get(position);
        holder.bind(calendarDay);
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    public void setCalendarDays(List<CalendarDay> calendarDays, String selectedDate) {
        this.calendarDays = calendarDays != null ? calendarDays : new ArrayList<>();
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }

    class CalendarDayViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay;
        private CardView cvDay;

        public CalendarDayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            cvDay = itemView.findViewById(R.id.cv_day);

            itemView.setOnClickListener(v -> {
                if (onDateClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    CalendarDay day = calendarDays.get(getAdapterPosition());
                    if (!day.getDay().isEmpty() && day.getFullDate() != null) {
                        selectedDate = day.getFullDate();
                        onDateClickListener.onDateClick(day.getFullDate());
                        notifyDataSetChanged(); // Refresh to update selection
                    }
                }
            });
        }

        public void bind(CalendarDay calendarDay) {
            if (calendarDay.getDay().isEmpty()) {
                // Empty day
                tvDay.setText("");
                cvDay.setCardBackgroundColor(Color.TRANSPARENT);
                cvDay.setCardElevation(0);
                itemView.setClickable(false);
                itemView.setAlpha(0.0f);
            } else {
                tvDay.setText(calendarDay.getDay());
                itemView.setClickable(true);
                itemView.setAlpha(1.0f);

                // Check if this day is selected
                boolean isSelected = calendarDay.getFullDate() != null &&
                        calendarDay.getFullDate().equals(selectedDate);

                if (isSelected) {
                    // Selected day
                    cvDay.setCardBackgroundColor(Color.parseColor("#6C63FF"));
                    tvDay.setTextColor(Color.WHITE);
                    cvDay.setCardElevation(4);
                } else if (calendarDay.isToday()) {
                    // Today but not selected
                    cvDay.setCardBackgroundColor(Color.parseColor("#E8E8F5"));
                    tvDay.setTextColor(Color.parseColor("#6C63FF"));
                    cvDay.setCardElevation(2);
                } else if (calendarDay.hasTask()) {
                    // Has task but not selected or today
                    cvDay.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                    tvDay.setTextColor(Color.parseColor("#FF9800"));
                    cvDay.setCardElevation(1);
                } else {
                    // Regular day
                    cvDay.setCardBackgroundColor(Color.WHITE);
                    tvDay.setTextColor(Color.parseColor("#2C3E50"));
                    cvDay.setCardElevation(1);
                }
            }
        }
    }
}