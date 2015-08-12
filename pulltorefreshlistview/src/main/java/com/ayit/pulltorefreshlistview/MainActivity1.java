package com.ayit.pulltorefreshlistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity implements PullToRefreshListView.OnRefreshListener {

    private PullToRefreshListView listView;
    private List<String> datas;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (PullToRefreshListView) findViewById(R.id.preListView);
        initData();


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, datas);

        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);


    }

    private void initData() {
        datas=new ArrayList<>();
        for (int i ='A'; i <'Z'; i++) {
            datas.add(""+(char)i);
        }

    }


    @Override
    public void onRefresh() {
        for (int i=0;i<2;i++){
            datas.add("刷新数据"+i);
        }
        android.os.Handler handler=new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        },2000);

        listView.refreshComplete();

    }

    /**
     * 上拉加载更多数据
     */
    @Override
    public void onLoading() {

    }
}
