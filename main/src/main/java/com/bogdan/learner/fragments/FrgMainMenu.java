package com.bogdan.learner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.bogdan.learner.R;

public class FrgMainMenu extends Fragment implements View.OnClickListener{
    private final String TAG = "onClick";
    FragmentListener mCallback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_m, null);
        Button btn_learnToday = (Button) view.findViewById(R.id.btn_learnToday);
        btn_learnToday.setOnClickListener(this);
        Button btn_addNewWord = (Button) view.findViewById(R.id.btn_addMoreWord);
        btn_addNewWord.setOnClickListener(this);
        Button btn_repeat     = (Button) view.findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        mCallback.onButtonSelected(v);
        Log.i(TAG,"Передача с Фрагмента");
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (FragmentListener) activity;
        }catch (ClassCastException cce){
            throw new ClassCastException(activity.toString());
        }
    }
}
