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
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bogdan.learner.fragments.FragmentListener;
import com.bogdan.learner.fragments.FrgAddOwnWordToBase;
import com.bogdan.learner.fragments.FrgAddWordForStudy;
import com.bogdan.learner.fragments.FrgLearnToDay;
import com.bogdan.learner.fragments.FrgMainMenu;
import com.bogdan.learner.fragments.FrgRepeatMenu;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity implements FragmentListener, TextToSpeech.OnInitListener{
    final String LOG_TAG = "MyLog";
    private static final String DATABASE_NAME = "dictionary.sqlite";
    public static String toDayDate;
    public static TextToSpeech toSpeech;

    DBHelper dbHelper;
    TreeMap<Integer, ArrayList<String[]>> uploadDb;
    FrgMainMenu frgMainMenu;
    FrgAddOwnWordToBase frgAddOwnWordToBase;
    FrgAddWordForStudy frgAddWordForStudy;
    FrgRepeatMenu frgRepeatMenu;
    FrgLearnToDay frgLearnToDay;
    FragmentTransaction fTrans;
    FirebaseAnalytics mFirebaseAnalytics;
    String downloadDbPath;
    String uploadDbPath;
    Context context;
    private Handler mUiHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        toDayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        dbHelper = DBHelper.getDbHelper(this);
        uploadDb = dbHelper.uploadDb;
        context = getBaseContext();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        toSpeech = new TextToSpeech(MainActivity.this, MainActivity.this);
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
                FrgAddOwnWordToBase f = (FrgAddOwnWordToBase) getFragmentManager().findFragmentByTag("com.bogdan.learner.fragments.frgAddOwnWordToBase");
                if (f != null && f.isVisible()) {
                    //do nothing
                } else {
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
                if (dbHelper.learnedWords.size() >= 1) {
                    fTrans.replace(R.id.fragment_container, frgRepeatMenu);
                    fTrans.addToBackStack("frgRepeatMenu");
                } else {
                    Toast.makeText(this, R.string.no_words, Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.btn_choesFile:
//                downloadDb();
//                break;
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
                        Log.d(LOG_TAG, "This Language is not supported");
                    } else {
                        Log.d(LOG_TAG, "Its fine!");
                    }
                } else {
                    Log.d(LOG_TAG, "Initilization Failed!");
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (toSpeech != null) {
//            toSpeech.stop();
            toSpeech.shutdown();
        }
        super.onDestroy();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(hasPermission (WRITE_EXTERNAL_STORAGE)){
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.save:
                    uploadDb();
                    return true;
                case R.id.load:
                    downloadDb();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }else {
            askForPermission(WRITE_EXTERNAL_STORAGE , 10);
            return super.onOptionsItemSelected(item);
        }
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
}
