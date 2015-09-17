package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;


public class FrgRepeatDay extends Fragment {
    String date;
    ArrayList<String[]> toDayListWords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.frg_repeat_day ,null);
        date = getArguments().getString("day_date");
        toDayListWords = DBHelper.getDbHelper(getActivity()).getListWordsByDate(date);
        Collections.shuffle(toDayListWords);
        return view;
    }

}
