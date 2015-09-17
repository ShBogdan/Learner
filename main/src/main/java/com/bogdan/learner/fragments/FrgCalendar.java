package com.bogdan.learner.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

public class FrgCalendar extends Fragment{

    CalendarView calendar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_calendar, null);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showToast();
//        initializeCalendar();
    }

    public void initializeCalendar(){
        calendar = (CalendarView) getActivity().findViewById(R.id.calendar);
        calendar.setShowWeekNumber(false);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (DBHelper.getDbHelper(getActivity()).getListWordsByDate(year + month + dayOfMonth + "") != null) {
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FrgLearnToDay()).commit();
                } else showToast();
            }
        });
    }
    public void showToast(){
        Toast.makeText(getActivity().getApplication(), "Нет дней для занятий", Toast.LENGTH_SHORT).show();

    }
}
