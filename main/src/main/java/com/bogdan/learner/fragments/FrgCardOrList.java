package com.bogdan.learner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bogdan.learner.R;


public class FrgCardOrList extends Fragment implements View.OnClickListener{

    Button btnCards, btnList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_m_card_or_list, null);
        btnCards = (Button) view.findViewById(R.id.btn_cards);
        btnList  = (Button) view.findViewById(R.id.btn_list);
        btnCards.setOnClickListener(this);
        btnList.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cards:
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FrgCardAllWords()).commit();
                break;
            case R.id.btn_list:
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FrgListAllWord()).commit();
                break;
        }
    }
}
