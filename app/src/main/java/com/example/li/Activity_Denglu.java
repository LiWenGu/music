package com.example.li;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.li.music.R;

import cn.bmob.v3.Bmob;

public class Activity_Denglu extends AppCompatActivity implements View.OnClickListener{

    private EditText yonghuming,xingbie,phone;
    private Button denglu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.denglu);
        init();
    }

    private void init() {
        Bmob.initialize(this,"80af05df5f8b1d636ee77ccce11c3612");
        yonghuming = (EditText) findViewById(R.id.denglu_et_yonghuming);
        xingbie = (EditText) findViewById(R.id.denglu_et_xingbie);
        phone = (EditText) findViewById(R.id.denglu_et_phone);
        denglu = (Button) findViewById(R.id.denglu_btn_denglu);
        denglu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.denglu_btn_denglu:
                Intent intent = new Intent(this,Activity_Main.class);
                startActivity(intent);
                break;
        }
    }
}
