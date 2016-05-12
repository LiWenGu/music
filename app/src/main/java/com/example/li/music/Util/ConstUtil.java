package com.example.li.music.Util;

/**
 * Created by li on 2016/5/11.
 */
public class ConstUtil {
    //一系列动作
    public static final String UPDATE_ACTION = "com.li.action.UPDATE_ACTION";   //更新动作
    public static final String CTL_ACTION = "com.li.action.CTL_ACTION";         //控制动作
    public static final String MUSIC_CURRENT = "com.li.action.MUSIC_CURRENT";   //当前音乐播放时间更新动作
    public static final String MUSIC_DURATION = "com.li.action.MUSIC_DURATION"; //新音乐长度更新动作
    public static final String REPEAT_ACTION = "com.li.action.REPEAT_ACTION";
    public static final String SHUFFLE_ACTION = "com.li.action.SHUFFLE_ACTION";

    //响应Action
    public static final String MUSICPlay_ACTION="com.example.li.music.ACTION";
    //响应Action
    public static final String MUSICService_ACTION="com.example.li.receiver.ACTION";
    //初始化flag
    public static final int STATE_NON=0x122;
    //播放的flag
    public static final int STATE_PLAY=0x123;
    //暂停的flag
    public static final int STATE_PAUSE=0x124;
    //停止放的flag
    public static final int STATE_STOP=0x125;
    //播放上一首的flag
    public static final int STATE_PREVIOUS=0x126;
    //播放下一首的flag
    public static final int STATE_NEXT=0x127;
}
