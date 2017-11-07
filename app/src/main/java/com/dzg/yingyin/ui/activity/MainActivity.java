package com.dzg.yingyin.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dzg.yingyin.R;
import com.dzg.yingyin.ui.fragment.LocalFragment;
import com.dzg.yingyin.ui.fragment.SettingFragment;
import com.dzg.yingyin.ui.fragment.ShareFragment;
import com.dzg.yingyin.util.ExeCommand;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView mBNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        ExeCommand exeCommand=new ExeCommand();
        exeCommand.run("dumpsys battery",2000);
        android.util.Log.e("以后的以后",exeCommand.getResult()+"没结果？");
    }

    private void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new LocalFragment()).commit();
        mBNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_local:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,new LocalFragment()).commit();
                    return true;
                case R.id.navigation_share:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,new ShareFragment()).commit();
                    return true;
                case R.id.navigation_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,new SettingFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
