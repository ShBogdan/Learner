package com.bogdan.learner.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import com.bogdan.learner.R;

public class FrgCalendar extends Fragment{
    CalendarView calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_calendar, null);
        initializeCalendar();
        return view;
    }
    public void initializeCalendar(){
        calendar = (CalendarView) getActivity().findViewById(R.id.calendar);
//        calendar.setShowWeekNumber(false);
//        calendar.setFirstDayOfWeek(1);
//        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
//        calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
//        calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
//        calendar.setSelectedDateVerticalBar(R.color.my_background);
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                Toast.makeText(getActivity(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
