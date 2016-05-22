package com.example.li.diary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.li.diary.Util.CheckInput;
import com.example.li.diary.db.DiaryDB;
import com.example.li.music.R;
import com.example.li.music.Util.LogUtil;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Diary_Edit extends Fragment implements View.OnClickListener{

    private EditText titleEt, bodyEt;
    private Button btn;
    private DiaryDB diaryDB;
    private Long id;
    private Fragment_Diary_begin fragment_diary_begin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_edit, null);
        diaryDB = new DiaryDB(getContext());

        initView(view);
        Bundle bundle = getArguments();
        if(bundle != null){
            id = bundle.getLong("position");
            String title = bundle.getString("title");
            String body = bundle.getString("body");
            if(title != null)
                titleEt.setText(title);
            if(body != null)
                bodyEt.setText(body);
        }
        return view;
    }

    private void initView(View view) {
        titleEt = (EditText) view.findViewById(R.id.diary_edit_et_title);
        bodyEt = (EditText) view.findViewById(R.id.diary_edit_et_body);
        btn = (Button) view.findViewById(R.id.diary_edit_btn);
        btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String title = titleEt.getText().toString();
        String body = bodyEt.getText().toString();
        if(CheckInput.CheckInput(title,body)){
            diaryDB.open();
            if(id != null){
                LogUtil.v("tag",title);
                LogUtil.v("tag",body);
                LogUtil.v("tag",id+"---");
                diaryDB.updateDiary(id, title, body);
            }else{
                diaryDB.createDiary(title, body);
            }
            diaryDB.close();
            fragment_diary_begin = new Fragment_Diary_begin();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_diary_layout, fragment_diary_begin);
            transaction.commit();
        }
    }
}
