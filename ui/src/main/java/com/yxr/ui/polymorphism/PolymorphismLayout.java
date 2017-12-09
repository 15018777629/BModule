package com.yxr.ui.polymorphism;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.yxr.ui.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 多状态的控件
 * Created by 63062 on 2017/8/11.
 */

public class PolymorphismLayout extends FrameLayout implements View.OnClickListener {
    public static final int ERROR = 0;
    public static final int EMPTY = 1;
    public static final int NET_EXCEPTION = 2;
    public static final int LOADING = 3;
    public static final int CONTENT = 4;

    private int mInAnim = R.anim.poly_in_anim;
    private int mOutAnim = R.anim.poly_out_anim;
    private int mState = LOADING;
    private int mPreState = CONTENT;
    public Map<Integer,Polymorphism> mPolyMap = new HashMap<>();
    private StateClickListener clickListener;
    private boolean isAnim = false;
    private View mCurrView;


    public PolymorphismLayout(@NonNull Context context) {
        this(context,null);
    }

    public PolymorphismLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PolymorphismLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化数据
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        Polymorphism errorPoly = new Polymorphism(ERROR, R.layout.layout_poly_error);
        Polymorphism emptyPoly = new Polymorphism(EMPTY,R.layout.layout_poly_empty);
        Polymorphism exceptionPoly = new Polymorphism(NET_EXCEPTION,R.layout.layout_poly_exception);
        Polymorphism loadingPoly = new Polymorphism(LOADING,R.layout.layout_poly_loading);
        Polymorphism contentPoly = new Polymorphism(CONTENT,0);

        if (attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PolymorphismLayout);

            mState = array.getInteger(R.styleable.PolymorphismLayout_state,LOADING);
            mInAnim = array.getResourceId(R.styleable.PolymorphismLayout_in_anim, R.anim.poly_in_anim);
            mOutAnim = array.getResourceId(R.styleable.PolymorphismLayout_out_anim, R.anim.poly_out_anim);

            int errorLayout = array.getResourceId(R.styleable.PolymorphismLayout_error_layout, R.layout.layout_poly_error);
            int emptyLayout = array.getResourceId(R.styleable.PolymorphismLayout_empty_layout, R.layout.layout_poly_empty);
            int exceptionLayout = array.getResourceId(R.styleable.PolymorphismLayout_net_exception_layout, R.layout.layout_poly_exception);
            int loadingLayout = array.getResourceId(R.styleable.PolymorphismLayout_loading_layout, R.layout.layout_poly_loading);
            int contentLayout = array.getResourceId(R.styleable.PolymorphismLayout_content_layout, 0);

            errorPoly.layoutId = errorLayout;
            emptyPoly.layoutId = emptyLayout;
            exceptionPoly.layoutId = exceptionLayout;
            loadingPoly.layoutId = loadingLayout;
            contentPoly.layoutId = contentLayout;
            array.recycle();
        }
        mPolyMap.put(ERROR,errorPoly);
        mPolyMap.put(EMPTY,emptyPoly);
        mPolyMap.put(NET_EXCEPTION,exceptionPoly);
        mPolyMap.put(LOADING,loadingPoly);
        mPolyMap.put(CONTENT,contentPoly);

        getView(contentPoly);
        setState(mState);
    }

    /**改变状态*/
    private void changedState() {
        View mPreView = getView(mPolyMap.get(mPreState));
        mCurrView = getView(mPolyMap.get(mState));

        if (isAnim || mPreView == null || mState == LOADING){
            animEnd(false);
            return;
        }

        setAnim(mPreView,mOutAnim,true);
        setAnim(mCurrView,mInAnim,false);

    }

    /**
     * 设置进入退出动画
     * @param view ： 设置的控件
     * @param anim ： 设置的动画
     * @param isPre ： 是否是上一个状态的控件
     */
    private void setAnim(final View view, final int anim, final boolean isPre) {
        if (view == null){
            return;
        }
        view.setVisibility(VISIBLE);
        view.clearAnimation();
        try {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), anim);
            if (animation == null){
                animEnd(isPre);
                return;
            }
            isAnim = true;
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    animEnd(isPre);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.setAnimation(animation);
        }catch (Exception e){
            e.printStackTrace();
            animEnd(isPre);
        }
    }

    /**
     * 动画结束
     * @param isPre
     */
    private void animEnd(boolean isPre) {
        Iterator<Map.Entry<Integer, Polymorphism>> iterator = mPolyMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Polymorphism> next = iterator.next();
            Polymorphism value = next.getValue();
            if (value == null || value.view == null){
                continue;
            }
            value.view.clearAnimation();
            if (mCurrView != null && mCurrView == value.view){
                value.view.setVisibility(VISIBLE);
            } else{
                value.view.setVisibility(GONE);
            }
        }
        if (!isPre){
            mPreState = mState;
        }
        isAnim = false;
    }

    /**
     * 获取对应的View
     * 如果View为null则通过layoutId inflate
     * @param poly
     * @return
     */
    private View getView(Polymorphism poly) {
        if (poly == null){
            return null;
        }
        if (poly.view == null){
            if (poly.layoutId == 0){
                return null;
            }
            try {
                View view = View.inflate(getContext(), poly.layoutId, null);
                if (poly.state != CONTENT){
                    view.setOnClickListener(this);
                }
                addView(view);
                poly.view = view;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return poly.view;
    }

    /**
     * 设置当前状态
     * @param state
     */
    public void setState(int state){
        mState = state;
        changedState();
    }


    @Override
    public void onClick(View v) {
        if (clickListener != null){
            clickListener.onStateClick(v,mState);
        }
    }

    public void setStateClickListener(StateClickListener clickListener){
        this.clickListener = clickListener;
    }

    public View getContentView() {
        Polymorphism polymorphism = mPolyMap.get(CONTENT);
        if (polymorphism == null){
            return null;
        }
        if (polymorphism.view == null){
            return getView(polymorphism);
        }
        return polymorphism.view;
    }


    public interface StateClickListener{
        void onStateClick(View view, int state);
    }

    public void setStateLayout(@NonNull int state, @LayoutRes int layoutId, boolean showNow){
        Polymorphism polymorphism = mPolyMap.get(state);
        if (polymorphism == null){
            return;
        }
        polymorphism.layoutId = layoutId;
        if (polymorphism.view != null){
            removeView(polymorphism.view);
            polymorphism.view = null;
        }
        if (showNow){
            setState(state);
        }
    }
}
