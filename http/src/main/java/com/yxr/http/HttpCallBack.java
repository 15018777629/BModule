package com.yxr.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.annotations.NonNull;

/**
 * 访问网络回调
 * Created by 63062 on 2017/9/25.
 */

public abstract class HttpCallBack<T> {
    public Class cls = null;

    /**
     * 访问网络回调
     * @param <T> ： 目标实例对象
     */
    public <T> HttpCallBack(){
        try {
            Class clz = this.getClass();
            ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            this.cls = (Class<T>) types[0];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public abstract void onSuccess(@NonNull T t);

    public abstract void onError(int code, @NonNull String msg);
}
