package com.bogdan.learner;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bogdan.learner.fragments.FragmentListener;
import com.bogdan.learner.fragments.FrgAddMyWord;
import com.bogdan.learner.fragments.FrgAddWordForStudy;
import com.bogdan.learner.fragments.FrgCalendar;
import com.bogdan.learner.fragments.FrgListAllWord;
import com.bogdan.learner.fragments.FrgMainMenu;
import com.bogdan.learner.fragments.FrgRepeatSelectively;
import com.bogdan.learner.fragments.FrgRepeatToDay;
import com.bogdan.learner.fragments.FrgStatistic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;


public class MainActivity extends Activity implements FragmentListener {
    private final String LOG_TAG = "MainActivity";

    public static TreeMap<Integer, ArrayList<String[]>> uploadDb;
    public static String toDayDate;
    public static DayLibrary studyDays;


    DBHelper dbHelper;

    FrgMainMenu frgMainMenu;
    FrgAddWordForStudy frgAddWordForStudy;
    FrgAddMyWord frgAddMyWord;
    FrgRepeatSelectively frgRepeatSelectively;
    FrgRepeatToDay frgRepeatToDay;
    FrgStatistic frgStatistic;
    FrgListAllWord frgListAllWord;
    FragmentTransaction fTrans;
    FrgCalendar frgCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        uploadDb = dbHelper.uploadDb();


        frgMainMenu = new FrgMainMenu();
        frgAddWordForStudy = new FrgAddWordForStudy();
        frgRepeatSelectively = new FrgRepeatSelectively();
        frgRepeatToDay = new FrgRepeatToDay();
        frgStatistic = new FrgStatistic();
        frgListAllWord = new FrgListAllWord();
        frgCalendar = new FrgCalendar();


        fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, frgMainMenu);
        fTrans.commit();

       }

    @Override
    public void onButtonSelected(View view) {
        fTrans = getFragmentManager().beginTransaction();
        switch (view.getId()) {
            /*Кнопки активити*/
            case R.id.btn_toMain:
                fTrans.replace(R.id.fragment_container, frgMainMenu);
                Log.i(LOG_TAG, "Activity: Основное меню");
                hideKeyboard();
                break;
            case R.id.btn_add:
                frgAddMyWord = new FrgAddMyWord();
                fTrans.replace(R.id.fragment_container, frgAddMyWord);
                Log.i(LOG_TAG, "Activity: Добавить");
                break;

            /*Кнопки фрагментов*/
            /*Фрагмент MainMenu*/
            case R.id.btn_addMoreWord:
                fTrans.replace(R.id.fragment_container, frgAddWordForStudy, "com.bogdan.learner.fragments.FrgAddWordForStudy");
                Log.i(LOG_TAG, "Fragment: Добавить еще слова");
                break;
            case R.id.btn_learnToday:
                if (new DayLibrary(this).getListWordsByDate(toDayDate) != null) {
                    fTrans.replace(R.id.fragment_container, frgRepeatToDay, "TAG_FRG_REPEAT_TO_DAY");
                } else
                    Toast.makeText(this, "Сегодня вы не добавили ни одного слова", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Fragment: Учить сегоднешние");
                break;
            case R.id.btn_repeat:
                fTrans.replace(R.id.fragment_container, frgRepeatSelectively);
                Log.i(LOG_TAG, "Fragment: Повторение изученого");
                break;

            /*Фрагмент Повторение изученого*/
            case R.id.btn_all_words:
                fTrans.replace(R.id.fragment_container, frgListAllWord);
                Log.i(LOG_TAG, "Fragment: Все слова");
                break;

            case R.id.btn_calendar:
                fTrans.replace(R.id.fragment_container, frgCalendar);
        }
        fTrans.commit();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Выход")
//                .setMessage("Закрыть приложение?")
//                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//
//                })
//                .setNegativeButton("Нет", null)
//                .show();
//    }

}

