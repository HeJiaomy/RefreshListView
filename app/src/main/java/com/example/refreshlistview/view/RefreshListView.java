package com.example.refreshlistview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.example.refreshlistview.R;

/**
 * Created by 12191 on 2018/1/4.
 */

public class RefreshListView extends ListView {

    View headerView;

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
    }

    //初始化头布局
    private void initHeaderView() {
        headerView= View.inflate(getContext(), R.layout.header_layout,null);
        addHeaderView(headerView);
    }
}
