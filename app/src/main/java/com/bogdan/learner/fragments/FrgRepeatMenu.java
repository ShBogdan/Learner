package com.bogdan.learner.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FrgRepeatMenu extends Fragment implements View.OnClickListener {
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    Bundle bundleDate;
    Button btn_repeat_day1, btn_repeat_day2, btn_repeat_day3, btn_repeat_day21, btn_repeat_day90, btn_calendar, btn_all_words;
    TextView tv_learned, tv_know;
    CheckBox changeWordPlace; // выводим слова в порядке rus-eng;
    CheckBox autoSpeech; // выводим слова в порядке rus-eng;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    CalendarPickerView calendar;
    Calendar nextYear;
    Calendar lastYear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_repeat_selectively, null);

//        tv_learned = (TextView)view.findViewById(R.id.tv_learned);
//        tv_learned.setText(getString(R.string.you_know) + DBHelper.getDbHelper(getActivity()).learnedWords.size() + getString(R.string.new_words));
//        tv_know    = (TextView)view.findViewById(R.id.tv_known);
//        tv_know.setText(getString(R.string.you_knew) +  DBHelper.getDbHelper(getActivity()).listKnownWords.size() + getString(R.string.words));

        btn_repeat_day1 = (Button) view.findViewById(R.id.btn_repeat_day1);
        btn_repeat_day1.setOnClickListener(this);
        btn_repeat_day2 = (Button) view.findViewById(R.id.btn_repeat_day2);
        btn_repeat_day2.setOnClickListener(this);
        btn_repeat_day3 = (Button) view.findViewById(R.id.btn_repeat_day3);
        btn_repeat_day3.setOnClickListener(this);
        btn_repeat_day21 = (Button) view.findViewById(R.id.btn_repeat_day21);
        btn_repeat_day21.setOnClickListener(this);
        btn_repeat_day90 = (Button) view.findViewById(R.id.btn_repeat_day90);
        btn_repeat_day90.setOnClickListener(this);
        btn_calendar = (Button) view.findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(this);
        btn_all_words = (Button) view.findViewById(R.id.btn_all_words);
        btn_all_words.setOnClickListener(this);
        bundleDate = new Bundle();
        return view;
    }


    @Override
    public void onClick(View v) {
        var id = v.getId();

        if (id == R.id.btn_repeat_day1) {
            startFragment(getOldDate(0));
        }
        if (id == R.id.btn_repeat_day2) {
            startFragment(getOldDate(-1));
        }
        if (id == R.id.btn_repeat_day3) {
            startFragment(getOldDate(-2));
        }
        if (id == R.id.btn_repeat_day21) {
            startFragment(getOldDate(-20));
        }
        if (id == R.id.btn_repeat_day90) {
            startFragment(getOldDate(-89));
        }
        if (id == R.id.btn_calendar) {
            FragmentTransaction fCTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fCTrans.replace(R.id.fragment_container, new FrgCalendar());
            fCTrans.addToBackStack(null).commit();
        }
        if (id == R.id.btn_all_words) {
            FragmentTransaction fTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.fragment_container, new FrgCardOrList());
            fTrans.addToBackStack(null).commit();
        }
    }

    /**
     * для получения конкретного дня по дате(метод принимает отрицательные значения)
     */
    private String getOldDate(int days) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, days);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    }

    private void startFragment(String dayDate) {
        if (DBHelper.getDbHelper(getActivity()).getListWordsByDate(dayDate) == null) {
            Toast.makeText(getActivity(), R.string.no_day, Toast.LENGTH_SHORT).show();
        } else {
            Fragment fr = new FrgRepeatDay();
            fr.setArguments(bundleDate);
            bundleDate.putString("com.bogdan.learner.fragments.day_date", dayDate);
            androidx.fragment.app.FragmentTransaction fTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.fragment_container, fr);
            fTrans.addToBackStack(null).commit();
        }
    }

    private void getChangeWordPlace() {
        if (!sp.contains("changeWordPlace")) {
            editor.putBoolean("changeWordPlace", false).apply();
        }
        if (sp.getBoolean("changeWordPlace", false)) {
        }
        if (sp.getBoolean("changeWordPlace", true)) {
            changeWordPlace.toggle();
        }


        changeWordPlace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (changeWordPlace.isChecked()) {
                    editor.putBoolean("changeWordPlace", true).apply();
                } else
                    editor.putBoolean("changeWordPlace", false).apply();
            }
        });
    }

    private void getAutoSpeech() {
        if (!sp.contains("autoSpeech")) {
            editor.putBoolean("autoSpeech", false).apply();
        }
        if (sp.getBoolean("autoSpeech", false)) {
        }
        if (sp.getBoolean("autoSpeech", true)) {
            autoSpeech.toggle();
        }

        autoSpeech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autoSpeech.isChecked()) {
                    editor.putBoolean("autoSpeech", true).apply();
                } else
                    editor.putBoolean("autoSpeech", false).apply();
            }
        });
    }

    private void getCalendar(View view) {
        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);
        ;

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
                Log.d("MyLog", "onDateSelected " + date.toString());
                startFragment(new SimpleDateFormat("yyyyMMdd").format(date));
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });
    }
}


