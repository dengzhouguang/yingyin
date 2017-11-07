/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzg.yingyin.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dzg.yingyin.R;
import com.dzg.yingyin.ui.view.VerticalSeekBar;
import com.dzg.yingyin.util.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

@SuppressLint("NewApi")
public class MediaPlayerActivity extends AppCompatActivity implements OnBufferingUpdateListener,
        OnCompletionListener, OnPreparedListener, TextureView.SurfaceTextureListener {
    private static final String TAG = "MediaPlayerDemo";
    @BindView(R.id.surface)
    TextureView mTextureView;
    @BindView(R.id.progressBarLoading)
    ProgressBar mProgressBar;
    @BindView(R.id.textViewVideoTitle)
    TextView mTitleTv;
    @BindView(R.id.seekBarVideoProgress)
    SeekBar mVideoSeekBar;
    @BindView(R.id.textViewTimeElapsed)
    TextView mTimeElapsedTv;
    @BindView(R.id.textViewVideoDuration)
    TextView mTimeDurationTv;
    @BindView(R.id.buttonPauseAndPlay)
    ImageButton mPauseOrPlayBtn;
    @BindView(R.id.buttonVol)
    ImageButton mVolButton;
    @BindView(R.id.buttonLightness)
    ImageButton mLightnessButton;
    @BindView(R.id.layoutVolControl)
    LinearLayout mLayoutVolControl;
    @BindView(R.id.seekbarVol)
    VerticalSeekBar mVolSeekBar;
    @BindView(R.id.layoutBrightnessControl)
    LinearLayout mLayoutBrightnessControl;
    @BindView(R.id.seekbarBrightness)
    VerticalSeekBar mBrightnessSeekBar;
    @BindView(R.id.layoutTopController)
    RelativeLayout mTopController;
    @BindView(R.id.layoutBottomController)
    LinearLayout mBottomController;
    @BindView(R.id.layoutPlayerMenu)
    LinearLayout mPlayerMenuLayout;
    @BindView(R.id.imageViewBatteryStatus)
    ImageView mBatteryStatusIv;
    @BindView(R.id.textViewSystemTime)
    TextView mTimeSystemTv;


    private MediaPlayer mMediaPlayer;
    private Surface surf;
    private String mFilePath;
    private String mVideoName;
    private boolean mIsVideoReadyToBePlayed = false;
    private boolean mIsPlaying;
    private int mVideoWidth;
    private int mVideoHeight;
    private AudioManager mAudioManager;
    private float mOldx, mOldy, mDownX, mDownY;
    public static int MESSAGE_GONE = 0x110;
    public static int MESSAGE_VISIABLE = 0x111;
    public static int MESSAGE_VOL_GONE = 0x112;
    public static int MESSAGE_BRIGHTNESS_GONE = 0x113;
    public static int MESSAGE_MENU_GONE = 0x114;
    private int mVolProgress;
    private int mBrightnessProgress;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.layout_mediaplayer);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mFilePath = getIntent().getStringExtra("filepath");
        mVideoName = getIntent().getStringExtra("videoname");
        mTitleTv.setText(mVideoName);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mVolSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 10);
        mVolSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("volSeekBar", fromUser + " : " + progress);
