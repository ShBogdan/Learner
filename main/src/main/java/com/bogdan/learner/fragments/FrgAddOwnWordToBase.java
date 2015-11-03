package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

import java.util.ArrayList;

public class FrgAddOwnWordToBase extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = ":::::FrgAddOwnWordToBase::::";
    private DBHelper dbHelper;
    private EditText russianWord;
    private String transcription;
    private AutoCompleteTextView englishWord;
    private Button btn_addToBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_my_word, null);
        dbHelper = DBHelper.getDbHelper(getActivity());
        ArrayList<String> sample = dbHelper.engWords;
        englishWord = (AutoCompleteTextView) view.findViewById(R.id.originTextV);
        russianWord = (EditText) view.findViewById(R.id.russianTextV);

        btn_addToBase = (Button) view.findViewById(R.id.btn_add_to_base);
        btn_addToBase.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sample);
        englishWord.setAdapter(adapter);
        englishWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                russianWord.setText(dbHelper.getWord(parent.getItemAtPosition(position).toString())[1]);
                transcription = dbHelper.getWord(parent.getItemAtPosition(position).toString())[2];
                Toast.makeText(getActivity(), transcription, Toast.LENGTH_SHORT).show();

            }
        });


        return view;
    }

    public void onClick(View v) {
        String[] word = dbHelper.getWord(englishWord.getText().toString());
        switch (v.getId()) {
            case R.id.btn_add_to_base:
                if (TextUtils.isEmpty(englishWord.getText()) || englishWord.getText().toString().contains(" ")) {
                    englishWord.setError(getResources().getString(R.string.cant_be_space));

                } else if (TextUtils.isEmpty(russianWord.getText()) || russianWord.getText().toString().indexOf(" ") == 0) {
                    russianWord.setError(getResources().getString(R.string.cant_be_space));
                } else {
                    dbHelper.insertWord(
                            englishWord.getText().toString(),
                            russianWord.getText().toString(),
                            transcription,
                            MainActivity.toDayDate);
                    hideKeyboard();
                    Toast.makeText(getActivity(), "\"" + englishWord.getText().toString() + "\" " + getResources().getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
                    englishWord.setText("");
                    russianWord.setText("");
                    transcription = "";

                }
                break;
        }
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
