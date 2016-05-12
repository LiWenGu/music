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

import com.example.li.music.R;
import com.example.li.music.Service.Music_Service;
import com.example.li.music.Util.ConstUtil;

/**
 * Created by li on 2016/5/11.
 */
public class Fragment_Music_Play extends Fragment implements View.OnClickListener{

    private String mtitle,martist,mduration,murl;
    TextView tv_title,tv_artist;
    public static SeekBar seekBar;
    private Button btn_previous,btn_play,btn_next,btn_repeat,btn_shuffle;
    private boolean isPlaying = false;
    private Music_Service music_service;
    private boolean isbtn = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.main_music_play, null);
        mtitle = (String) getArguments().get("title");
        martist = (String) getArguments().get("artist");
        mduration = (String) getArguments().get("duration");
        murl = (String) getArguments().get("url");

        initView(view);
        init_service_receiver();

        return view;
    }

    private void init_service_receiver() {
        //本身的监听，注册广播接受者
        Music_Play_Receiver music_Play_receiver = new Music_Play_Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstUtil.MUSICPlay_ACTION);
        getActivity().registerReceiver(music_Play_receiver, filter);
        //启动后台Service，用于播放音乐
        Intent intent = new Intent();
        intent.setClass(getContext(),Music_Service.class);
        getActivity().startService(intent);
    }

    private void initView(View view) {
        tv_title = (TextView) view.findViewById(R.id.music_play_musictitle);
        tv_artist = (TextView) view.findViewById(R.id.music_play_musicartist);
        seekBar = (SeekBar) view.findViewById(R.id.music_play_seekBar);
        tv_title.setText(mtitle);
        tv_artist.setText(martist);
        btn_previous = (Button) view.findViewById(R.id.music_play_btn_previous);
        btn_repeat = (Button) view.findViewById(R.id.music_play_btn_repeat);
        btn_play = (Button) view.findViewById(R.id.music_play_btn_play);
        btn_shuffle = (Button) view.findViewById(R.id.music_play_btn_shuffle);
        btn_next = (Button) view.findViewById(R.id.music_play_btn_next);
        btn_play.setOnClickListener(this);
        if(isbtn){
            sendBroadcastToService(ConstUtil.STATE_PLAY, 0);
            isPlaying = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.music_play_btn_previous:
                sendBroadcastToService(ConstUtil.STATE_PREVIOUS, 1);
                isPlaying = true;
                break;
            case R.id.music_play_btn_repeat:
                break;
            case R.id.music_play_btn_play:
                if(!isPlaying){
                    sendBroadcastToService(ConstUtil.STATE_PLAY, 1);
                    isPlaying = true;
                }else{
                    sendBroadcastToService(ConstUtil.STATE_PAUSE, 1);
                    isPlaying = false;
                }
                break;
            case R.id.music_play_btn_shuffle:
                break;
            case R.id.music_play_btn_next:
                sendBroadcastToService(ConstUtil.STATE_NEXT, 1);
                isPlaying = true;
                break;
        }
    }


    //向后台Service发送控制广播
    protected void sendBroadcastToService(int state, int btn){
        Intent intent = new Intent();
        intent.setAction(ConstUtil.MUSICService_ACTION);
        intent.putExtra("control", state);
        Log.d("ar",mtitle);
        intent.putExtra("btn", btn);
        intent.putExtra("title", mtitle);
        intent.putExtra("artist", martist);
        intent.putExtra("url", murl);
        intent.putExtra("duration", mduration);
        //向后台Service发送播放控制的广播
        getActivity().sendBroadcast(intent);
    }

    public class Music_Play_Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String artist = intent.getStringExtra("artist");
            tv_title.setText(title);
            tv_artist.setText(artist);
        }
    }
}
