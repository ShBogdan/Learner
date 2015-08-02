package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

public class FrgAddMyWord extends Fragment implements View.OnClickListener{
    private final  String LOG_TAG = ":::::FrgAddMyWord::::";
    private DBHelper dbHelper;
    private EditText englishWord, transWord, russianWord;
    private Button btn_addToBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_my_word, null);

        englishWord   = (EditText) view.findViewById(R.id.originTextV);
        transWord     = (EditText) view.findViewById(R.id.transcriptionTextV);
        russianWord   = (EditText) view.findViewById(R.id.russianTextV);

        btn_addToBase = (Button)   view.findViewById(R.id.btn_add_to_base);
        btn_addToBase.setOnClickListener(this);

        return view;
    }

    public void onClick(View v){
        dbHelper = new DBHelper(getActivity());
        switch (v.getId()){

            case R.id.btn_add_to_base:
                dbHelper.insertWords(
                        englishWord.getText().toString(),
                        transWord.getText().toString(),
                        russianWord.getText().toString(),
                        MainActivity.toDayDate);

                hideKeyboard();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FrgAddMyWord()).commit();
                Log.i(LOG_TAG, "Add to base: " + englishWord.getText().toString()+ "." + MainActivity.toDayDate);
                break;
        }
        dbHelper.close();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
