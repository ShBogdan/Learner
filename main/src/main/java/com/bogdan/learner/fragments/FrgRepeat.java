package com.bogdan.learner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.bogdan.learner.R;

public class FrgRepeat extends Fragment implements View.OnClickListener{
    FragmentListener mCallback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_repeat,null);
        Button btn_repeat_day1 = (Button) view.findViewById(R.id.btn_repeat_day1);
        btn_repeat_day1.setOnClickListener(this);
        Button btn_repeat_day2 = (Button) view.findViewById(R.id.btn_repeat_day2);
        btn_repeat_day2.setOnClickListener(this);
        Button btn_repeat_day3 = (Button) view.findViewById(R.id.btn_repeat_day3);
        btn_repeat_day3.setOnClickListener(this);
        Button btn_repeat_day21 = (Button) view.findViewById(R.id.btn_repeat_day21);
        btn_repeat_day21.setOnClickListener(this);
        Button btn_repeat_day90 = (Button) view.findViewById(R.id.btn_repeat_day90);
        btn_repeat_day90.setOnClickListener(this);
        Button btn_calendar = (Button) view.findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(this);
        Button btn_all_words = (Button) view.findViewById(R.id.btn_all_words);
        btn_all_words.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
                mCallback.onButtonSelected(v);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (FragmentListener) activity;
        }catch (ClassCastException cce){
            throw new ClassCastException(activity.toString());
        }

    }
}


