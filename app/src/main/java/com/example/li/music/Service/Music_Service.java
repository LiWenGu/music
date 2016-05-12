package com.example.li.music.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.li.music.Util.ConstUtil;
import com.example.li.music.activity.Fragment_Music_Play;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by li on 2016/5/11.
 */
public class Music_Service extends Service {

    private String title;
    private String artist;
    private String url;
    private String duration;
    private int btn;
    Timer mtimer;
    TimerTask mTimerTask;
    Fragment_Music_Play fragment_music_play = new Fragment_Music_Play();

    public static boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    public static MediaPlayer mediaPlayer;
    //当前播放的音乐
    int current = 0;
    //当前播放状态
    int state = ConstUtil.STATE_NON;
    //记录Timer运行状态
    boolean isTimerRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册接收器
        Music_Service_Receiver receiver = new Music_Service_Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstUtil.MUSICService_ACTION);
        registerReceiver(receiver, filter);
        mediaPlayer = new MediaPlayer();
    }

    //装载和播放音乐
    protected void prepareAndPlay(String title, String artist, String url){
        //放送广播停止前台Activity更新界面
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("artist", artist);
        intent.setAction(ConstUtil.MUSICPlay_ACTION);
        sendBroadcast(intent);
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            fragment_music_play.seekBar.setMax(mediaPlayer.getDuration());//设置SeekBar的长度
        }catch (Exception e){
        }
        mtimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerRunning = true;
                if(isChanging == true){
                    return;
                }
                fragment_music_play.seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        };
        //每隔10毫秒检测以下播放进度
        mtimer.schedule(mTimerTask, 0, 10);
    }

    public class Music_Service_Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control",-1);
            btn = intent.getIntExtra("btn", 0);
            title = intent.getStringExtra("title");
            artist = intent.getStringExtra("artist");
            url = intent.getStringExtra("url");
            duration = intent.getStringExtra("duration");
            if(btn == 0) {
                prepareAndPlay(title, artist, url);
                state = ConstUtil.STATE_PLAY;
            }if(btn == 1){
                switch (control) {
                    case ConstUtil.STATE_PLAY:
                        if (state == ConstUtil.STATE_PAUSE) {
                            mediaPlayer.start();
                        } else if (state != ConstUtil.STATE_PLAY) {
                            prepareAndPlay(title, artist, url);
                        }
                        state = ConstUtil.STATE_PLAY;
                        break;
                    case ConstUtil.STATE_PAUSE:
                        if (state == ConstUtil.STATE_PLAY) {
                            mediaPlayer.pause();
                            state = ConstUtil.STATE_PAUSE;
                        }
                        break;
                    case ConstUtil.STATE_STOP:
                        if (state == ConstUtil.STATE_PLAY || state == ConstUtil.STATE_PAUSE) {
                            mediaPlayer.stop();
                            state = ConstUtil.STATE_STOP;
                        }
                        break;
                /*case ConstUtil.STATE_PREVIOUS:
                    prepareAndPlay(--current);
                    state = ConstUtil.STATE_PLAY;
                    break;
                case ConstUtil.STATE_NEXT:
                    prepareAndPlay(++current);
                    state = ConstUtil.STATE_PLAY;
                    break;*/
                    default:
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
