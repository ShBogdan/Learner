package com.bogdan.learner.fragments;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class FrgRepeatDay extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "MyLog";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    String date;
    ArrayList<String[]> toDayListWords;
    String[] randomWord;
    TextView englishWord, transWord, russianWord, tvSumWords, btn_nextTV;
    Button btnNext;
    CardView btn_audio;
    CheckBox favorite;
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

        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        englishWord = (TextView) view.findViewById(R.id.englishWord);
        transWord = (TextView) view.findViewById(R.id.transWord);
        russianWord = (TextView) view.findViewById(R.id.russianWord);
        tvSumWords = (TextView) view.findViewById(R.id.tvSumWords);
        favorite = (CheckBox) view.findViewById(R.id.favorite);
        btnNext = (Button) view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(this);
        btn_audio.setOnClickListener(this);
        favorite.setOnClickListener(this);

        reloadFragment();
        return view;
    }

    /*заполняет view фрагмента*/
    private void reloadFragment() {
        if (toDayListWords.size() != 0) {
            Collections.shuffle(toDayListWords);
            tvSumWords.setText(String.valueOf(toDayListWords.size()));
            btnNext.setText(R.string.answer);
            randomWord = toDayListWords.get(0);
            eng = randomWord[0];
            rus = randomWord[1];
            trn = randomWord[2];

            if (MainActivity.isReversWordPlace) {
                String temp = eng;
                eng = rus;
                rus = temp;
                trn = "";
            }
            voice = eng;
            englishWord.setText(eng);
            transWord.setText(trn);
            russianWord.setText("");
            if (MainActivity.isAutoSpeech) {
                MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
            }

            Log.d(LOG_TAG, Arrays.toString(randomWord));
            favorite.setChecked(false);
            favorite.setChecked(null != randomWord[4] && !randomWord[4].equals("false"));
            Log.d(LOG_TAG, String.valueOf((randomWord[4])));

        } else {
            toDayListWords = new ArrayList<>(DBHelper.getDbHelper(getActivity()).getListWordsByDate(date));
            reloadFragment();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.favorite:
                if (favorite.isChecked()) {
                    DBHelper.getDbHelper(getActivity()).setFavorite("true", randomWord[3]);
                    randomWord[4] = "true";
                } else {
                    DBHelper.getDbHelper(getActivity()).setFavorite("false", randomWord[3]);
                    randomWord[4] = "false";
                }
//                MainActivity.isBaseChanged = true;
                break;
        }
    }
}
