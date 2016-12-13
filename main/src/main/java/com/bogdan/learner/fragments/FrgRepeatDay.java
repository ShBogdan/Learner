package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class FrgRepeatDay extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "MyLog";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    SharedPreferences sp;
    String date;
    boolean reversWord;
    boolean autoSpeech;
    ArrayList<String[]> toDayListWords;
    TextView englishWord, transWord, russianWord, tvSumWords, btn_nextTV;
    Button btnNext;
    CardView btn_audio;
    TextToSpeech toSpeech;
    int countBtnClick;
    String voice;
    String eng;
    String rus;
    String trn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_repeat_day, null);

//      счет кликов 1.показывает ответ 2.переход к следующему слову
        countBtnClick = 2;

//      получаем дату ранее изученных слов
        date = getArguments().getString("com.bogdan.learner.fragments.day_date");
        toDayListWords = new ArrayList<>(DBHelper.getDbHelper(getActivity()).getListWordsByDate(date));

//      установлен ли пункт менять местами перевод
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        reversWord = sp.getBoolean("changeWordPlace", false);
        autoSpeech = sp.getBoolean("autoSpeech", false);

        Log.d(LOG_TAG, "FrgRepeatDay: " + reversWord);


        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        englishWord = (TextView) view.findViewById(R.id.englishWord);
        transWord = (TextView) view.findViewById(R.id.transWord);
        russianWord = (TextView) view.findViewById(R.id.russianWord);
        tvSumWords = (TextView) view.findViewById(R.id.tvSumWords);

        btnNext = (Button) view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        btn_audio.setOnClickListener(this);
//        toSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener(){
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR){
//                    toSpeech.setLanguage(Locale.ENGLISH);
//                }
//            }
//        });
        reloadFragment();
        return view;
    }

    /*заполняет view фрагмента*/
    private void reloadFragment() {
        if (toDayListWords.size() != 0) {
            Collections.shuffle(toDayListWords);
            tvSumWords.setText(String.valueOf(toDayListWords.size()));
            btnNext.setText(R.string.answer);
            eng = toDayListWords.get(0)[0];
            rus = toDayListWords.get(0)[1];
            trn = toDayListWords.get(0)[2];
            if (reversWord) {
                String temp = eng;
                eng = rus;
                rus = temp;
                trn = "";
            }
            voice = eng;
            englishWord.setText(eng);
            transWord.setText(trn);
            russianWord.setText("");
            if(autoSpeech){
                MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
            }
        } else {
            toDayListWords = new ArrayList<>(DBHelper.getDbHelper(getActivity()).getListWordsByDate(date));
            reloadFragment();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                countBtnClick--;
                if (countBtnClick >= 1) {
                    btnNext.setText(R.string.next);
                    russianWord.setText(rus);
                    transWord.setText(toDayListWords.get(0)[2]);
                    toDayListWords.remove(0);
                } else {
                    countBtnClick = 2;
                    reloadFragment();
                }
                break;
            case R.id.btn_audio:
                MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
                break;
        }
    }
}
