package com.example.li.music.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.li.music.R;
import com.example.li.music.Service.Music_Service;
import com.example.li.music.Util.ConstUtil;
import com.example.li.music.Util.MediaUtil;
import com.example.li.music.model.Mp3Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Music_Begin extends Fragment implements View.OnClickListener{


    private int repeatState;                //循环标识
    private final int isCurrentRepeat = 1;  // 单曲循环
    private final int isAllRepeat = 2;      // 全部循环
    private final int isNoneRepeat = 3;     // 无重复播放
    private boolean isFirstTime = true;     //是否第一次按,暂停和播放是一个按钮
    private boolean isPlaying;              // 正在播放
    private boolean isPause;                // 处于暂停
    private boolean isNoneShuffle = true;   // 顺序播放
    private boolean isShuffle = false;      // 随机播放
    private int listPosition = 0;           //标识列表位置
    private int currentTime;
    private int duration;

    private ListView listView;              //歌曲列表
    private List<Mp3Info> mp3Infos = null;
    private SimpleAdapter mAdapter;         //简单适配器
    private Button previousBtn;         // 上一首
    private Button repeatBtn;           // 重复（单曲循环、全部循环）
    private Button playBtn;             // 播放（播放、暂停）
    private Button shuffleBtn;          // 随机播放
    private Button nextBtn;             // 下一首
    private TextView musicTitle;        //歌曲标题
    private TextView musicDuration;     //歌曲时间
    private RelativeLayout musicPlaying;//歌曲专辑
    private HomeReceiver homeReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_music_begin, null);
        findViewById(view);
        setViewOnclickListener();
        initListView();
        createReceiver();
        return view;
    }

    private void findViewById(View view) {
        listView = (ListView) view.findViewById(R.id.music_list);
        previousBtn = (Button) view.findViewById(R.id.music_begin_btn_previous);
        repeatBtn = (Button) view.findViewById(R.id.music_begin_btn_repeat);
        playBtn = (Button) view.findViewById(R.id.music_begin_btn_play);
        shuffleBtn = (Button) view.findViewById(R.id.music_begin_btn_shuffle);
        nextBtn = (Button) view.findViewById(R.id.music_begin_btn_next);
        musicTitle = (TextView) view.findViewById(R.id.music_begin_tv_musictitle);
        musicDuration = (TextView) view.findViewById(R.id.music_begin_tv_musicdutation);
        musicPlaying = (RelativeLayout) view.findViewById(R.id.music_begin_playinglayout);
    }

    private void setViewOnclickListener() {
        previousBtn.setOnClickListener(this);
        repeatBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        shuffleBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        musicPlaying.setOnClickListener(this);
        listView.setOnItemClickListener(new MyMusicListItemClickListener());  //歌曲列表监听
        listView.setOnCreateContextMenuListener(new MusicListItemContextMenuListener());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.music_begin_btn_previous:  //上一首
                isFirstTime = false;
                isPlaying = true;
                isPause = false;
                previous();
                break;
            case R.id.music_begin_btn_repeat:
                if(repeatState == isNoneRepeat){
                    repeat_one();
                    shuffleBtn.setClickable(false);
                    repeatState = isCurrentRepeat;
                }else if(repeatState == isCurrentRepeat){
                    repeat_all();
                    shuffleBtn.setClickable(false);
                    repeatState = isAllRepeat;
                }else if(repeatState == isAllRepeat){
                    repeat_none();
                    shuffleBtn.setClickable(true);
                    repeatState = isNoneRepeat;
                }
                switch (repeatState){
                    case isNoneRepeat:      //无重复
                        //donghua
                        Toast.makeText(getContext(),"没有循环", Toast.LENGTH_SHORT).show();
                        break;
                    case isCurrentRepeat:   //单曲循环
                        //donghua
                        Toast.makeText(getContext(),"正在循环当前歌曲", Toast.LENGTH_SHORT).show();
                        break;
                    case isAllRepeat:       //全部循环
                        //donghua
                        Toast.makeText(getContext(),"正在循环所有歌曲", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.music_begin_btn_play:
                if(isFirstTime){
                    play();
                    isFirstTime = false;
                    isPlaying = true;
                    isPause = false;
                }else{
                    if(isPlaying){
                        //donghua
                        intent.setClass(getActivity(), Music_Service.class);
                        intent.putExtra("MSG","PAUSE_MSG");
                        getActivity().startService(intent);
                        isPlaying = false;
                        isPause = true;
                    }else if(isPause){
                        //donghua
                        intent.setClass(getActivity(), Music_Service.class);intent.putExtra("MSG","CONTINUE_MSG");
                        intent.putExtra("MSG","CONTINUE_MSG");
                        getActivity().startService(intent);
                        isPause = false;
                        isPlaying = true;
                    }
                }
                break;
            case R.id.music_begin_btn_shuffle:  //随机播放
                if(isNoneShuffle){
                    //donghua
                    Toast.makeText(getActivity(), "随机播放", Toast.LENGTH_SHORT).show();
                    isNoneShuffle = false;
                    isShuffle = true;
                    shuffleMusic();
                    repeatBtn.setClickable(false);
                }else if(isShuffle){
                    //donghua
                    Toast.makeText(getActivity(), "不会随机播放", Toast.LENGTH_SHORT).show();
                    isShuffle = false;
                    isNoneShuffle = true;
                    repeatBtn.setClickable(true);
                }
                break;
            case R.id.music_begin_btn_next:   //下一首
                //donghua
                isFirstTime = false;
                isPlaying = true;
                isPause = false;
                next();
                break;
            case R.id.music_begin_playinglayout:   //正在播放
                Mp3Info mp3Info = mp3Infos.get(listPosition);
                intent.setClass(getActivity(),Fragment_Music_Play.class);
                intent.putExtra("title", mp3Info.getTitle());
                intent.putExtra("artist", mp3Info.getArtist());
                intent.putExtra("url", mp3Info.getUrl());

                intent.putExtra("listPosition", listPosition);
                intent.putExtra("duration", duration);
                intent.putExtra("currentTime", currentTime);
                intent.putExtra("MSG", "PLAYING_MSG");
                startActivity(intent);
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

    //下一首歌曲
    public void next(){
        Log.d("next",  "访问"+listPosition);
        listPosition = listPosition + 1;
        if(listPosition <= mp3Infos.size() - 1){
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setClass(getActivity(),Music_Service.class);
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", "NEXT_MSG");
            Log.d("next",  "url"+mp3Info.getUrl());
            getActivity().startService(intent);
        }else{
            Toast.makeText(getActivity(), "没有下一首了", Toast.LENGTH_SHORT).show();
        }
    }

    //上一首歌曲
    public void previous(){
        listPosition = listPosition - 1;
        if(listPosition >= 0){
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setClass(getActivity(), Music_Service.class);
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", "PRIVIOUS_MSG");
            getActivity().startService(intent);
        }else{
            Toast.makeText(getActivity(), "没有上一首了", Toast.LENGTH_SHORT).show();
        }
    }

    //播放歌曲
    public void play(){
        //donghua
        Mp3Info mp3Info = mp3Infos.get(listPosition);
        musicTitle.setText(mp3Info.getTitle());
        Intent intent = new Intent();
        intent.setClass(getActivity(), Music_Service.class);
        intent.putExtra("listPosition", 0);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", "PLAY_MSG");
        getActivity().startService(intent);
    }

    //单曲循环
    public void repeat_one(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control", 1);
        getActivity().sendBroadcast(intent);
    }

    //全部循环
    public void repeat_all(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control" ,2);
        getActivity().sendBroadcast(intent);
    }

    //顺序播放循环
    public void repeat_none(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control", 3);
        getActivity().sendBroadcast(intent);
    }

    //随机播放
    public void shuffleMusic(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control", 4);
        getActivity().sendBroadcast(intent);
    }

    public void playMusic(int listPosition){
        if(mp3Infos != null){
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            /*HashMap map = mp3list.get(position);
            String title = (String) map.get("title");
            String author = (String) map.get("artist");
            String duration = (String) map.get("duration");
            String url = (String) map.get("url");*/
            Bundle bundle = new Bundle();
            bundle.putString("title", mp3Info.getTitle());
            bundle.putString("artist", mp3Info.getArtist());
            bundle.putString("url", mp3Info.getUrl());
            bundle.putInt("listPosition", listPosition);
            bundle.putInt("currentTime", currentTime);
            bundle.putInt("repeatState", repeatState);
            bundle.putBoolean("shuffleState", isShuffle);
            bundle.putString("MSG", "PLAY_MSG");
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment_Music_Play fragment_music_play = new Fragment_Music_Play();
            fragment_music_play.setArguments(bundle);
            transaction.replace(R.id.main_music_layout,fragment_music_play);
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
            if(action.equals(ConstUtil.MUSIC_CURRENT)){
                //currentTime代表当前播放的时间
                currentTime = intent.getIntExtra("currentTime", -1);
                musicDuration.setText(MediaUtil.formatTime(currentTime));
            }else if(action.equals(ConstUtil.MUSIC_DURATION)){
                duration = intent.getIntExtra("duration", -1);
            }else if(action.equals(ConstUtil.UPDATE_ACTION)){
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                if(listPosition >= 0){
                    musicTitle.setText(mp3Infos.get(listPosition).getTitle());
                }
            }else if(action.equals(ConstUtil.REPEAT_ACTION)){
                repeatState = intent.getIntExtra("repeatState", -1);
                switch (repeatState) {
                    case isCurrentRepeat: // 单曲循环
                        //donghua
                        shuffleBtn.setClickable(false);
                        break;
                    case isAllRepeat: // 全部循环
                        //donghua
                        shuffleBtn.setClickable(false);
                        break;
                    case isNoneRepeat: // 无重复
                        //donghua
                        shuffleBtn.setClickable(true);
                        break;
                }
            }
            else if(action.equals(ConstUtil.SHUFFLE_ACTION)) {
                isShuffle = intent.getBooleanExtra("shuffleState", false);
                if(isShuffle) {
                    isNoneShuffle = false;
                    //donghua
                    repeatBtn.setClickable(false);
                } else {
                    isNoneShuffle = true;
                    //donghua
                    repeatBtn.setClickable(true);
                }
            }
        }
    }
}
