package com.prp.detectionsystemclient.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.prp.detectionsystemclient.R;
import com.prp.detectionsystemclient.fragment.FourFragment;
import com.prp.detectionsystemclient.fragment.ThreeFragment;
import com.prp.detectionsystemclient.fragment.WifiListFragment;
import com.prp.detectionsystemclient.fragment.WifiMapFragment;
import com.prp.detectionsystemclient.util.Util;
import com.prp.detectionsystemclient.view.TabButton;
import com.prp.detectionsystemclient.view.ViewPagerEx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements OnClickListener, AppCompatCallback {

    /**
     * 四个导航按钮
     */
    TabButton buttonOne;
    TabButton buttonTwo;
    TabButton buttonThree;
    TabButton buttonFour;

    /**
     * 作为页面容器的ViewPager
     */
    ViewPagerEx mViewPager;
    /**
     * 页面集合
     */
    List<Fragment> fragmentList;

    /**
     * 四个Fragment（页面）
     */
    WifiListFragment wifiListFragment;
    WifiMapFragment wifiMapFragment;
    ThreeFragment threeFragment;
    FourFragment fourFragment;

    //覆盖层
    ImageView imageviewOvertab;

    //屏幕宽度
    int screenWidth;
    //当前选中的项
    int currenttab=-1;

    private AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        //fixFocusedViewLeak(getApplication());
        /*delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.home_activity);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        delegate.setSupportActionBar(toolbar);*/

        buttonOne=(TabButton)findViewById(R.id.btn_one);
        buttonTwo=(TabButton)findViewById(R.id.btn_two);
        buttonThree=(TabButton)findViewById(R.id.btn_three);
        buttonFour=(TabButton)findViewById(R.id.btn_four);

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);
        buttonFour.setOnClickListener(this);

        resetState(R.id.btn_one);

        mViewPager=(ViewPagerEx) findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        resetState(R.id.btn_one);
                        break;
                    case 1:
                        resetState(R.id.btn_two);
                        break;
                    case 2:
                        resetState(R.id.btn_three);
                        break;
                    case 3:
                        resetState(R.id.btn_four);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fragmentList=new ArrayList<Fragment>();

        fragmentList.add(wifiListFragment = new WifiListFragment());
        fragmentList.add(wifiMapFragment = new WifiMapFragment());
        fragmentList.add(threeFragment = new ThreeFragment());
        fragmentList.add(fourFragment = new FourFragment());

        screenWidth=getResources().getDisplayMetrics().widthPixels;

        buttonTwo.measure(0, 0);
        /*imageviewOvertab=(ImageView) findViewById(R.id.imgv_overtab);
        RelativeLayout.LayoutParams imageParams=new RelativeLayout.LayoutParams(screenWidth/4, buttonTwo.getMeasuredHeight());
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageviewOvertab.setLayoutParams(imageParams);*/

        mViewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
    }

    private void resetState(int id) {
        // 将四个按钮背景设置为未选中
        buttonOne.setSelected(false);
        buttonTwo.setSelected(false);
        buttonThree.setSelected(false);
        buttonFour.setSelected(false);

        // 将点击的按钮背景设置为已选中
        switch (id) {
            case R.id.btn_one:
                buttonOne.setSelected(true);
                break;
            case R.id.btn_two:
                buttonTwo.setSelected(true);
                break;
            case R.id.btn_three:
                buttonThree.setSelected(true);
                break;
            case R.id.btn_four:
                buttonFour.setSelected(true);
                break;
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter
    {

        public MyFrageStatePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container)
        {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            int currentItem=mViewPager.getCurrentItem();
            if (currentItem==currenttab)
            {
                return ;
            }
            //imageMove(mViewPager.getCurrentItem());
            currenttab=mViewPager.getCurrentItem();
        }

    }

    /**
     * 移动覆盖层
     * @param moveToTab 目标Tab，也就是要移动到的导航选项按钮的位置
     * 第一个导航按钮对应0，第二个对应1，以此类推
     */
    private void imageMove(int moveToTab)
    {
        int startPosition=0;
        int movetoPosition=0;

        startPosition=currenttab*(screenWidth/4);
        movetoPosition=moveToTab*(screenWidth/4);
        //平移动画
        TranslateAnimation translateAnimation=new TranslateAnimation(startPosition,movetoPosition, 0, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200);
        imageviewOvertab.startAnimation(translateAnimation);
    }


    @Override
    public void onClick(View v)
    {
        resetState(v.getId());
        switch (v.getId())
        {
            case R.id.btn_one:
                changeView(0);
                break;
            case R.id.btn_two:
                changeView(1);
                ((WifiMapFragment)fragmentList.get(1)).initMyLocation(Util.getLatitude(), Util.getLongtitude());
                break;
            case R.id.btn_three:
                changeView(2);
                break;
            case R.id.btn_four:
                changeView(3);
                break;
            default:
                break;
        }
    }
    //手动设置ViewPager要显示的视图
    private void changeView(int desTab)
    {
        mViewPager.setCurrentItem(desTab, true);
    }

    private static boolean shown = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为了让 Toolbar 的 Menu 有作用，此处代码不可省略
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        Log.e(getClass().getName(), "onDestroy");
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0;i < arr.length;i ++) {
            String param = arr[i];
            try{
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            }catch(Throwable t){
                t.printStackTrace();
            }
        }
    }

}
