package com.dzg.yingyin.ui.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzg.yingyin.R;
import com.dzg.yingyin.adapter.VideoAdapter;
import com.dzg.yingyin.bean.VideoBean;
import com.dzg.yingyin.ui.view.ReFlashListView;
import com.dzg.yingyin.util.AutoLocatedPopupMenu;
import com.dzg.yingyin.util.AutoLocatedPopupOrder;
import com.dzg.yingyin.util.ImageUtil;
import com.dzg.yingyin.util.TextUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dengzhouguang on 2017/11/1.
 */

public class LocalFragment extends Fragment {
    @BindView(R.id.reFlashListView)
    ReFlashListView mReFlashListView;
    @BindView(R.id.ll_contain)
    LinearLayout mLinearLayout;

    VideoAdapter mAdapter;
    private AutoLocatedPopupMenu autoLocatedPopupMenu;
    private AutoLocatedPopupOrder autoLocatedPopupOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_localfragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        getVideo();
        ArrayList<VideoBean> list=getVideo();
        mAdapter=new VideoAdapter(getActivity(),list);
        mReFlashListView.setAdapter(mAdapter);
        mReFlashListView.setInterface(new ReFlashListView.IReflashListener() {
            @Override
            public void onReflash() {
                mAdapter.onDateChange(getVideo());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mReFlashListView.reflashComplete();
                    }
                },1000);

            }
        });
        mLinearLayout.setVisibility(View.GONE);
        autoLocatedPopupMenu = new AutoLocatedPopupMenu(getActivity());
        autoLocatedPopupOrder = new AutoLocatedPopupOrder(getActivity());
    }

    @OnClick({R.id.menu, R.id.order})
    public void onClick(View v) {
        if (v.getId() == R.id.menu)
            autoLocatedPopupMenu.showPopupWindow(v);
        else
            autoLocatedPopupOrder.showPopupWindow(v);
    }
    public  ArrayList<VideoBean> getVideo() {
        // TODO Auto-generated method stub
        String []projection = {
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MINI_THUMB_MAGIC
        };
        String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ArrayList<VideoBean> listImage = new ArrayList<VideoBean>();
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null,
                null, orderBy);
        if (null == cursor) {
            return listImage ;
        }
        String cache=getActivity().getCacheDir().getAbsolutePath();
        while (cursor.moveToNext()) {
            VideoBean bean=new VideoBean();
            bean.setTitle(cursor.getString(0));
            bean.setFilePath(cursor.getString(1));
            bean.setTime(TextUtil.parseTime(cursor.getString(2)));
            bean.setSize(TextUtil.parseSize(cursor.getString(3)));
            saveVideoThumbnail(bean.getFilePath(),cache+"/"+bean.getTitle());
            bean.setUrl(cache+"/"+bean.getTitle());
            listImage.add(bean);
        }
        return listImage;
    }
    public void saveVideoThumbnail(String filePath, String desFileName) {

        if (new File(desFileName).exists())
            return;
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        ImageUtil.saveBitmap(desFileName,bitmap);
    }
}
