package com.bogdan.learner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "DBHelper";
    private Integer date = Integer.parseInt(MainActivity.toDayDate);
    private static DBHelper dbHelper;
    private Context mContext;

    private static final String KEY_ROWID = "_id";
    private static final String KEY_ENG = "english";
    private static final String KEY_RUS = "russian";
    private static final String KEY_TRANS = "transcription";
    private static final String KEY_TRANSLATIONS = "translations";
    private static final String KEY_DATE = "date";

    private static String DB_PATH;
    private static final String DATABASE_NAME = "dictionary.sqlite";
    private static final String DATABASE_TABLE = "words";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table words (_id integer primary key autoincrement, " +
            "english text not null, " +
            "russian text not null, " +
            "transcription text, " +
            "translations text, " +
            "date text not null);";


    public TreeMap<Integer, ArrayList<String[]>> uploadDb;
    public ArrayList<String[]> listUnknownWords;       //под ключем 1 не изученные слова
    public ArrayList<String[]> listKnownWords;         //под ключем 0 слова известные ранее
    public ArrayList<String[]> learnedWords;           //изученные имеют дату
    public ArrayList<String> engWords;                          //перечень имеющихся слов
    private String[] word;
    private SQLiteDatabase sqLiteDatabase;
    private ContentValues contentValues;
    private Cursor cursor;
    private int rdWord;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "onCreate");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper = this;
        this.mContext = context;
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = mContext.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = mContext.getFilesDir() + mContext.getPackageName() + "/databases/";
        }
        if(checkDataBase()){
            Log.d(LOG_TAG, "exist");
        }else{
            try {
                copyDataBase(mContext);
            } catch (IOException e) {
                Log.e(LOG_TAG, "DBHelper+copyDataBase catch");
            }
            Log.d(LOG_TAG, "DATABASE_CREATE");
        }
