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
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FrgCardFavorite extends Fragment implements View.OnClickListener {

    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String FILE_NAME_WORDS = "favorites_words";
    SharedPreferences sp;
    TextToSpeech toSpeech;
    ArrayList<String[]> arrayWords;
    Set<String> wordsInToFile;
    Set<String> wordsFromFile;
    String[] randomWord;
    //    int randomIndexWord;
    int clickCount = 0;

    CardView btn_audio;
    TextView tv_english, tv_russian, tv_transcription, tv_sumWords;
    Button btn_next, btn_remove;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_card_favorite, null);
        sp = getActivity().getSharedPreferences(FILE_NAME_WORDS, Context.MODE_PRIVATE);

        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        btn_next = (Button) view.findViewById(R.id.btn_next);
        btn_remove = (Button) view.findViewById(R.id.btn_remove);

        btn_audio.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_remove.setOnClickListener(this);

        tv_english = (TextView) view.findViewById(R.id.tv_english);
        tv_russian = (TextView) view.findViewById(R.id.tv_russian);
        tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
        tv_sumWords = (TextView) view.findViewById(R.id.tv_sumWords);

        toSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    toSpeech.setLanguage(Locale.ENGLISH);
            }
        });


        createDataForFile();
        writeFile();

        inflateView();
        return view;
    }

    void createDataForFile() {
        wordsInToFile = new HashSet<>();
        for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
            if(Boolean.parseBoolean(el[4]))
                wordsInToFile.add(el[0]);
        }
    }

    void writeFile() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("words", wordsInToFile);
        editor.apply();
    }

    void readFile() {
        //        arrayWords = new ArrayList<>();
        //        if (sp.contains("words")) {
        wordsFromFile = sp.getStringSet("words", null);
        //            if (wordsFromFile == null || wordsFromFile.size() == 0){
        //                getFragmentManager().popBackStack();
        //                Toast.makeText(getActivity(), R.string.no_words, Toast.LENGTH_SHORT).show();
        //                }
        //        } else
        //            return;
        for (String el : wordsFromFile) {
            for (String[] _el : DBHelper.getDbHelper(getActivity()).learnedWords) {
                if(_el[0].equals(el)){
                    arrayWords.add(_el);
                }
            }
            Collections.shuffle(arrayWords);
        }
    }

    void removeWordFromFile() {
        wordsInToFile = new HashSet<>();

        for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
            if(el[0].equals(arrayWords.get(0)[0])){
               el[4] = "null";
            }
        }
        arrayWords.remove(0);
        writeFile();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_remove:
                removeWordFromFile();
                break;
            case R.id.btn_next:
                inflateView();
                break;
            case R.id.btn_audio:
                toSpeech.speak(tv_english.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                break;
        }
    }

    void inflateView() {
        if(arrayWords == null)
            arrayWords = new ArrayList<>();

        if (arrayWords.size() == 0) {
            createDataForFile();
            writeFile();
            readFile();
        }

        if (arrayWords.size() != 0){
            if(clickCount == 0){

                randomWord = arrayWords.get(0);

                String eng = randomWord[0];
                String trans = randomWord[randomWord.length-3];

                tv_english.setText(eng);
                tv_russian.setText("");
                tv_transcription.setText(trans);
                tv_sumWords.setText(String.valueOf(arrayWords.size()));
            }
            if(clickCount == 1){
                String rus = randomWord[1];
                tv_russian.setText(rus);
                clickCount = 0;
                arrayWords.add(arrayWords.get(0));
                arrayWords.remove(0);
                for (int i = 0; i < arrayWords.size(); i++) {

                    Log.d("MyLog", String.valueOf(arrayWords.get(i)[0]));
                }
                return;
            }
            clickCount++;


        } else {
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), R.string.no_favor, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (wordsFromFile == null)
            writeFile();
    }
}
