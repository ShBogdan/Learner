package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.bogdan.learner.DayLibrary;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;

public class FrgRepeatToDay extends Fragment {
    private final String LOG_TAG = ":::::FrgLearnToDay:::::";

    ArrayList<String[]> toDayListWords;
    ArrayList<String> lettersEngWord;
    StringBuilder answer;
    String[] word;
    String englishWord;
    String russianWord;
    String transWord;

    Handler handler;
    boolean onCreate;
    boolean onResume;
    boolean onDestroy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_repeat_to_day, null);
        onCreate = true;
        onResume = false;

        if(toDayListWords == null || toDayListWords.size() == 0){
            toDayListWords = new ArrayList<>(new DayLibrary(getActivity()).getListWordsByDate(MainActivity.toDayDate));
        }
        returnRandomWord(toDayListWords);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (onCreate){
            drawTheWord();}
        if(onResume){
            reloadFragment();}
        onCreate = false;
        onDestroy = false;
        onResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        onResume = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroy = true;
        lettersEngWord = null;

    }

    /**Рисует кнопки для ответа*/
    protected void drawTheWord(){
        int buttonSize = 100;
        handler = new Handler();
        /*Создаем Макет*/
        LinearLayout linearLayoutMain = (LinearLayout)getActivity().findViewById(R.id.testLayout);
        LinearLayout linearLayoutInnerButtonLine_1 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout linearLayoutInnerButtonLine_2 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout linearLayoutInnerButtonLine_3 = new LinearLayout(getActivity());
        linearLayoutInnerButtonLine_3.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        /*Создаем кнопки*/
        ViewGroup.LayoutParams btnViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnViewParams.width  = buttonSize;
        btnViewParams.height = buttonSize;

        final TextView txtAnswer = (TextView)getActivity().findViewById(R.id.russianWord);
        txtAnswer.setText(russianWord.toLowerCase());
        txtAnswer.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
        txtAnswer.setTextColor(Color.parseColor("#000000"));

        final TextView tvAnswer = (TextView)getActivity().findViewById(R.id.answerBtn);
        tvAnswer.setText("");
        tvAnswer.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
        tvAnswer.setTag("com.bogdan.learner.fragments.answer");

        int countLetter = 0;
        final ArrayList<String[]> deletedBtn = new ArrayList<>();
        for (String x : lettersEngWord) {
            countLetter++;
            final Button btnLater = new Button(getActivity());
            btnLater.setId(countLetter);
            btnLater.setText(x);
            btnLater.setTag(x);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String letter = v.getTag().toString();
                    if (lettersEngWord.contains(letter)) {
                        lettersEngWord.remove(letter);
                        btnLater.setEnabled(false);
                        deletedBtn.add(new String[]{letter, String.valueOf((int) btnLater.getId())});
                        answer.append(letter);
                        tvAnswer.setText(answer);
                        if(englishWord.equals(answer.toString())){
                            Toast.makeText(getActivity(), "Правильно", Toast.LENGTH_SHORT).show();
                            tvAnswer.setEnabled(false);
                            tvAnswer.setTextColor(Color.parseColor("#000000"));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (toDayListWords.size() != 0) {
                                        toDayListWords.remove(0);
                                    } else {
                                        Toast.makeText(getActivity(), "Вы повторили все слова", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.d(LOG_TAG, onDestroy +"");
                                    if(!onDestroy){
                                        reloadFragment();}
                                }
                            }, 1500);
                        } if (englishWord.length() == answer.length() && !englishWord.equals(answer.toString())) {
                            tvAnswer.setTextColor(Color.parseColor("#E63434"));
                        }
                    }
                    /*вернуть кнопку*/
                    if(v.getTag() == "com.bogdan.learner.fragments.answer" && answer.length()>=1){
                        if (englishWord.length() == answer.length()) {
                            tvAnswer.setTextColor(Color.parseColor("#000000"));
                        }
                        if(answer.length()>=2){
                            String returnLetter = String.valueOf(answer.substring(answer.length() - 1));
                            lettersEngWord.add(returnLetter);
                            Log.d(LOG_TAG, "Вернули букву: " + returnLetter);
                            answer.delete(answer.length() - 1, answer.length());
                            tvAnswer.setText(answer.toString());
                            for(String[] s : deletedBtn) {
                                if(s[0].equals(returnLetter)) {
                                    getActivity().getWindow().getDecorView().findViewById(Integer.parseInt(s[1])).setEnabled(true);
                                    deletedBtn.remove(s);
                                    break;
                                }
                            }
                        }
                        else {
                            lettersEngWord.add(String.valueOf(answer));
                            for(String[] s : deletedBtn) {
                                if(s[0].equals(String.valueOf(answer))) {
                                    getActivity().getWindow().getDecorView().findViewById(Integer.parseInt(s[1])).setEnabled(true);
                                    deletedBtn.remove(s);
                                    break;
                                }
                            }
                            answer.setLength(0);
                            tvAnswer.setText(answer.toString());
                        }
                    }
                }
            };
            tvAnswer.setOnClickListener(onClickListener);
            btnLater.setOnClickListener(onClickListener);

            /*Проверяет ширину экрана и возращает количество кнопок на строчку*/
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            int buttonOnDisplayWidth = metricsB.widthPixels/buttonSize;

            if(countLetter < buttonOnDisplayWidth){
                linearLayoutInnerButtonLine_1.addView(btnLater, btnViewParams);
            }
            if(countLetter >= buttonOnDisplayWidth && countLetter < buttonOnDisplayWidth*2){
                linearLayoutInnerButtonLine_2.addView(btnLater, btnViewParams);
            }
            if(countLetter >= buttonOnDisplayWidth*2){
                linearLayoutInnerButtonLine_3.addView(btnLater, btnViewParams);
            }
        }
        linearLayoutMain.addView(linearLayoutInnerButtonLine_1, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerButtonLine_2, layoutParams);
        linearLayoutMain.addView(linearLayoutInnerButtonLine_3, layoutParams);

    }

    /**Перемешивает!!! и возвращает случайное слово из сегоднешнего списка*/
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
        Collections.shuffle(lettersEngWord);
    }

    public void reloadFragment(){
        Fragment thisFrg = getActivity().getFragmentManager().findFragmentByTag("TAG_FRG_REPEAT_TO_DAY");
        final FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.detach(thisFrg);
        fTrans.attach(thisFrg);
        fTrans.commit();


    }
}

