package com.example.li.diary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.li.music.R;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Diary extends Fragment {
    Fragment_Diary_begin fragment_diary_begin;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_diary_layout, null);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragment_diary_begin = new Fragment_Diary_begin();
        transaction.replace(R.id.main_diary_layout, fragment_diary_begin);
        transaction.commit();
        return view;
    }
}
