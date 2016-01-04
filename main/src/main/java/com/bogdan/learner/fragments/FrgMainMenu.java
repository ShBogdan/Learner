package com.bogdan.learner.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bogdan.learner.R;

public class FrgMainMenu extends Fragment implements View.OnClickListener{
    private final String TAG = "onClick";
    FragmentListener mCallback;
    String appPackageName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_m, null);

        appPackageName = getActivity().getApplication().getPackageName();

        Button btn_learnToday = (Button) view.findViewById(R.id.btn_learnToday);
        btn_learnToday.setOnClickListener(this);
        Button btn_addNewWord = (Button) view.findViewById(R.id.btn_addMoreWord);
        btn_addNewWord.setOnClickListener(this);
        Button btn_repeat = (Button) view.findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(this);
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
}