//заполняем списки
        uploadDb();


        Log.d(LOG_TAG, "learnedWords=" + learnedWords.size());


    }

    public static DBHelper getDbHelper(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    private boolean checkDataBase(){
        sqLiteDatabase = null;
        try{
            String myPath = DB_PATH + DATABASE_NAME;
            sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
        }
        return sqLiteDatabase != null ? true : false;
    }

    private void copyDataBase(Context context) throws IOException {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        //Открываем локальную БД как входящий поток
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DATABASE_NAME;
        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Загружает таблицу и возвращает Map. Ключ это дата изучения, значение это массив строк(значения слова)
     */
    public void uploadDb() {
        Log.d(LOG_TAG, "uploadDb");
        TreeMap<Integer, ArrayList<String[]>> wordsDb = new TreeMap<>();
        engWords = new ArrayList<>();
        sqLiteDatabase = dbHelper.getReadableDatabase();
        cursor = sqLiteDatabase.query(DATABASE_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String english = cursor.getString(cursor.getColumnIndex(KEY_ENG));
            String russian = cursor.getString(cursor.getColumnIndex(KEY_RUS));
            String transcription = cursor.getString(cursor.getColumnIndex(KEY_TRANS));
            String voicePatch = cursor.getString(cursor.getColumnIndex(KEY_TRANSLATIONS));
            Integer date = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            String id = cursor.getString(cursor.getColumnIndex(KEY_ROWID));
            engWords.add(english);
            if (wordsDb.containsKey(date)) {
                    /*Put in TreeMap "wordsDB" an ArrayList new String[]*/
                wordsDb.get(date).add(new String[]{english, russian, transcription, id,voicePatch});
            } else {
                wordsDb.put(date, new ArrayList<String[]>());
                wordsDb.get(date).add(new String[]{english, russian, transcription, id,voicePatch});
            }
        }

        sqLiteDatabase.close();

        uploadDb = wordsDb;

        listUnknownWords = uploadDb.get(1);

        if(uploadDb.get(0) == null){
            listKnownWords = new ArrayList<>();
        }else {
            listKnownWords = uploadDb.get(0);
        }

        learnedWords = new ArrayList<String[]>();
        for (Map.Entry<Integer, ArrayList<String[]>> el : uploadDb.entrySet()) {
            if (el.getKey() != 0 && el.getKey() != 1) {
                for (String[] word : el.getValue())
                    learnedWords.add(word);
            }
        }
    }

    /**
     * Добавляет ваше слово в таблицу
     */
    public void insertWord(String english, String russian, String transcription ,String date) {
        contentValues = new ContentValues();
        sqLiteDatabase = dbHelper.getWritableDatabase();
        contentValues.put(KEY_ENG, english);
        contentValues.put(KEY_RUS, russian);
        contentValues.put(KEY_TRANS, transcription);
        contentValues.put(KEY_DATE, date);
        sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
        sqLiteDatabase.close();
//        uploadDb = uploadDb();
    }

    /**
     * Обновляет дату изучения слова в таблице.
     *
     *
     * ИСПОЛЬЗОВАТЬ uploadDb(); после данного метода
     */
    public void updateWordDate(String date, String id) {
        contentValues = new ContentValues();
        sqLiteDatabase = dbHelper.getWritableDatabase();
        contentValues.put(KEY_DATE, date);
        sqLiteDatabase.update(DATABASE_TABLE, contentValues, KEY_ROWID + "= ?", new String[]{id});
        sqLiteDatabase.close();
//        uploadDb = uploadDb();
    }

    /**
     * Удаляет слово с базы.
     *
     *
     * ИСПОЛЬЗОВАТЬ uploadDb(); после данного метода
     */
    public void removeWordFromDb(String id) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_ROWID + " = " + "\"" + id + "\"");
        sqLiteDatabase.close();
//        uploadDb = uploadDb();
       }

    /**
     * Проверяем на наличие не известных слов в базе. Если есть то выбираем случайное
     */
    public String[] getRandomWord() throws NullPointerException {
        if (listUnknownWords == null || listUnknownWords.size() == 0) {
            Log.d(LOG_TAG, "слова завершились");
            throw new NullPointerException("Слова закончились");
        } else {
            Random random = new Random();
            rdWord = random.nextInt((listUnknownWords.size() < 100) ? listUnknownWords.size() : 100);
            Log.d(LOG_TAG, "Учим неизвестное " + listUnknownWords.get(rdWord)[0]);
            word = listUnknownWords.get(rdWord);
        }
        return word;
    }

    /**
     * (true) учить:Проверяет есть ли в базе такой день.
     * Нет то создаем и добавляем туда слово. Есть то просто добавляем.
     * (false) не учить.
     * Вносим измениния в памяти и в файле
     */
    public void isLearnWord(boolean bln) {
        if (bln) {/*Учить*/
            if (uploadDb.containsKey(date)) {
                uploadDb.get(date)
                        .add(word);
            } else {
                uploadDb.put(date, new ArrayList<String[]>());
                uploadDb.get(date)
                        .add(word);
            }
            updateWordDate(date.toString(), listUnknownWords.get(rdWord)[0]);
            listUnknownWords.remove(rdWord);
            learnedWords.add(word);
            Toast.makeText(mContext, "Учим", Toast.LENGTH_SHORT).show();
        } else {/*Не учить*/
            updateWordDate("0", listUnknownWords.get(rdWord)[0]);
            listUnknownWords.remove(rdWord);
            Toast.makeText(mContext, "Не учим", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Получить все слова по конкретной дате(дню)
     */
    public ArrayList<String[]> getListWordsByDate(String date) {
        return uploadDb.get(Integer.parseInt(date));
    }


    public ArrayList listUnknownWords() {
        ArrayList<String> listUnknownWords = new ArrayList<>();
        sqLiteDatabase = this.getReadableDatabase();
        cursor = sqLiteDatabase.query(DATABASE_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String english = cursor.getString(cursor.getColumnIndex(KEY_ENG));
            Integer date = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            if (date == 1) listUnknownWords.add(english);
        }
        return listUnknownWords;
    }

    public String[] getWord(String engWord){
        for(String[] word : listUnknownWords){
            if (word[0].equals(engWord))
                return word;
        }
        for(String[] word : listKnownWords){
            if (word[0].equals(engWord))
                return word;
        }
        for(String[] word : learnedWords) {
            if (word[0].equals(engWord))
                return word;
        }
        return null;
    }
}