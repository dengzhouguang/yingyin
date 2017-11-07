package com.dzg.yingyin.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.dzg.yingyin.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by 大灯泡 on 2016/11/23.
 * <p>
 * 自动定位的popup，空间不足显示在上面
 */
public class AutoLocatedPopupOrder extends BasePopupWindow implements View.OnClickListener {
    private View popupMenuView;
    public AutoLocatedPopupOrder(Activity context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAutoLocatePopup(true);
        bindEvent();
    }


    @Override
    protected Animation initShowAnimation() {
        return getDefaultAlphaAnimation();
    }


    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
            popupMenuView = LayoutInflater.from(getContext()).inflate(R.layout.popup_order, null);
            return popupMenuView;
    }

    @Override
    public View initAnimaView() {
        return null;
    }

    private void bindEvent() {
        if (popupMenuView != null)
        {
            popupMenuView.findViewById(R.id.ll_name).setOnClickListener(this);
            popupMenuView.findViewById(R.id.ll_time).setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_name:
                break;
            case R.id.ll_time:
                break;
            default:
                break;
        }

    }
}
