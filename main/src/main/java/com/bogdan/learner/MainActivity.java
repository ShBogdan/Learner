package com.bogdan.learner;

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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.fragments.FragmentListener;
import com.bogdan.learner.fragments.FrgAddOwnWordToBase;
import com.bogdan.learner.fragments.FrgAddWordForStudy;
import com.bogdan.learner.fragments.FrgLearnToDay;
import com.bogdan.learner.fragments.FrgMainMenu;
import com.bogdan.learner.fragments.FrgRepeatMenu;
import com.bogdan.learner.util.Billing;
import com.bogdan.learner.util.CallBackBill;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity implements FragmentListener, TextToSpeech.OnInitListener, CompoundButton.OnCheckedChangeListener, CallBackBill {
    //var
    //region
    final String LOG_TAG = "MyLog";
    private static final String DATABASE_NAME = "dictionary.sqlite";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    public static String toDayDate;
    public static TextToSpeech toSpeech;
    public static boolean isAutoSpeech;
    public static boolean isReversWordPlace;
    public static boolean isBaseChanged;
    public static int wordAlternation;
    public static boolean isPremium = false;
    public static boolean isTrialTimeEnd = false;


    DBHelper dbHelper;
    TreeMap<Integer, ArrayList<String[]>> uploadDb;
    FrgMainMenu frgMainMenu;
    FrgAddOwnWordToBase frgAddOwnWordToBase;
    FrgAddWordForStudy frgAddWordForStudy;
    FrgRepeatMenu frgRepeatMenu;
    FrgLearnToDay frgLearnToDay;
    FragmentTransaction fTrans;
    String downloadDbPath;
    String uploadDbPath;
    Context context;
    DrawerLayout mDrawerLayout;
    View mMainView;
    ActionBarDrawerToggle mDrawerToggle;
    TextView tv_learned, tv_know, tv_today, tv_seekBarValue;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    CheckBox mAutoSpeech, mChangeWordPlace, mNotifyMorning, mNotifyEvening;

    FirebaseAnalytics mFirebaseAnalytics;
    public AdView mAdView;
    Billing bill;

    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.main);
        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        dbHelper = DBHelper.getDbHelper(this);
        uploadDb = dbHelper.uploadDb;
        context = this;

        sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        editor = sp.edit();

        bill = new Billing(this);
        bill.startSetup(); //add to comment for emulator


        if(!sp.contains("IsPremium")) {
            editor.putBoolean("IsPremium", false).apply();
        }
        isPremium = sp.getBoolean("IsPremium", false);
        mAdView = (AdView) findViewById(R.id.adView);

