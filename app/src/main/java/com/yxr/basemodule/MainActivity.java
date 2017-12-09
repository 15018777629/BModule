package com.yxr.basemodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yxr.base.ui.BaseActivity;
import com.yxr.ui.TitleBar;

@Route(path = "/app/main")
public class MainActivity extends BaseActivity {
    private TextView tvContent;

    @Override
    public int contentView() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void initListener() {
        addRightAction(new TitleBar.TextAction("前往Share") {
            @Override
            public void performAction(View view) {
                ARouter.getInstance().build("/share/shareActivity").navigation();
            }
        });
    }

    @Override
    public void initData() {
        setCommonTitle(getClass().getSimpleName());
    }
}
