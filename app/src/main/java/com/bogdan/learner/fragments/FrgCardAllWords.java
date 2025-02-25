package com.bogdan.learner.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FrgCardAllWords extends Fragment implements View.OnClickListener {

    final String LOG_TAG = "MyLog";
    final String FILE_NAME_WORDS = "card_words";
    final String SETTINGS = "com.bogdan.learner.SETTINGS";
    SharedPreferences sp;
    ArrayList<String[]> arrayWords;
    Set<String> wordsInToFile;
    Set<String> wordsFromFile;
    String[] randomWord;
    String voice;
    int randomIndexWord;
    int clickCount = 0;

    CardView btn_audio;
    CheckBox favorite;
    TextView tv_english, tv_russian, tv_transcription, tv_sumWords;
    Button btn_known, btn_unknown, btn_reset;
    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_card_all_words, null);
        mContext = getActivity();
        sp = mContext.getSharedPreferences(FILE_NAME_WORDS, Context.MODE_PRIVATE);

        btn_known = (Button) view.findViewById(R.id.lay_known);
        btn_known.setClickable(true);
        btn_unknown = (Button) view.findViewById(R.id.lay_unknown);
        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        btn_reset = (Button) view.findViewById(R.id.reset);

        favorite = (CheckBox) view.findViewById(R.id.favorite);
        favorite.setOnClickListener(this);

        btn_known.setOnClickListener(this);
        btn_unknown.setOnClickListener(this);
        btn_audio.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        tv_english = (TextView) view.findViewById(R.id.tv_english);
        tv_russian = (TextView) view.findViewById(R.id.tv_russian);
        tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
        tv_sumWords = (TextView) view.findViewById(R.id.tv_sumWords);

        readFile();
        if (arrayWords.size() == 0) {
            createDataForFile();
            writeFile();
        }
        inflateView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    void createDataForFile() {
        wordsInToFile = new HashSet<>();
        for (String[] el : DBHelper.getDbHelper(getActivity()).learnedWords) {
            wordsInToFile.add(el[0]);
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
            for (String[] _el : DBHelper.getDbHelper(getActivity()).learnedWords) {
                if (_el[0].equals(el)) {
                    arrayWords.add(_el);
                }
            }
        }
    }

    void removeWordFromFile() {
        wordsInToFile = new HashSet<>();
        arrayWords.remove(randomIndexWord);
        for (String[] word : arrayWords) {
            wordsInToFile.add(word[0]);
        }
        writeFile();
    }


    @Override
    public void onClick(View v) {
        var id = v.getId();
        if (id == R.id.lay_known) {
            if (clickCount == 0)
                removeWordFromFile();
            inflateView();
        }
        if (id == R.id.lay_unknown) {
            inflateView();
        }
        if (id == R.id.btn_audio) {
            MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
        }
        if (id == R.id.reset) {
            arrayWords.clear();
            inflateView();
            tv_sumWords.setText(arrayWords.size() + "/" + DBHelper.getDbHelper(getActivity()).learnedWords.size());
        }
        if (id == R.id.favorite) {
            if (favorite.isChecked()) {
                DBHelper.getDbHelper(getActivity()).setFavorite("true", randomWord[3]);
                randomWord[4] = "true";

                Log.d(LOG_TAG, "В избранное");

            } else {
                DBHelper.getDbHelper(getActivity()).setFavorite("false", randomWord[3]);
                randomWord[4] = "false";

                Log.d(LOG_TAG, "Удалить избранное");

            }
        }

    }

    void inflateView() {
        if (arrayWords.size() == 0) {
            createDataForFile();
            writeFile();
            readFile();
        }

        if (arrayWords.size() != 0) {
            if (clickCount == 0) {
                Random random = new Random();
                randomIndexWord = random.nextInt((arrayWords.size()));
                randomWord = arrayWords.get(randomIndexWord);

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
                tv_sumWords.setText(arrayWords.size() + "/" + DBHelper.getDbHelper(getActivity()).learnedWords.size());
            }
            if (clickCount == 1) {
                String rus = randomWord[1];
                //если повторять ру-англ
                if (MainActivity.isReversWordPlace) {
                    rus = randomWord[0];
                    tv_transcription.setText(randomWord[2]);
                }

                tv_russian.setText(rus);
                clickCount = 0;
                return;
            }
            clickCount++;

            if (MainActivity.isAutoSpeech) {
                MainActivity.toSpeech.speak(voice, TextToSpeech.QUEUE_ADD, null);
            }
            Log.d(LOG_TAG, Arrays.toString(randomWord));

            favorite.setChecked(false);
            favorite.setChecked(null != randomWord[4] && !randomWord[4].equals("false"));

        } else {
            getActivity().getSupportFragmentManager().popBackStack();
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