//        setPremium(false); //test



        advertise(!isPremium);

        frgMainMenu = new FrgMainMenu();
        frgAddWordForStudy = new FrgAddWordForStudy();
        frgRepeatMenu = new FrgRepeatMenu();
        frgLearnToDay = new FrgLearnToDay();
        frgAddOwnWordToBase = new FrgAddOwnWordToBase();

        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.fragment_container, frgMainMenu, "com.bogdan.learner.fragments.MAIN_MENU");
        fTrans.commit();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SomeId");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SomeName");
        bundle.putString("my_param", "param");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        initDrawerLayout();

    }

    void advertise(Boolean isShow){
        if(isShow){
            //get in as search:addTestDevice
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("7D52B52E8021375F847F513F5BCC161D")
                    .addTestDevice("62D8BB95BA97339C7A028147DA6DE5AA")
                    .build();
            mAdView.loadAd(adRequest);
            Log.d(LOG_TAG,"Стартп текламы");
        }else {
            mAdView.destroy();
            mAdView.setLayoutParams(new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, 0));
        }
    }

    @Override
    public void onButtonSelected(View view) {
        fTrans = getFragmentManager().beginTransaction();
        Integer wordsAllowed = 1000;
        if(!isPremium){
            wordsAllowed = (DBHelper.getDbHelper(context).getListWordsByDate(toDayDate) == null) ? 0 : DBHelper.getDbHelper(context).getListWordsByDate(toDayDate).size();}
        switch (view.getId()) {
            /*Кнопки активити*/
            case R.id.btn_toMain:
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fTrans.replace(R.id.fragment_container, frgMainMenu, "com.bogdan.learner.fragments.MAIN_MENU");
                fTrans.addToBackStack("frgMainMenu");
                hideKeyboard();
                break;

            case R.id.btn_add:
                if(!isPremium && isTrialTimeEnd && wordsAllowed > 4){
                    Toast.makeText(getApplication(), R.string.more_than_6, Toast.LENGTH_SHORT).show();
                }else{
                    FrgAddOwnWordToBase f = (FrgAddOwnWordToBase) getFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.frgAddOwnWordToBase");
                    if (f != null && f.isVisible()) {
                        //do nothing
                    } else {
                        fTrans.replace(R.id.fragment_container, frgAddOwnWordToBase, "com.bogdan.learner.fragments.frgAddOwnWordToBase");
                        fTrans.addToBackStack("frgAddOwnWordToBase");
                    }
                }
                break;


            /*Кнопки фрагментов*/
            case R.id.btn_addMoreWord:
                if(!isPremium && isTrialTimeEnd && wordsAllowed > 4){
                    Toast.makeText(getApplication(), R.string.more_than_6, Toast.LENGTH_SHORT).show();
                }else{
                    fTrans.replace(R.id.fragment_container, frgAddWordForStudy, "com.bogdan.learner.fragments.FrgAddWordForStudy");
                    fTrans.addToBackStack("frgAddWordForStudy");
                }
                break;

            case R.id.btn_learnToday:
                if (dbHelper.getListWordsByDate(toDayDate) != null) {
                    fTrans.replace(R.id.fragment_container, frgLearnToDay, "com.bogdan.learner.fragments.TAG_FRG_REPEAT_TO_DAY");
                    fTrans.addToBackStack("frgLearnToDay");
                } else
                    Toast.makeText(this, R.string.no_words_today, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_repeat:
                if (dbHelper.learnedWords.size() >= 1) {
                    fTrans.replace(R.id.fragment_container, frgRepeatMenu);
                    fTrans.addToBackStack("frgRepeatMenu");
                } else {
                    Toast.makeText(this, R.string.no_words, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_buyIt:
                try {
                    bill.launchPurchaseFlow();
                }catch (IllegalStateException e){
                    Toast.makeText(this,"Попробуйте позже", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        fTrans.commit();
    }

    public void uploadDb() {
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            uploadDbPath = this.getApplicationInfo().dataDir + "/databases/"+DATABASE_NAME;
        } else {
            uploadDbPath = this.getFilesDir() + this.getPackageName() + "/databases/"+DATABASE_NAME;
        }
        try {
            InputStream in = new FileInputStream(uploadDbPath);
            OutputStream out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+DATABASE_NAME);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog(getResources().getString(R.string.base_saved));
    }

    public void downloadDb() {
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            uploadDbPath = this.getApplicationInfo().dataDir + "/databases/"+DATABASE_NAME;
        } else {
            uploadDbPath = this.getFilesDir() + this.getPackageName() + "/databases/"+DATABASE_NAME;
        }
        new FileChooser(this).setFileListener(new FileChooser.FileSelectedListener() {
            @Override public void fileSelected(final File file) {

                if(file.getName().equals("dictionary.sqlite")){
                    try {
                        downloadDbPath = file.getPath();
                        InputStream in = new FileInputStream(downloadDbPath);
                        OutputStream out = new FileOutputStream(uploadDbPath);

                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        deleteCache(context);
                        restartApp();
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    dialog(getResources().getString(R.string.unknoun_file));}
            }
        }).showDialog();
        dialog(getResources().getString(R.string.load_base));

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
        } else {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    @Override
    public void onInit(final int status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (status == TextToSpeech.SUCCESS) {
                    int result = toSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d(LOG_TAG, "onInit_This Language is not supported");
                    } else {
//                        Log.d(LOG_TAG, "onInit_Its fine!");
                    }
                } else {
                    Log.d(LOG_TAG, "onInit_Initilization Failed!");
                }
            }
        }).start();
    }

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void restartApp(){
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public void dialog(String massage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help)
                .setMessage(massage)
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "false");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Log.d(LOG_TAG, "true");
        }
    }

    private boolean hasPermission(String perm) {
        return(ContextCompat.checkSelfPermission( this, perm) == PackageManager.PERMISSION_GRANTED);
    }

    void initDrawerLayout(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMainView = (LinearLayout) findViewById(R.id.main_activity);
        //реакция на открытие-закрытие
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* значок-гамбургер для замены стрелки 'Up' */
                R.string.drawer_open,  /* добавьте строку "open drawer" - описание для  accessibility */
                R.string.drawer_close  /* добавьте "close drawer" - описание для accessibility */
        ) {
            public void onDrawerClosed(View view) {

            }
            public void onDrawerOpened(View drawerView) {
                tv_learned = (TextView) findViewById(R.id.tv_learned);
                tv_learned.setText(getString(R.string.you_know) +
                        DBHelper.getDbHelper(context).learnedWords.size() + getString(R.string.new_words));
                tv_know    = (TextView) findViewById(R.id.tv_known);
                tv_know.setText(getString(R.string.you_knew) +
                        DBHelper.getDbHelper(context).listKnownWords.size() + getString(R.string.words));
                tv_today    = (TextView) findViewById(R.id.tv_today);
                Integer size = (DBHelper.getDbHelper(context).getListWordsByDate(toDayDate) == null) ? 0 : DBHelper.getDbHelper(context).getListWordsByDate(toDayDate).size();
                tv_today.setText(getString(R.string.today_learned) + size + getString(R.string.words));
            }
        };

        getAutoSpeech();
        getChangeWordPlace();
        getWordAlternation();
        setUseBase();
        setAlarm();

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


    }
    private void getChangeWordPlace(){
        mChangeWordPlace = (CheckBox) findViewById(R.id.changeWordPlace);
        if(!sp.contains("changeWordPlace")) {
            editor.putBoolean("changeWordPlace", false).apply();
        }
        if(sp.getBoolean("changeWordPlace", false)){
            isReversWordPlace = false;
        }
        if(sp.getBoolean("changeWordPlace", true)){
            mChangeWordPlace.setChecked(true);
            isReversWordPlace = true;
        }
        mChangeWordPlace.setOnCheckedChangeListener(this);
    }
    private void getAutoSpeech(){
        mAutoSpeech = (CheckBox) findViewById(R.id.autoSpeech);
        if(!sp.contains("autoSpeech")) {
            editor.putBoolean("autoSpeech", false).apply();
        }
        if(sp.getBoolean("autoSpeech", false)){
            isAutoSpeech = false;
        }
        if(sp.getBoolean("autoSpeech", true)){
            mAutoSpeech.setChecked(true);
            isAutoSpeech = true;
        }

        mAutoSpeech.setOnCheckedChangeListener(this);
    }
    private void getWordAlternation(){
        if(!sp.contains("WordAlternation")) {
            editor.putInt("WordAlternation", 2).apply();
        }
        wordAlternation = sp.getInt("WordAlternation", 2);
        tv_seekBarValue = (TextView)findViewById(R.id.tv_seekBarValue);
        tv_seekBarValue.setText(getApplication().getString(R.string.alternation) + " " + String.valueOf(wordAlternation));


        final SeekBar seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setMax(10);
        seekbar.setProgress(wordAlternation);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getProgress() == 0){
                    wordAlternation = seekBar.getProgress()+1;
                }else{
                    wordAlternation = seekBar.getProgress();
                }
                tv_seekBarValue.setText(getApplication().getString(R.string.alternation) + " " + String.valueOf(wordAlternation));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() == 0){
                    wordAlternation = seekBar.getProgress()+1;

                }else{
                    wordAlternation = seekBar.getProgress();
                }
                tv_seekBarValue.setText(getApplication().getString(R.string.alternation) + " " + String.valueOf(wordAlternation));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() == 0){
                    wordAlternation = seekBar.getProgress()+1;
                }else{
                    wordAlternation = seekBar.getProgress();
                }
                tv_seekBarValue.setText(getApplication().getString(R.string.alternation) + " " + String.valueOf(wordAlternation));
                editor.putInt("WordAlternation", wordAlternation).apply();
            }
        });


    }
    private void setUseBase(){
        Button load_base = (Button) findViewById(R.id.load_base);
        Button save_base = (Button) findViewById(R.id.save_base);
        load_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission (WRITE_EXTERNAL_STORAGE)){
                    downloadDb();
                }else {
                    askForPermission(WRITE_EXTERNAL_STORAGE , 10);
                }
            }
        });
        save_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPermission (WRITE_EXTERNAL_STORAGE)){
                    uploadDb();
                }else {
                    askForPermission(WRITE_EXTERNAL_STORAGE , 10);
                }
            }
        });
    }
    private void setAlarm(){
        mNotifyMorning = (CheckBox) findViewById(R.id.notify_morning);
        if(!sp.contains("notify_morning")) {
            editor.putBoolean("notify_morning", false).apply();
        }
        if(sp.getBoolean("notify_morning", false)){
        }
        if(sp.getBoolean("notify_morning", true)){
            mNotifyMorning.setChecked(true);
        }

        mNotifyEvening = (CheckBox) findViewById(R.id.notify_evening);

        if(!sp.contains("notify_evening")) {
            editor.putBoolean("notify_evening", false).apply();
        }
        if(sp.getBoolean("notify_evening", false)){
        }
        if(sp.getBoolean("notify_evening", true)){
            mNotifyEvening.setChecked(true);
        }

        mNotifyMorning.setOnCheckedChangeListener(this);
        mNotifyEvening.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.notify_morning:
                if(mNotifyMorning.isChecked()){
                    editor.putBoolean("notify_morning", true).apply();
                    new AlarmManagerBroadcastReceiver().setMorningAlarm(context);
                } else {
                    editor.putBoolean("notify_morning", false).apply();
                    new AlarmManagerBroadcastReceiver().cancelMorning(context);
                }
                break;
            case R.id.notify_evening:
                if(mNotifyEvening.isChecked()){
                    editor.putBoolean("notify_evening", true).apply();
                    new AlarmManagerBroadcastReceiver().setEveningAlarm(context);
                } else {
                    editor.putBoolean("notify_evening", false).apply();
                    new AlarmManagerBroadcastReceiver().cancelEvening(context);
                }
                break;

            case R.id.autoSpeech:
                if(mAutoSpeech.isChecked()){
                    editor.putBoolean("autoSpeech", true).apply();
                    isAutoSpeech = true;
                } else {
                    editor.putBoolean("autoSpeech", false).apply();
                    isAutoSpeech = false;
                }
                break;
            case R.id.changeWordPlace:
                if(mChangeWordPlace.isChecked()){
                    editor.putBoolean("changeWordPlace", true).apply();
                    isReversWordPlace = true;
                } else{
                    editor.putBoolean("changeWordPlace", false).apply();
                    isReversWordPlace = false;
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        dbHelper.date = Integer.parseInt(toDayDate);
        isTrialTimeEnd = isTrialTimeEmd();
        toSpeech = new TextToSpeech(MainActivity.this, MainActivity.this);
        Log.d(LOG_TAG,"onResume");
        mAdView.resume();
    }
    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //tospeech
        if (toSpeech != null) {
            toSpeech.shutdown();
        }
//        //advertise
        mAdView.destroy();

        super.onDestroy();

        //billing
        if (bill.mHelper != null)
            bill.mHelper.dispose();
        bill.mHelper = null;
    }

    @Override
    public void setPremium(Boolean boo){
        Log.d(LOG_TAG,"Hello CallBack = " + boo);
        editor.putBoolean("IsPremium", boo).apply();
        isPremium = sp.getBoolean("IsPremium", false);
        if(isPremium){
            Log.d(LOG_TAG,"Remove advertise");
            mAdView.destroy();
            mAdView.setLayoutParams(new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, 0));
        }else {
            Log.d(LOG_TAG,"Show advertise");
            advertise(!isPremium);
        }
    }

    boolean isTrialTimeEmd(){
        long installedDate = 0;
        try {
            installedDate = getApplication()
                    .getPackageManager()
                    .getPackageInfo("com.bogdan.english.card", 0)
                    .firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        installedDate = installedDate + TimeUnit.DAYS.toMillis(3);

        Log.d(LOG_TAG,"end data" + new SimpleDateFormat("yyyyMMdd").format(installedDate));

        Calendar time = Calendar.getInstance();
        if(time.getTimeInMillis()>installedDate){
            return true;
        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult " + requestCode + "," + resultCode + "," + data);
        if (bill.mHelper == null) return;
        if (!bill.mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(LOG_TAG, "onActivityResult handled by IABUtil.");
        }
    }
}

















//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(hasPermission (WRITE_EXTERNAL_STORAGE)){
//            // Handle item selection
//            switch (item.getItemId()) {
//                case R.id.save:
//                    uploadDb();
//                    return true;
//                case R.id.load:
//                    downloadDb();
//                    return true;
//                default:
//                    return super.onOptionsItemSelected(item);
//            }
//        }else {
//            askForPermission(WRITE_EXTERNAL_STORAGE , 10);
//            return super.onOptionsItemSelected(item);
//        }
//    }