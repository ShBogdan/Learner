package com.bogdan.learner.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.bogdan.learner.MainActivity;

import java.util.ArrayList;
import java.util.Map;

public class FrgListAllWord extends ListFragment {
    private final String LOG_TAG = ":::::::::::FrgList:::::::::::";
    ArrayList<String> data;
    String[] words;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = new ArrayList<>();

        for (Map.Entry<Integer, ArrayList<String[]>> el : MainActivity.uploadDb.entrySet()) {
            Log.d(LOG_TAG, String.valueOf(el.getKey()));
            data.add(String.valueOf(el.getKey()));
        }
        words = data.toArray(new String[data.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, words);
        setListAdapter(adapter);
    }
}
