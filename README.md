# PullToRefreshListView
#in the layout
   <com.ayit.pulltorefreshlistview.PullToRefreshListView
        android:id="@+id/pullToRefreshListView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
        
        
#in the Activity
            PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
            pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // do something when the pull down to refresh 
            }

            @Override
            public void onLoading() {
                 //In the tensile loads on doing something
            }
        });
