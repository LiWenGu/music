package com.example.li;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.li.music.R;
import com.example.li.diary.activity.Fragment_Diary;
import com.example.li.music.activity.Fragment_Music;
import com.example.li.setting.activity.Fragment_Setting;

import java.util.ArrayList;

/**
 * Created by li on 2016/5/10.
 */
public class Activity_Main extends FragmentActivity {

    private ViewPager viewPager;
    private TextView t1, t2, t3;
    //页面管理
    ArrayList<Fragment> fragmentList;

    Fragment_Music fragment_music;
    Fragment_Diary fragment_diary;
    Fragment_Setting fragment_setting;

    private ImageView cursor;
    private int bmpw = 0; //游标宽度
    private int offset = 0; //动画图片偏移量
    private int currIndex = 0; //当前页面编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化指示器位置
        initImageView();
        initTextView();
        initViewPager();
    }

    private void initTextView() {
        t1 = (TextView) findViewById(R.id.main_tv_riji);
        t2 = (TextView) findViewById(R.id.main_tv_music);
        t3 = (TextView) findViewById(R.id.main_tv_setting);

        t1.setOnClickListener(new MyOnClickListenener(0));
        t2.setOnClickListener(new MyOnClickListenener(1));
        t3.setOnClickListener(new MyOnClickListenener(2));
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        fragment_music = new Fragment_Music();
        fragment_diary = new Fragment_Diary();
        fragment_setting = new Fragment_Setting();

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(fragment_setting);
        fragmentList.add(fragment_music);
        fragmentList.add(fragment_diary);

        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
    }

    private void initImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpw = BitmapFactory.decodeResource(getResources(), R.mipmap.a)
                .getWidth(); //获取图片宽度
        //获取分辨率宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        //获取分辨率宽度
        offset = (screenW/3-bmpw)/2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);  //设置动画初始位置
    }

    public class MyOnClickListenener implements View.OnClickListener{

        private int index = 0;
        public MyOnClickListenener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener{

        int one = offset * 2 + bmpw; //页面1 -> 页面2 偏移量
        int two = one * 2; //页面1 -> 页面3 偏移量


        @Override
        public void onPageSelected(int position) {
            Animation animation = null;
            switch (position){
                case 0:
                    if(currIndex == 1){
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    }else if(currIndex == 2){
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if(currIndex == 0){
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    }else if(currIndex == 2){
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    if(currIndex == 0){
                        animation = new TranslateAnimation(offset, two, 0 ,0);
                    }else if(currIndex == 1){
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;
            }
            currIndex = position;
            animation.setFillAfter(true); //图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public class MyFragmentAdapter extends FragmentPagerAdapter {


        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
