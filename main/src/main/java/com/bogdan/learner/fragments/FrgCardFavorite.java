package com.bogdan.learner.fragments;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class FrgCardFavorite extends Fragment implements View.OnClickListener {

    final String LOG_TAG = "::::FrgListAllWord::::";
    final String FILE_NAME_WORDS = "favorites_words";
    final String SETTINGS = "com.bogdan.learner.SETTINGS";
    SharedPreferences sp;
    String voice;
    ArrayList<String[]> arrayWords;
    String[] randomWord;
    int clickCount = 0;
    CheckBox favorite;
    CardView btn_audio;
    TextView tv_english, tv_russian, tv_transcription, tv_sumWords;
    Button btn_next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_card_favorite, null);

        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        btn_next  = (Button) view.findViewById(R.id.btn_next);
        favorite  = (CheckBox) view.findViewById(R.id.favorite);

        favorite. setOnClickListener(this);
        btn_audio.setOnClickListener(this);
        btn_next. setOnClickListener(this);

        tv_english       = (TextView) view.findViewById(R.id.tv_english);
        tv_russian       = (TextView) view.findViewById(R.id.tv_russian);
        tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
        tv_sumWords      = (TextView) view.findViewById(R.id.tv_sumWords);

        inflateView();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                inflateView();
                break;
            case R.id.btn_audio:
                MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.favorite:
                if(favorite.isChecked()){
                    DBHelper.getDbHelper(getActivity()).setFavorite("true", randomWord[3]);
                    randomWord[4] = "true";
                }else {
                    DBHelper.getDbHelper(getActivity()).setFavorite("false", randomWord[3]);
                    randomWord[4] = "false";
                }
//                MainActivity.isBaseChanged = true;
                break;
        }
    }

    void inflateView() {
        btn_next.setText(R.string.answer);

        if(arrayWords == null){
//            arrayWords = DBHelper.getDbHelper(getActivity()).getListFavoriteWords();
            arrayWords = new ArrayList<>();
            for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
                if(null != el[4] && !el[4].equals("false")){
                  arrayWords.add(el);
                }
            }
        }

        if (arrayWords.size() == 0) {
//            arrayWords = DBHelper.getDbHelper(getActivity()).getListFavoriteWords();
            for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
                if(null != el[4] && !el[4].equals("false")){
                    arrayWords.add(el);
                }
            }
            Collections.shuffle(arrayWords);
        }

        if (arrayWords.size() != 0){
            if(clickCount == 0){
                randomWord = arrayWords.get(0);
                String eng = randomWord[0];
                String trans = randomWord[2];

                //если повторять ру-англ
                if (MainActivity.isReversWordPlace) {
                    eng = randomWord[1];
                    trans = "";
                }
                voice = eng;

                tv_english.setText(eng);
                tv_russian.setText("");
                tv_transcription.setText(trans);
                tv_sumWords.setText(String.valueOf(arrayWords.size()));
            }
            if(clickCount == 1){
                btn_next.setText(R.string.next);
                String rus = randomWord[1];
                //если повторять ру-англ
                if (MainActivity.isReversWordPlace) {
                    rus = randomWord[0];
                    tv_transcription.setText(randomWord[2]);
                }

                tv_russian.setText(rus);
                clickCount = 0;
                arrayWords.remove(0);
                return;
            }
            clickCount++;

            if(MainActivity.isAutoSpeech){
                MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
            }
            favorite.setChecked(true);

        } else {
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), R.string.no_favor, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }
}
