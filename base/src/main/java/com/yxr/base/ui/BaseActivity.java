package com.yxr.base.ui;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yxr.base.R;
import com.yxr.http.HttpCode;
import com.yxr.http.HttpHelper;
import com.yxr.ui.TitleBar;
import com.yxr.ui.polymorphism.PolymorphismLayout;

/**
 * Activity基类
 * Created by 63062 on 2017/12/1.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseUi {
    protected TitleBar titleBar;
    protected PolymorphismLayout polymorphismLayout;
    protected HttpHelper httpHelper = new HttpHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initTitleBar();
        initPolymorphismLayout();
        initView(savedInstanceState);
        initListener();
        initData();
    }

    @Override
    public void toast(@NonNull String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setContentLayoutParams(@NonNull RelativeLayout.LayoutParams layoutParam) {
        polymorphismLayout.setLayoutParams(layoutParam);
    }

    @Override
    public View findViewById(@IdRes int id) {
        View contentView = polymorphismLayout.getContentView();
        if (contentView == null) {
            return null;
        }
        return contentView.findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpHelper != null){
            httpHelper.clearDisposable();
            httpHelper = null;
        }
    }

    public void setCommonTitle(@NonNull String title) {
        titleBar.setTitle(title);
    }

    /**
     * 初始化TitleBar
     */
    private void initTitleBar() {
        int color = getResources().getColor(R.color.gray39364D);
        titleBar = (TitleBar) super.findViewById(R.id.baseTitleBar);
        titleBar.setTitleColor(color);
        titleBar.setLeftTextColor(color);
        titleBar.setActionTextColor(color);
        setLeft(R.drawable.go_back_normal, "    ", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化多状态控件
     */
    private void initPolymorphismLayout() {
        polymorphismLayout = (PolymorphismLayout) super.findViewById(R.id.basePolymorphismLayout);
        polymorphismLayout.setStateLayout(PolymorphismLayout.CONTENT, contentView(), false);
        polymorphismLayout.setStateClickListener(new PolymorphismLayout.StateClickListener() {
            @Override
            public void onStateClick(View view, int state) {
                if (state == PolymorphismLayout.NET_EXCEPTION){
                    initData();
                }
            }
        });
    }

    /**
     * 设置TitleBar左边数据
     *
     * @param imageIcon     ：左边显示图片
     * @param left          ：左边文本
     * @param clickListener ：左边控件点击事件
     */
    public void setLeft(@DrawableRes int imageIcon, @NonNull String left, View.OnClickListener clickListener) {
        titleBar.setLeftImageResource(imageIcon);
        titleBar.setLeftText(left);
        titleBar.setLeftClickListener(clickListener);
    }

    /**
     * 添加右边事件
     */
    public void addRightAction(@NonNull TitleBar.Action action) {
        titleBar.addAction(action);
    }

    /**
     * 展示Loading状态
     */
    public void showLoading() {
        setState(PolymorphismLayout.LOADING);
    }

    /**
     * 停止展示Loading,展示内容布局
     */
    public void dismissLoading() {
        setState(PolymorphismLayout.CONTENT);
    }

    /**
     * 停止展示Loading,展示其他错误布局
     *
     * @param code      ：错误码
     * @param showEmpty ：是否强制展示没有数据的状态
     */
    public void dismissLoading(int code, boolean showEmpty) {
        if (showEmpty) {
            setState(PolymorphismLayout.EMPTY);
        } else if (HttpCode.EXCEPTION_NO_CONNECT == code || HttpCode.EXCEPTION_TIME_OUT == code) {
            setState(PolymorphismLayout.NET_EXCEPTION);
        } else {
            setState(PolymorphismLayout.ERROR);
        }
    }

    /**
     * 设置控件状态
     */
    public void setState(int state) {
        polymorphismLayout.setState(state);
    }
}
