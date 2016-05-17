package com.example.li.music.Util;

import android.util.Log;

/**
 * Created by li on 2016/5/16.
 */
public class LogUtil {

    public static final int Verbose = 1;

    public static final int Debug = 2;

    public static final int Info = 3;

    public static final int Warn = 4;

    public static final int Error = 5;

    public static final int Assert = 6;

    public static final int Level = 0;

    public static void v(String tag, String msg){
        if(Level <= Verbose){
            Log.v(tag, msg);
        }
    }
}
