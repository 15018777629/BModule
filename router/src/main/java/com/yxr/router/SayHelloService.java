package com.yxr.router;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by 63062 on 2017/12/9.
 */

public interface SayHelloService extends IProvider {
    String sayHello(String name);
}
