package com.example.li.music.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.li.music.R;
import com.example.li.music.Service.Music_Service;
import com.example.li.music.Util.ConstUtil;
import com.example.li.music.Util.LogUtil;
import com.example.li.music.Util.MediaUtil;
import com.example.li.music.model.Mp3Info;

import java.util.List;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Music_Play extends Fragment implements View.OnClickListener{

    private String mtitle,martist;                //从列表页面获得的信息
    private int mlistPosition;                //从列表页面获得的信息
    private long mduration = 0;                            //从列表页面获得的信息
    private List<Mp3Info> mp3Infos = null;                 //获得音乐列表
    private Mp3Info mp3Info;
    private boolean isFirstTime = false;    //是否第一次按,暂停和播放是一个按钮,因为这个页面开始就自动播放歌，所以不是第一次
    private boolean isPlaying = true;       // 正在播放
    private boolean isPause;                // 处于暂停
    private boolean isNoneShuffle = true;   // 顺序播放
    private boolean isShuffle = false;      // 随机播放
    private int repeatState = 3;            //循环标识
    private final int isCurrentRepeat = 1;  // 单曲循环
    private final int isAllRepeat = 2;      // 全部循环
    private final int isNoneRepeat = 3;     // 无重复播放
    private Boolean fromList = true;

    TextView tv_title,tv_artist;
    public static SeekBar seekBar;
    private Button btn_previous,btn_play,btn_next,btn_repeat,btn_shuffle;
    private Music_Service music_service;
    private Music_Play_Receiver music_Play_receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.main_music_play, null);
        mp3Infos = MediaUtil.getMp3Infos(getActivity().getContentResolver()); //获取歌曲对象集合
        fromList = getArguments().getBoolean("fromList");
        mlistPosition = (int) getArguments().get("listPosition");
        mp3Info = mp3Infos.get(mlistPosition);
        mduration = mp3Info.getDuration();
        mtitle = mp3Info.getTitle();
        martist = mp3Info.getArtist();
        initView(view);
        init_service_receiver();

        return view;
    }

    private void initView(View view) {
        tv_title = (TextView) view.findViewById(R.id.music_play_musictitle);
        tv_artist = (TextView) view.findViewById(R.id.music_play_musicartist);
        seekBar = (SeekBar) view.findViewById(R.id.music_play_seekBar);
        seekBar.setMax((int)mduration);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        tv_title.setText(mtitle);
        tv_artist.setText(martist);
        btn_previous = (Button) view.findViewById(R.id.music_play_btn_previous);
        btn_repeat = (Button) view.findViewById(R.id.music_play_btn_repeat);
        btn_play = (Button) view.findViewById(R.id.music_play_btn_play);
        btn_shuffle = (Button) view.findViewById(R.id.music_play_btn_shuffle);
        btn_next = (Button) view.findViewById(R.id.music_play_btn_next);
        btn_previous.setOnClickListener(this);
        btn_repeat.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_shuffle.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    private void init_service_receiver() {
        //本身的监听，注册广播接受者
        music_Play_receiver = new Music_Play_Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstUtil.UPDATE_ACTION);
        filter.addAction(ConstUtil.MUSIC_CURRENT);
        filter.addAction(ConstUtil.MUSIC_DURATION);
        filter.addAction(ConstUtil.REPEAT_ACTION);
        filter.addAction(ConstUtil.SHUFFLE_ACTION);
        getActivity().registerReceiver(music_Play_receiver, filter);
        //点击列表直接开始播放音乐
        if(fromList) {
            Intent intentService = new Intent();
            intentService.setClass(getActivity(), Music_Service.class);
            intentService.putExtra("listPosition", mlistPosition);
            intentService.putExtra("MSG", "PLAY_MSG");
            getActivity().startService(intentService);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.music_play_btn_previous:
                isFirstTime = false;
                isPlaying = true;
                isPause = false;
                previous();
                break;
            case R.id.music_play_btn_repeat:
                LogUtil.v("tag",repeatState+"1");
                if(repeatState == isNoneRepeat){
                    btn_repeat.setBackgroundResource(R.mipmap.music_btn_repeat_pressed);
                    repeat_one();
                    btn_shuffle.setClickable(false);
                    repeatState = isCurrentRepeat;
                }else if(repeatState == isCurrentRepeat){
                    btn_repeat.setBackgroundResource(R.mipmap.music_btn_repeat);
                    repeat_all();
                    btn_shuffle.setClickable(false);
                    repeatState = isAllRepeat;
                }else if(repeatState == isAllRepeat){
                    btn_repeat.setBackgroundResource(R.mipmap.music_btn_repeat);
                    repeat_none();
                    btn_shuffle.setClickable(true);
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
            case R.id.music_play_btn_play:
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
                        LogUtil.v("sss",mlistPosition+"==");
                        intent.putExtra("listPosition", mlistPosition);
                        getActivity().startService(intent);
                        isPlaying = false;
                        isPause = true;
                    }else if(isPause){
                        //donghua
                        intent.setClass(getActivity(), Music_Service.class);
                        intent.putExtra("MSG","CONTINUE_MSG");
                        getActivity().startService(intent);
                        isPause = false;
                        isPlaying = true;
                    }
                }
                break;
            case R.id.music_play_btn_shuffle:
                if(isNoneShuffle){
                    btn_shuffle.setBackgroundResource(R.mipmap.music_btn_shuffle_pressed);
                    //donghua
                    Toast.makeText(getActivity(), "随机播放", Toast.LENGTH_SHORT).show();
                    isNoneShuffle = false;
                    isShuffle = true;
                    shuffleMusic();
                    btn_repeat.setClickable(false);
                }else if(isShuffle){
                    btn_shuffle.setBackgroundResource(R.mipmap.music_btn_shuffle);
                    //donghua
                    Toast.makeText(getActivity(), "不会随机播放", Toast.LENGTH_SHORT).show();
                    isShuffle = false;
                    isNoneShuffle = true;
                    btn_repeat.setClickable(true);
                }
                break;
            case R.id.music_play_btn_next:
                //donghua
                isFirstTime = false;
                isPlaying = true;
                isPause = false;
                next();
                break;
        }
    }

    //下一首歌曲
    public void next(){
        mlistPosition = mlistPosition + 1;
        if(mlistPosition <= mp3Infos.size() - 1){
            mp3Info = mp3Infos.get(mlistPosition);
            tv_title.setText(mp3Info.getTitle());
            tv_artist.setText(mp3Info.getArtist());
            Intent intent = new Intent();
            intent.setClass(getActivity(),Music_Service.class);
            intent.putExtra("listPosition", mlistPosition);
            intent.putExtra("MSG", "NEXT_MSG");
            getActivity().startService(intent);
        }else{
            Toast.makeText(getActivity(), "没有下一首了", Toast.LENGTH_SHORT).show();
        }
    }

    //上一首歌曲
    public void previous(){
        mlistPosition = mlistPosition - 1;
        if(mlistPosition >= 0){
            mp3Info = mp3Infos.get(mlistPosition);
            tv_title.setText(mp3Info.getTitle());
            tv_artist.setText(mp3Info.getArtist());
            Intent intent = new Intent();
            intent.setClass(getActivity(), Music_Service.class);
            intent.putExtra("listPosition", mlistPosition);
            intent.putExtra("MSG", "PRIVIOUS_MSG");
            getActivity().startService(intent);
        }else{
            Toast.makeText(getActivity(), "没有上一首了", Toast.LENGTH_SHORT).show();
        }
    }

    //播放歌曲
    public void play(){
        //donghua
        mp3Info = mp3Infos.get(mlistPosition);
        tv_title.setText(mp3Info.getTitle());
        tv_artist.setText(mp3Info.getArtist());
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
        intent.putExtra("control", "1");
        getActivity().sendBroadcast(intent);
    }

    //全部循环
    public void repeat_all(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control" ,"2");
        getActivity().sendBroadcast(intent);
    }

    //顺序播放循环
    public void repeat_none(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control", "3");
        getActivity().sendBroadcast(intent);
    }

    //随机播放
    public void shuffleMusic(){
        Intent intent = new Intent(ConstUtil.CTL_ACTION);
        intent.putExtra("control", "4");
        getActivity().sendBroadcast(intent);
    }

    //进度条重写
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Intent intent = new Intent(ConstUtil.CTL_ACTION);
            intent.putExtra("control", 5);
            getActivity().sendBroadcast(intent);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtil.v("tag",seekBar.getProgress()+"----");
            Intent intent = new Intent(ConstUtil.CTL_ACTION);
            intent.putExtra("control", seekBar.getProgress());
            getActivity().sendBroadcast(intent);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }
    };

    public class Music_Play_Receiver extends BroadcastReceiver {

        private int currentTime;
        private int listPosition;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ConstUtil.MUSIC_CURRENT)){
                //currentTime代表当前播放的时间
                currentTime = intent.getIntExtra("currentTime", -1);
                seekBar.setProgress(currentTime);
            }else if(action.equals(ConstUtil.MUSIC_DURATION)){
                mduration = intent.getIntExtra("duration", -1);
                seekBar.setMax((int) mduration);
            }else if(action.equals(ConstUtil.UPDATE_ACTION)){
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                if(listPosition >= 0){
                    tv_title.setText(mp3Infos.get(listPosition).getTitle());
                    tv_artist.setText(mp3Infos.get(listPosition).getArtist());
                }
            }else if(action.equals(ConstUtil.REPEAT_ACTION)){
                repeatState = intent.getIntExtra("repeatState", -1);
                switch (repeatState) {
                    case isCurrentRepeat: // 单曲循环
                        //donghua
                        btn_shuffle.setClickable(false);
                        break;
                    case isAllRepeat: // 全部循环
                        //donghua
                        btn_shuffle.setClickable(false);
                        break;
                    case isNoneRepeat: // 无重复
                        //donghua
                        btn_shuffle.setClickable(true);
                        break;
                }
            }
            else if(action.equals(ConstUtil.SHUFFLE_ACTION)) {
                isShuffle = intent.getBooleanExtra("shuffleState", false);
                if(isShuffle) {
                    isNoneShuffle = false;
                    //donghua
                    btn_repeat.setClickable(false);
                } else {
                    isNoneShuffle = true;
                    //donghua
                    btn_repeat.setClickable(true);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(music_Play_receiver);
    }


}
