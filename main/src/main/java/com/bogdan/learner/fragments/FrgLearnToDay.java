package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class FrgLearnToDay extends Fragment {
    private final String LOG_TAG = ":::::::::::FrgLearnToDay:::::::::::";
    private int buttonSize = 80;
    ArrayList<String> array = new ArrayList<String>(Arrays.asList("s","t","a","l","a","v","i","s","t","a","x")){};
    StringBuilder answer = new StringBuilder("answer");

    LinearLayout linearLayoutMain;
    LinearLayout linearLayoutInnerAnswer;
    LinearLayout linearLayoutInnerVariant;
    LinearLayout linearLayoutInnerVariant2;
    LinearLayout.LayoutParams layoutParams;

    Display display;
    DisplayMetrics metricsB;
    int amountLettersForLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*Создаем основной Макет*/
        linearLayoutMain = new LinearLayout(getActivity());
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
//        ViewGroup.LayoutParams layoutParamsMain = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        setContentView(linearLayoutMain,layoutParamsMain);
        /*определяем гирину дисплея*/
        display = getActivity().getWindowManager().getDefaultDisplay();
        metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        amountLettersForLayout = metricsB.widthPixels/buttonSize;
        Log.d(LOG_TAG, amountLettersForLayout + "");

        drawVariant();
        return linearLayoutMain;
    }


    protected void drawVariant(){
        /*Создаем вложеный Макет*/
        linearLayoutInnerVariant = new LinearLayout(getActivity());
        linearLayoutInnerVariant.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutInnerVariant2 = new LinearLayout(getActivity());
        linearLayoutInnerVariant2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutInnerAnswer = new LinearLayout(getActivity());
        linearLayoutInnerAnswer.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        /*Создаем вложеный кнопки*/

        ViewGroup.LayoutParams btnViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button btnAnswer = new Button(getActivity());
        btnAnswer.setBackgroundColor(Color.parseColor("#ffafcfff"));
        btnAnswer.setText(answer);
        linearLayoutInnerAnswer.addView(btnAnswer);
        int indexLetter = 0;
        for (String x : array) {
            indexLetter++;
            Log.d(LOG_TAG, indexLetter+"");
            final Button btnLater = new Button(getActivity());
            btnViewParams.width  = buttonSize /*(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())*/;
            btnViewParams.height = buttonSize /*(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())*/;
            btnLater.setText(x);
            btnLater.setTag(x);
            btnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String letter = v.getTag().toString();
                    if(array.contains(letter)){
                        Log.e("MyLOG++++++++++", letter);
//                        linearLayoutInnerVariant.removeView(v);
                        array.remove(letter);
                        btnLater.setEnabled(false);
                        v.setTag("to not used");
                        if(answer.toString().equals("answer")){
                            answer.setLength(0);
                            answer.append(letter);
                            btnAnswer.setText(answer);
                        }else{
                            answer.append(letter);
                            btnAnswer.setText(answer);
                        }
                    }
                }
            });

            if(indexLetter>amountLettersForLayout-1/*1 - вскидка на пиксели между кнопками*/){
                linearLayoutInnerVariant.addView(btnLater, btnViewParams);
            } else {
                linearLayoutInnerVariant2.addView(btnLater, btnViewParams);
            }
        }
        linearLayoutMain.addView(linearLayoutInnerAnswer, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerVariant2, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerVariant, layoutParams);
    }

}
