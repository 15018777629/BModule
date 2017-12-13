package com.yxr.http;

import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 网络访问帮助类
 * Created by 63062 on 2017/9/25.
 */

public class HttpHelper {
    private CompositeDisposable compositeDisposable;
    /**
     * post请求
     *
     * @param url      ： 没有配置BaseUrl的地址
     * @param map      ： 请求参数
     * @param callBack ： 访问回调
     */
    public <T> void obPost(String url, Map<String, Object> map, HttpCallBack<T> callBack) {
        if (map == null) {
            map = new HashMap<>();
        }
        OkGo.<String>post(url)
                .upJson(GsonHelper.instance().toJson(map))
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver(callBack));
    }

    public <T> void obGet(String url, Map<String, String> map, HttpCallBack<T> callBack) {
        if (map == null) {
            map = new HashMap<>();
        }
        OkGo.<String>get(url)
                .params(map)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver(callBack));
    }

    public <T> void obDelete(String url, Map<String, String> map, HttpCallBack<T> callBack) {
        if (map == null) {
            map = new HashMap<>();
        }
        OkGo.<String>delete(url)
                .params(map)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver(callBack));
    }

    public <T> void obPut(String url, Map<String, Object> map, HttpCallBack<T> callBack) {
        if (map == null) {
            map = new HashMap<>();
        }
        OkGo.<String>put(url)
                .upJson(GsonHelper.instance().toJson(map))
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver(callBack));
    }

    private class HttpObserver implements Observer<Response<String>> {

        private final HttpCallBack callBack;

        public <T> HttpObserver(HttpCallBack<T> callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
        }

        @Override
        public void onNext(@io.reactivex.annotations.NonNull Response<String> response) {
            onNextCallBack(response, callBack);
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
            e.printStackTrace();
            if (e instanceof HttpException) {
                HttpException ex = (HttpException) e;
                onErrorCallBack(ex.code(), e.getMessage(), callBack);
            } else if (e instanceof SocketTimeoutException) {           // 连接超时或没有网络连接
                onErrorCallBack(HttpCode.EXCEPTION_TIME_OUT, "连接超时", callBack);
            } else if (e instanceof UnknownHostException) {              // 没有网络连接
                onErrorCallBack(HttpCode.EXCEPTION_NO_CONNECT, "没有网络连接", callBack);
            } else {
                onErrorCallBack(HttpCode.EXCEPTION_OTHER, "其他错误", callBack);
            }
        }

        @Override
        public void onComplete() {
        }
    }

    /**
     * 访问网络数据成功
     *
     * @param response ： 回传数据
     * @param callBack ： 回调
     */
    private <T> void onNextCallBack(Response<String> response, HttpCallBack<T> callBack) {
        if (callBack == null) {
            return;
        }
        String body = response.body();
        Log.e("HTTP_HELPER", "http code : " + response.code());
        if (response.code() != 200) {
            onErrorCallBack(response.code(), "服务器异常", callBack);
            return;
        }
        if (callBack.cls == null) {
            callBack.onError(HttpCode.EXCEPTION_OTHER, "数据异常");
            return;
        }

        if (callBack.cls == String.class){
            callBack.onSuccess((T) body);
            return;
        }

        T t = null;
        try {
            t = (T) GsonHelper.instance().fromJson(body, callBack.cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (t == null) {
            onErrorCallBack(HttpCode.EXCEPTION_DATA_PARSE, "数据解析异常", callBack);
        } else {
            callBack.onSuccess(t);
        }
    }

    /**
     * 访问网络错误
     *
     * @param code     ： 错误码
     * @param errorMsg ： 错误信息
     * @param callBack ： 回调
     */
    private <T> void onErrorCallBack(int code, String errorMsg, HttpCallBack<T> callBack) {
        Log.e("HTTP_HELPER", "onErrorCallBack code : " + code + " ===== message : " + errorMsg);
        if (callBack == null) {
            return;
        }
        callBack.onError(code, errorMsg);
    }

    private void addDisposable(Disposable disposable) {
        if (compositeDisposable == null){
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    public void clearDisposable(){
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
    }

}
