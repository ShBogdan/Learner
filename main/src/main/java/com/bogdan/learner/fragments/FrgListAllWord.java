package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FrgListAllWord extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    ArrayList<Word> arrayList;
    RecyclerView mRecyclerView;
    MyAdapter adapter;
    SharedPreferences sp;
    Button btn_remove, btn_relearn;


    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.frg_list_all_words, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.r_view);
        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        btn_remove = (Button) view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(this);
        btn_relearn = (Button) view.findViewById(R.id.btn_relearn);
        btn_relearn.setOnClickListener(this);

        fillDate();


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter( arrayList);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onClick(View v) {
        boolean isChange = false;
        //        удаляем выделенные слова
        if (v.getId() == R.id.btn_remove) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isSelect) {
                    DBHelper.getDbHelper(getActivity()).removeWordFromDb(arrayList.get(i).id);
                    arrayList.remove(i);
                    i--;
                    isChange = true;
                }
            }
        }
        //        обновляем дату у выделенных слов
        if (v.getId() == R.id.btn_relearn) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isSelect){
                    DBHelper.getDbHelper(getActivity()).updateWordDate(MainActivity.toDayDate, Integer.parseInt(arrayList.get(i).id));
                    arrayList.get(i).isSelect = false;
                    isChange = true;
                }
            }
        }

        if(isChange) {
            Toast.makeText(getActivity(), R.string.wasUpdate, Toast.LENGTH_SHORT).show();
            DBHelper.getDbHelper(getActivity()).uploadDb();
            //        перегружаем view
            adapter.notifyDataSetChanged();
        }else
            Toast.makeText(getActivity(), R.string.nothing_to_update, Toast.LENGTH_SHORT).show();


    }


    class Word implements Comparable<Word> {
        String eng;
        String rus;
        String trans;
        String id;
        boolean isSelect;


        public Word(String eng, String rus, String trans, boolean isSelect, String id) {
            this.eng = eng;
            this.rus = rus;
            this.isSelect = isSelect;
            this.trans = trans;
            this.id = id;

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
        boolean add_know_words = sp.getBoolean("add_know_words", false);
        //        по дате изучения "date"
        for (Map.Entry<Integer, ArrayList<String[]>> el : DBHelper.getDbHelper(getActivity()).uploadDb.entrySet()) {
            if ((el.getKey() != 0 || add_know_words) && el.getKey() != 1) {
                for (String[] word : el.getValue()) {
                    arrayList.add(new Word(word[0], word[1], word[2], false, word[3]));
                    Log.d(LOG_TAG, el.getKey().toString());
                    Log.d(LOG_TAG, word[0]);
                }
            }
            Collections.reverse(arrayList);
        }
        //        В случайнов порядке "random"
        if (setting.equals("random")) {
            Collections.shuffle(arrayList);
        }
        //        В алфавитном порядке "alphabet"
        if (setting.equals("alphabet")) {
            Collections.sort(arrayList);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        Context context;
        ArrayList<Word> _object;
        Word word;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView eng;
            public TextView rus;
            public TextView trans;
            LinearLayout btn;
            CardView thisCv;

            public ViewHolder(View v) {
                super(v);

                eng   = (TextView) v.findViewById(R.id.englishWord);
                rus   = (TextView) v.findViewById(R.id.russianWord);
                trans = (TextView) v.findViewById(R.id.tv_trans);
                btn = (LinearLayout) v.findViewById(R.id.btn_card);
                thisCv = (CardView) v.findViewById(R.id.myLay);
                btn.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                ((CardView)thisCv).setCardBackgroundColor(Color.parseColor("#818CD6"));
                Word word = _object.get(getAdapterPosition());
                //                меняем состояние Word и красим view
                if (word.isSelect) {
                    word.isSelect = false;
                } else {
                    word.isSelect = true;
                }
                if (word.isSelect) {
                    ((CardView)thisCv).setCardBackgroundColor(Color.parseColor("#818CD6"));

                } else {
                    ((CardView)thisCv).setCardBackgroundColor(Color.parseColor("#ffffff"));

                }
            }
        }

        public MyAdapter(ArrayList<Word> object) {
            _object = object;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.frg_list_words, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.eng.setText(_object.get(position).eng);
            holder.rus.setText(_object.get(position).rus);
            holder.trans.setText(_object.get(position).trans);
            Word word = _object.get(position);
            Log.d(LOG_TAG, word.eng);

            //                меняем состояние Word и красим view
            if (word.isSelect) {
                ((CardView)holder.thisCv).setCardBackgroundColor(Color.parseColor("#818CD6"));
            } else {
                ((CardView)holder.thisCv).setCardBackgroundColor(Color.parseColor("#ffffff"));
            }

        }

        @Override
        public int getItemCount() {
            return _object.size();
        }

        public List<Word> getList() {
            return this._object;
        }

    }

}
