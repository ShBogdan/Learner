package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;


public class FrgRepeatDay extends Fragment implements View.OnClickListener {
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    SharedPreferences sp;
    String date;
    boolean changeWord; //меняем местами eng и rus
    ArrayList<String[]> toDayListWords;
    TextView englishWord, transWord, russianWord, tvSumWords, btn_nextTV;
    LinearLayout btnNext;
    int countBtnClick;
    String eng;
    String rus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_repeat_day, null);
        countBtnClick = 2;

        date = getArguments().getString("com.bogdan.learner.fragments.day_date");
        toDayListWords = new ArrayList<>(DBHelper.getDbHelper(getActivity()).getListWordsByDate(date));


        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        changeWord = sp.getBoolean("changeWordPlace", false);






        englishWord = (TextView) view.findViewById(R.id.englishWord);
        transWord = (TextView) view.findViewById(R.id.transWord);
        russianWord = (TextView) view.findViewById(R.id.russianWord);
        tvSumWords = (TextView) view.findViewById(R.id.tvSumWords);
        btn_nextTV = (TextView) view.findViewById(R.id.btn_nextTv);
        btnNext = (LinearLayout) view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        reloadFragment();
        countBtnClick = 2;

        return view;
    }

    private void reloadFragment() {
        if (toDayListWords.size() != 0) {
            Collections.shuffle(toDayListWords);
            tvSumWords.setText(String.valueOf(toDayListWords.size()));
            btn_nextTV.setText(R.string.answer);
            eng = toDayListWords.get(0)[0];
            rus = toDayListWords.get(0)[1];
            if(changeWord){
                String temp = eng;
                eng = rus;
                rus = temp;
            }
            englishWord.setText(eng);
            transWord.setText(toDayListWords.get(0)[2]);
            russianWord.setText("");
        } else {
            toDayListWords = new ArrayList<>(DBHelper.getDbHelper(getActivity()).getListWordsByDate(date));
            reloadFragment();
        }
    }

    @Override
    public void onClick(View v) {
        countBtnClick--;
        if (countBtnClick >= 1) {
            btn_nextTV.setText(R.string.next);
            russianWord.setText(rus);
            toDayListWords.remove(0);
        } else {
            countBtnClick = 2;
            reloadFragment();
        }
    }
}
