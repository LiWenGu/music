package com.example.li.music.activity;

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
public class Fragment_Music extends Fragment {

    Fragment_Music_Begin fragment_music_begin;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_music_layout, null);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragment_music_begin = new Fragment_Music_Begin();
        transaction.replace(R.id.main_music_layout, fragment_music_begin);
        transaction.commit();
        return view;
    }
}