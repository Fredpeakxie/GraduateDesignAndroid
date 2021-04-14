package com.fred.moonker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import com.fred.moonker.Activity.LoginActivity;
import com.fred.moonker.Fragment.MarkFragment;
import com.fred.moonker.Fragment.MsgFragment;
import com.fred.moonker.Fragment.MyFragment;
import com.fred.moonker.richeditor.ui.RichTextEditActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MsgFragment msgFragment;
    private MarkFragment markFragment;
    private MyFragment myFragment;
    private Fragment[] fragments;
    private int lastFragment;//用于记录上个选择的Fragment
    private ImageButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//去掉actionbar
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        initFragment();
    }

    private void initView() {
        Log.d(TAG, "setView: "+Thread.currentThread().getName());
        btnAdd = findViewById(R.id.tool_bar_add);
    }

    private void setListener() {
        btnAdd.setOnClickListener(v-> {
            Intent it = new Intent(this, RichTextEditActivity.class);
            startActivity(it);
        });
    }

    /**
     * 界面切换跳转回来时调用
     */
    @Override
    protected void onResume() {
        super.onResume();
        int id = getIntent().getIntExtra("id", 0);
        if(id==2){
            bottomNavigationView.setSelectedItemId(R.id.set);
        }
    }


//TODO login status
    /**
     * 用户登录状态检测
     */
    private void loginStateCheck(){
        MoonkerApplication application = (MoonkerApplication) this.getApplication();
        if(application.isLogin == false){
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
        }
    }


    //初始化fragment和fragment数组
    private void initFragment() {
        msgFragment = new MsgFragment();
        markFragment = new MarkFragment();
        myFragment = new MyFragment();
        fragments = new Fragment[]{msgFragment, markFragment, myFragment};
        lastFragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, msgFragment).show(msgFragment).commit();
        bottomNavigationView = findViewById(R.id.bv_bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }

    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.msg: {
                    //这部分的if else 并没有看懂什么原因
                    //防止重复切换造成的视觉混乱
                    if (lastFragment != 0) {
                        switchFragment(lastFragment, 0);
                        lastFragment = 0;
                    }
                    return true;
                }
                case R.id.mark: {
                    if (lastFragment != 1) {
                        switchFragment(lastFragment, 1);
                        lastFragment = 1;
                    }
                    return true;
                }
                case R.id.set: {
                    if (lastFragment != 2) {
                        switchFragment(lastFragment, 2);
                        lastFragment = 2;
                    }
                    return true;
                }
            }
            return false;
        }
    };

    //切换Fragment
    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastFragment]);
        //隐藏上个Fragment 如果添加过了
        if(fragments[index].isAdded()==false){
            transaction.add(R.id.fragment,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }
}