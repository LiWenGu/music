package com.example.li.music.Util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.li.music.model.Mp3Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by li on 2016/5/12.
 */
public class MediaUtil {

    public static String formatTime(long time){
        int mtime = (int)time/1000;
        String min = mtime/60 + "";
        String sec = mtime%60 + "";
        if(min.length() < 2){
            min = "0" + min;
        }
        if(sec.length() < 2){
            sec = "0" +sec;
        }
        return min + ":" +sec;
    }

    public static List<Mp3Info> getMp3Infos(ContentResolver contentResolver) {
        ContentResolver resolver = contentResolver;
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            if(isMusic != 0){
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
            }
        }
        return mp3Infos;
    }

    /**
     * 将每首歌的信息，放在Map里一一对应，最后歌单一起放在集合中
     * @param mp3Infos
     * @return
     */
    public static List<HashMap<String, String>> getMusicList(List<Mp3Info> mp3Infos){
        List<HashMap<String, String>>mp3list = new ArrayList<HashMap<String, String>>();
        for(Iterator iterator = mp3Infos.iterator(); iterator.hasNext();){
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String>map = new HashMap<String, String>();
            map.put("id",String.valueOf(mp3Info.getId()));
            map.put("title", mp3Info.getTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("duration", formatTime(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        return mp3list;
    }
}
