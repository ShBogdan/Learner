package com.bogdan.learner;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.bogdan.learner.fragments.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;


public class MainActivity extends Activity implements FragmentListener {
    private final String LOG_TAG = "MainActivity";

    public static TreeMap<Integer, ArrayList<String[]>> uploadDb;
    public static String toDayDate;
    public static DayLibrary studyDays;
    public static ArrayList<String[]> toDayListWords;

    FrgMainMenu frgMainMenu;
    FrgAddWordForStudy frgAddWordForStudy;
    FrgAddMyWord frgAddMyWord;
    FrgRepeatSelectively frgRepeatSelectively;
    FrgRepeatToDay frgRepeatToDay;
    FrgStatistic frgStatistic;
    FrgListAllWord frgListAllWord;
    FragmentTransaction fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        uploadDb  = new DBHelper(this).uploadDb();
        d(this);
//        studyDays = new DayLibrary(this);

        frgMainMenu = new FrgMainMenu();
        frgAddWordForStudy = new FrgAddWordForStudy();
        frgRepeatSelectively = new FrgRepeatSelectively();
        frgRepeatToDay = new FrgRepeatToDay();
        frgStatistic = new FrgStatistic();
        frgListAllWord = new FrgListAllWord();


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
                hideKeyboard();
                break;
            case R.id.btn_add:
                frgAddMyWord    = new FrgAddMyWord();
                fTrans.replace(R.id.fragment_container, frgAddMyWord);
                Log.i(LOG_TAG, "Activity: Добавить");
                break;

            /*Кнопки фрагментов*/
            /*Фрагмент MainMenu*/
            case R.id.btn_addMoreWord:
                fTrans.replace(R.id.fragment_container, frgAddWordForStudy);
                Log.i(LOG_TAG, "Fragment: Добавить еще слова");
                break;
            case R.id.btn_learnToday:
                fTrans.replace(R.id.fragment_container, frgRepeatToDay, "TAG_FRG_REPEAT_TO_DAY");
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
        }
        fTrans.commit();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Выход")
                .setMessage("Закрыть приложение?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Нет", null)
                .show();
    }

    public static void d (Context context){
        toDayListWords = new ArrayList<>(new DayLibrary(context).getListWordsByDate("20150801"/*toDayDate*/));
    }
}

