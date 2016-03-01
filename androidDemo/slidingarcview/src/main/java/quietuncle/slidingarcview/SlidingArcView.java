package quietuncle.slidingarcview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： quietUncle on 2016/2/28
 * 项目地址:https://github.com/quietUncle/androidDemo
 */
public class SlidingArcView extends ViewGroup {
    public static String TAG = "QTView";
    private String titles[] = {"跑步", "乒乓球", "排球", "攀岩", "骑马", "跑步", "乒乓球", "排球", "攀岩", "骑马"};
    private int src[] = {R.mipmap.i1, R.mipmap.i2, R.mipmap.i3, R.mipmap.i4, R.mipmap.i5, R.mipmap.i1, R.mipmap.i2, R.mipmap.i3, R.mipmap.i4, R.mipmap.i5};
    private List<SignView> views = new ArrayList<>();

    Bitmap bgBitmap;

    public SlidingArcView(Context context) {
        this(context, null);
    }

    public SlidingArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    int mSize;

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(mSize, mSize);
    }

    private void init() {
        bgBitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.bg)).getBitmap();
        CentX = ScreenUtils.getScreenW() / 2;
        //CentY = ScreenUtils.getScreenH() / 2 - 100;

        RADIUS = ScreenUtils.getScreenW() / 2 + 100;
        for (int i = 0, len = src.length; i < len; i++) {
            View v = new View(getContext());
            v.setBackgroundResource(src[i]);
            SignView signView = new SignView(v, i);
            views.add(signView);
            this.addView(v);
        }
        // setBackgroundResource(R.mipmap.bg);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.setClickable(true);
    }

    Bitmap bitmap;
    Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setTextSize(50f);
        canvas.drawCircle(CentX, CentY - viewTopChange, RADIUS, paint);
        if (chooseView != null) {
            canvas.drawText(chooseView.title, CentX - 60, CentY + 150, paint);
        }
        for (SignView view : views) {
            view.flush();
        }
    }

    private void drawBg(Canvas canvas) {
        //得到原bitmap
        //把bitmap缩小为和View大小一致
        Bitmap newBitmp = Bitmap.createScaledBitmap(bgBitmap, mSize, mSize, false);
        if (newBitmp == null) {
            return;
        }
        //将缩小后的bitmap设置为画笔的shader
        BitmapShader mBitmapShader = new BitmapShader(newBitmp, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        //生成用来绘图的bitmap，并在其上用画笔绘图
        Bitmap dest = Bitmap.createBitmap(bgBitmap.getWidth(), bgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        if (dest == null) {
            return;
        }
        Canvas c = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(mBitmapShader);
        c.drawCircle(CentX, CentY - viewTopChange, RADIUS, paint);
        //将最后生成的bitmap绘制到View的canvas上
        canvas.drawBitmap(dest, 0, 0, paint);    //得到原bitmap

    }

    boolean canScroll = true;
    int lastX;
    int downPointId;
    int downX;
    int downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnimated) {//正在运行动画中
            return super.onTouchEvent(event);
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();//获得VelocityTracker类实例
        }
        mVelocityTracker.addMovement(event);//将事件加入到VelocityTracker类实例中
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (canScroll) {
                    flushViews((int) (event.getX() - lastX));
                    lastX = (int) event.getX();
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                //先判断是否是点击事件
                final int pi = event.findPointerIndex(downPointId);
                if (isClickable() && (Math.abs(event.getX(pi) - downX) <=3) ||Math.abs(event.getY(pi) - downY) <= 3) {
                    if (isFocusable() && isFocusableInTouchMode() && !isFocused())
                        requestFocus();
                    performViewClick();
                    return true;
                }
                //判断当ev事件是MotionEvent.ACTION_UP时：计算速率
                final VelocityTracker velocityTracker = mVelocityTracker;
                // 1000 provides pixels per second
                velocityTracker.computeCurrentVelocity(1, (float) 0.01);
                velocityTracker.computeCurrentVelocity(1000);//设置units的值为1000，意思为一秒时间内运动了多少个像素  
                if (velocityTracker.getXVelocity() > 2000 || velocityTracker.getXVelocity() < -2000) {//自动滚动最低要求
                    autoTime = (int) (velocityTracker.getXVelocity() / 1000 * 200);
                    autoTime = autoTime > 1500 ? 1500 : autoTime;
                    autoTime = autoTime < -1500 ? -1500 : autoTime;
                    isAnimated = true;
                    handler.sendEmptyMessageDelayed(1, 10);
                } else {
                    isAnimated = false;
                    resetView();
                }
                return true;
            case MotionEvent.ACTION_DOWN:
                downPointId = event.getPointerId(0);
                downX = lastX = (int) event.getX();
                downY = (int) event.getY();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void performViewClick() {
        for (SignView signView : views) {
            Rect r = new Rect(signView.centX - signView.size / 2, signView.centY - signView.size / 2 - viewTopChange, signView.centX + signView.size / 2, signView.centY + signView.size / 2 - viewTopChange);
            if (r.contains( downX, downY)) {
                if (qtItemClickListener != null && !isAnimated) {
                    isClick=true;
                    chooseView =signView;
                    autoScrollX = ScreenUtils.getScreenW() / 2 - signView.centX;
                    handler.sendEmptyMessageDelayed(0, 10);


                }
            }
        }
    }

    private void flushViews(int scrollX) {
        for (SignView view : views) {
            view.scroll(scrollX);
        }
    }




    //停止滚动，归位
    public void resetView() {
        for (SignView view : views) {
            if (view.centX > CentX && (view.centX - CentX < view.width)) {//屏幕右半部分移动运动，变小
                int dis = view.centX - CentX;
                if (dis > view.width / 2) {
                    autoScrollX = view.width - dis;
                } else {
                    autoScrollX = dis * -1;
                }
                break;
            }

        }
        handler.sendEmptyMessageDelayed(0, 10);
    }

    int veSpeed = 0;//松开自动滚动速度
    int autoTime = 0;//送开自动滚动
    int autoScrollX = 0;//归位滚动

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (autoScrollX != 0) {
                        if (Math.abs(autoScrollX) > SPEED) {
                            SPEED = Math.abs(SPEED);
                            if (autoScrollX > 0) {
                                autoScrollX -= SPEED;
                            } else {
                                autoScrollX += SPEED;
                                SPEED = SPEED * -1;
                            }
                            for (SignView view : views) {
                                view.scroll(SPEED);
                            }
                        } else {
                            for (SignView view : views) {
                                view.scroll(autoScrollX);
                            }
                            autoScrollX = 0;
                            isAnimated = false;
                            if (chooseView != null && qtScrollListener != null&&lastChooseView!=chooseView) {
                                if(!isClick){
                                    qtScrollListener.onSelect(chooseView.view, chooseView.index);
                                    lastChooseView=chooseView;
                                }else{
                                    qtItemClickListener.onClick(chooseView.view, chooseView.index);
                                    lastChooseView=chooseView;
                                    isClick=false;
                                }
                            }
                        }
                        invalidate();
                        handler.sendEmptyMessageDelayed(0, 10);
                    }
                    break;
                case 1:
                    if (autoTime > 0) {
                        if (autoTime > 1500) {
                            veSpeed = 80;
                        } else if (autoTime > 1000) {
                            veSpeed = 80;
                        } else if (autoTime > 500) {
                            veSpeed = 40;
                        } else if (autoTime > 200) {
                            veSpeed = 20;
                        } else {
                            veSpeed = 10;
                        }
                        for (SignView view : views) {
                            view.scroll(veSpeed);
                        }
                        autoTime -= 20;
                        if (autoTime < 0) {
                            isAnimated = false;
                            autoTime = 0;
                        }
                        invalidate();
                        handler.sendEmptyMessageDelayed(1, 20);
                    } else if (autoTime < 0) {
                        if (autoTime < -1500) {
                            veSpeed = -80;
                        } else if (autoTime < -1000) {
                            veSpeed = -60;
                        } else if (autoTime < -500) {
                            veSpeed = -40;
                        } else if (autoTime < -200) {
                            veSpeed = -20;
                        } else {
                            veSpeed = -10;
                        }
                        for (SignView view : views) {
                            view.scroll(veSpeed);
                        }
                        autoTime += 20;
                        if (autoTime > 0) {
                            isAnimated = false;
                            autoTime = 0;
                        }
                        invalidate();
                        handler.sendEmptyMessageDelayed(1, 20);
                    } else {
                        resetView();
                        invalidate();
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * listener
     */
    QTScrollListener qtScrollListener;
    QTItemClickListener qtItemClickListener;

    public interface QTScrollListener {
        void onSelect(View v, int index);
    }

    public interface QTItemClickListener {
        void onClick(View v, int index);
    }

    public void setQtScrollListener(QTScrollListener qtScrollListener) {
        this.qtScrollListener = qtScrollListener;
    }

    public void setQtItemClickListener(QTItemClickListener qtItemClickListener) {
        this.qtItemClickListener = qtItemClickListener;
    }
    private boolean isAnimated = false;//是否正在动画中
    private int viewTopChange = ScreenUtils.dp2px(80f);//view往上偏移的位置
    private VelocityTracker mVelocityTracker;//速度跟踪
    private int SPEED = 30;//归位自动滚动速度
    private SignView leftView;//屏幕最左边的viwe
    private SignView rightView;//屏幕最右边的view
    private int CentX;//外层圆中心x
    private int CentY;//外层圆中心Y
    private int RADIUS;//外层圆半径
    private static final int VX = 50;//第一个view的x
    private SignView chooseView;
    private  SignView lastChooseView;
    private  boolean isClick=false;
    private class SignView {
        private int indexInScreen;//在屏幕中的位置
        private View view;
        private String title;
        private int centX;//view的中心点坐标
        private int centY;
        private int index;
        private int size = 120;//view大小
        private int width = (ScreenUtils.getScreenW()) / 5;
        private float normalScale = 1.0f;
        private float maxScale = 0.2f;
        private boolean stop;//停止滚动，用来判断是否自动进行归位
        private boolean isChoose = false;

        public SignView(View v, final int index) {
            this.index = index;
            this.view = v;
            this.title = titles[index];
            if (index == 0) {
                leftView = this;
            }
            if (index == src.length - 1) {
                rightView = this;
            }
            if (index == 2) {
                isChoose = true;
                chooseView = this;
            }
            initView();
        }

        //计算view的坐标
        private void initView() {
            centX = (width) / 2 + width * index;
            centY = CentY + (int) Math.sqrt(Math.pow(RADIUS, 2) - Math.pow((centX - CentX), 2));
        }

        public void scroll(int scrollX) {
            this.centX += scrollX;
            centY = CentY + (int) Math.sqrt(Math.pow(RADIUS, 2) - Math.pow((centX - CentX), 2));
        }

        public int getCentX() {
            return centX;
        }

        public int getCentY() {
            return centY;
        }

        public View getView() {
            return view;
        }

        public void flush() {
            clean();
            //每次计算view的位置
            view.layout(centX - size / 2, centY - size / 2 - viewTopChange, centX + size / 2, centY + size / 2 - viewTopChange);
            //以是否靠近中心点 来判断是否变大变小
            if (centX >= CentX && centX - CentX <= width) {//屏幕右半部分移动运动，变小
                float scale = (float) (centX - width - width / 2) / (float) width - 1.0f;
                view.setScaleX((normalScale + maxScale - maxScale * scale));
                view.setScaleY((normalScale + maxScale - maxScale * scale));
                if (scale >= 0.5) {
                    isChoose = false;
                }
            } else if (centX <= CentX && CentX - centX <= width) {//屏幕左半部分移动运动，变大
                float scale = (float) (centX - width - width / 2) / (float) width;
                view.setScaleX((normalScale + maxScale * scale));
                view.setScaleY((normalScale + maxScale * scale));
                if (scale >= 0.5) {
                    isChoose = true;
                }
            } else {
                isChoose = false;
                view.setScaleX(normalScale);
                view.setScaleY(normalScale);
            }
            if (isChoose) {
                chooseView = this;
            }
        }


        //无限循环的判断
        private void clean() {
            if (leftView.notLeftView()) {//最左边没有view了，把最右边的移到最左边
                rightView.centX = leftView.centX - width;
                rightView.changeY();
                leftView = rightView;
                rightView = views.get(rightView.index == 0 ? views.size() - 1 : rightView.index - 1);
            }
            if (rightView.notRightView()) {//最右边没有view了，把最左边的移到最右边
                leftView.centX = rightView.centX + width;
                leftView.changeY();
                rightView = leftView;
                leftView = views.get(leftView.index == views.size() - 1 ? 0 : leftView.index + 1);
            }
        }

        //重新计算Y点坐标
        public void changeY() {
            centY = CentY + (int) Math.sqrt(Math.pow(RADIUS, 2) - Math.pow((centX - CentX), 2));
        }

        public boolean notLeftView() {
            return centX - width / 2 > width / 2;
        }

        public boolean notRightView() {
            return centX + width / 2 + width / 2 < ScreenUtils.getScreenW();
        }
    }
}
