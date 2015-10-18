package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class FrgListAllWord extends Fragment {
    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    ArrayList<Word> arrayList;
    ListView listView;
    MyAdapter adapter;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_list_all_words, null);
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        arrayList = new ArrayList<>();

        fillDate();

        adapter = new MyAdapter(getActivity(), arrayList);
        listView = (ListView) view.findViewById(R.id.lvMain);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        return view;
    }

    class MyAdapter extends BaseAdapter{
        Context context;
        ArrayList<Word> object;
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


            return convertView;
        }

    }

    class Word implements Comparable<Word>{
        String eng;
        String rus;
        boolean box;


        public Word(String eng, String rus, boolean box) {
            this.eng = eng;
            this.rus = rus;
            this.box = box;

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
                    arrayList.add(new Word(word[0], word[1], false));
                    Log.d(LOG_TAG, el.getKey().toString());
                    Log.d(LOG_TAG, word[0]);
                }
            }
        }
//        В сулчайнов порядке "random"
        if(setting.equals("random")) {
            Collections.shuffle(arrayList);
        }
//        В алфавитном порядке "alphabet"
        if(setting.equals("alphabet")){
            Collections.sort(arrayList);
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ((MyAdapter) getListAdapter()).notifyDataSetChanged();
//    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < arrayList.size(); i++) {
//            удаляем слово из бащы
            if (arrayList.get(i).box)
                DBHelper.getDbHelper(getActivity()).removeWordFromDb(arrayList.get(i).eng);
//            учим слово заново
//            if(arrayList.get(i).boxReLearn){
//                DBHelper.getDbHelper(getActivity()).updateWordDate(MainActivity.toDayDate , arrayList.get(i).eng);
//            }
        }
    }
}
