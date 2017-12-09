package com.yxr.ui.polymorphism;

import android.view.View;

/**
 * Created by 63062 on 2017/8/11.
 */

public class Polymorphism {
    public int state;
    public int layoutId;
    public View view;

    public Polymorphism(int state,int layoutId){
        this.state = state;
        this.layoutId = layoutId;
    }
}
