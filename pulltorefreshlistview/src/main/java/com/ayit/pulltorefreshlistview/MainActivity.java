package com.ayit.pulltorefreshlistview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PullToRefreshListView.OnRefreshListener {

    ArrayList<ApkEntity> apk_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();
        showList(apk_list);
    }

    MyAdapter adapter;
    PullToRefreshListView listview;
    private void showList(ArrayList<ApkEntity> apk_list) {
        if (adapter == null) {
            listview = (PullToRefreshListView) findViewById(R.id.preListView);
            listview.setOnRefreshListener(this);
            adapter = new MyAdapter(this, apk_list);
            listview.setAdapter(adapter);
        } else {
            adapter.onDateChange(apk_list);
        }
    }

    private void setData() {
        apk_list = new ArrayList<ApkEntity>();
        for (int i = 0; i < 10; i++) {
            ApkEntity entity = new ApkEntity();
            entity.setName("默认数据");
            entity.setDes("这是一个神奇的应用");
            entity.setInfo("50w用户");
            apk_list.add(entity);
        }
    }

    private void setReflashData() {
        for (int i = 0; i < 2; i++) {
            ApkEntity entity = new ApkEntity();
            entity.setName("刷新数据");
            entity.setDes("这是一个神奇的应用");
            entity.setInfo("50w用户");
            apk_list.add(0,entity);
        }
    }

    private void setLoadData() {
        for (int i = 0; i < 2; i++) {
            ApkEntity entity = new ApkEntity();
            entity.setName("加载更多数据");
            entity.setDes("这是一个神奇的应用");
            entity.setInfo("50w用户");
            apk_list.add(entity);
        }
    }


    /**
     * 下拉加载更多数据
     */
    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //获取最新数据
                setReflashData();
                //通知界面显示
                showList(apk_list);
                //通知listview 刷新数据完毕；
                listview.refreshComplete();
            }
        }, 2000);
    }

    /**
     * 上拉加载更多数据
     */
    @Override
    public void onLoading() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //获取最新数据
                setLoadData();
                //通知界面显示
                showList(apk_list);
                //通知listview 刷新数据完毕；
                listview.loadComplete();
            }
        }, 2000);
    }
}