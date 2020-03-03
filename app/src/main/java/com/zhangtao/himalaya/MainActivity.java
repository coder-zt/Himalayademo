package com.zhangtao.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.zhangtao.himalaya.adapters.IndicatorAdapter;
import com.zhangtao.himalaya.adapters.MainContentAdapter;
import com.zhangtao.himalaya.base.BaseActivity;
import com.zhangtao.himalaya.base.BaseApplication;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ResourceBundle;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private IndicatorAdapter mMagicIndicatorAdapter;
    private ViewPager mContentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置状态栏颜色
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        initView();
        InitEvent();
    }

    private void InitEvent() {
        mMagicIndicatorAdapter.setOnIndicatorTabClicklistener(new IndicatorAdapter.OnIndictorTapbClickListene() {
            @Override
            public void onTabClick(int index) {
                if(mContentPager != null){
                    mContentPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建适配器
        mMagicIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mMagicIndicatorAdapter);


        //ViewPager
        mContentPager = findViewById(R.id.vp_content);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mMainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mMainContentAdapter);
        //把ViewPager和indicator绑定到一起
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);
    }
}
