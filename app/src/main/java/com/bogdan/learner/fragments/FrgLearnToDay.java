package com.bogdan.learner.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;
import android.widget.Button;


import java.util.ArrayList;
import java.util.Collections;

public class FrgLearnToDay extends Fragment {
    final String LOG_TAG = "MyLog";
    ArrayList<String[]> wordsForFrgLetters;
    ArrayList<String[]> toDayListWords;
    ArrayList<String[]> wordsForFrgRepeat;
    ArrayList<String> lettersEngWord;
    StringBuilder answer;
    String[] word;
    String englishWord;
    String russianWord;
    String transWord;
    int countAttempt;
    int count; // для чередования фрагмента карточа - набор слова
    CardView btn_audio;
    Handler handler;
    boolean onCreate;
    boolean onResume;
    boolean onDestroy;
    boolean onStop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OnCreate");
        View view;
        toDayListWords = DBHelper.getDbHelper(getActivity()).getListWordsByDate(MainActivity.toDayDate);
        onCreate = true;
        onResume = false;

        if (wordsForFrgLetters == null || wordsForFrgLetters.size() == 0) {
            Collections.shuffle(toDayListWords);
            wordsForFrgLetters = new ArrayList<>(toDayListWords);
            wordsForFrgRepeat = new ArrayList<>();
            for (int i = 0; i < MainActivity.wordAlternation; i++) {
                if (wordsForFrgLetters.size() > i) {
                    wordsForFrgRepeat.add(wordsForFrgLetters.get(i));
                }
            }
            count = wordsForFrgRepeat.size();
        }
        if (count == 0) {
            for (int i = 0; i < MainActivity.wordAlternation; i++) {
                if (wordsForFrgLetters.size() > i) {
                    wordsForFrgRepeat.add(wordsForFrgLetters.get(i));
                }
            }
            count = wordsForFrgRepeat.size();
        }

        Log.d(LOG_TAG, "wordsForFrgRepeat  " + wordsForFrgRepeat.size());

        if (wordsForFrgRepeat.size() != 0) {
            view = inflater.inflate(R.layout.frg_repeat_to_day_word, null);
            returnRandomWord(wordsForFrgRepeat);
        } else {
            view = inflater.inflate(R.layout.frg_repeat_to_day_letters, null);
            returnRandomWord(wordsForFrgLetters);
        }

