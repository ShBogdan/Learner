package com.bogdan.learner.fragments;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FrgCalendar extends Fragment{
    Bundle bundleDate;

    CalendarPickerView calendar;
    Calendar nextYear;
    Calendar lastYear;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_calendar, null);
        bundleDate = new Bundle();

        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DATE, 1);

        lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);;

        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        Date today = new Date();

        //пометить изученные
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//        ArrayList<Date> dates = new ArrayList<Date>();
//
//        for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
//            Date d = null;
//            try {
//                d = formatter.parse(el[5]);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            dates.add(d);
//        }

        calendar.init(lastYear.getTime(), nextYear.getTime())
                .withSelectedDate(today);

//                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
//                .withSelectedDates(dates);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.d("MyLog", "onDateSelected " +date.toString());
                startFragment(new SimpleDateFormat("yyyyMMdd").format(date));
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });

        calendar.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                //чтобы не выводило сообщение о не верной дате
            }
        });
        return view;
    }

    private void startFragment(String dayDate){
        if(DBHelper.getDbHelper(getActivity()).getListWordsByDate(dayDate) == null){
            Toast.makeText(getActivity(), R.string.no_day, Toast.LENGTH_SHORT).show();
        } else {
            Fragment fr = new FrgRepeatDay();
            fr.setArguments(bundleDate);
            bundleDate.putString("com.bogdan.learner.fragments.day_date", dayDate);
            FragmentTransaction fTrans = getActivity().getFragmentManager().beginTransaction();
            fTrans.replace(R.id.fragment_container, fr);
            fTrans.addToBackStack(null).commit();
        }
    }

}