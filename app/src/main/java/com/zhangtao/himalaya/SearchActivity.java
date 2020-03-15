package com.zhangtao.himalaya;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.zhangtao.himalaya.base.BaseActivity;
import com.zhangtao.himalaya.interfaces.ISearchCallBack;
import com.zhangtao.himalaya.presents.SearchPresenter;

import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallBack {

    private ImageView mBackBtn;
    private EditText mInputBox;
    private FrameLayout mResultContainer;
    private TextView mSearchBtn;
    private SearchPresenter mSearchPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registerViewCallback(this);
    }

    private void initView() {
        mBackBtn = findViewById(R.id.search_back);
        mInputBox = findViewById(R.id.search_input);
        mSearchBtn = findViewById(R.id.search_btn);
        mResultContainer = findViewById(R.id.search_container);
    }

    private void initEvent() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2020/3/16 开始搜索
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWorldList) {

    }
}
