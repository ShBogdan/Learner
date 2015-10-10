package com.bogdan.learner.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FrgCardAllWords extends Fragment implements View.OnClickListener {

    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String FILE_NAME_WORDS = "card_words";
    private final String FILE_NAME_LAST_UPDATE = "last_update";
    SharedPreferences sp;
    TextToSpeech toSpeech;
    ArrayList<String[]> arrayWords;
    Set<String> wordsInToFile;
    Set<String> wordsFromFile;
    String[] randomWord;
    int randomIndexWord;

    Button btn_know, btn_unknown, btn_audio;
    TextView tv_english, tv_russian;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_card_all_words, null);
        sp = getActivity().getSharedPreferences(FILE_NAME_WORDS, Context.MODE_PRIVATE);

        btn_know = (Button) view.findViewById(R.id.btn_know);
        btn_unknown = (Button) view.findViewById(R.id.btn_unknown);
        btn_audio = (Button) view.findViewById(R.id.btn_audio);

        btn_know.setOnClickListener(this);
        btn_unknown.setOnClickListener(this);
        btn_audio.setOnClickListener(this);

        tv_english = (TextView) view.findViewById(R.id.tv_english);
        tv_russian = (TextView) view.findViewById(R.id.tv_russian);

        toSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    toSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        readFile();
        if (arrayWords.size() == 0) {
            createFile();
            writeFile();
        }
        inflateView();
        return view;
    }

    void createFile() {
        wordsInToFile = new HashSet<>();
        for (Map.Entry<Integer, ArrayList<String[]>> el : DBHelper.getDbHelper(getActivity()).uploadDb.entrySet()) {
            if (el.getKey() != 0 && el.getKey() != 1) {
                for (String[] word : el.getValue()) {
                    wordsInToFile.add(Arrays.toString(word));
                }
            }
        }
    }

    void writeFile() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(FILE_NAME_WORDS, wordsInToFile);
        editor.apply();
    }

    void readFile() {
        arrayWords = new ArrayList<>();
        if (sp.contains(FILE_NAME_WORDS)) {
            wordsFromFile = sp.getStringSet(FILE_NAME_WORDS, null);
            if (wordsFromFile == null || wordsFromFile.size() == 0)
                return;
        } else
            return;
        for (String el : wordsFromFile) {
            arrayWords.add(el.substring(1, el.length() - 1).split(", "));
        }
    }

    void removeWordFromFile() {
        wordsInToFile = new HashSet<>();
        arrayWords.remove(randomIndexWord);
        for (String[] word : arrayWords) {
            wordsInToFile.add(Arrays.toString(word));
        }
        writeFile();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_know:
                removeWordFromFile();
                inflateView();
                break;
            case R.id.btn_unknown:
                inflateView();
                break;
            case R.id.btn_audio:
                toSpeech.speak(tv_english.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                break;
        }
    }

    void inflateView() {
        if (arrayWords.size() == 0) {
            createFile();
            writeFile();
            readFile();
        }
        Random random = new Random();
        randomIndexWord = random.nextInt((arrayWords.size()));
        randomWord = arrayWords.get(randomIndexWord);
        tv_english.setText(randomWord[0]);
        tv_russian.setText(randomWord[1]);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (wordsFromFile == null)
            writeFile();
    }

    int checkLastUpdate() {
        SharedPreferences sp = getActivity().getSharedPreferences(FILE_NAME_LAST_UPDATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(FILE_NAME_LAST_UPDATE, 1);
        editor.apply();
        return 1;
    }

}
