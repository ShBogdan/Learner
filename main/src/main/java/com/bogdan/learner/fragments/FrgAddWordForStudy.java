package com.bogdan.learner.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

public class FrgAddWordForStudy extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "FrgAddWordForStudy";
    private DBHelper dayLibrary;
    private String[] word;
    LinearLayout btn_know, btn_unknown;
    ImageButton btn_audio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_word_for_study, null);
        dayLibrary = DBHelper.getDbHelper(getActivity());

        btn_know = (LinearLayout) view.findViewById(R.id.btn_know);
        btn_know.setOnClickListener(this);
        btn_unknown = (LinearLayout) view.findViewById(R.id.btn_unknown);
        btn_unknown.setOnClickListener(this);
        btn_audio = (ImageButton) view.findViewById(R.id.btn_audio);
        btn_audio.setOnClickListener(this);

        try {
            word = dayLibrary.getRandomWord();
            TextView tv_english = (TextView) view.findViewById(R.id.tv_english);
            tv_english.setText(word[0]);
            TextView tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
            tv_transcription.setText(word[2]);
            TextView tv_russian = (TextView) view.findViewById(R.id.tv_russian);
            tv_russian.setText(word[1]);
        } catch (NullPointerException nullPointerException) {
            getFragmentManager().beginTransaction().
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
        switch (v.getId()) {
            case R.id.btn_know:
                dayLibrary.isLearnWord(false);
                reloadFragment();
                break;

            case R.id.btn_unknown:
                dayLibrary.isLearnWord(true);
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