//                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress/10,0);
                if (mVolProgress != progress) {
                    mHandle.removeMessages(MESSAGE_VOL_GONE);
                    mHandle.sendEmptyMessageDelayed(MESSAGE_VOL_GONE, 5000);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress / 10, 0);
                    mVolProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mBrightnessProgress != progress) {
                    mHandle.removeMessages(MESSAGE_BRIGHTNESS_GONE);
                    mHandle.sendEmptyMessageDelayed(MESSAGE_BRIGHTNESS_GONE, 5000);
                    WindowManager.LayoutParams attributes = getWindow().getAttributes();
                    attributes.screenBrightness = (float) (0.2 + progress / 150 * 0.8);
                    getWindow().setAttributes(attributes);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new BatteryReceiver(), filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_TIME_TICK);
        filter2.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new TimeReceiver(), filter2);
        mTimeSystemTv.setText(TextUtil.getHourAndMinutes());
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    private void playVideo(SurfaceTexture surfaceTexture) {
        doCleanUp();
        try {
            if (mFilePath == "") {
                Toast.makeText(MediaPlayerActivity.this, "选定的文件不是有效的视屏", Toast.LENGTH_LONG).show();
                finish();
            }
            mMediaPlayer = new MediaPlayer(this, true);
            mMediaPlayer.setDataSource(mFilePath);
            if (surf == null) {
                surf = new Surface(surfaceTexture);
            }
            mMediaPlayer.setSurface(surf);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mVideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mHandle.removeMessages(MESSAGE_GONE);
                        mHandle.sendEmptyMessageDelayed(MESSAGE_GONE, 5000);
                        mMediaPlayer.seekTo(progress);
                    }
                    mTimeElapsedTv.setText(TextUtil.parseTime(String.valueOf(progress)));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed) {
            startVideoPlayback();
        }
        long max = mMediaPlayer.getDuration();
        mVideoSeekBar.setMax((int) max);
        mTimeDurationTv.setText(" / " + TextUtil.parseTime(String.valueOf(max)));
        new Thread() {
            public void run() {
                mIsPlaying = true;
                while (mIsPlaying) {
                    long position = mMediaPlayer.getCurrentPosition();
                    mVideoSeekBar.setProgress((int) position);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            ;

        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null && mIsPlaying) {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    private void releaseMediaPlayer() {
        mIsPlaying = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }
    }

    private void doCleanUp() {
        mIsVideoReadyToBePlayed = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        mVideoWidth = mMediaPlayer.getVideoWidth();
        mVideoHeight = mMediaPlayer.getVideoHeight();
        adjustAspectRatio(mVideoWidth, mVideoHeight, mTextureView.getWidth(), mTextureView.getHeight());
        mMediaPlayer.start();
        mHandle.removeMessages(MESSAGE_GONE);
        mHandle.sendEmptyMessageDelayed(MESSAGE_GONE, 5000);
    }

    /**
     * Sets the TextureView transform to preserve the aspect ratio of the video.
     */
    private void adjustAspectRatio(int videoWidth, int videoHeight, int viewWidth, int viewHeight) {
        double aspectRatio = (double) videoHeight / videoWidth;
        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Matrix txform = new Matrix();
        mTextureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        txform.postTranslate(xoff, yoff);
        mTextureView.setTransform(txform);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        playVideo(surface);
        mProgressBar.clearAnimation();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mMediaPlayer.setSurface(surf);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @OnClick({R.id.buttonPauseAndPlay, R.id.buttonPrevious, R.id.buttonNextVideo, R.id.imageButtonReturn, R.id.buttonSmallWindow, R.id.buttonLightness, R.id.buttonVol, R.id.imageButtonMenu})
    public void onClick(View view) {
        mHandle.removeMessages(MESSAGE_GONE);
        mHandle.sendEmptyMessageDelayed(MESSAGE_GONE, 5000);
        if (view.getId() == R.id.buttonPauseAndPlay) {
            if (mIsPlaying) {
                mPauseOrPlayBtn.setImageResource(R.mipmap.player_pause_normal);
                mIsPlaying = false;
                mMediaPlayer.pause();
            } else {
                mPauseOrPlayBtn.setImageResource(R.mipmap.player_play_normal);
                mMediaPlayer.start();
                mIsPlaying = true;
            }
        } else if (view.getId() == R.id.imageButtonReturn) {
            onBackPressed();
        } else if (view.getId() == R.id.buttonSmallWindow) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else if (view.getId() == R.id.buttonVol) {
            if (mLayoutVolControl.getVisibility() == View.VISIBLE) {
                mLayoutVolControl.setVisibility(View.GONE);
            } else {
                mLayoutVolControl.setVisibility(View.VISIBLE);
                mHandle.sendEmptyMessageDelayed(MESSAGE_VOL_GONE, 5000);
            }
        } else if (view.getId() == R.id.buttonLightness) {
            if (mLayoutBrightnessControl.getVisibility() == View.VISIBLE) {
                mLayoutBrightnessControl.setVisibility(View.GONE);
            } else {
                mLayoutBrightnessControl.setVisibility(View.VISIBLE);
                mHandle.sendEmptyMessageDelayed(MESSAGE_BRIGHTNESS_GONE, 5000);
            }
        } else if (view.getId() == R.id.imageButtonMenu) {
            if (mPlayerMenuLayout.getVisibility() == View.VISIBLE)
                mPlayerMenuLayout.setVisibility(View.GONE);
            else {
                mPlayerMenuLayout.setVisibility(View.VISIBLE);
                mHandle.removeMessages(MESSAGE_MENU_GONE);
                mHandle.sendEmptyMessageDelayed(MESSAGE_MENU_GONE, 5000);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = event.getX();
            mDownY = event.getY();
            mOldx = event.getY();
            mOldy = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mDownX > getResources().getDisplayMetrics().widthPixels / 2) {
                if (Math.abs(event.getY() - mOldy) > 20) {
                    if (mLayoutVolControl.getVisibility() == View.GONE)
                        mLayoutVolControl.setVisibility(View.VISIBLE);
                    double proportion = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / ((double) getResources().getDisplayMetrics().heightPixels) * 3;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + proportion * (mOldy - event.getY())), 0);
                    int progress = (int) (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 10 + proportion * (mOldy - event.getY()));
                    if (progress < 0)
                        progress = 0;
                    else if (progress > 150)
                        progress = 150;
                    mVolSeekBar.setProgress(progress);
                    mOldx = event.getX();
                    mOldy = event.getY();
                    mVolProgress = progress;
                    mHandle.removeMessages(MESSAGE_VOL_GONE);
                    mHandle.sendEmptyMessageDelayed(MESSAGE_VOL_GONE, 5000);
                }
            } else {
                if (Math.abs(event.getY() - mOldy) > 20) {
                    if (mLayoutBrightnessControl.getVisibility() == View.GONE)
                        mLayoutBrightnessControl.setVisibility(View.VISIBLE);
                    WindowManager.LayoutParams attributes = getWindow().getAttributes();
                    double proportion = 0.8 / (double) getResources().getDisplayMetrics().heightPixels * 3;
                    float screenBrightness = (float) (attributes.screenBrightness + proportion * (mOldy - event.getY()));
                    if (screenBrightness > 1)
                        screenBrightness = 1;
                    else if (screenBrightness < 0.2)
                        screenBrightness = 0.2f;
                    attributes.screenBrightness = screenBrightness;
                    getWindow().setAttributes(attributes);
                    mBrightnessProgress = (int) ((attributes.screenBrightness - 0.2) * 150 / 0.8);
                    mBrightnessSeekBar.setProgress(mBrightnessProgress);
                    mOldx = event.getX();
                    mOldy = event.getY();
                    mHandle.removeMessages(MESSAGE_BRIGHTNESS_GONE);
                    mHandle.sendEmptyMessageDelayed(MESSAGE_BRIGHTNESS_GONE, 5000);
                }

            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mDownX == event.getX() && mDownY == event.getY()) {
                int i = mTopController.getVisibility();
                if (mTopController.getVisibility() == View.VISIBLE) {
                    mHandle.sendEmptyMessage(MESSAGE_GONE);
                    mPlayerMenuLayout.setVisibility(View.GONE);
                } else {

                    mHandle.sendEmptyMessage(MESSAGE_VISIABLE);
                    mHandle.removeMessages(MESSAGE_GONE);
                    mHandle.sendEmptyMessageDelayed(MESSAGE_GONE, 5000);
                }

            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adjustAspectRatio(getResources().getDisplayMetrics().heightPixels, getResources().getDisplayMetrics().widthPixels, mTextureView.getWidth(), mTextureView.getHeight());
            mLightnessButton.setVisibility(View.VISIBLE);
            mVolButton.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            adjustAspectRatio(mVideoWidth, mVideoHeight, mTextureView.getHeight(), mTextureView.getWidth());
            mLightnessButton.setVisibility(View.GONE);
            mVolButton.setVisibility(View.GONE);
        }
    }

    public void setForegroundVisibale() {
        mTopController.setVisibility(View.VISIBLE);
        mBottomController.setVisibility(View.VISIBLE);
    }

    public void setForegroundGone() {
        mTopController.setVisibility(View.GONE);
        mBottomController.setVisibility(View.GONE);
    }

    private Handler mHandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_GONE)
                setForegroundGone();
            else if (msg.what == MESSAGE_VISIABLE)
                setForegroundVisibale();
            else if (msg.what == MESSAGE_BRIGHTNESS_GONE)
                mLayoutBrightnessControl.setVisibility(View.GONE);
            else if (msg.what == MESSAGE_VOL_GONE)
                mLayoutVolControl.setVisibility(View.GONE);
            else if (msg.what == MESSAGE_MENU_GONE)
                mPlayerMenuLayout.setVisibility(View.GONE);
            return true;
        }
    });

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            getIntExtra("level")
//            getIntExtra("scale")
            int level = intent.getIntExtra("level", 0);
            int scale=intent.getIntExtra("scale",100);
            int percent=(level*100/scale);
            if (percent >= 90)
                mBatteryStatusIv.setImageResource(R.mipmap.battery_100);
            else if (percent >= 70)
                mBatteryStatusIv.setImageResource(R.mipmap.battery_80);
            else if (percent >= 50)
                mBatteryStatusIv.setImageResource(R.mipmap.battery_50);
            else if (percent >= 20)
                mBatteryStatusIv.setImageResource(R.mipmap.battery_20);
            else if (percent >= 0)
                mBatteryStatusIv.setImageResource(R.mipmap.battery_10);
        }
    }

    class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mTimeSystemTv.setText(TextUtil.getHourAndMinutes());
            }
        }
    }

    ;

}
