# Himalayademo
喜马拉FM电台项目实战
## 2.18

### 项目初始化
1. 创建项目
2. 使用git管理自己的代码
3. 创建相应的文件夹
    - 适配器文件夹---adapters
    - 工具类文件夹---utils
    - fragment文件夹---fragments
    - 逻辑相关文件夹---presenters
    - 接口文件夹---interfaces
    - 自定义控件文件夹
### 喜马拉雅SDK
1. 下载SDK
2. 根据官方给的demo配置自己的项目
3. 加载第三方库
4. 测试是否继集成SDK成功
---
## 2.19

### 编写自己的日志打印工具类
### 下载第三方开源框架MagicIndicator
- 下载第三方开源框架后先运行其中DEMO
    * 修改grade文件和properties，保持于自己程序同步，减少加载时间
    * 安装软件查看demo样式
## 2.24
### 将MagicIndicator集成到自己的项目中(p6)
- 添加依赖
    ```java
    implementation 'com.github.hackware1993:MagicIndicator:1.5.0'
    ```
- 添加控件
    ```xml
      <net.lucode.hackware.magicindicator.MagicIndicator
        android:id="@+id/main_indicator"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        tools:ignore="MissingConstraints" />
    ```
- 寻找控件并设置相关属性和适配器
    ```java
     mMagicIndicator = findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建适配器
        mMagicIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(mMagicIndicatorAdapter);

    ```
- Indicator适配器
    * 标题数量
    * 标题样式
    * 标题下划线样式
### 绑定MagicIndicator与ViewPager(p7)
- ViewPager
    * 它的适配器为PagerAdapter,且FragmentAdapter是它的子类，可以使Fragment作为容器的子元素
    * FragmentAdapter
        - 返回Item数量
        - 根据当前位置返回对应子元素(Fragment)
        - 在上面为了返回相应的Fragment创建FragmentCreator类
- FragmentCreator类
    * 建立了Map<index, Fragment>用于存储Fragment，对此进行缓存，提高软件效率
    * 当前缓存Map中如果没有相应的Fragment则创建并且放到缓存map中
- 创建Fragment
    * 由于三个Fragment的属性相似，因此使三个Fragment继承父类BaseFragment
    * BaseFragment
        - 继承与Fragment
        - onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bunle savedInstanceState)
            - 调用抽象函数，获得View
        - 拥有一抽象函数，返回View
        - 三个子类
            * 重写抽象函数，创建各自的View


### tab与ViewPager的联动(p8)(有待进一步理解)
- 为了实现联动，那么当点击tab会调用click事件更换ViewPager里的内容
    * 了解接口的使用
## 2.19
### indicator和搜索按钮的布局(p9)
- 使tab的item平均分布 - commonNavigator.setAdjustMode(true);
- 编辑布局文件，加入搜索按钮
### 获取推荐内容数据(p10)
- 根据SDK获取猜你喜欢的数据，验证适合成功
    ```java
    /**
     * 获取推荐内容，--猜你喜欢
     */
    private void getRecommendData() {
        //封装数据
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albums = gussLikeAlbumList.getAlbumList();
                    if (albums != null) {
                        LogUtil.d(TAG, "SIZE ---> " + albums.size());
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
                //数据获取失败
                LogUtil.d(TAG,"error --> " + i);
                LogUtil.d(TAG,"errorMsg --> " + s);
            }
        });
    }
    ```
### 获取推荐内容数据UI展示设计(p11)
- 使用RecyclerView
- 使用Picasso(图片加载)

### 推荐内容数据UI展示设计优化（p12)
- 修改ImageView的填充方式
    * android:scaleType="fitXY"
- 修改RecyclerView滑动到末端的阴影显示
    * android:overScrollMode="never"
- 修改RecyclerView作为的间隙
    ```java
    //RecyclerView控件
     mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom =UIUtil.dip2px(view.getContext(), 5);
                outRect.left =UIUtil.dip2px(view.getContext(), 5);
                outRect.right =UIUtil.dip2px(view.getContext(), 5);
            }
        });
    ```
- 修改RecyclerView每个Item的背景-圆角矩形
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <shape xmlns:android="http://schemas.android.com/apk/res/android">
        <size android:width="350dp"
            android:height="75dp"/>
        <solid  android:color="#ffffff"/>
        <corners android:radius="4dp"/>
    </shape
    ```
- 修改图片的边框为圆角矩形-自定义View
    ```java
    public class RoundRectIamgeView extends AppCompatImageView{
        private float roundRatio = 0.2f;
        private Path path;
        public RoundRectIamgeView(Context context, AttributeSet attrs){
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(path == null){
                path = new Path();
                path.addRoundRect(new RectF(0,0,getWidth(), getHeight()), roundRatio*getWidth(), roundRatio*getHeight(), Path.Direction.CW);
            }
            canvas.save();
            canvas.clipPath(path);
            super.onDraw(canvas);
            canvas.restore();
        }
    }
    ```
### 推荐内容代码重构（p13)
- 在原始项目中，我们将网络请求推荐歌曲和更新UI全部写在了RecommendFargment.java中,导致程序结构复杂，使得数据与UI混合在一起，减少了请求数据的复用性，因此将数据独立出来
- 利用接口将二者之间建立联系
    * 数据请求接口-IRecommendPresenter
        - getRecommendList()\\\请求数据列表
        - registerViewCallback(IRecommendViewCallback callback)\\\注册UI的回调函数
        - unRegisterViewCallback(IRecommendViewCallback callback)
        \\\取消已注册的UI回调函数
    * 请求后，UI获取数据接口-IRecommendViewCallback
        - onRecommendListLoaded(List\<Album> result)\\\获取数据成功后UI的回调函数
    * RecommendFragment实现接口IRecommendViewCallback---RecommendPresenter实现接口IRecommendPressenter
        - RF通过IRP注册自己位IRP中的成员(IRVC)，并调用IRP的函数getRecommendList()获取数据，数据获取成功后，IRP使用成员(IRVC)调用onRecommendListLoaded()函数，通知RF已经获取数据成功。
- 获取RecommendPresenter对象-单例模式（设计模式）
    * 单例模式是指控制程序只能获取一个

