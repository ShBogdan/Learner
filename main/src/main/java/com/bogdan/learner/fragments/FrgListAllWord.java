package com.bogdan.learner.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class FrgListAllWord extends ListFragment {
    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    ArrayList<Word> data1;
    MyAdapter adapter;
    SharedPreferences sp;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        data1 = new ArrayList<>();

        fillDate();

        adapter = new MyAdapter(getActivity(), data1);
        setListAdapter(adapter);
    }

    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
        Context context;
        ArrayList<Word> object;
        CheckBox cbRemove;
        CheckBox cbReLearn;
        Word word;

        MyAdapter(Context context, ArrayList<Word> words){
            this.context = context;
            this.object = words;
        }

        @Override
        public int getCount() {
            return object.size();
        }

        @Override
        public Object getItem(int position) {
            return object.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.frg_list_words, parent, false);
            }
            word = (Word)getItem(position);

            ((TextView) convertView.findViewById(R.id.englishWord)).setText(word.eng);
            ((TextView) convertView.findViewById(R.id.russianWord)).setText(word.rus);

            cbRemove = (CheckBox) convertView.findViewById(R.id.cbRemove);
            cbReLearn = (CheckBox) convertView.findViewById(R.id.cbReLearn);

            cbRemove.setTag(position);
            cbRemove.setChecked(word.boxRemove);
            cbRemove.setOnCheckedChangeListener(this);
            cbReLearn.setTag(position);
            cbReLearn.setChecked(word.boxReLearn);
            cbReLearn.setOnCheckedChangeListener(this);

            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.cbRemove) {
                if (isChecked) {
                    word.boxRemove = true;
                    word.boxReLearn = false;
                    cbReLearn.setEnabled(false);
                } else {
                    word.boxRemove = false;
                    cbReLearn.setEnabled(true);
                }
            }
            if (buttonView.getId() == R.id.cbReLearn) {
                if (isChecked) {
                    word.boxReLearn = true;
                    word.boxRemove = false;
                    cbRemove.setEnabled(false);
                } else {
                    word.boxReLearn = false;
                    cbRemove.setEnabled(true);
                }
            }
        }
    }

    class Word implements Comparable<Word>{
        String eng;
        String rus;
        boolean boxRemove;
        boolean boxReLearn;

        public Word(String eng, String rus, boolean boxRemove, boolean boxReLearn) {
            this.eng = eng;
            this.rus = rus;
            this.boxRemove = boxRemove;
            this.boxReLearn = boxReLearn;
        }
        @Override
        public int compareTo(Word another) {
            return this.eng.toUpperCase()
                    .compareTo(another.eng.toUpperCase());
        }
    }

    /*заполняем базу для адаптнра*/
    void fillDate(){
        String setting = sp.getString("how_to_repeat", null);
//        по дате изучения "date"
        for (Map.Entry<Integer, ArrayList<String[]>> el : DBHelper.getDbHelper(getActivity()).uploadDb.entrySet()) {
            if (el.getKey() != 0 && el.getKey() != 1) {
                for (String[] word : el.getValue()) {
                    data1.add(new Word(word[0], word[1], false, false));
                    Log.d(LOG_TAG, el.getKey().toString());
                    Log.d(LOG_TAG, word[0]);
                }
            }
        }
//        В сулчайнов порядке "random"
        if(setting.equals("random")) {
            Collections.shuffle(data1);
        }
//        В алфавитном порядке "alphabet"
        if(setting.equals("alphabet")){
            Collections.sort(data1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MyAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < data1.size(); i++) {
//            удаляем слово из бащы
            if (data1.get(i).boxRemove)
                DBHelper.getDbHelper(getActivity()).removeWordFromDb(data1.get(i).eng);
//            учим слово заново
            if(data1.get(i).boxReLearn){
                DBHelper.getDbHelper(getActivity()).updateWordDate(MainActivity.toDayDate , data1.get(i).eng);
            }
        }
    }
}
