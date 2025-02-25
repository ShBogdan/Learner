package com.bogdan.learner.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

public class FrgAddWordForStudy extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "MyLog";
    private DBHelper dayLibrary;
    private String[] word;
    Button btn_know, btn_unknown;
    TextView tv_english, tv_transcription, tv_russian, tvSumWords;
    CardView btn_audio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_word_for_study, null);
        dayLibrary = DBHelper.getDbHelper(getActivity());


        btn_know = (Button) view.findViewById(R.id.btn_know);
        btn_know.setOnClickListener(this);
        btn_unknown = (Button) view.findViewById(R.id.btn_unknown);
        btn_unknown.setOnClickListener(this);
        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        btn_audio.setOnClickListener(this);


        try {
            int size;
            if (dayLibrary.getListWordsByDate(MainActivity.toDayDate) == null) {
                size = 0;
            } else {
                size = dayLibrary.getListWordsByDate(MainActivity.toDayDate).size();
            }
            word = dayLibrary.getRandomWord();
            tv_english = (TextView) view.findViewById(R.id.tv_english);
            tv_english.setText(word[0]);
            tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
            tv_transcription.setText(word[2]);
            tv_russian = (TextView) view.findViewById(R.id.tv_russian);
            tv_russian.setText(word[1]);
            tvSumWords = (TextView) view.findViewById(R.id.tvSumWords);
            tvSumWords.setText(getResources().getText(R.string.add_to_study) + " " + size);

            if (MainActivity.isAutoSpeech) {
                MainActivity.toSpeech.speak(word[0], TextToSpeech.QUEUE_ADD, null);
            }
        } catch (NullPointerException nullPointerException) {
            getActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new FrgMainMenu()).
                    addToBackStack(null).commit();

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo));
            builder.setTitle(R.string.no_words_for_study)
                    .setCancelable(true);

            AlertDialog alert = builder.create();
            alert.show();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Integer wordsAllowed = 1000;
        if (!MainActivity.isPremium) {
            wordsAllowed = (DBHelper.getDbHelper(getActivity()).getListWordsByDate(MainActivity.toDayDate) == null)
                    ? 0 : DBHelper.getDbHelper(getActivity()).getListWordsByDate(MainActivity.toDayDate).size();
        }
        var id = v.getId();
        if (id == R.id.btn_know) {
            dayLibrary.isLearnWord(false);
            reloadFragment();
        }

        if (id == R.id.btn_unknown) {

            if (!MainActivity.isPremium && MainActivity.isTrialTimeEnd && wordsAllowed > 4) {
                Toast.makeText(getActivity(), R.string.more_than_6, Toast.LENGTH_SHORT).show();
            } else {
                dayLibrary.isLearnWord(true);
                reloadFragment();
            }
        }

        if (id == R.id.btn_audio) {
            MainActivity.toSpeech.speak(tv_english.getText().toString(), TextToSpeech.QUEUE_ADD, null);
        }

    }


    public void reloadFragment() {
        Fragment thisFrg = getActivity().getSupportFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.FrgAddWordForStudy");
        final FragmentTransaction fTrans = getActivity().getSupportFragmentManager().beginTransaction();
        fTrans.detach(thisFrg);
        fTrans.attach(thisFrg);
        fTrans.commit();
    }
}


