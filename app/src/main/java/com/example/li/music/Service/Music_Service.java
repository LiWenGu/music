package com.example.li.music.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.li.music.Util.ConstUtil;
import com.example.li.music.Util.MediaUtil;
import com.example.li.music.model.Mp3Info;

import java.util.List;

/**
 * Created by li on 2016/5/11.
 */
public class Music_Service extends Service {

    public MediaPlayer mediaPlayer;
    private String path;            // 音乐文件路径
    private String msg;
    private boolean isPause;        // 暂停状态
    private int current = 0;        // 记录当前正在播放的音乐
    private List<Mp3Info> mp3Infos; //存放Mp3Info对象的集合
    private int status = 3;         //播放状态，默认为顺序播放
    private MyReceiver myReceiver;  //自定义广播接收器
    private int currentTime;        //当前播放进度
    private int duration;           //播放长度

    //handler用来接收消息，来发送广播更新播放时间
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                if(mediaPlayer != null){
                    currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放器进度条的位置
                    Intent intent = new Intent();
                    intent.setAction(ConstUtil.MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentTime);
                    intent.putExtra("current" , current);           //给begin界面，如果程序退出，进入begin界面，就不会显示正在播放的歌曲
                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1, 100);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mp3Infos = MediaUtil.getMp3Infos(Music_Service.this.getContentResolver());

        //设置音乐播放完成时的监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(status ==1){        //单曲循环
                    mediaPlayer.start();
                }else if(status ==2){  //全部循环
                    current++;
                    if(current > mp3Infos.size() - 1){ //变为第一首的位置继续播放
                        current = 0;
                    }
                    Intent sendIntent = new Intent(ConstUtil.UPDATE_ACTION);
                    sendIntent.putExtra("current", current);
                    //放送广播，将被Activity组件中的BroadcastReceiver接受到
                    sendBroadcast(sendIntent);
                    path = mp3Infos.get(current).getUrl();
                    play(0);
                }else if(status ==3){   //顺序循环
                    current++;  //下一个位置
                    if(current <= mp3Infos.size() - 1){
                        Intent sendIntent = new Intent(ConstUtil.UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        sendBroadcast(sendIntent);
                        path = mp3Infos.get(current).getUrl();
                        play(0);
                    }else{
                        mediaPlayer.seekTo(0);
                        current = 0;
                        Intent sendIntent = new Intent(ConstUtil.UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        sendBroadcast(sendIntent);
                    }
                }else if(status == 4) {  //随机播放
                    current = getRandomIndex(mp3Infos.size() - 1);
                    Log.d("service","currentIndex->"+current);
                    Intent sendIntent = new Intent(ConstUtil.UPDATE_ACTION);
                    sendIntent.putExtra("current", current);
                    sendBroadcast(sendIntent);
                    path = mp3Infos.get(current).getUrl();
                    play(0);
                }
            }
        });

        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstUtil.CTL_ACTION);
        registerReceiver(myReceiver, filter);
    }

    //获取随机位置
    protected int getRandomIndex(int end){
        int index = (int) (Math.random() * end);
         return index;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        current = intent.getIntExtra("listPosition", -1);   //当前播放歌曲的在mp3Infos的位置
        if(current!=-1)
            path = mp3Infos.get(current).getUrl();
        msg = intent.getStringExtra("MSG");                 //播放信息
        if (msg.equals("PLAY_MSG")) {                       //直接播放音乐
            play(0);
        } else if (msg.equals("PAUSE_MSG")) {               //暂停
            pause();
        } else if (msg.equals("STOP_MSG")) {                //停止
            stop();
        } else if (msg.equals("CONTINUE_MSG")) {            //继续播放
            resume();
        } else if (msg.equals("PRIVIOUS_MSG")) {            //上一首
            previous();
        } else if (msg.equals("NEXT_MSG")) {                //下一首
            next();
        } else if (msg.equals("PROGRESS_CHANGE")) {         //进度更新
            currentTime = intent.getIntExtra("progress", -1);
            play(currentTime);
        } else if (msg.equals("PLAYING_MSG")) {
            handler.sendEmptyMessage(1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //播放音乐
    private void play(int currentTime){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));   //注册监听器
            handler.sendEmptyMessage(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //暂停音乐
    private void pause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;
        }
    }

    //恢复音乐
    private void resume(){
        if(isPause){
            mediaPlayer.start();
            isPause = false;
        }
    }

    //上一首
    private void previous(){
        Intent sendIntent = new Intent(ConstUtil.UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        sendBroadcast(sendIntent);
        play(0);
    }

    //下一首
    private void next(){
        Intent sendIntent = new Intent(ConstUtil.UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        sendBroadcast(sendIntent);
        play(0);
    }

    //停止音乐
    private void stop(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener{

        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
            if(currentTime > 0){      //如果音乐不是从头播放
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(ConstUtil.MUSIC_DURATION);   //新音乐更新进度条长度
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration", duration);
            sendBroadcast(intent);
        }
    }

    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control",-1);
            switch (control){
                case 1:
                    status = 1; //将播放状态置为1表示：单曲循环
                    break;
                case 2:
                    status = 2; //将播放状态置为2表示：全部循环
                    break;
                case 3:
                    status = 3; //将播放状态置为3表示：顺序播放
                    break;
                case 4:
                    status = 4; //将播放状态置为4表示：随机播放
                    break;
                case 5:
                    mediaPlayer.pause();  //拖动进度条暂停
                    break;
                default:
                    mediaPlayer.seekTo(control);
                    mediaPlayer.start();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
