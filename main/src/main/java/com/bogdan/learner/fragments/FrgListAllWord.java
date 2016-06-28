package com.bogdan.learner.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class FrgListAllWord extends Fragment implements View.OnClickListener{
    private final String LOG_TAG = "::::FrgListAllWord::::";
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    private ArrayList<Word> arrayList;
    private RecyclerView mRecyclerView;
    private MyAdapter adapter;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private Button btn_remove, btn_relearn;
    private CardView buttons;
    private boolean isChange = false;
    private Animation mAnimation;
    private int mSelectCount;
    public SearchView search;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.frg_list_all_words, container, false);
//        RecyclerViewHeader header = (RecyclerViewHeader) view.findViewById(R.id.header);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.r_view);

        sp = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        search = (SearchView) view.findViewById(R.id.search);

        btn_remove = (Button) view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(this);
        btn_relearn = (Button) view.findViewById(R.id.btn_relearn);
        btn_relearn.setOnClickListener(this);


        buttons = (CardView) view.findViewById(R.id.buttons);
        buttons.setVisibility(View.INVISIBLE);
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.show_buttons);


        fillDate();

        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(mRecyclerView);
        fastScroller.setHandleColor(Color.parseColor("#CFD8DC"));
        fastScroller.setBarColor(Color.parseColor("#CFD8DC"));
        mRecyclerView.setOnScrollListener(fastScroller.getOnScrollListener());

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(arrayList);
        mRecyclerView.setAdapter(adapter);
