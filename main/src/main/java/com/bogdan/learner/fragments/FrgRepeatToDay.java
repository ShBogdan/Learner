package com.bogdan.learner.fragments;

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

import com.bogdan.learner.DayLibrary;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;

public class FrgRepeatToDay extends Fragment {
    private final String LOG_TAG = ":::::FrgLearnToDay:::::";
    private int buttonSize = 80;
    ArrayList<String[]> toDayListWords;
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
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OnCreateView");
        handler = new Handler();
        if(toDayListWords == null || toDayListWords.size() == 0){
            toDayListWords = new ArrayList<>(new DayLibrary(getActivity()).getListWordsByDate(MainActivity.toDayDate));
        }

        Log.d(LOG_TAG, "итерация " + toDayListWords.size());
        returnRandomWord(toDayListWords);
        return drawTheWord();



//        View view = inflater.inflate(R.layout.frg_repeat_to_day, null);
//
//
//        for (String[] el: toDayListWords){
//            Log.d(LOG_TAG, Arrays.asList(el)+"");
//        }
//
//        return view;
//
//

    }
    /**Перемешивает!!! и возвращает случайное слово из сегоднешнего списка*/

    protected View drawTheWord(){
        /*Создаем Макет*/
        linearLayoutMain = new LinearLayout(getActivity());
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        linearLayoutMain.setId(R.id.btn_learnToday);
        linearLayoutMain.setTag("MYTAG");
        linearLayoutMain.setBackgroundColor(getResources().getColor(R.color.my_background));


        /*Создаем вложеный поля*/
        linearLayoutInnerButtonLine_1 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutInnerButtonLine_2 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutInnerAnswer = new LinearLayout(getActivity());
        linearLayoutInnerAnswer.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        /*Создаем кнопки*/
        ViewGroup.LayoutParams btnViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button btnAnswer = new Button(getActivity());
        btnAnswer.setBackgroundColor(getResources().getColor(R.color.my_background));
        btnAnswer.setText(russianWord);
        btnAnswer.setTag("answerButton");
        linearLayoutInnerAnswer.addView(btnAnswer);

        int countLetter = 0;
        for (String x : lettersEngWord) {
            countLetter++;
            final Button btnLater = new Button(getActivity());
            btnViewParams.width  = buttonSize /*(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())*/;
            btnViewParams.height = buttonSize /*(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())*/;
            btnLater.setText(x);
            btnLater.setTag(x);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String letter = v.getTag().toString();
                    if (lettersEngWord.contains(letter)) {
                        lettersEngWord.remove(letter);
                        Log.d(LOG_TAG, "Тег кнопки: " + btnLater.getTag());
                        btnLater.setEnabled(false);
//                        v.setTag("to not used");
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
                            }, 0);
                        } if (englishWord.length() == answer.length() && !englishWord.equals(answer.toString())) {
                            btnAnswer.setTextColor(Color.parseColor("#E63434"));
                        }
                    }
                    if(v.getTag() == "answerButton" && answer.length()>=1){
                        if (englishWord.length() == answer.length()) {
                            btnAnswer.setTextColor(Color.parseColor("#000000"));
                        }
                        if(answer.length()>=2){
                            String returnLetter = String.valueOf(answer.substring(answer.length() - 1));
                            lettersEngWord.add(returnLetter);
                            Log.d(LOG_TAG, "Вернули букву: " + returnLetter);
                            answer.delete(answer.length() - 1, answer.length());
                            btnAnswer.setText(answer.toString());
                            for (String l : lettersEngWord){
                                if(l.equals(returnLetter))
                                    getActivity().getWindow().getDecorView().findViewWithTag(returnLetter).setEnabled(true);
                                getActivity().getWindow().getDecorView().findViewWithTag(returnLetter).setTag("temp");

                                Log.d(LOG_TAG, "Вернули кнопку");
                            }
                        }
                        else {
                            lettersEngWord.add(String.valueOf(answer));
                            getActivity().getWindow().getDecorView().findViewWithTag(answer.toString()).setEnabled(true);
                            for (String l : lettersEngWord){
                                if(l.equals(answer.toString()))
                                    getActivity().getWindow().getDecorView().findViewWithTag(answer.toString()).setEnabled(true);
                                getActivity().getWindow().getDecorView().findViewWithTag(answer.toString()).setTag("temp");
                                Log.d(LOG_TAG, "Вернули кнопку");
                            }
                            answer.setLength(0);
                            btnAnswer.setText(answer.toString());

                        }
                    }
                }
            };
            btnAnswer.setOnClickListener(onClickListener);
            btnLater.setOnClickListener(onClickListener);

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

    protected void returnRandomWord(ArrayList<String[]>arrayWords){
        Collections.shuffle(arrayWords);
        word        = arrayWords.get(0);
        englishWord = word[0];
        russianWord = word[2];
        transWord   = word[1];
        answer      = new StringBuilder();
        if( lettersEngWord == null){
            lettersEngWord = new ArrayList<>();
            for(char c : englishWord.toCharArray()){
                lettersEngWord.add(String.valueOf(c));
            }
        }
    }

    /**Проверяет ширину экрана и делит ее назмер кнопки и возращает количество кнопок на строчку*/
    protected int buttonOnDisplayWidth(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        return metricsB.widthPixels/buttonSize;
    }

    public void reloadFragment(){
        Fragment thisFrg = getFragmentManager().findFragmentByTag("TAG_FRG_REPEAT_TO_DAY");
        final FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.detach(thisFrg);
        fTrans.attach(thisFrg);
        fTrans.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lettersEngWord = null;
    }
}

