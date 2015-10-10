package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bogdan.learner.DBHelper;
import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;

public class FrgAddOwnWordToBase extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = ":::::FrgAddOwnWordToBase::::";
    private DBHelper dbHelper;
    private EditText englishWord, russianWord;
    private Button btn_addToBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_my_word, null);

        englishWord = (EditText) view.findViewById(R.id.originTextV);
        russianWord = (EditText) view.findViewById(R.id.russianTextV);

        btn_addToBase = (Button) view.findViewById(R.id.btn_add_to_base);
        btn_addToBase.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        dbHelper = DBHelper.getDbHelper(getActivity());
        switch (v.getId()) {
            case R.id.btn_add_to_base:
                if (TextUtils.isEmpty(englishWord.getText()) || englishWord.getText().toString().contains(" ")) {
                    englishWord.setError(getResources().getString(R.string.cant_have_space));

                } else if (TextUtils.isEmpty(russianWord.getText()) || russianWord.getText().toString().indexOf(" ") == 0) {
                    russianWord.setError(getResources().getString(R.string.cant_have_space));

                } else {
                    if (!dbHelper.isWordInBase(englishWord.getText().toString())) {
                        dbHelper.insertWord(
                                englishWord.getText().toString(),
                                russianWord.getText().toString(),
                                MainActivity.toDayDate);
                        hideKeyboard();
                        Toast.makeText(getActivity(), "\"" + englishWord.getText().toString() + "\" " + getResources().getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
                        englishWord.setText("");
                        russianWord.setText("");
                        break;
                    } else
                        Toast.makeText(getActivity(), R.string.word_in_the_base, Toast.LENGTH_SHORT).show();
                }
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
