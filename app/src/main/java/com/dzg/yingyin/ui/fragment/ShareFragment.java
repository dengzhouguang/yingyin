package com.dzg.yingyin.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzg.yingyin.R;

import butterknife.ButterKnife;

/**
 * Created by dengzhouguang on 2017/11/1.
 */

public class ShareFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_sharefragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
