package quietuncle.eventbus30demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 作者： quietUncle on 2016/2/25
 */
public class SecondActivity extends Activity {
    public  static  String TAG="SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t=new TextView(getApplicationContext());
        setContentView(t);
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true)
    public void getStickyEvent(StickyEvent event){
        Log.e(TAG,"getStickyEvent on SecondActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(StickyEvent.class);
        EventBus.getDefault().unregister(this);
    }
}
