package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Arrays;

public class FrgGridLearnToday extends Fragment {
    ArrayList<String> later = new ArrayList<String>(Arrays.asList("A", "B", "C", "A", "B", "C", "d", "g", "n", "v", "x", "A", "B", "C", "A", "B", "C", "d", "g", "n", "v", "x"));

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_grid_learn_today, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.frg_grid_learn_today_adapter, R.id.gridbutton, later);
        gridview.setAdapter(arrayAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}
