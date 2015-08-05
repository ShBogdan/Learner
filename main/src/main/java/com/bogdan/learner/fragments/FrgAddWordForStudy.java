package com.bogdan.learner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bogdan.learner.DayLibrary;
import com.bogdan.learner.R;

public class FrgAddWordForStudy extends Fragment implements View.OnClickListener{
    private final  String LOG_TAG = "::::FrgAddWordToDay::::";
    private FragmentListener mCallback;
    private DayLibrary dayLibrary;
    private String[] word;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_word_for_study,null);
        dayLibrary = new DayLibrary(getActivity());
        try {
            word = dayLibrary.getWord();
            TextView tv_english       = (TextView) view.findViewById(R.id.tv_english);
            tv_english.setText(word[0]);
            TextView tv_transcription = (TextView) view.findViewById(R.id.tv_transcription);
            tv_transcription.setText(word[1]);
            TextView tv_russian       = (TextView) view.findViewById(R.id.tv_russian);
            tv_russian.setText(word[2]);
        } catch (NullPointerException nullPointerException) {
            Log.d(LOG_TAG, "Ловим exception");
            getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FrgMainMenu()).commit();
            Toast.makeText(getActivity(), "В базе больше нет слов", Toast.LENGTH_LONG).show();
        }

        Button btn_know         = (Button) view.findViewById(R.id.btn_know);
        btn_know.setOnClickListener(this);
        Button btn_unknown      = (Button) view.findViewById(R.id.btn_unknown);
        btn_unknown.setOnClickListener(this);
        Button btn_audio        = (Button) view.findViewById(R.id.btn_audio);
        btn_audio.setOnClickListener(this);

        /*Костыль*/
        view.setId(R.id.btn_addMoreWord);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_know:
                dayLibrary.setWord(false);
                Log.d(LOG_TAG,"знаю");
                mCallback.onButtonSelected(getView().findViewById(R.id.btn_addMoreWord));
                break;

            case R.id.btn_unknown:
                dayLibrary.setWord(true);
                Log.d(LOG_TAG,"учить");
                mCallback.onButtonSelected(getView().findViewById(R.id.btn_addMoreWord));
                break;

            case R.id.btn_audio:
                break;
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (FragmentListener) activity;
        }catch (ClassCastException cce){
            throw new ClassCastException(activity.toString());
        }
    }
}


