package com.yxr.im;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yxr.base.ui.BaseActivity;
import com.yxr.ui.TitleBar;

/**
 * Created by 63062 on 2017/12/9.
 */

@Route(path = "/im/imActivity")
public class ImActivity extends BaseActivity {
    @Override
    public int contentView() {
        return R.layout.im_activity;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        setCommonTitle(getClass().getSimpleName());
        addRightAction(new TitleBar.TextAction("跳转回App") {
            @Override
            public void performAction(View view) {
                ARouter.getInstance().build("/app/main").navigation();
            }
        });
    }
}
