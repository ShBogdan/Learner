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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FrgRepeatSelectively extends Fragment implements View.OnClickListener {
    FragmentListener mCallback;
    Bundle bundleDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_repeat_selectively, null);
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
        bundleDate = new Bundle();

        return view;
    }


    @Override
    public void onClick(View v) {
//        mCallback.onButtonSelected(v);
        Fragment fr = new FrgListAllWord();
        fr.setArguments(bundleDate);
        bundleDate.putString("day_date", getOldDate(1));
        getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, fr).commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (FragmentListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString());
        }
    }

    /**
     * для получения конкретного дня по дате(метод принимает отрицательные значения)
     */
    private String getOldDate(int days){
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, days);
       return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
       }

}


