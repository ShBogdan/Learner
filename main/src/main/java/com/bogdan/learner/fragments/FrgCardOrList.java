package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.bogdan.learner.R;


public class FrgCardOrList extends Fragment implements View.OnClickListener {
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Button btnCards, btnList;
//    RadioGroup radioGroup;
    RadioButton radioRandom, radioDate, radioAlphabet;
    CheckBox cbKnowWords;
    View.OnClickListener radioListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_card_or_list, null);

        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        editor = sp.edit();


        btnCards = (Button) view.findViewById(R.id.btn_cards);
        btnList = (Button) view.findViewById(R.id.btn_list);
        btnCards.setOnClickListener(this);
        btnList.setOnClickListener(this);

        radioRandom = (RadioButton) view.findViewById(R.id.radioRandom);
        radioDate = (RadioButton) view.findViewById(R.id.radioDate);
        radioAlphabet = (RadioButton) view.findViewById(R.id.radioAlphabet);
        cbKnowWords = (CheckBox) view.findViewById(R.id.cb_know_word);

//        при первом запуске приложения задаем рандом
        if(!sp.contains("how_to_repeat")){
            editor.putString("how_to_repeat", "random").apply();
            radioRandom.toggle();
        }else {
            if(sp.getString("how_to_repeat",null).equals("random"))
                radioRandom.toggle();
            if(sp.getString("how_to_repeat",null).equals("date"))
                radioDate.toggle();
            if(sp.getString("how_to_repeat",null).equals("alphabet"))
                radioAlphabet.toggle();
        }



        if(!sp.contains("add_know_words")) {
            editor.putBoolean("add_know_words", false).apply();
        }
        if(sp.getBoolean("add_know_words", false)){
        }
        if(sp.getBoolean("add_know_words", true)){
            cbKnowWords.toggle();
        }

        cbKnowWords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbKnowWords.isChecked()){
                    editor.putBoolean("add_know_words", true).apply();
                } else
                    editor.putBoolean("add_know_words", false).apply();
            }
        });

        radioRandom.setOnClickListener(onClickRadio());
        radioDate.setOnClickListener(onClickRadio());
        radioAlphabet.setOnClickListener(onClickRadio());



        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fTrans = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.btn_cards:
                fTrans.replace(R.id.fragment_container, new FrgCardAllWords());
                fTrans.addToBackStack(null).commit();
                break;
            case R.id.btn_list:
                fTrans.replace(R.id.fragment_container, new FrgListAllWord());
                fTrans.addToBackStack(null).commit();
                break;
        }
    }

    View.OnClickListener onClickRadio(){
        radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.radioRandom:
                        editor.putString("how_to_repeat", "random");
                        break;
                    case R.id.radioDate:
                        editor.putString("how_to_repeat", "date");
                        break;
                    case R.id.radioAlphabet:
                        editor.putString("how_to_repeat", "alphabet");
                        break;
                }
                editor.apply();
            }
        };
        return radioListener;
    }

}
