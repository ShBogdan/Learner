package com.bogdan.learner.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    ArrayList<MyListItem> data;
    ArrayAdapter<MyListItem> adapter;
    SharedPreferences sp;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        data = new ArrayList<>();

        fillDate();

        adapter = new MyAdapter(getActivity(), R.layout.frg_list_words, data);
        setListAdapter(adapter);
    }

    class MyAdapter extends ArrayAdapter<MyListItem> {
        public MyAdapter(Context context, int resource, ArrayList<MyListItem> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyListItem data = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.frg_list_words, null);
            }

            ((TextView) convertView.findViewById(R.id.englishWord)).setText(data.eng);
            ((TextView) convertView.findViewById(R.id.russianWord)).setText(data.rus);

            final CheckBox cbRemove = (CheckBox) convertView.findViewById(R.id.cbRemove);
            final CheckBox cbReLearn = (CheckBox) convertView.findViewById(R.id.cbReLearn);

//            Обрабатываем CheckBox. Либо удалить слово либо учить заново.
            CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.getId() == R.id.cbRemove) {
                        if (isChecked) {
                            data.boxRemove = true;
                            data.boxReLearn = false;
                            cbReLearn.setEnabled(false);
                        } else {
                            data.boxRemove = false;
                            cbReLearn.setEnabled(true);
                        }
                    }
                    if (buttonView.getId() == R.id.cbReLearn) {
                        if (isChecked) {
                            data.boxReLearn = true;
                            data.boxRemove = false;
                            cbRemove.setEnabled(false);
                        } else {
                            data.boxReLearn = false;
                            cbRemove.setEnabled(true);
                        }
                    }

                }
            };

            cbRemove.setTag(position);
            cbRemove.setOnCheckedChangeListener(onCheckedChangeListener);
            cbReLearn.setTag(position);
            cbReLearn.setOnCheckedChangeListener(onCheckedChangeListener);


            return convertView;
        }
    }

    class MyListItem implements Comparable<MyListItem>{
        String eng;
        String rus;
        boolean boxRemove;
        boolean boxReLearn;

        public MyListItem(String eng, String rus, boolean boxRemove, boolean boxReLearn) {
            this.eng = eng;
            this.rus = rus;
            this.boxRemove = boxRemove;
            this.boxReLearn = boxReLearn;
        }
        @Override
        public int compareTo(MyListItem another) {
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
                    data.add(new MyListItem(word[0], word[1], false, false));
                    Log.d(LOG_TAG, el.getKey().toString());
                    Log.d(LOG_TAG, word[0]);
                }
            }
        }
//        В сулчайнов порядке "random"
        if(setting.equals("random")) {
            Collections.shuffle(data);
        }
//        В алфавитном порядке "alphabet"
        if(setting.equals("alphabet")){
            Collections.sort(data);
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
        for (int i = 0; i < data.size(); i++) {
//            удаляем слово из бащы
            if (data.get(i).boxRemove)
                DBHelper.getDbHelper(getActivity()).removeWordFromDb(data.get(i).eng);
//            учим слово заново
            if(data.get(i).boxReLearn){
                DBHelper.getDbHelper(getActivity()).updateWordDate(MainActivity.toDayDate , data.get(i).eng);
            }
        }
    }
}
