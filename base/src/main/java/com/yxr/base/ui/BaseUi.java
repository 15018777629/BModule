package com.yxr.base.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

/**
 * Created by 63062 on 2017/12/1.
 */

public interface BaseUi {

    @LayoutRes
    int contentView();

    void initView(@Nullable Bundle savedInstanceState);

    void initListener();

    void initData();

    void setContentLayoutParams(@NonNull RelativeLayout.LayoutParams layoutParam);

    void toast(@NonNull String toast);
}
