package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bogdan.learner.DayLibrary;
import com.bogdan.learner.MainActivity;

import java.util.ArrayList;

public class FrgRepeatToDay extends Fragment {
    private final String LOG_TAG = ":::::FrgLearnToDay:::::";
    private int buttonSize = 80;
    ArrayList<String[]> toDayListWords = new ArrayList<>();
    ArrayList<String> lettersEngWord;
    StringBuilder answer;
    String[] word;
    String englishWord;
    String russianWord;
    String transWord;


    LinearLayout linearLayoutMain;
    LinearLayout linearLayoutInnerAnswer;
    LinearLayout linearLayoutInnerButtonLine_1;
    LinearLayout linearLayoutInnerButtonLine_2;
    LinearLayout.LayoutParams layoutParams;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        toDayListWords = new DayLibrary(getActivity()).getListWordsByDate(MainActivity.toDayDate);
        word        = toDayListWords.get(0);
        englishWord = word[0];
        russianWord = word[2];
        transWord   = word[1];
        answer      = new StringBuilder();





        Log.d(LOG_TAG, "OnCreateView");
//        View view = inflater.inflate(R.layout.frg_repeat_to_day, null);
//
//
//        for (String[] el: toDayListWords){
//            Log.d(LOG_TAG, Arrays.asList(el)+"");
//        }
//
//        return view;
//




        return drawTheWord();

    }


    protected View drawTheWord(){
        /*Создаем основной Макет*/
        linearLayoutMain = new LinearLayout(getActivity());
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);

        /*Создаем вложеный Макет*/
        linearLayoutInnerButtonLine_1 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutInnerButtonLine_2 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutInnerAnswer = new LinearLayout(getActivity());
        linearLayoutInnerAnswer.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        /*Создаем вложеные кнопки*/
        if( lettersEngWord == null){
            lettersEngWord = new ArrayList<>();
            for(char c : englishWord.toCharArray()){
                lettersEngWord.add(String.valueOf(c));
            }
        }
        ViewGroup.LayoutParams btnViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button btnAnswer = new Button(getActivity());
        btnAnswer.setBackgroundColor(Color.parseColor("#ffafcfff"));
        btnAnswer.setText(russianWord);
        linearLayoutInnerAnswer.addView(btnAnswer);
        int countLetter = 0;
        for (String x : lettersEngWord) {
            countLetter++;
            final Button btnLater = new Button(getActivity());
            btnViewParams.width  = buttonSize /*(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())*/;
            btnViewParams.height = buttonSize /*(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())*/;
            btnLater.setText(x);
            btnLater.setTag(x);
            btnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String letter = v.getTag().toString();
                    if (lettersEngWord.contains(letter)) {
                        lettersEngWord.remove(letter);
                        btnLater.setEnabled(false);
                        v.setTag("to not used");
                        answer.append(letter);
                        btnAnswer.setText(answer);
                        Log.d(LOG_TAG, englishWord + " " + answer.toString());
                        if(englishWord.equals(answer.toString())){
                            Log.d(LOG_TAG, "ПРАВИЛЬНО"); /*Вызвать фрагмент снова*/
                        }

                    }
                }
            });


            if(countLetter> buttonOnDisplayWidth()-1/*1 - вскидка на пиксели между кнопками*/){
                linearLayoutInnerButtonLine_1.addView(btnLater, btnViewParams);
            } else {
                linearLayoutInnerButtonLine_2.addView(btnLater, btnViewParams);
            }
        }
        linearLayoutMain.addView(linearLayoutInnerAnswer, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerButtonLine_2, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerButtonLine_1, layoutParams);
        return linearLayoutMain;
    }


    protected int buttonOnDisplayWidth(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        return metricsB.widthPixels/buttonSize;
    }

}
