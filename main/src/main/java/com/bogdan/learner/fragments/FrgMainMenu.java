package com.bogdan.learner.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bogdan.learner.MainActivity;
import com.bogdan.learner.R;
import com.rey.material.widget.Button;

public class FrgMainMenu extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    final String TAG = "onClick";
    final String LOG_TAG = "MyLog";
    FragmentListener mCallback;
    String appPackageName = "com.bogdan.english.card";
    CardView cardView_1, cardView_2, cardView_3, cardView_4, cardView_5;
    Button btn_learnToday, btn_addNewWord, btn_repeat, btn_buyIt, btn_options;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.frg_m_m, null);
        sp = mContext.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        showHelp();

        cardView_1 = (CardView) view.findViewById(R.id.card_view_1);
        cardView_2 = (CardView) view.findViewById(R.id.card_view_2);
        cardView_3 = (CardView) view.findViewById(R.id.card_view_3);
        cardView_4 = (CardView) view.findViewById(R.id.card_view_4);
        cardView_5 = (CardView) view.findViewById(R.id.card_view_5);

        btn_addNewWord = (Button) view.findViewById(R.id.btn_addMoreWord);
        btn_addNewWord.setOnClickListener(this);
//        btn_addNewWord.setOnTouchListener(this);

        btn_learnToday = (Button) view.findViewById(R.id.btn_learnToday);
        btn_learnToday.setOnClickListener(this);
//        btn_learnToday.setOnTouchListener(this);

        btn_repeat = (Button) view.findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(this);
//        btn_repeat.setOnTouchListener(this);

        btn_buyIt = (Button) view.findViewById(R.id.btn_buyIt);
        btn_buyIt.setOnClickListener(this);

        btn_options = (Button) view.findViewById(R.id.btn_options);
        btn_options.setOnClickListener(this);

        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Button btn_info = (Button) view.findViewById(R.id.btn_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                // layout to display
                dialog.setContentView(R.layout.info);
                // set color transpartent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        Button btn_like = (Button) view.findViewById(R.id.btn_like);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });

        if (MainActivity.isPremium) {
            cardView_4.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        mCallback.onButtonSelected(v);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (FragmentListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (view.getId() == (R.id.btn_addMoreWord))
                    cardView_1.setCardElevation(2);
                if (view.getId() == (R.id.btn_learnToday))
                    cardView_2.setCardElevation(2);
                if (view.getId() == (R.id.btn_repeat))
                    cardView_3.setCardElevation(2);
                if (view.getId() == (R.id.btn_buyIt))
                    cardView_4.setCardElevation(2);
                if (view.getId() == (R.id.btn_options))
                    cardView_5.setCardElevation(2);
                break;
            case MotionEvent.ACTION_UP:
                if (view.getId() == (R.id.btn_addMoreWord))
                    cardView_1.setCardElevation(6);
                if (view.getId() == (R.id.btn_learnToday))
                    cardView_2.setCardElevation(6);
                if (view.getId() == (R.id.btn_repeat))
                    cardView_3.setCardElevation(6);
                if (view.getId() == (R.id.btn_buyIt))
                    cardView_4.setCardElevation(6);
                if (view.getId() == (R.id.btn_options))
                    cardView_5.setCardElevation(6);
                break;
        }

        return true;
    }

    public void showHelp() {
        final CheckBox dontShowAgain;
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        LayoutInflater adbInflater = LayoutInflater.from(getActivity());
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = mContext.getSharedPreferences(SETTINGS, 0);
        String skipMessage = settings.getString("mmOption", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle(R.string.help);
        adb.setMessage(R.string.mmOptions);

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = mContext.getSharedPreferences(SETTINGS, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("mmOption", checkBoxResult);
                editor.apply();

                // Do what you want to do on "OK" action
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = mContext.getSharedPreferences(SETTINGS, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("mmOption", checkBoxResult);
                editor.apply();

                // Do what you want to do on "CANCEL" action
            }
        });

        if (skipMessage != null && !skipMessage.equals("checked")) {
            adb.show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    public void billInfo() {
        final CheckBox dontShowAgain;
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        LayoutInflater adbInflater = LayoutInflater.from(getActivity());
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = mContext.getSharedPreferences(SETTINGS, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle(R.string.toread);
        adb.setMessage(R.string.billInfo);

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = mContext.getSharedPreferences(SETTINGS, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.apply();

                // Do what you want to do on "OK" action
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = mContext.getSharedPreferences(SETTINGS, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.apply();

                // Do what you want to do on "CANCEL" action
            }
        });

        if (skipMessage != null && !skipMessage.equals("checked")) {
            adb.show();
        }
    }
}