//        header.attachTo(mRecyclerView);
        search.setOnQueryTextListener(listener);

        showHelp();

        return view;
    }

    @Override
    public void onClick(View v) {
        isChange = false;
        //        удаляем выделенные слова
        if (v.getId() == R.id.btn_remove) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).isSelect) {
                    DBHelper.getDbHelper(getActivity()).removeWordFromDb(arrayList.get(i).id);
                    arrayList.remove(i);
                    i--;
                    isChange = true;
                    search.onActionViewCollapsed();

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
                    search.refreshDrawableState();
                }
            }
        }

        if(isChange) {
            Toast.makeText(getActivity(), R.string.wasUpdate, Toast.LENGTH_SHORT).show();
            //            DBHelper.getDbHelper(getActivity()).uploadDb();
            new MyTask().execute();

            //        перегружаем view
            adapter.notifyDataSetChanged();
        }else
            Toast.makeText(getActivity(), R.string.nothing_to_update, Toast.LENGTH_SHORT).show();

        mSelectCount = 0;
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.hidd_button);

        buttons.setAnimation(mAnimation);
        mRecyclerView.setPadding(0, 0, 0, 0);
        buttons.setAnimation(mAnimation);
        buttons.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttons.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }

    class Word implements Comparable<Word> {
        String eng;
        String rus;
        String trans;
        String id;
        boolean isSelect;
        boolean isFavorite;


        public Word(String eng, String rus, String trans, boolean isSelect, boolean isFavorite, String id) {
            this.eng = eng;
            this.rus = rus;
            this.isSelect = isSelect;
            this.isFavorite = isFavorite;
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
                    arrayList.add(new Word(word[0], word[1], word[2], false, Boolean.parseBoolean(word[4]), word[3]));
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
        ArrayList<Word> wordsArray;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView eng;
            public TextView rus;
            public TextView trans;
            public CheckBox favorite;
            public LinearLayout btn;
            public CardView thisCv;
            public ImageView btn_change_word;
            String id;

            public ViewHolder(View v) {
                super(v);
                favorite = (CheckBox) v.findViewById(R.id.favorite);
                eng   = (TextView) v.findViewById(R.id.englishWord);
                rus   = (TextView) v.findViewById(R.id.russianWord);
                trans = (TextView) v.findViewById(R.id.tv_trans);
                btn = (LinearLayout) v.findViewById(R.id.btn_card);
                thisCv = (CardView) v.findViewById(R.id.myLay);
                btn_change_word = (ImageView) v.findViewById(R.id.btn_change);
                btn.setOnClickListener(this);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isChange = true;
                        if(favorite.isChecked()){
                            DBHelper.getDbHelper(getActivity()).setFavorite("true", id);
                            for (int i = 0; i <arrayList.size() ; i++) {
                                if(arrayList.get(i).id.equals(id)){
                                    arrayList.get(i).isFavorite = true;
                                }
                            }
                        }else {
                            DBHelper.getDbHelper(getActivity()).setFavorite("false", id);
                            for (int i = 0; i <arrayList.size() ; i++) {
                                if(arrayList.get(i).id.equals(id)){
                                    arrayList.get(i).isFavorite = false;
                                }
                            }
                        }
                    }
                });
                btn_change_word.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        dialog.setContentView(R.layout.dialog_change_item);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        final EditText et_eng, et_rus;
                        et_eng = (EditText) dialog.findViewById(R.id.englishWord);
                        et_rus = (EditText) dialog.findViewById(R.id.russianWord);

                        et_eng.setText(eng.getText());
                        et_rus.setText(rus.getText());


                        Button btn_save, btn_cancel;
                        btn_save = (Button) dialog.findViewById(R.id.btn_save);
                        btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                        btn_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eng.setText(et_eng.getText());
                                rus.setText(et_rus.getText());
                                DBHelper.getDbHelper(getActivity()).changeWord(eng.getText().toString(), rus.getText().toString(), id);
                                isChange = true;
                                dialog.cancel();
                            }
                        });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });

                    }
                });

            }

            @Override
            public void onClick(View v) {
                //                ((CardView)thisCv).setCardBackgroundColor(Color.parseColor("#818CD6"));
                Word word = wordsArray.get(getAdapterPosition());

                int colorFrom = Color.parseColor("#ffffff");
                int colorTo = Color.parseColor("#818CD6");


                //                меняем состояние Word и красим view
                if (word.isSelect) {
                    word.isSelect = false;
                } else {
                    word.isSelect = true;
                }
                if (word.isSelect) {
                    //                    ((CardView)thisCv).setCardBackgroundColor(Color.parseColor("#818CD6"));
                    colorFrom = Color.parseColor("#ffffff");
                    colorTo = getResources().getColor(R.color.my_background_1);
                    mSelectCount++;


                } else {
                    //                    ((CardView)thisCv).setCardBackgroundColor(Color.parseColor("#ffffff"));
                    colorFrom = getResources().getColor(R.color.my_background_1);
                    colorTo = Color.parseColor("#ffffff");
                    mSelectCount--;
                }
                if(mSelectCount>0 && buttons.getVisibility() == View.INVISIBLE){
                    mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.show_buttons);
                    buttons.setAnimation(mAnimation);
                    buttons.setVisibility(View.VISIBLE);
                    buttons.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setPadding(0,0,0,buttons.getHeight()-(dpToPx(10)));
                        }
                    }, 200);

                }if (mSelectCount == 0 && buttons.getVisibility() == View.VISIBLE) {
                    mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.hidd_button);
                    buttons.setAnimation(mAnimation);
                    buttons.setVisibility(View.INVISIBLE);
                    buttons.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setPadding(0, 0, 0, 0);
                        }
                    }, 400);
                }

                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(500); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        thisCv.setCardBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }
        }

        public MyAdapter(ArrayList<Word> object) {
            wordsArray = object;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.frg_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.eng.setText(wordsArray.get(position).eng);
            holder.rus.setText(wordsArray.get(position).rus);
            holder.trans.setText(wordsArray.get(position).trans);
            holder.favorite.setChecked(wordsArray.get(position).isFavorite);
            Word word = wordsArray.get(position);
            holder.id = word.id;

            //                меняем состояние Word и красим view
            if (word.isSelect) {
                ((CardView)holder.thisCv).setCardBackgroundColor( getResources().getColor(R.color.my_background_1));

            } else {
                ((CardView)holder.thisCv).setCardBackgroundColor(Color.parseColor("#ffffff"));
            }

            if(word.isFavorite){
                holder.favorite.setChecked(word.isFavorite);
            }else {
                holder.favorite.setChecked(word.isFavorite);

            }

        }

        @Override
        public int getItemCount() {
            return wordsArray.size();
        }

        public List<Word> getList() {
            return this.wordsArray;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MyLog", "onStop");
        new MyTask().execute();


    }
    class MyTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog mProgressDialog = createProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
             if(isChange){
                DBHelper.getDbHelper(getActivity()).uploadDb();
                isChange = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();


        }

        public ProgressDialog createProgressDialog(Context mContext) {
            ProgressDialog dialog = new ProgressDialog(mContext);
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {

            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.progressdialog);
            // dialog.setMessage(Message);
            return dialog;
        }

    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final ArrayList<Word> filteredList = new ArrayList<>();

            for (int i = 0; i < arrayList.size(); i++) {

                final String text = arrayList.get(i).eng.toLowerCase();
                if (text.contains(query)) {
                    filteredList.add(arrayList.get(i));
                }
            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new MyAdapter(filteredList);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();  // data set changed
            return true;

        }
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    public void showHelp(){
        editor = sp.edit();
        if(!sp.contains("howToDell")) {
            editor.putBoolean("howToDell", true).apply();
        }
        if(sp.getBoolean("howToDell", true)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.help)
                    .setMessage(R.string.help_show)
                    .setCancelable(false)
                    .setNegativeButton("Я прочитал",
                                       new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int id) {
                                               editor.putBoolean("howToDell", false).apply();
                                               dialog.cancel();
                                           }
                                       });
            AlertDialog alert = builder.create();
            alert.show();
        }

    };
}
