package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FrgLearnToDay extends Fragment {
    ArrayList<String> array = new ArrayList<String>(){{add("n");add("a");add("p");add("o");add("a");add("r");add("a");add("m");}};
    StringBuilder answer = new StringBuilder("answer");

    LinearLayout linearLayoutMain;
    LinearLayout linearLayoutInnerAnswer;
    LinearLayout linearLayoutInnerVariant;
    LinearLayout.LayoutParams layoutParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*Создаем основной Макет*/
        linearLayoutMain = new LinearLayout(getActivity());
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
//        ViewGroup.LayoutParams layoutParamsMain = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        setContentView(linearLayoutMain,layoutParamsMain);
        drawVariant();
        return linearLayoutMain;
    }


    protected void drawVariant(){
        /*Создаем вложеный Макет*/
        linearLayoutInnerVariant = new LinearLayout(getActivity());
        linearLayoutInnerVariant.setOrientation(LinearLayout.HORIZONTAL);
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
        for (String x : array) {
            Button btnLater = new Button(getActivity());
            btnViewParams.width  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            btnViewParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            btnLater.setText(x);
            btnLater.setTag(x);
            btnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String letter = v.getTag().toString();
                    if(array.contains(letter)){
                        Log.e("MyLOG++++++++++", letter);
                        linearLayoutInnerVariant.removeView(v);
                        array.remove(letter);
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
            linearLayoutInnerVariant.addView(btnLater, btnViewParams);
        }
        linearLayoutMain.addView(linearLayoutInnerAnswer, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerVariant, layoutParams);
    }

}
