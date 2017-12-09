package com.yxr.im;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yxr.router.SayHelloService;

/**
 * Created by 63062 on 2017/12/9.
 */
@Route(path = "/im/sayHelloService")
public class SayHelloServiceImp implements SayHelloService{
    @Override
    public String sayHello(String name) {
        return "hello," + name;
    }

    @Override
    public void init(Context context) {

    }
}
