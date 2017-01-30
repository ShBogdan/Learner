package com.bogdan.learner.fragments;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FrgCalendar extends Fragment{
    Bundle bundleDate;

    CalendarPickerView calendar;
    Calendar nextYear;
    Calendar lastYear;
    ArrayList<Date> dates;
    View view;
    Date today;
    private Handler mUiHandler = new Handler();
    Context mContext = getActivity();
    ProgressDialog mProgressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_m_calendar, null);
        mProgressDialog = createProgressDialog(getActivity());
        mProgressDialog.show();
        bundleDate = new Bundle();
        initCalendar(view);
//        calendar.highlightDates(dates);

        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            calendar.highlightDates(dates);
                        }catch (Exception e){
                        }

                    }
                });
                mProgressDialog.dismiss();

            }
        });
        myThread.start();

        return view;
    }

    private void startDayFragment(String dayDate){
        if(DBHelper.getDbHelper(getActivity()).getListWordsByDate(dayDate) == null){
            Toast.makeText(getActivity(), R.string.no_day, Toast.LENGTH_SHORT).show();
        } else {
            Fragment fr = new FrgRepeatDay();
            fr.setArguments(bundleDate);
            bundleDate.putString("com.bogdan.learner.fragments.day_date", dayDate);
            FragmentTransaction fTrans = getActivity().getFragmentManager().beginTransaction();
            fTrans.replace(R.id.fragment_container, fr);
            fTrans.addToBackStack(null).commit();
//            fTrans.commit();
        }
    }

    private void initCalendar(View v){
        //пометить изученные
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        dates = new ArrayList<Date>();
        for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
            Date d = null;
            try {
                d = formatter.parse(el[5]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dates.add(d);
        }

        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DATE, 1);

        lastYear = Calendar.getInstance();
//        lastYear.add(Calendar.YEAR, -3);;
//        cal.setTime(dates.get(0));
        try {
            lastYear.setTime(dates.get(0));
        }catch (Exception e){
            lastYear.add(Calendar.MONTH, -1);
        }



//        lastYear.add(cal.DATE, -1);;
        try {
        calendar = (CalendarPickerView) v.findViewById(R.id.calendar_view);
        today = new Date();
        }catch (Exception e){
            Toast.makeText(getActivity(), R.string.calendar_problem, Toast.LENGTH_SHORT).show();
            Log.d("MyLog", "ERROR CALENDAR");

        }


        calendar.init(lastYear.getTime(), nextYear.getTime())
                .withSelectedDate(today);
        //выделить массив
//                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
//                .withSelectedDates(dates);
        //пометить изученные
//        calendar.highlightDates(dates);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Log.d("MyLog", "onDateSelected " +date.toString());
                startDayFragment(new SimpleDateFormat("yyyyMMdd").format(date));
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
    }


    //    class MyTask extends AsyncTask<Void, Void, Void> {
//        ProgressDialog mProgressDialog = createProgressDialog(getActivity());
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            mProgressDialog.dismiss();
//        }
//
//
//
//    }
    public ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }
}