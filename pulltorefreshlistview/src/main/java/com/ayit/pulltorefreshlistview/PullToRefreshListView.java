package com.ayit.pulltorefreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sweetheart on 2015/8/10 14:13.
 * Email: 378398018@qq.com
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private LayoutInflater inflater;
    //header View
    private View headerView;

    //foot Layout
    private  View footLayout;

    //顶部布局文件的高度
    private int headerdHeight;

    //当前第一个可见的item
    private int firstVisibleItem;

    // item总数量；
    private int totalItemCount;

    // 最后一个可见的item；
    private int lastVisibleItem;

    //当前的滚动状态
    private int scrollState;

    //按下时的Y值
    private int startY;

    //标记是否是在最顶端按下去的
    private boolean isHeaderPress;

    //是否正在加载
    private boolean isLoading;

    // 当前的状态；
    private int state;
    private static final int NONE = 0;// 正常状态；
    private static final int PULL = 1;// 提示下拉状态；
    private static final int RELEASE = 2;// 提示释放状态；
    private static final int REFRESHING = 3;// 刷新状态；


    private OnRefreshListener refreshListener;


    private TextView tip;//提示信息
    private ImageView arrow;//下拉图标
    private ProgressBar progress;//滚动条
    private TextView lastTime;//设置最后刷新时间

    public PullToRefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {

        inflater = LayoutInflater.from(context);

        initHeaderView();
        initFootView();


        setOnScrollListener(this);
    }

    /**
     * 初始化headerView
     */
    private void initHeaderView() {


        headerView = inflater.inflate(R.layout.header_layout, null);

        measuerView(headerView);

        headerdHeight = headerView.getMeasuredHeight();
        topPadding(-headerdHeight);

        tip = (TextView) headerView.findViewById(R.id.tip);
        arrow = (ImageView) headerView.findViewById(R.id.arrow);
        progress = (ProgressBar) headerView.findViewById(R.id.progress);
        lastTime = (TextView) headerView.findViewById(R.id.lastupdate_time);
        addHeaderView(headerView);
    }

    /**
     * 初始化FootView
     */
    private void initFootView(){

        View footView = inflater.inflate(R.layout.foot_layout, null);

        footLayout = footView.findViewById(R.id.load_layout);
        footLayout.setVisibility(View.GONE);
        addFooterView(footView);
    }

    /**
     * 设置header布局上边距
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {

        headerView.setPadding(headerView.getPaddingLeft(), topPadding,
                headerView.getPaddingRight(), headerView.getPaddingBottom());
        headerView.invalidate();

    }

    /**
     * 通知父布局header占据的宽和高
     * 如果：高度>0,则有父类完全决定子窗口高度大小；否则，由子窗口自己觉得自己的高度大小
     *
     * @param headerView
     */
    private void measuerView(View headerView) {
        ViewGroup.LayoutParams lp = headerView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int with = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int height;
        int tempHeight = lp.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        headerView.measure(with, height);

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;

        if(!isLoading && scrollState==SCROLL_STATE_IDLE){
            isLoading=true;

            if(refreshListener!=null){

                footLayout.setVisibility(View.VISIBLE);
                //加载更多数据
                refreshListener.onLoading();
            }

        }


    }

    /**
     * Callback method to be invoked when the list or grid has been scrolled. This will be
     * called after the scroll has completed
     *
     * @param view             The view whose scroll state is being reported
     * @param firstVisibleItem the index of the first visible cell (ignore if
     *                         visibleItemCount == 0)
     * @param visibleItemCount the number of visible cells
     * @param totalItemCount   the number of items in the list adaptor
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.totalItemCount=totalItemCount;
        this.lastVisibleItem=firstVisibleItem+visibleItemCount;
    }

    ;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (firstVisibleItem == 0) {
                    isHeaderPress = true;
                    startY = (int) ev.getY();
                }


                break;
            case MotionEvent.ACTION_MOVE:

                onMove(ev);

                break;
            case MotionEvent.ACTION_UP:

                if (state == RELEASE) {
                    state = REFRESHING;

                    //加载新数据
                    if (refreshListener != null) {
                        refreshListener.onRefresh();
                    }

                } else if (state == PULL) {
                    state = NONE;
                    isHeaderPress = false;

                }

                refreshHeaderView();

                break;
        }


        return super.onTouchEvent(ev);
    }

    private void onMove(MotionEvent ev) {

        if (!isHeaderPress)
            return;

        int tempY = (int) ev.getY();

        int offset = tempY - startY;
        int topPadding = offset - headerdHeight;

        switch (state) {
            case NONE:

                if (offset > 0)
                    state = PULL;
                break;
            case PULL:
                if (offset > headerdHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL)
                    state = RELEASE;
                break;
            case RELEASE:
                if (offset < headerdHeight + 30) {
                    state = PULL;
                } else if (offset <= 0) {
                    state = NONE;
                }

                break;
        }
        topPadding(topPadding);
        //根据状态，改变界面显示
        refreshHeaderView();


    }

    /**
     * 刷新头部布局中的状态
     */
    private void refreshHeaderView() {

        switch (state) {
            case NONE:
                arrow.clearAnimation();
                topPadding(-headerdHeight);

                break;
            case PULL:
                arrow.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setAnimation(rotateAnimation(0,180, 300));
                tip.setText("下拉刷新");
                progress.setVisibility(View.GONE);

                break;
            case RELEASE:
                arrow.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setAnimation(rotateAnimation(180,0, 300));
                tip.setText("释放立即刷新");
                progress.setVisibility(View.GONE);

                break;
            case REFRESHING:
                topPadding(45);
                arrow.setVisibility(View.GONE);
                arrow.clearAnimation();
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新");

                break;
        }

    }

    /**
     * 为下拉图标设置旋转动画
     *
     * @param start
     * @param end
     * @param duration
     * @return
     */
    private Animation rotateAnimation(int start, int end, int duration) {

        RotateAnimation rotateAnimation = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);
        return rotateAnimation;


    }


    /**
     * 下拉获取完数据
     */
    public void refreshComplete() {
        state = NONE;
        isHeaderPress = false;
        refreshHeaderView();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        lastTime.setText(format.format(new Date()));

    }

    /**
     * 加载更多数据完成
     */
    public void loadComplete(){

        isLoading=false;
        footLayout.setVisibility(View.GONE);

    }


    /**
     * 刷新数据回调接口
     */
    public interface OnRefreshListener {
        /**
         * 下拉加载更多数据
         */
        void onRefresh();

        /**
         * 上拉加载更多数据
         */
        void onLoading();
    }


    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }
}
