package com.example.li.diary.Util;

/**
 * Created by li on 2016/5/23.
 */
public class CheckInput {

    public static boolean CheckInput(String title, String body) {
        if(null == title || title.trim().length() == 0){
            return false;
        }
        if(null == body || body.trim().length() == 0){
            return false;
        }
        return true;
    }
}
