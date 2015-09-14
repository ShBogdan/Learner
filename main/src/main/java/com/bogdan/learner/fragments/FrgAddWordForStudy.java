package com.bogdan.learner.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

public class FrgAddWordForStudy extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "::::FrgAddWordToDay::::";
    private DBHelper dayLibrary;
    private String[] word;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_word_for_study, null);
        dayLibrary = DBHelper.getDbHelper(getActivity());
        try {
            word = dayLibrary.getWord();
            TextView tv_english = (TextView) view.findViewById(R.id.tv_english);
            tv_english.setText(word[0]);
            TextView tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
            tv_transcription.setText(word[1]);
            TextView tv_russian = (TextView) view.findViewById(R.id.tv_russian);
            tv_russian.setText(word[2]);

            Button btn_know = (Button) view.findViewById(R.id.btn_know);
            btn_know.setOnClickListener(this);
            Button btn_unknown = (Button) view.findViewById(R.id.btn_unknown);
            btn_unknown.setOnClickListener(this);
            Button btn_audio = (Button) view.findViewById(R.id.btn_audio);
            btn_audio.setOnClickListener(this);
        } catch (NullPointerException nullPointerException) {
            getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FrgMainMenu()).commit();
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo));
            builder.setTitle("Нет слов для изучения")
                    .setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_know:
                dayLibrary.setWord(false);
                Log.d(LOG_TAG, "знаю");
                reloadFragment();
                break;

            case R.id.btn_unknown:
                dayLibrary.setWord(true);
                Log.d(LOG_TAG, "учить");
                reloadFragment();
                break;

            case R.id.btn_audio:
                break;
        }
    }


    public void reloadFragment() {
        Fragment thisFrg = getActivity().getFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.FrgAddWordForStudy");
        final FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.detach(thisFrg);
        fTrans.attach(thisFrg);
        fTrans.commit();


    }
}


