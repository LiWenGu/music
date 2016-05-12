package com.example.li.music.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.li.music.R;
import com.example.li.music.Util.MediaUtil;
import com.example.li.music.model.Mp3Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Music_Begin extends Fragment {

    MediaUtil mediaUtil = new MediaUtil();
    List<Mp3Info> mp3Infos;
    private ListView listView;
    private  Fragment_Music_Play fragment_music_play;
    private List<HashMap<String, String>> mp3list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_music_begin, null);
        findViewById(view);
        initListView(view);
        return view;
    }

    private void findViewById(View view) {

    }

    private void initListView(View view) {
        listView = (ListView) view.findViewById(R.id.music_list);
        mp3list = new ArrayList<HashMap<String, String>>();
        mp3Infos = mediaUtil.getMp3Infos(getActivity().getContentResolver());
        mp3list = MediaUtil.getMusicList(mp3Infos);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),mp3list,R.layout.music_list_item,new String[] { "title", "artist", "duration" },
                new int[] { R.id.music_list_musictitle, R.id.music_list_musicauthor, R.id.music_list_musicduration });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyMusicListItemClickListener());
    }

    public class MyMusicListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap map = mp3list.get(position);
            String title = (String) map.get("title");
            String author = (String) map.get("artist");
            String duration = (String) map.get("duration");
            String url = (String) map.get("url");
            Bundle bundle = new Bundle();
            bundle.putString("title",title);
            bundle.putString("artist",author);
            bundle.putString("duration",duration);
            bundle.putString("url",url);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            fragment_music_play = new Fragment_Music_Play();
            fragment_music_play.setArguments(bundle);
            transaction.replace(R.id.main_music_layout,fragment_music_play);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
