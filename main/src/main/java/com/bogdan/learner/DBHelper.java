package com.bogdan.learner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = ":::::::::::::DBHelper:::::::::::::";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ENG   = "english";
    public static final String KEY_RUS   = "russian";
    public static final String KEY_TRANS = "transcription";
    public static final String KEY_VOICE = "voice";
    public static final String KEY_DATE  = "date";

    private static final String DATABASE_NAME   = "dictionary.sqlite";
    private static final String DATABASE_TABLE  = "words";
    private static final int DATABASE_VERSION   = 1;
    private static final String DATABASE_CREATE = "create table words (_id integer primary key autoincrement, " +
            "english text not null, " +
            "russian text not null, " +
            "transcription text, " +
            "voice text, " +
            "date text not null);";

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    ContentValues contentValues;
    Cursor cursor;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(LOG_TAG, "DATABASE_CREATE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper = this;
    }

    /**Добавляет ваше слово в таблицу*/
    public void insertWords(String english, String russian, String transcription, String date) {
        contentValues = new ContentValues();
        sqLiteDatabase = dbHelper.getWritableDatabase();
        contentValues.put(KEY_ENG, english);
        contentValues.put(KEY_RUS, russian);
        contentValues.put(KEY_TRANS, transcription);
        contentValues.put(KEY_DATE, date);
        sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
        sqLiteDatabase.close();
        MainActivity.uploadDb = this.uploadDb();
    }

    /**Обновляет дату изучения слова в таблице*/
    public void updateWordDate(String date, String english) {
        contentValues = new ContentValues();
        sqLiteDatabase = dbHelper.getWritableDatabase();
        contentValues.put(KEY_DATE, date);
        sqLiteDatabase.update(DATABASE_TABLE, contentValues, KEY_ENG + "= ?", new String[]{english});
    }

    /**Загружает таблибу и возвращает Map. Ключ дата изучения значение массив строк(значения слова)*/
    public TreeMap uploadDb() {
        TreeMap<Integer, ArrayList<String[]>> wordsDb = new TreeMap<>();
        sqLiteDatabase = this.getReadableDatabase();
        cursor = sqLiteDatabase.query(DATABASE_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String english = cursor.getString(cursor.getColumnIndex(KEY_ENG));
            String russian = cursor.getString(cursor.getColumnIndex(KEY_RUS));
            String transcription = cursor.getString(cursor.getColumnIndex(KEY_TRANS));
            String voicePatch = cursor.getString(cursor.getColumnIndex(KEY_VOICE));
            Integer date = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_DATE)));

            if (wordsDb.containsKey(date)) {
                    /*Put in TreeMap "wordsDB" an ArrayList new String[]*/
                wordsDb.get(date).add(new String[]{english, russian, transcription, voicePatch});
            } else {
                wordsDb.put(date, new ArrayList<String[]>());
                wordsDb.get(date).add(new String[]{english, russian, transcription, voicePatch});
            }
        }
//        Log.d(LOG_TAG, "words_size===============================================" + wordsDb.size());
//
//        for (Map.Entry<String, ArrayList<String[]>> el : wordsDb.entrySet()) {
//            ArrayList<String[]> al = el.getValue();
//            Log.d(LOG_TAG, ("Kay::::::::::::::::::::::::::::::::::::::::::::::: " + el.getKey()));
//            for (int i = 0; i < al.size(); i++) {
//                Log.d(LOG_TAG, "array:::::::::::::::::::::::::::::::::::::::::: " + Arrays.asList(al.get(i)) + ", ");
//            }
//        }
        return wordsDb;
    }

    /*Уже не нужен*/
    public ArrayList listUnknownWords(){
        ArrayList<String> listUnknownWords = new ArrayList<>();
        sqLiteDatabase = this.getReadableDatabase();
        cursor = sqLiteDatabase.query(DATABASE_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            String english = cursor.getString(cursor.getColumnIndex(KEY_ENG));
            Integer date = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            if(date == 1) listUnknownWords.add(english);
        }
        return listUnknownWords;
    }
}
