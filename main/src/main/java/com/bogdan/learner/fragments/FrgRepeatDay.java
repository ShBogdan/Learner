package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;


public class FrgRepeatDay extends Fragment implements View.OnClickListener{
    String date;
    ArrayList<String[]> toDayListWords;
    TextView englishWord, transWord,russianWord, tvSumWords, btn_nextTV;
    LinearLayout btnNext;
    int countBtnClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.frg_repeat_day ,null);
        date = getArguments().getString("day_date");
        toDayListWords = new ArrayList<>(DBHelper.getDbHelper(getActivity()).getListWordsByDate(date));
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

    private void reloadFragment(){
        if(toDayListWords.size()!=0) {
            Collections.shuffle(toDayListWords);
            tvSumWords.setText(String.valueOf(toDayListWords.size()));
            btn_nextTV.setText("Ответ");
            englishWord.setText(toDayListWords.get(0)[0]);
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
        if(countBtnClick >= 1){
            btn_nextTV.setText("Далее");
            russianWord.setText(toDayListWords.get(0)[1]);
            toDayListWords.remove(0);
        }
        else {
            countBtnClick = 2;
            reloadFragment();}
    }
}
