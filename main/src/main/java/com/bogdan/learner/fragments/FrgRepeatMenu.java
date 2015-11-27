package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FrgRepeatMenu extends Fragment implements View.OnClickListener {
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    Bundle bundleDate;
    Button btn_repeat_day1, btn_repeat_day2, btn_repeat_day3, btn_repeat_day21, btn_repeat_day90, btn_all_words;
    TextView tv_learned, tv_know;
    CheckBox changeWordPlace; // выводим слова в порядке rus-eng;
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_repeat_selectively, null);

        tv_learned = (TextView)view.findViewById(R.id.tv_learned);
        tv_learned.setText("Вы изучили: " + DBHelper.getDbHelper(getActivity()).learnedWords.size() + " новых слов");
        tv_know    = (TextView)view.findViewById(R.id.tv_known);
        tv_know.setText("Вы уже знали: " +  DBHelper.getDbHelper(getActivity()).listKnownWords.size() + " слов");

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
        btn_all_words = (Button) view.findViewById(R.id.btn_all_words);
        btn_all_words.setOnClickListener(this);
        changeWordPlace = (CheckBox) view.findViewById(R.id.changeWordPlace);

        bundleDate = new Bundle();

        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        editor = sp.edit();

        if(!sp.contains("changeWordPlace")) {
            editor.putBoolean("changeWordPlace", false).apply();
        }
        if(sp.getBoolean("changeWordPlace", false)){
             }
        if(sp.getBoolean("changeWordPlace", true)){
            changeWordPlace.toggle();
        }


        changeWordPlace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(changeWordPlace.isChecked()){
                    editor.putBoolean("changeWordPlace", true).apply();
                } else
                    editor.putBoolean("changeWordPlace", false).apply();
            }
        });



        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_repeat_day1:
                startFragment(getOldDate(0));
                break;
            case R.id.btn_repeat_day2:
                startFragment(getOldDate(-1));
                break;
            case R.id.btn_repeat_day3:
                startFragment(getOldDate(-2));
                break;
            case R.id.btn_repeat_day21:
                startFragment(getOldDate(-20));
                break;
            case R.id.btn_repeat_day90:
                startFragment(getOldDate(-89));
                break;
            case R.id.btn_all_words:
                FragmentTransaction fTrans = getActivity().getFragmentManager().beginTransaction();
                fTrans.replace(R.id.fragment_container, new FrgCardOrList());
                fTrans.addToBackStack(null).commit();
                break;
        }
    }

    /**
     * для получения конкретного дня по дате(метод принимает отрицательные значения)
     */
    private String getOldDate(int days){
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, days);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
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


