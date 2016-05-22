package com.example.li.music.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.li.music.R;
import com.example.li.music.Util.ConstUtil;
import com.example.li.music.Util.MediaUtil;
import com.example.li.music.model.Mp3Info;

import java.util.HashMap;
import java.util.List;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Music_Begin extends Fragment implements View.OnClickListener{

    private boolean isPlaying;              // 正在播放
    private boolean isShuffle = false;      // 随机播放
    private int listPosition = 0;           //标识列表位置
    private int currentTime;
    private int duration;

    private ListView listView;              //歌曲列表
    private List<Mp3Info> mp3Infos = null;
    private SimpleAdapter mAdapter;         //简单适配器
    private TextView musicTitle;        //歌曲标题
    private TextView musicDuration;     //歌曲时间
    private RelativeLayout musicPlaying;//歌曲专辑
    private HomeReceiver homeReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_begin, null);
        findViewById(view);
        setViewOnclickListener();
        initListView();
        createReceiver();
        return view;
    }

    private void findViewById(View view) {
        listView = (ListView) view.findViewById(R.id.music_list);
        musicTitle = (TextView) view.findViewById(R.id.music_begin_tv_musictitle);
        musicDuration = (TextView) view.findViewById(R.id.music_begin_tv_musicdutation);
        musicPlaying = (RelativeLayout) view.findViewById(R.id.music_begin_playinglayout);
        musicPlaying.setOnClickListener(this);
    }

    private void setViewOnclickListener() {
        listView.setOnItemClickListener(new MyMusicListItemClickListener());  //歌曲列表监听
        listView.setOnCreateContextMenuListener(new MusicListItemContextMenuListener());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.music_begin_playinglayout:   //正在播放
               /* Mp3Info mp3Info = mp3Infos.get(listPosition);
                Bundle bundle = new Bundle();
                bundle.putString("title", mp3Info.getTitle());
                bundle.putString("artist", mp3Info.getArtist());
                bundle.putString("url", mp3Info.getUrl());
                bundle.putInt("listPosition", listPosition);
                bundle.putInt("duration", duration);
                bundle.putInt("currentTime", currentTime);
                bundle.putString("MSG", "PLAYING_MSG");*/
                if(isPlaying){
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Fragment_Music_Play fragment_music_play = new Fragment_Music_Play();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromList",false);
                    bundle.putInt("listPosition",listPosition);
                    fragment_music_play.setArguments(bundle);
                    transaction.hide(this);
                    transaction.add(R.id.main_music_layout,fragment_music_play);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        }
    }

    private void initListView() {
        mp3Infos = MediaUtil.getMp3Infos(getActivity().getContentResolver()); //获取歌曲对象集合
        setListAdpter(MediaUtil.getMusicList(mp3Infos));                      //绑定歌曲到ListView
    }

    public void setListAdpter(List<HashMap<String, String>> mp3list){
        mAdapter = new SimpleAdapter(getActivity(), mp3list,
                R.layout.music_list_item, new String[] { "title",
                "artist", "duration" }, new int[] { R.id.music_list_musictitle,
                R.id.music_list_musicartist, R.id.music_list_musicduration });
        listView.setAdapter(mAdapter);
    }

    public class MyMusicListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listPosition = position;
            playMusic(listPosition);
        }
    }

    public class MusicListItemContextMenuListener implements AdapterView.OnCreateContextMenuListener {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(50);       //长按振动
            //musicListItemDialog();      //长按后弹出的对话框
        }
    }

    public void playMusic(int listPosition){
        if(mp3Infos != null){
            isPlaying = true;
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromList",true);
            bundle.putInt("listPosition", listPosition);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment_Music_Play fragment_music_play = new Fragment_Music_Play();
            fragment_music_play.setArguments(bundle);
            transaction.hide(this);
            transaction.add(R.id.main_music_layout,fragment_music_play);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void createReceiver() {
        homeReceiver = new HomeReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(ConstUtil.UPDATE_ACTION);
        filter.addAction(ConstUtil.MUSIC_CURRENT);
        filter.addAction(ConstUtil.MUSIC_DURATION);
        filter.addAction(ConstUtil.REPEAT_ACTION);
        filter.addAction(ConstUtil.SHUFFLE_ACTION);
        // 注册BroadcastReceiver
        getActivity().registerReceiver(homeReceiver, filter);
    }

    public class HomeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ConstUtil.UPDATE_ACTION)){
                listPosition = intent.getIntExtra("current",-1);
                musicTitle.setText(mp3Infos.get(listPosition).getTitle());
            }else if(action.equals(ConstUtil.MUSIC_CURRENT)){
                currentTime = intent.getIntExtra("currentTime", -1);
                listPosition = intent.getIntExtra("current",-1);
                musicDuration.setText(MediaUtil.formatTime(currentTime));
                musicTitle.setText(mp3Infos.get(listPosition).getTitle());
            }
            if(currentTime!=-1){
                isPlaying = true;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(homeReceiver);
    }
}
