package com.bogdan.learner;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends Activity implements FragmentListener {
    private final String LOG_TAG = "MainActivity";
    public static String toDayDate;


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
                Log.i(LOG_TAG, dbHelper.uploadDb.size() + "");               /*ПОЧЕМУ НЕ РАБОТАЕТ локальный uploadDb ??????????*/
                boolean isData = false;
                for (Map.Entry<Integer, ArrayList<String[]>> el : dbHelper.uploadDb.entrySet()) {
                    if (el.getKey() > 1) {
                        fTrans.replace(R.id.fragment_container, frgRepeatMenu);
                        fTrans.addToBackStack("frgRepeatMenu");
                        isData = true;
                        break;
                    }
                }
                if (!isData) {
                    Toast.makeText(this, R.string.no_words, Toast.LENGTH_SHORT).show();
                }
                break;

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
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setTitle(null)
//                .setMessage(R.string.exit)
//                .setPositiveButton(R.string.no, null)
//                .setNegativeButton(R.string.yas, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                }).show();
//
//    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
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

    }
}
