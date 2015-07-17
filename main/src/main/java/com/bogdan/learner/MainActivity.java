package com.bogdan.learner;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.bogdan.learner.fragments.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;


public class MainActivity extends Activity implements FragmentListener {
    private final String LOG_TAG = ":::::::::::::MainActivity:::::::::::::";

    public static TreeMap<Integer, ArrayList<String[]>> uploadDb;
    public static String toDayDate;
    public static DayLibrary studyDays;

    FrgMainMenu frgMainMenu;
    FrgAddWordToDay frgAddWord;
    FrgAddMyWord frgAddMyWord;
    FrgRepeat frgRepeat;
    FrgLearnToDay frgLearnToDay;
    FrgStatistic frgStatistic;
    FrgListAllWord frgListAllWord;
    FragmentTransaction fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        uploadDb  = new DBHelper(this).uploadDb();

        frgMainMenu     = new FrgMainMenu();
        frgAddWord      = new FrgAddWordToDay();
        frgRepeat       = new FrgRepeat();
        frgLearnToDay   = new FrgLearnToDay();
        frgStatistic    = new FrgStatistic();
        frgListAllWord  = new FrgListAllWord();

        fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, frgMainMenu);
        fTrans.commit();

    }

    @Override
    public void onButtonSelected(View view) {
        fTrans = getFragmentManager().beginTransaction();
        switch (view.getId()){
            /*Кнопки активити*/
            case R.id.btn_toMain:
                fTrans.replace(R.id.fragment_container, frgMainMenu);
                Log.i(LOG_TAG, "Activity: Основное меню");
                break;
            case R.id.btn_add:
                frgAddMyWord    = new FrgAddMyWord();
                fTrans.replace(R.id.fragment_container, frgAddMyWord);
                Log.i(LOG_TAG, "Activity: Добавить");
                break;

            /*Кнопки фрагментов*/

            /*Фрагмент MainMenu*/
            case R.id.btn_learnToday:
                fTrans.replace(R.id.fragment_container,frgLearnToDay);
                Log.i(LOG_TAG, "Fragment: Учить сегоднешние");
                break;

            case R.id.btn_addMoreWord:
                frgAddWord      = new FrgAddWordToDay();
                fTrans.replace(R.id.fragment_container, frgAddWord);
                Log.i(LOG_TAG, "Fragment: Добавить еще слова");
                break;

            case R.id.btn_repeat:
                fTrans.replace(R.id.fragment_container, frgRepeat);
                Log.i(LOG_TAG, "Fragment: Повторение изученого");
                break;
            /*Фрагмент Повторение изученого*/
            case R.id.btn_all_words:
                fTrans.replace(R.id.fragment_container, frgListAllWord);
                Log.i(LOG_TAG, "Fragment: Все слова");
                break;


        }
        fTrans.commit();
    }

}

