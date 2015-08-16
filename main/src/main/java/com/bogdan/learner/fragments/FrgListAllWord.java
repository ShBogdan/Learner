package com.bogdan.learner.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Map;

public class FrgListAllWord extends ListFragment {
    private final String LOG_TAG = "::::FrgList::::";
    ArrayList<String[]> data;


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String[] strings = (String[])getListAdapter().getItem(position);
        Toast.makeText(getActivity(), strings[0], Toast.LENGTH_LONG).show();
        DBHelper.getDbHelper(getActivity()).removeWordFromDb(strings[0]);
        DBHelper.getDbHelper(getActivity()).removeWordFromUploadDb();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<String[]>> el : MainActivity.uploadDb.entrySet()) {
            for(String[] word : el.getValue()){
                data.add(word);
                Log.d(LOG_TAG, word[0]);
            }
        }
        ArrayAdapter<String[]> adapter = new MyAdapter(getActivity(), R.layout.frg_list_words, data);
        setListAdapter(adapter);
    }
    class MyAdapter extends ArrayAdapter<String[]>{

        public MyAdapter(Context context, int resource, ArrayList<String[]> objects) {
            super(context, resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String[] data = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.frg_list_words, null);
            }
            ((TextView)convertView.findViewById(R.id.englishWord)).setText(data[0]);
            ((TextView)convertView.findViewById(R.id.russianWord)).setText(data[1]);
            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MyAdapter)getListAdapter()).notifyDataSetChanged();
    }
}
