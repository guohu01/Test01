package com.yoyo.mobilesafe2020.utils;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yoyo.mobilesafe2020.R;
import com.yoyo.mobilesafe2020.adapter.HomeAdapter;

public class HomeActivity extends AppCompatActivity {

    private GridView gv_home;

    //适配器
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    private void initData() {
        gv_home.setAdapter(new HomeAdapter(this));
        //响应activity事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0://手机防盗模块
                        System.out.println("手机防盗模块");
                        break;
                    case 1:
                        System.out.println("通信卫士");
                        break;
                }
            }
        });
    }

    private void initView() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }
}