        btn_audio = (CardView) view.findViewById(R.id.btn_audio);
        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.toSpeech.speak(englishWord, TextToSpeech.QUEUE_ADD, null);
            }
        });
        return view;
    }


    /**
     * Рисует кнопки для ответа
     */
    protected void drawTheLetters() {
        int buttonSize = dpToPx(48);
        int buttonMargin = 2;
        int textSize = 18;

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                Log.d("MyLog", "SCREENLAYOUT_SIZE_XLARGE");
                buttonSize = dpToPx(75);
                buttonMargin = 6;
                textSize = 22;
                break;

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                Log.d("MyLog", "SCREENLAYOUT_SIZE_LARGE");
                buttonSize = dpToPx(55);
                buttonMargin = 4;
                textSize = 20;
                break;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                Log.d("MyLog", "SCREENLAYOUT_SIZE_NORMAL");
                buttonSize = dpToPx(48);
                break;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                Log.d("MyLog", "SCREENLAYOUT_SIZE_SMALL");
                buttonSize = dpToPx(48);
                break;
            default:
                Log.d("MyLog", "UNDEFINE");

        }


        countAttempt = 3;
        handler = new Handler();
        /*Создаем Макет*/
        LinearLayout linearLayoutMain = (LinearLayout) getActivity().findViewById(R.id.randomLettersLayout);
        LinearLayout linearLayoutInnerButtonLine_1 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_1);
        LinearLayout linearLayoutInnerButtonLine_2 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_2);
        LinearLayout linearLayoutInnerButtonLine_3 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_3);
        LinearLayout linearLayoutInnerButtonLine_4 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_4);
        LinearLayout linearLayoutInnerButtonLine_5 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_5);
        LinearLayout linearLayoutInnerButtonLine_6 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_6);
        LinearLayout linearLayoutInnerButtonLine_7 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_7);
        LinearLayout linearLayoutInnerButtonLine_8 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_8);
        LinearLayout linearLayoutInnerButtonLine_9 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_9);
        LinearLayout linearLayoutInnerButtonLine_10 = (LinearLayout) getActivity().findViewById(R.id.linearLayoutInnerButtonLine_10);
        /*Создаем кнопки*/
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = buttonSize;
        params.height = buttonSize;
        params.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin);


        /*заполяем русское слово*/
        final TextView tvRuss = (TextView) getActivity().findViewById(R.id.russianWord);
        tvRuss.setText(russianWord.toLowerCase());

        final TextView tvInput = (TextView) getActivity().findViewById(R.id.tv_input);

        final Button layWipe = (Button) getActivity().findViewById(R.id.lay_wipe);

        final TextView tvCorrectAnswer = (TextView) getActivity().findViewById(R.id.tv_correct_answer);
        tvCorrectAnswer.setText(englishWord);

        final TextView tvSumWords = (TextView) getActivity().findViewById(R.id.tvSumWords);
        tvSumWords.setText(wordsForFrgLetters.size() + "/" + toDayListWords.size());
        Log.d(LOG_TAG, wordsForFrgLetters.size() + "/" + toDayListWords.size());

        int buttonOnDisplayWidth = 0;
        int countLetter = 0;
        final ArrayList<String[]> deletedBtn = new ArrayList<>();

        /*Проверяет ширину экрана и возращает количество кнопок на строчку*/
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        buttonOnDisplayWidth = (metricsB.widthPixels - dpToPx(36)) / (buttonSize + buttonMargin + buttonMargin);

        for (String x : lettersEngWord) {
            countLetter++;

            final CardView btnLater = new CardView(getActivity());
            TextView tv = new TextView(getActivity());
            tv.setText(x);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            tv.setGravity(Gravity.CENTER);
            btnLater.setId(countLetter);
            btnLater.addView(tv);
            btnLater.setTag(x);
            btnLater.setCardElevation(4);
            btnLater.setUseCompatPadding(true);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String letter = v.getTag().toString();
                    Log.d(LOG_TAG, "Попытки:  " + countAttempt);

                    if (lettersEngWord.contains(letter)) {
                        lettersEngWord.remove(letter);
                        btnLater.setEnabled(false);
                        btnLater.setCardElevation(1);
                        btnLater.setCardBackgroundColor(getResources().getColor(R.color.my_background_1));
                        deletedBtn.add(new String[]{letter, String.valueOf((int) btnLater.getId())});
                        answer.append(letter);
                        tvInput.setText(answer);

                        /*Если написано верно бросаем следующее слово*/
                        if (englishWord.equals(answer.toString())) {
                            Toast.makeText(getActivity(), R.string.right, Toast.LENGTH_SHORT).show();
                            count--;
                            layWipe.setClickable(false);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (wordsForFrgLetters.size() > 0) {
                                        wordsForFrgLetters.remove(0);
                                    }
                                    if (wordsForFrgLetters.size() == 0 && !onDestroy && !onStop) {
                                        Dialog dialog = new Dialog(getActivity());
                                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                        // layout to display
                                        dialog.setContentView(R.layout.about_program_dialog_layout);
                                        // set color transpartent
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();
                                    }
                                    if (!onDestroy && !onStop) {
                                        reloadFragment();
                                    }
                                }
                            }, 1000);
                        }

                        /*Если не правильно то пишем красным и даем три попытки*/
                        if (englishWord.length() == answer.length() && !englishWord.equals(answer.toString())) {
                            tvInput.setTextColor(Color.parseColor("#E63434"));
                            countAttempt--;
                            Log.d(LOG_TAG, "Попытки:  " + countAttempt);
                            if (countAttempt == 0) {
                                ((TextView) getActivity().findViewById(R.id.tv_correct_answer)).setTextColor(Color.parseColor("#ff1744"));
                                countAttempt = 3;
                            }
                        }
                    }
                    /*вернуть кнопку*/
                    if (v.getId() == R.id.lay_wipe && answer.length() >= 1) {
                        if (englishWord.length() == answer.length()) {
                            tvInput.setTextColor(Color.parseColor("#000000"));
                        }
                        if (answer.length() >= 2) {
                            String returnLetter = String.valueOf(answer.substring(answer.length() - 1));
                            lettersEngWord.add(returnLetter);
                            Log.d(LOG_TAG, "Вернули букву: " + returnLetter);
                            answer.delete(answer.length() - 1, answer.length());
                            tvInput.setText(answer.toString());
                            for (String[] s : deletedBtn) {
                                if (s[0].equals(returnLetter)) {
                                    CardView c = (CardView) getActivity().getWindow().getDecorView().findViewById(Integer.parseInt(s[1]));
                                    c.setEnabled(true);
                                    c.setCardElevation(4);
                                    c.setCardBackgroundColor(Color.parseColor("#ffffff"));
                                    deletedBtn.remove(s);
                                    break;
                                }
                            }
                        } else {
                            lettersEngWord.add(String.valueOf(answer));
                            for (String[] s : deletedBtn) {
                                if (s[0].equals(String.valueOf(answer))) {
                                    CardView c = (CardView) getActivity().getWindow().getDecorView().findViewById(Integer.parseInt(s[1]));
                                    c.setEnabled(true);
                                    c.setCardElevation(4);
                                    c.setCardBackgroundColor(Color.parseColor("#ffffff"));

                                    deletedBtn.remove(s);
                                    break;
                                }
                            }
                            answer.setLength(0);
                            tvInput.setText(answer.toString());
                        }
                    }

                }
            };
            tvCorrectAnswer.setOnClickListener(onClickListener);
            layWipe.setOnClickListener(onClickListener);
            btnLater.setOnClickListener(onClickListener);


            if (countLetter < buttonOnDisplayWidth) {
                linearLayoutInnerButtonLine_1.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth && countLetter < buttonOnDisplayWidth * 2) {
                linearLayoutInnerButtonLine_2.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 2 && countLetter < buttonOnDisplayWidth * 3) {
                linearLayoutInnerButtonLine_3.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 3 && countLetter < buttonOnDisplayWidth * 4) {
                linearLayoutInnerButtonLine_4.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 4 && countLetter < buttonOnDisplayWidth * 5) {
                linearLayoutInnerButtonLine_5.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 5 && countLetter < buttonOnDisplayWidth * 6) {
                linearLayoutInnerButtonLine_6.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 6 && countLetter < buttonOnDisplayWidth * 7) {
                linearLayoutInnerButtonLine_7.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 7 && countLetter < buttonOnDisplayWidth * 8) {
                linearLayoutInnerButtonLine_7.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 8 && countLetter < buttonOnDisplayWidth * 9) {
                linearLayoutInnerButtonLine_7.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 9 && countLetter < buttonOnDisplayWidth * 10) {
                linearLayoutInnerButtonLine_7.addView(btnLater, params);
            }
            if (countLetter >= buttonOnDisplayWidth * 10) {
                linearLayoutInnerButtonLine_8.addView(btnLater, params);
            }
        }
        /*если буквы не влазят то слово пропускаем*/
        if (countLetter / buttonOnDisplayWidth > 8) {
            count--;
            if (wordsForFrgLetters.size() > 0) {
                wordsForFrgLetters.remove(0);
            }
            reloadFragment();
        }

        /*кнопка "пропустить"*/
        final Button next = (Button) getActivity().findViewById(R.id.lay_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                if (wordsForFrgLetters.size() > 0) {
                    wordsForFrgLetters.remove(0);
                }
                reloadFragment();
            }
        });


    }

    protected void drawTheWord() {
        final TextView tvSumWords = (TextView) getActivity().findViewById(R.id.tvSumWords);
        TextView tvEnglishWord = (TextView) getActivity().findViewById(R.id.englishWord);
        TextView tvTransWord = (TextView) getActivity().findViewById(R.id.transWord);
        TextView tvRussianWord = (TextView) getActivity().findViewById(R.id.russianWord);
        Button nextBtn = (Button) getActivity().findViewById(R.id.btn_next);

        tvEnglishWord.setText(englishWord);
        tvTransWord.setText(transWord);
        tvRussianWord.setText(russianWord);
        tvSumWords.setText(wordsForFrgLetters.size() + "/" + toDayListWords.size());

        if (MainActivity.isAutoSpeech) {
            MainActivity.toSpeech.speak(englishWord, TextToSpeech.QUEUE_ADD, null);
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordsForFrgRepeat.size() != 0) wordsForFrgRepeat.remove(0);
                reloadFragment();
            }
        });
    }

    /**
     * Перемешивает!!! и возвращает случайное слово из сегоднешнего списка
     */
    protected void returnRandomWord(ArrayList<String[]> arrayWords) {
        word = arrayWords.get(0);
        englishWord = word[0];
        russianWord = word[1];
        transWord = word[2];
        answer = new StringBuilder();
        if (lettersEngWord == null) {
            lettersEngWord = new ArrayList<>();
            for (char c : englishWord.toCharArray()) {
                lettersEngWord.add(String.valueOf(c));
            }
        }
        Collections.shuffle(lettersEngWord);
    }

    public void reloadFragment() {
        Fragment thisFrg = getActivity().getSupportFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.TAG_FRG_REPEAT_TO_DAY");
        FragmentTransaction fTrans = getActivity().getSupportFragmentManager().beginTransaction();
        fTrans.detach(thisFrg);
        fTrans.attach(thisFrg);
        fTrans.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        if (onCreate && wordsForFrgRepeat.size() == 0) {
            drawTheLetters();
        } else if (!onCreate && wordsForFrgRepeat.size() == 0) {
            reloadFragment();
        } else {
            drawTheWord();
        }
        if (onResume) {
            reloadFragment();
        }
        onCreate = false;
        onDestroy = false;
        onResume = false;
        onStop = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        onResume = true;

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        onStop = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "onDestroyView");
        onDestroy = true;
        lettersEngWord = null;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}

