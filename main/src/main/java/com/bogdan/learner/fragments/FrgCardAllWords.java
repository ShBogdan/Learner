package com.bogdan.learner.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
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
    int clickCount = 0;

    LinearLayout btn_audio;
    TextView tv_english, tv_russian, tv_transcription, tv_sumWords;
    LinearLayout lay_known, lay_unknown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_card_all_words, null);
        sp = getActivity().getSharedPreferences(FILE_NAME_WORDS, Context.MODE_PRIVATE);

        lay_known = (LinearLayout) view.findViewById(R.id.lay_known);
        lay_known.setClickable(true);
        lay_unknown = (LinearLayout) view.findViewById(R.id.lay_unknown);
        btn_audio = (LinearLayout) view.findViewById(R.id.btn_audio);

        lay_known.setOnClickListener(this);
        lay_unknown.setOnClickListener(this);
        btn_audio.setOnClickListener(this);

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

        readFile();
        if (arrayWords.size() == 0) {
            createDataForFile();
            writeFile();
        }
        inflateView();
        return view;
    }

    void createDataForFile() {
        wordsInToFile = new HashSet<>();
        for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
            wordsInToFile.add(Arrays.toString(el));
            Log.d(LOG_TAG, Arrays.toString(el));
            Log.d(LOG_TAG, String.valueOf(el.length));
        }
    }

    void writeFile() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("words", wordsInToFile);
        editor.apply();
    }

    void readFile() {
        arrayWords = new ArrayList<>();
        if (sp.contains("words")) {
            wordsFromFile = sp.getStringSet("words", null);
            if (wordsFromFile == null || wordsFromFile.size() == 0)
                return;
        } else
            return;
        for (String el : wordsFromFile) {
            String[] word = el.substring(1, el.length() - 1).split(", ");
            Log.d(LOG_TAG, Arrays.toString(word));
            Log.d(LOG_TAG, word.length+"");
            arrayWords.add(word);
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
            case R.id.lay_known:
                if(clickCount == 0)
                    removeWordFromFile();
                inflateView();
                break;
            case R.id.lay_unknown:
                inflateView();
                break;
            case R.id.btn_audio:
                toSpeech.speak(tv_english.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                break;
        }
    }

    void inflateView() {
        if (arrayWords.size() == 0) {
            createDataForFile();
            writeFile();
            readFile();
        }

        if (arrayWords.size() != 0){
            if(clickCount == 0){
                Random random = new Random();
                randomIndexWord = random.nextInt((arrayWords.size()));
                randomWord = arrayWords.get(randomIndexWord);

                String eng = randomWord[0];
                String trans = randomWord[randomWord.length-3];

                tv_english.setText(eng);
                tv_russian.setText("");
                tv_transcription.setText(trans);
                tv_sumWords.setText(arrayWords.size() + "/" + DBHelper.getDbHelper(getActivity()).learnedWords.size());
            }
            if(clickCount == 1){
                String rus = randomWord[1];
                if(randomWord.length == 6)
                    rus = randomWord[1] + ", " + randomWord[2];
                if(randomWord.length == 7)
                    rus = randomWord[1] + ", " + randomWord[2] + ", " + randomWord[3];
                if(randomWord.length == 8)
                    rus = randomWord[1] + ", " + randomWord[2] + ", " + randomWord[3] + ", " + randomWord[4];
                tv_russian.setText(rus);
                clickCount = 0;
                return;
            }
            clickCount++;

        } else {
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), R.string.no_words, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (wordsFromFile == null)
            writeFile();
    }
}
