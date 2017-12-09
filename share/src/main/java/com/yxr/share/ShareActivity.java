package com.yxr.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yxr.base.ui.BaseActivity;
import com.yxr.router.SayHelloService;

/**
 * Created by 63062 on 2017/12/9.
 */

@Route(path = "/share/shareActivity")
public class ShareActivity extends BaseActivity implements View.OnClickListener {
    @Autowired()
    SayHelloService sayHelloService;

//    建议使用这种方式,因为接口是可以被多实现的,除非你100%确定SayHelloService这个接口只被一个实现
//    @Autowired(name = "/im/sayHelloService")
//    SayHelloService sayHelloService;

    public ShareActivity() {
        ARouter.getInstance().inject(this);
    }

    @Override
    public int contentView() {
        return R.layout.share_activity;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {
        findViewById(R.id.btnHello).setOnClickListener(this);
        findViewById(R.id.btnJumpIm).setOnClickListener(this);
    }

    @Override
    public void initData() {
        setCommonTitle(getClass().getSimpleName());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnHello){
            // 为什么要判断不为空呢？因为组件间没有互相依赖，当你单独运行某个组件时
            // 另外一个组件并没有编译进来，所以会发现不了实现这个接口的服务
            if (sayHelloService != null){
                toast(sayHelloService.sayHello("组件化"));
            }
        } else if (id == R.id.btnJumpIm){
            ARouter.getInstance().build("/im/imActivity").navigation();
        }
    }
}
