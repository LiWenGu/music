package com.example.li.diary.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.li.diary.db.DiaryDB;
import com.example.li.diary.model.Diary;
import com.example.li.music.R;

import java.util.List;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Diary_begin extends Fragment implements View.OnClickListener{

    private ListView listView;
    private DiaryDB diaryDB;
    private Cursor cursor;
    private ListAdapter diaryAdapter;
    private ImageView addView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_begin, null);
        addView = (ImageView) view.findViewById(R.id.diary_begin_iv_add);
        addView.setOnClickListener(this);
        diaryDB = new DiaryDB(getContext());
        diaryDB.open();
        initListView(view);
        return view;
    }

    private void initListView(View view) {
        cursor = diaryDB.getAllnotes();
        String[] from = new String[]{DiaryDB.KEY_TITLE, DiaryDB.KEY_BODY, DiaryDB.KEY_CREATED};
        int[] to = new int[]{R.id.diary_list_title, R.id.diary_list_intro, R.id.diary_list_time};
        listView = (ListView) view.findViewById(R.id.diary_begin_list);
        diaryAdapter = new SimpleCursorAdapter(getContext(),R.layout.diary_begin_list_item,cursor,from,to);
        listView.setAdapter(diaryAdapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.diary_begin_iv_add:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment_Diary_Edit fragment_diary_edit = new Fragment_Diary_Edit();
                transaction.replace(R.id.main_diary_layout, fragment_diary_edit);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
                break;
        }
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = cursor;
            c.move(position);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment_Diary_Edit fragment_diary_edit = new Fragment_Diary_Edit();
            Bundle bundle = new Bundle();
            bundle.putLong("position", position);
            bundle.putString("title", c.getString(c.getColumnIndexOrThrow(DiaryDB.KEY_TITLE)));
            bundle.putString("body", c.getString(c.getColumnIndexOrThrow(DiaryDB.KEY_BODY)));
            fragment_diary_edit.setArguments(bundle);
            transaction.replace(R.id.main_diary_layout,fragment_diary_edit);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
















    class DiaryAdapter extends ArrayAdapter {

        private int resourceId;

        public DiaryAdapter(Context context, int textViewResourceId, List<Diary> diaryList) {
            super(context, textViewResourceId, diaryList);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Diary diary = (Diary) getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            TextView title = (TextView) view.findViewById(R.id.diary_list_title);
            TextView intro = (TextView) view.findViewById(R.id.diary_list_intro);
            TextView time = (TextView) view.findViewById(R.id.diary_list_time);
            title.setText(diary.getTitle());
            intro.setText(diary.getIntro());
            time.setText(diary.getCreated());
            return view;
        }
    }
}
