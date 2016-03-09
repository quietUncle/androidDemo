package quietuncle.qtscrollview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： quietUncle on 2016/3/2
 * 博客地址:
 */
public class QTScrollView extends HorizontalScrollView implements View.OnTouchListener {
    private String TAG = "QTScrollView";
    private int layoutIds[] = {R.layout.item_1, R.layout.item_2, R.layout.item_3};//不同布局
    private ViewBean data;
    OverScroller mscroller;

    public QTScrollView(Context context) {
        super(context);
        init();
    }

    public QTScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QTScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mscroller = (OverScroller) get(this, "mScroller");
    }

    public void setDatas(ViewBean data) {
        this.data = data;
        initView();
        this.setOnTouchListener(this);
    }

    private void initView() {
        LinearLayout layout = new LinearLayout(getContext());
        for (int i = 0, len = data.getContent().size(); i < len; i++) {
            initPageView(layout, i);
        }
        this.addView(layout);
    }

    String lastDayTime = "";
    int nowDay = 1;

    //初始化每个页面的数据
    private void initPageView(LinearLayout root, int i) {
        DayBean bean = data.getContent().get(i);
        int layoutIndex = lastDayTime != bean.getTime() ? 0 : (bean.getSrc().length == 1 ? 1 : 2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
        View v = View.inflate(getContext(), layoutIds[layoutIndex], null);
        v.setLayoutParams(params);
        if (layoutIndex == 0) {
            initItem1(v, bean);
            lastDayTime = bean.getTime();
            nowDay++;
            root.addView(v);
            pageViews.add(new PageView(v,pageSum,bean,true));
            pageSum++;
            //如果是日期页面，则继续加载
            params = new LinearLayout.LayoutParams(ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
            layoutIndex=(bean.getSrc().length == 1 ? 1 : 2);
            v = View.inflate(getContext(), layoutIds[layoutIndex], null);
            v.setLayoutParams(params);
        }
        if (layoutIndex == 1) {
            initItem2(v, bean);
        } else {
            initItem3(v, bean);
        }

        root.addView(v);
        pageViews.add(new PageView(v,pageSum,bean,false));
        pageSum++;
    }

    private void initItem3(View v, DayBean bean) {
        ImageView iv2 = (ImageView) v.findViewById(R.id.iv2);
        ImageView iv1 = (ImageView) v.findViewById(R.id.iv1);
        Glide.with(getContext()).load(bean.getSrc()[0]).placeholder(R.mipmap.bg).error(R.mipmap.bg).into(iv1);
        Glide.with(getContext()).load(bean.getSrc()[1]).placeholder(R.mipmap.bg).error(R.mipmap.bg).into(iv2);
        Log.e(TAG,"url="+bean.getSrc()[0]);
    }

    private void initItem2(View v, DayBean bean) {
        TextView t1 = (TextView) v.findViewById(R.id.t1);
        ImageView iv1 = (ImageView) v.findViewById(R.id.iv1);
        Glide.with(getContext()).load(bean.getSrc()[0]).placeholder(R.mipmap.bg).error(R.mipmap.bg).into(iv1);
        t1.setText(bean.getContent());
        Log.e(TAG,"url="+bean.getSrc()[0]);
    }


    private void initItem1(View view, DayBean bean) {
        TextView t1 = (TextView) view.findViewById(R.id.t1);
        TextView t2 = (TextView) view.findViewById(R.id.t2);
        TextView t3 = (TextView) view.findViewById(R.id.t3);
        t1.setText("Day" + nowDay);
        t2.setText(bean.getTime().substring(0, 7));
        t3.setText(bean.getTime().substring(8, 10));
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(ViewScrollTag), SpaceTime);
                break;
            case MotionEvent.ACTION_DOWN:
                lastX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }


    private List<PageView> pageViews = new ArrayList<>();//保存所有页面，用来判断是否动画
    int nowPage;
    int pageSum = 0;//总页数
    int MINSPEED=20;//最小滑动速度
    int speed = 20;//自动滑动速度
    int distance = 0;//自动滑动距离
    private static int ViewScrollTag = -10086;
    private int autoScroll = 1;
    private int lastX = 0;//最后一次滑动的x，用了判断滑动是否停止
    private int SpaceTime=10;//间隔时间//不能太短，否则判断停止会有失误
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -10086:
                    if (lastX == getScrollX()) {
                        resetView();
                    } else {
                        if (Math.abs(getScrollX() - lastX) <=MINSPEED) {
                            stopScroll();
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(ViewScrollTag), SpaceTime);
                        }
                        lastX = getScrollX();

                    }
                    break;
                case 1://自动滚动
                    int add = distance > 0 ? 1 : -1;
                    if (Math.abs(distance) > speed) {
                        scrollTo(speed * add + getScrollX(), 0);
                        sendEmptyMessageDelayed(autoScroll, SpaceTime);
                        distance -= speed * add;
                    } else {
                        scrollTo(distance * add + getScrollX(), 0);
                        distance = 0;
                        nowPage=getScrollX()/ScreenUtils.getScreenW();
                        Log.e(TAG,nowPage+"");
                        if(onPageSelectItem!=null){
                            onPageSelectItem.onSelect(pageViews.get(nowPage));
                        }
                    }
                    break;
            }
        }
    };

    //强制停止滚动
    private void stopScroll() {
        mscroller.abortAnimation();
        resetView();
    }

    //滚动停止 ，重置view
    private void resetView() {
        int dis= getScrollX()%ScreenUtils.getScreenW();
        if(Math.abs(dis)>=ScreenUtils.getScreenW()/2){
            distance=ScreenUtils.getScreenW()-dis;
        } else{
            distance=dis*-1;
        }
        handler.sendEmptyMessageDelayed(autoScroll, SpaceTime);
    }


    @Override
    public void fling(int velocityX) {
        super.fling(velocityX / 2);
    }
    onPageSelectItemListener onPageSelectItem;
    public interface  onPageSelectItemListener{
        void   onSelect(PageView view);
    }

    public void setOnPageSelectItem(QTScrollView.onPageSelectItemListener onPageSelectItem) {
        this.onPageSelectItem = onPageSelectItem;
    }

    class PageView{
        private View v;
        private  int index;//页面下标
        private boolean isTitle;//是否标题,比如第几天
        private  DayBean bean;

        public PageView(View v,int index,DayBean bean,boolean isTitle){
            this.v=v;
            this.index=index;
            this.isTitle=isTitle;
            this.bean=bean;
        }

        public int getIndex() {
            return index;
        }

        public boolean isTitle() {
            return isTitle;
        }

        public DayBean getBean() {
            return bean;
        }
    }





    public Object get(Object instance, String variableName) {
        Class targetClass = instance.getClass().getSuperclass();
        // YourSuperClass 替换为实际的父类名字
        HorizontalScrollView superInst = (HorizontalScrollView) targetClass.cast(instance);
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);
            //修改访问限制
            field.setAccessible(true);
            // superInst 为 null 可以获取静态成员
            // 非 null 访问实例成员
            return field.get(superInst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
