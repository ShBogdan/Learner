package com.bogdan.learner;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class DayLibrary {
    private final String LOG_TAG = "::::DayLibrary::::";
    private Context context;
    private Integer date = Integer.parseInt(MainActivity.toDayDate);
    private TreeMap<Integer, ArrayList<String[]>> uploadDb = MainActivity.uploadDb; /*ключ: дата изучения. Валюе: изученые слова.*/
    private ArrayList<String[]> listUnknownWords = uploadDb.get(1);       /*под ключем 1 не изученные слова*/
    private ArrayList<String[]> listKnownWords = uploadDb.get(0);       /*под ключем 0 ранее известные слова пользователю*/
    private int rdWord;
    private String[] word;

    public DayLibrary(Context context) {
        this.context = context;
    }

    /**
     * Проверяем на наличие не известных слов в базе. Если есть то выбираем случайное
     */
    public String[] getWord() {
        if (listUnknownWords == null || listUnknownWords.size() == 0) {
            Log.d(LOG_TAG, "Поздравляем слова завершились");
            word = new String[]{"EMPTY"};
        } else {
            Random random = new Random();
            rdWord = random.nextInt((listUnknownWords.size() < 100) ? listUnknownWords.size() : listUnknownWords.size() / 5);/*если больше 100 слов то рандом в 5 раз меньше*/
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
    public void setWord(boolean bln) {
        if (bln) {/*Учить*/
            if (uploadDb.containsKey(date)) {
                uploadDb.get(date)
                        .add(word);
            } else {
                uploadDb.put(date, new ArrayList<String[]>());
                uploadDb.get(date)
                        .add(word);
            }
            new DBHelper(context).updateWordDate(date.toString(), listUnknownWords.get(rdWord)[0]);
            listUnknownWords.remove(rdWord);
            Toast.makeText(context, "Учим", Toast.LENGTH_SHORT).show();
        } else {/*Не учить*/
            new DBHelper(context).updateWordDate("0", listUnknownWords.get(rdWord)[0]);
            listUnknownWords.remove(rdWord);
            Toast.makeText(context, "Не учим", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Получить все слова по конкретной дате(дню)
     */
    public ArrayList<String[]> getListWordsByDate(String date) {
        return uploadDb.get(Integer.parseInt(date));
    }
}



























//    /**1. Естьли не известные слова?
//     * 2. Рандомно выбираем из ~первой сотни
//     * 3. Учить: Ставим дату в upload, удаляем из памяти
//     * 4. Записываем в sqlDb*/
//    public void addWord() {
//        if (listUnknownWords == null || listUnknownWords.size() == 0) {
//            Log.d(LOG_TAG, "Поздравляем слова завершились");
//        } else {
//            Random random = new Random();
//            int rdWord = random.nextInt((listUnknownWords.size() < 100) ? listUnknownWords.size() : listUnknownWords.size() / 5);/*если больше 100 слов то рандом в 5 раз меньше*/
//            Log.d(LOG_TAG, "Учим неизвестное " +listUnknownWords.get(rdWord)[0]);
//            if(true) {/*Не знаю слово*/
//                if (uploadDb.containsKey(date)) {
//                    uploadDb.get(date).add(listUnknownWords.get(rdWord));
//                    Log.d(LOG_TAG, "Изменили в загруженной базе " + listUnknownWords.get(rdWord)[0] + " " + rdWord);
//                } else {
//                    uploadDb.put(date, new ArrayList<String[]>());
//                    uploadDb.get(date).add(listUnknownWords.get(rdWord));
//                    Log.d(LOG_TAG, "Создали дату и изменили в загруженной базе " + listUnknownWords.get(rdWord)[0] + " " + rdWord);
//                }
//                new DBHelper(context).updateWordDate(date.toString(), listUnknownWords.get(rdWord)[0]);
//                Log.d(LOG_TAG, "Удалили из списка незвестных и везде(т.к. ссылка) " + listUnknownWords.get(rdWord)[0] + " " + rdWord);
//                listUnknownWords.remove(rdWord);
//            } else {/*Знаю*/
//                new DBHelper(context).updateWordDate("0", listUnknownWords.get(rdWord)[0]);
//                listUnknownWords.remove(rdWord);
//            }
//        }
//    }

