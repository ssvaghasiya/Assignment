package com.demoapp.datepicker.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demoapp.R;
import com.demoapp.datepicker.OnDateSelectedListener;
import com.demoapp.datepicker.TimelineView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private static final String TAG = "TimelineAdapter";
    private static final String[] WEEK_DAYS = DateFormatSymbols.getInstance().getShortWeekdays();
    private static final String[] MONTH_NAME = DateFormatSymbols.getInstance().getShortMonths();

    private Calendar calendar = Calendar.getInstance();
    private Calendar selectedCalendar = Calendar.getInstance();
    private TimelineView timelineView;
    private Date[] deactivatedDates;

    private OnDateSelectedListener listener;

    private View selectedView;
    private int selectedPosition;
    private int currentMonth = -1;

    public TimelineAdapter(TimelineView timelineView, int selectedPosition) {
        this.timelineView = timelineView;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item_layout, parent, false);
        return new TimelineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        resetCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, position);

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        final boolean isDisabled = holder.bind(month, day, dayOfWeek, year, position);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedView != null) {
                    selectedView.setBackground(ContextCompat.getDrawable(timelineView.getContext(), R.drawable.transparent_date_layout));
                    notifyDataSetChanged();
                }
                if (!isDisabled) {
                    v.setBackground(ContextCompat.getDrawable(timelineView.getContext(), R.drawable.background_shape));
                    holder.monthView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                    holder.dateView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                    holder.dayView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));

                    setCenterPosition(selectedPosition, position);
                    selectedPosition = position;
                    selectedView = v;
                    notifyDataSetChanged();

                    if (listener != null) {
                        selectedCalendar.set(year, month, day, 0, 0, 0);
                        listener.onDateSelected(year, month, day, dayOfWeek);
                    }
                } else {
                    if (listener != null)
                        listener.onDisabledDateSelected(year, month, day, dayOfWeek, isDisabled);
                }
            }
        });
    }

    private void resetCalendar() {
        calendar.set(timelineView.getYear(), timelineView.getMonth(), timelineView.getDate(),
                1, 0, 0);
    }

    /**
     * Set the position of selected date
     *
     * @param selectedPosition active date Position
     */
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public void disableDates(Date[] dates) {
        this.deactivatedDates = dates;
        notifyDataSetChanged();
    }

    public void setDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView monthView, dateView, dayView;
        private View rootView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            monthView = itemView.findViewById(R.id.monthView);
            dateView = itemView.findViewById(R.id.dateView);
            dayView = itemView.findViewById(R.id.dayView);
            rootView = itemView.findViewById(R.id.rootView);
        }

        boolean bind(int month, int day, int dayOfWeek, int year, int position) {
            monthView.setTextColor(timelineView.getMonthTextColor());
            dateView.setTextColor(timelineView.getDateTextColor());
            dayView.setTextColor(timelineView.getDayTextColor());

            Calendar currentDate = Calendar.getInstance();
            String dayCD = (String) DateFormat.format("dd", currentDate); // 20
            String monthCD = (String) DateFormat.format("M", currentDate); // 6
            String yearCD = (String) DateFormat.format("yyyy", currentDate); // 2013


//            if(position == 0) {
//                dayView.setText("Today");
//            }
//            else if(position == 1){
//                dayView.setText("Tomorrow");
//
//            } else{
//                dayView.setText(WEEK_DAYS[dayOfWeek].toUpperCase(Locale.US));
//            }

            dayView.setText(WEEK_DAYS[dayOfWeek].toUpperCase());
            monthView.setText(MONTH_NAME[month].toUpperCase());
            dateView.setText(String.valueOf(day));

            rootView.setBackgroundColor(ContextCompat.getColor(timelineView.getContext(), R.color.transparent));
            if (selectedPosition == position) {
                monthView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                dateView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.blue1));
                dateView.setBackground(ContextCompat.getDrawable(timelineView.getContext(), R.drawable.circle_white));
                dayView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                selectedView = rootView;
            } else {
                if (day == Integer.parseInt(dayCD) && (month + 1) == Integer.parseInt(monthCD) && year == Integer.parseInt(yearCD)) {
                    dateView.setBackground(ContextCompat.getDrawable(timelineView.getContext(), R.drawable.transparent_date_layout));
                    dateView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                    dayView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                    monthView.setTextColor(ContextCompat.getColor(timelineView.getContext(), R.color.white));
                } else {
                    dateView.setBackground(ContextCompat.getDrawable(timelineView.getContext(), R.drawable.transparent_date_layout));
                }
            }

            for (Date date : deactivatedDates) {
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.setTime(date);
                if (tempCalendar.get(Calendar.DAY_OF_MONTH) == day &&
                        tempCalendar.get(Calendar.MONTH) == month &&
                        tempCalendar.get(Calendar.YEAR) == year) {
                    monthView.setTextColor(timelineView.getDisabledDateColor());
                    dateView.setTextColor(timelineView.getDisabledDateColor());
                    dayView.setTextColor(timelineView.getDisabledDateColor());
                    rootView.setBackgroundColor(ContextCompat.getColor(timelineView.getContext(), R.color.transparent));
                    return true;
                }
            }

            if (!isSelectedDateVisible()) {
                if (currentMonth != month) {
                    listener.monthListener(year, month, day, dayOfWeek);
                    currentMonth = month;
                }
            } else {
                final int yearSel = selectedCalendar.get(Calendar.YEAR);
                final int monthSel = selectedCalendar.get(Calendar.MONTH);
                final int dayOfWeekSel = selectedCalendar.get(Calendar.DAY_OF_WEEK);
                final int daySel = selectedCalendar.get(Calendar.DAY_OF_MONTH);
                listener.monthListener(yearSel, monthSel, daySel, dayOfWeekSel);
            }
            return false;
        }
    }

    void setCenterPosition(int selectedPosition, int position) {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) timelineView.getLayoutManager());
        int totalVisibleItems = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
        int centeredItemPosition = totalVisibleItems / 2;
        if (selectedPosition >= position) {
            layoutManager.scrollToPosition(position - centeredItemPosition);
        } else {
            layoutManager.scrollToPosition(position + centeredItemPosition);
        }
    }

    boolean isSelectedDateVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) timelineView.getLayoutManager());
        if (selectedPosition <= layoutManager.findLastVisibleItemPosition() &&
                selectedPosition >= layoutManager.findFirstVisibleItemPosition()) {

            return true;
        }
        return false;
    }
}