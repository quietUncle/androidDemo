package quietuncle.eventbus30demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView content;
    public  static  String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        initListen();
    }

    private void initListen() {
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
    }

    private void initView() {
        content = (TextView) findViewById(R.id.content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1://子线程发送事件,不同treadmode所在线程测试
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new ThreadModeEvent());
                    }
                }).start();
                break;
            case R.id.bt2://主线程发送事件,不同treadmode所在线程测试
                EventBus.getDefault().post(new ThreadModeEvent());
                break;
            case R.id.bt3://优先级测试
                EventBus.getDefault().post(new PriorityEvent());
                break;
            case R.id.bt4://sticky测试
                EventBus.getDefault().postSticky(new StickyEvent());
                startActivity(new Intent(this,SecondActivity.class));
                break;
        }
    }

    //Priority 测试方法
    @Subscribe(priority =0)
    public  void getPriorityEventWith0(PriorityEvent event){
        Log.e(TAG,"priority =0");
    }
    @Subscribe(priority =1)
    public  void getPriorityEventWith1(PriorityEvent event){
        Log.e(TAG,"priority =1");
    }

    @Subscribe(priority =2)
    public  void getPriorityEventWith2(PriorityEvent event){
        Log.e(TAG,"priority =2");
    }


    //threadMode 测试方法

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getThreadModeEventMain(ThreadModeEvent event) {
        LogUtils.e("main ---"+Thread.currentThread().getName().toString());
        QTLog.e("main ---"+Thread.currentThread().getName().toString());
        Log.e(TAG,"main ---"+Thread.currentThread().getName().toString());
    }
    @Subscribe()
    public void getThreadModeEventDefault(ThreadModeEvent event) {
        Log.e(TAG,"default ---"+Thread.currentThread().getName().toString());
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getThreadModeEventBack(ThreadModeEvent event) {
        Log.e(TAG,"Background ---"+Thread.currentThread().getName().toString());
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getThreadModeEventAsync(ThreadModeEvent event) {
        Log.e(TAG,"async ---"+Thread.currentThread().getName().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
