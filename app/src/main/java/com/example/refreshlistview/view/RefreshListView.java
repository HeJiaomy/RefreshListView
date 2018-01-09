package com.example.refreshlistview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.refreshlistview.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by 12191 on 2018/1/4.
 */

public class RefreshListView extends ListView {

    View headerView;    //头布局
    float downY, moveY, upY;
    int headerViewHeight;
    public static final int PULL_TO_REFRESH = 0;  //下拉刷新
    public static final int RELEASE_REFRESH = 1;  //释放刷新
    public static final int REFRESHING = 2;   //刷新中
    public int currentState = PULL_TO_REFRESH;    //当前状态
    private RotateAnimation rotateUpAnimation;
    private RotateAnimation rotateDownAnimation;
    ImageView mArrowView;
    TextView mTitleText;
    TextView mTimeText;
    ProgressBar pb;
    public OnRefreshListener mRefreshListener;

    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化头布局、脚布局
     */
    private void init() {
        initHeaderView();
        initAnimation();

    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        //以自己为中心，向上逆时针旋转180
        rotateUpAnimation = new RotateAnimation(0f, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnimation.setDuration(300);
        rotateUpAnimation.setFillAfter(true); //动画执行完毕停留在结束位置

        //以自己为中心，向下逆时针旋转180
        rotateDownAnimation = new RotateAnimation(-180f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnimation.setDuration(300);
        rotateDownAnimation.setFillAfter(true); //动画执行完毕停留在结束位置
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.header_layout, null);
        mArrowView = headerView.findViewById(R.id.img_head_arrow);
        mTitleText = headerView.findViewById(R.id.title_tv);
        pb = headerView.findViewById(R.id.head_pb);
        pb.setVisibility(View.INVISIBLE);
        mTimeText= headerView.findViewById(R.id.time_tv);

        //提前手动测量宽高，按照设置的规则测量
        headerView.measure(0, 0);
        //得到测量的高度
        headerViewHeight = headerView.getMeasuredHeight();
        //设置内边距，可以隐藏当前控件，-自身高度
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        addHeaderView(headerView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                Log.e("downY:", downY + "");
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = ev.getY();
                Log.e("moveY:", moveY + "");
                float offset = moveY - downY;  //设置移动偏移量
                //只有偏移量大于0，并且当前第一个可见条目是0，才放大头布局
                if (offset > 0 && getFirstVisiblePosition() == 0) {
                    int paddingTop = (int) (-headerViewHeight + offset);
                    headerView.setPadding(0, paddingTop, 0, 0);
                    if (paddingTop >= 0 && currentState != RELEASE_REFRESH) { //头布局完全显示了
                        //变成释放刷新模式
                        currentState = RELEASE_REFRESH;
                        updateHeader();
                    } else if (paddingTop < 0 && currentState != PULL_TO_REFRESH) { //头布局不完全显示
                        //变成下拉刷新模式
                        currentState = PULL_TO_REFRESH;
                        updateHeader();
                    }
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                upY = ev.getY();
                Log.e("upY:", upY + "");
                if (currentState== PULL_TO_REFRESH){
//                    paddingTop<0
                    headerView.setPadding(0,-headerViewHeight,0,0);
                }else if (currentState== RELEASE_REFRESH){
//                    paddingTop>=0，完全显示
                    headerView.setPadding(0,0,0,0);
                    currentState= REFRESHING;
                    updateHeader();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 根据状态改变头布局
     */
    private void updateHeader() {
        switch (currentState) {
            case PULL_TO_REFRESH:   //切换下拉刷新
                //执行动画，改变文字
                mArrowView.startAnimation(rotateDownAnimation);
                mTitleText.setText("下拉刷新");
                break;
            case RELEASE_REFRESH: //切换释放刷新
                mArrowView.startAnimation(rotateUpAnimation);
                mTitleText.setText("释放刷新");
                break;
            case REFRESHING:    //刷新中
                mArrowView.clearAnimation();
                mArrowView.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);
                mTitleText.setText("正在刷新中...");

                if (mRefreshListener!= null){
                    mRefreshListener.onRefresh();   //通知调用者
                }
                break;

        }
    }

    /**
     * 下拉刷新完成
     */
    public void onRefreshComplete() {
        currentState= PULL_TO_REFRESH;
        mTitleText.setText("下拉刷新");
        headerView.setPadding(0,-headerViewHeight,0,0);//隐藏头布局
        pb.setVisibility(View.INVISIBLE);
        mArrowView.setVisibility(View.VISIBLE);
        String time= getTime();
        mTimeText.setText(time);
    }

    public String getTime() {
        long currentTime= System.currentTimeMillis();
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return format.format(currentTime);
    }

    public interface OnRefreshListener {
        void onRefresh();   //下拉刷新
    }

    public void setRefreshListener(OnRefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }
}
