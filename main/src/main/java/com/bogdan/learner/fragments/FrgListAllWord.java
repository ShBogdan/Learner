package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class FrgListAllWord extends Fragment implements View.OnClickListener{
    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    ArrayList<Word> arrayList;
    ListView listView;
    MyAdapter adapter;
    SharedPreferences sp;
    Button btn_remove, btn_relearn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.frg_list_all_words, null);
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        btn_remove = (Button) view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(this);
        btn_relearn = (Button) view.findViewById(R.id.btn_relearn);
        btn_relearn.setOnClickListener(this);

        fillDate();
        adapter = new MyAdapter(getActivity(), arrayList);
        listView = (ListView) view.findViewById(R.id.lvMain);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = (Word) parent.getAdapter().getItem(position);
//                меняем состояние Word и кравим view
                if (word.box) {
                    word.box = false;
                } else {
                    word.box = true;
                }
                if (word.box) {
                    view.findViewById(R.id.myLay).setBackgroundColor(Color.parseColor("#818CD6"));
                } else {
                    view.findViewById(R.id.myLay).setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_remove){
            for (int i = 0; i < arrayList.size(); i++){
                if (arrayList.get(i).box)
                    DBHelper.getDbHelper(getActivity()).removeWordFromDb(arrayList.get(i).eng);
                arrayList.remove(i);
            }
            listView.invalidate();
            adapter = new MyAdapter(getActivity(), arrayList);
            listView.setAdapter(adapter);

        }
        if(v.getId()==R.id.btn_relearn){
            for (int i = 0; i < arrayList.size(); i++){
                if (arrayList.get(i).box)
                    DBHelper.getDbHelper(getActivity()).updateWordDate(MainActivity.toDayDate, arrayList.get(i).eng);
                arrayList.remove(i);
            }
            Toast.makeText(getActivity(),"Слова обновлены", Toast.LENGTH_SHORT).show();
        }
        DBHelper.getDbHelper(getActivity()).uploadDb();
    }

    class MyAdapter extends BaseAdapter {
        Context context;
        ArrayList<Word> object;
        Word word;

        MyAdapter(Context context, ArrayList<Word> words) {
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
            word = (Word) getItem(position);

            if (word.box) {
                convertView.findViewById(R.id.myLay).setBackgroundColor(Color.parseColor("#818CD6"));
            } else {
                convertView.findViewById(R.id.myLay).setBackgroundColor(Color.parseColor("#ffffff"));
            }

            ((TextView) convertView.findViewById(R.id.englishWord)).setText(word.eng);
            ((TextView) convertView.findViewById(R.id.russianWord)).setText(word.rus);
            ((TextView) convertView.findViewById(R.id.tv_trans)).setText(String.valueOf(word.trans));

            return convertView;
        }

    }

    class Word implements Comparable<Word> {
        String eng;
        String rus;
        String trans;
        boolean box;


        public Word(String eng, String rus, String trans, boolean box) {
            this.eng = eng;
            this.rus = rus;
            this.box = box;
            this.trans = trans;

        }

        @Override
        public int compareTo(Word another) {
            return this.eng.toUpperCase()
                    .compareTo(another.eng.toUpperCase());
        }
    }

    /*заполняем базу для адаптнра*/
    void fillDate() {
        arrayList = new ArrayList<>();
        String setting = sp.getString("how_to_repeat", null);
//        по дате изучения "date"
        for (Map.Entry<Integer, ArrayList<String[]>> el : DBHelper.getDbHelper(getActivity()).uploadDb.entrySet()) {
            if (el.getKey() != 0 && el.getKey() != 1) {
                for (String[] word : el.getValue()) {
                    arrayList.add(new Word(word[0], word[1], word[2], false));
                    Log.d(LOG_TAG, el.getKey().toString());
                    Log.d(LOG_TAG, word[0]);
                }
            }
        }
//        В сулчайнов порядке "random"
        if (setting.equals("random")) {
            Collections.shuffle(arrayList);
        }
//        В алфавитном порядке "alphabet"
        if (setting.equals("alphabet")) {
            Collections.sort(arrayList);
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ((MyAdapter) getListAdapter()).notifyDataSetChanged();
//    }

}
