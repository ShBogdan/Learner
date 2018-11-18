package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;

public class FrgAddOwnWordToBase extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "FrgAddOwnWordToBase";
    private DBHelper dbHelper;
    private EditText russianWord;
    private String transcription;
    private String takenWord;
    private String russWord;
    private int word_id;
    private AutoCompleteTextView englishWord;
    private Button btn_addToBase;
    private boolean isChange;
//    NativeExpressAdView adView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_my_word, null);
        dbHelper = DBHelper.getDbHelper(getActivity());
        ArrayList<String> sample = dbHelper.engWords;
        englishWord = (AutoCompleteTextView) view.findViewById(R.id.originTextV);
        russianWord = (EditText) view.findViewById(R.id.russianTextV);

        btn_addToBase = (Button) view.findViewById(R.id.btn_add_to_base);
        btn_addToBase.setOnClickListener(this);

        // авто заполнение полей если есть слово в базе
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sample);
        englishWord.setAdapter(adapter);
        englishWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                russWord = dbHelper.getWord(parent.getItemAtPosition(position).toString())[1];
                russianWord.setText(russWord);
                takenWord = dbHelper.getWord(parent.getItemAtPosition(position).toString())[0];
                transcription = dbHelper.getWord(parent.getItemAtPosition(position).toString())[2];
                word_id = Integer.parseInt(dbHelper.getWord(parent.getItemAtPosition(position).toString())[3]);

                if(Integer.parseInt(dbHelper.getWord(parent.getItemAtPosition(position).toString())[5]) > 1){
                    Toast.makeText(getActivity(), "Это слово уже учили", Toast.LENGTH_LONG).show();
                }

            }
        });

//        adView = (NativeExpressAdView)view.findViewById(R.id.adView);

        AdRequest request = new AdRequest.Builder()
//                .addTestDevice("62D8BB95BA97339C7A028147DA6DE5AA")
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
//        adView.loadAd(request);
        return view;
    }

    public void onClick(View v) {
        Integer wordsAllowed = 10000;
//        if(!MainActivity.isPremium){
//            wordsAllowed = (DBHelper.getDbHelper(getActivity()).getListWordsByDate(MainActivity.toDayDate) == null)
//                    ? 0 : DBHelper.getDbHelper(getActivity()).getListWordsByDate(MainActivity.toDayDate).size();
//        }

        if(false){
            Toast.makeText(getActivity(), R.string.more_than_6, Toast.LENGTH_SHORT).show();
        }else{
            // проверяем на наличе заволненых полей и отсутствие пробелов
            if (TextUtils.isEmpty(englishWord.getText())) {
                englishWord.setError(getResources().getString(R.string.cant_be_space));
            } else if (TextUtils.isEmpty(russianWord.getText()) || russianWord.getText().toString().indexOf(" ") == 0) {
                russianWord.setError(getResources().getString(R.string.cant_be_space));
            } else {
                // если слово пользователя менялось то транскипции не будет
                if(!(englishWord.getText().toString()).equals(takenWord)){
                    transcription = "";
                    dbHelper.insertWord(
                            englishWord.getText().toString(),
                            russianWord.getText().toString(),
                            transcription,
                            MainActivity.toDayDate);
                    //если менялся только перевод
                } else if ((englishWord.getText().toString()).equals(takenWord) && !russWord.equals(russianWord.getText().toString())){
                    dbHelper.insertWord(
                            englishWord.getText().toString(),
                            russianWord.getText().toString(),
                            transcription,
                            MainActivity.toDayDate);
                }
                //если слово уже в базе и не менялось
                else {
                    dbHelper.updateWordDate(MainActivity.toDayDate, word_id);
                }

                hideKeyboard();
                Toast.makeText(getActivity(), "\"" + englishWord.getText().toString() + "\" " + getResources().getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
                englishWord.setText("");
                russianWord.setText("");
                transcription = "";
                isChange = true;
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

    @Override
    public void onStop() {
        super.onStop();
        if(isChange){
            DBHelper.getDbHelper(getActivity()).uploadDb();
            Log.d(LOG_TAG, "База перегруженна");
        }
        isChange = false;
    }
    @Override
    public void onResume() {
        super.onResume();

//        adView.resume();
    }

    @Override
    public void onPause() {
//        adView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
//        adView.destroy();

        super.onDestroy();
    }
}
