package com.bogdan.learner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;

public class FrgRepeatToDay extends Fragment {
    private final String LOG_TAG = ":::::FrgLearnToDay:::::";
    private int buttonSize = 80;
    ArrayList<String[]> toDayListWords = MainActivity.toDayListWords;
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
    FragmentListener mCallback;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        handler = new Handler();
        if(toDayListWords.size()==0){
            Log.d(LOG_TAG, "до объявления " + toDayListWords.size());
            MainActivity.d(getActivity());

            toDayListWords = MainActivity.toDayListWords;
            Log.d(LOG_TAG, "после объявления " + toDayListWords.size());}

        Log.d(LOG_TAG, "итерация "+toDayListWords.size());
        returnRandomWord(toDayListWords);


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
    /**Перемешивает!!! и возвращает случайное слово из сегоднешнего списка*/
    protected void returnRandomWord(ArrayList<String[]>arrayWords){
        if(arrayWords.size()!=0) {
            Collections.shuffle(arrayWords);
            word        = arrayWords.get(0);
            englishWord = word[0];
            russianWord = word[2];
            transWord   = word[1];
            answer      = new StringBuilder();
        }
    }

    protected View drawTheWord(){
        Log.d(LOG_TAG, "РЕСУЕМ ЕЩЕ РАЗ");
        /*Создаем основной Макет*/
        linearLayoutMain = new LinearLayout(getActivity());
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        linearLayoutMain.setId(R.id.btn_learnToday);

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
            Log.d(LOG_TAG, "ПОСЛЕДНЕЕ СЛОВО" + englishWord);
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
                        if(englishWord.equals(answer.toString())){
                            Toast.makeText(getActivity(), "Правильно", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Удаляем" + toDayListWords.get(0)[0]);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (toDayListWords.size() != 0) {
                                        Log.d(LOG_TAG, "до удаления одной позиции " + toDayListWords.size());
                                        toDayListWords.remove(0);
                                        Log.d(LOG_TAG, "Удалили одну позицию " + toDayListWords.size());
                                    } else {
                                        Toast.makeText(getActivity(), "Вы повторили все слова", Toast.LENGTH_SHORT).show();
                                    }
                                    reloadFragment();
                                }
                            }, 1000);
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

    /**Проверяет ширину экрана и делит ее назмер кнопки и возращает количество кнопок на строчку*/
    protected int buttonOnDisplayWidth(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        return metricsB.widthPixels/buttonSize;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lettersEngWord = null;
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
    public void reloadFragment(){
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("TAG_FRG_REPEAT_TO_DAY");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }



}

