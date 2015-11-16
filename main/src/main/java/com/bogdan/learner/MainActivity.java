package com.bogdan.learner;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bogdan.learner.fragments.FragmentListener;
import com.bogdan.learner.fragments.FrgAddOwnWordToBase;
import com.bogdan.learner.fragments.FrgAddWordForStudy;
import com.bogdan.learner.fragments.FrgLearnToDay;
import com.bogdan.learner.fragments.FrgMainMenu;
import com.bogdan.learner.fragments.FrgRepeatMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

public class MainActivity extends Activity implements FragmentListener {
    private final String LOG_TAG = "MainActivity";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    public static String toDayDate;
    SharedPreferences sp;

    DBHelper dbHelper;
    TreeMap<Integer, ArrayList<String[]>> uploadDb;
    FrgMainMenu frgMainMenu;
    FrgAddOwnWordToBase frgAddOwnWordToBase;
    FrgAddWordForStudy frgAddWordForStudy;
    FrgRepeatMenu frgRepeatMenu;
    FrgLearnToDay frgLearnToDay;
    FragmentTransaction fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        dbHelper = DBHelper.getDbHelper(this);
        uploadDb = dbHelper.uploadDb;

        frgMainMenu = new FrgMainMenu();
        frgAddWordForStudy = new FrgAddWordForStudy();
        frgRepeatMenu = new FrgRepeatMenu();
        frgLearnToDay = new FrgLearnToDay();
        frgAddOwnWordToBase = new FrgAddOwnWordToBase();




        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.fragment_container, frgMainMenu, "com.bogdan.learner.fragments.MAIN_MENU");
        fTrans.commit();

//        startNotify();
    }

    @Override
    public void onButtonSelected(View view) {
        fTrans = getFragmentManager().beginTransaction();
        switch (view.getId()) {
            /*Кнопки активити*/
            case R.id.btn_toMain:
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fTrans.replace(R.id.fragment_container, frgMainMenu, "com.bogdan.learner.fragments.MAIN_MENU");
                fTrans.addToBackStack("frgMainMenu");
                hideKeyboard();
                break;

            case R.id.btn_add:
                FrgAddOwnWordToBase f = (FrgAddOwnWordToBase)getFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.frgAddOwnWordToBase");
                if( f !=null && f.isVisible()){
                    //do nothing
                }else {
                    fTrans.replace(R.id.fragment_container, frgAddOwnWordToBase, "com.bogdan.learner.fragments.frgAddOwnWordToBase");
                    fTrans.addToBackStack("frgAddOwnWordToBase");
                }
                break;


            /*Кнопки фрагментов*/
            case R.id.btn_addMoreWord:
                fTrans.replace(R.id.fragment_container, frgAddWordForStudy, "com.bogdan.learner.fragments.FrgAddWordForStudy");
                fTrans.addToBackStack("frgAddWordForStudy");
                break;

            case R.id.btn_learnToday:
                if (dbHelper.getListWordsByDate(toDayDate) != null) {
                    fTrans.replace(R.id.fragment_container, frgLearnToDay, "com.bogdan.learner.fragments.TAG_FRG_REPEAT_TO_DAY");
                    fTrans.addToBackStack("frgLearnToDay");
                } else
                    Toast.makeText(this, R.string.no_words_today, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_repeat:
//                Log.i(LOG_TAG, dbHelper.uploadDb.size() + "");               /*ПОЧЕМУ НЕ РАБОТАЕТ локальный uploadDb ??????????*/
//                Log.i(LOG_TAG, dbHelper.learnedWords.size() + "");               /*ПОЧЕМУ НЕ РАБОТАЕТ локальный uploadDb ??????????*/
                if (dbHelper.learnedWords.size() >= 1) {
                    fTrans.replace(R.id.fragment_container, frgRepeatMenu);
                    fTrans.addToBackStack("frgRepeatMenu");
                } else {
                    Toast.makeText(this, R.string.no_words, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        fTrans.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.MAIN_MENU");
        if (fragment != null && fragment.isVisible()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(null)
                    .setMessage(R.string.exit)
                    .setPositiveButton(R.string.no, null)
                    .setNegativeButton(R.string.yas, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }else {
            super.onBackPressed();
        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected  void startNotify(){
        Intent nf = new Intent(this, NotifyService.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, nf, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pendingIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("kill", "yes");
        editor.apply();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String flag = preferences.getString("kill", null);
        if(flag!=null && flag.equals("yes")){
            Log.d(LOG_TAG, "was onDestroy()");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("kill", "yes");
        editor.apply();
    }
}
