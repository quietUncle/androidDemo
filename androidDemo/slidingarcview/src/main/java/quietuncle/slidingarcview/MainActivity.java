package quietuncle.slidingarcview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.initScreen(this);
        setContentView(R.layout.activity_main);
        SlidingArcView qtView= (SlidingArcView) findViewById(R.id.view);
        qtView.setQtScrollListener(new SlidingArcView.QTScrollListener() {
            @Override
            public void onSelect(View v, int index) {
                Toast.makeText(getApplicationContext(),"选中条目"+index,Toast.LENGTH_SHORT).show();
            }
        });
        qtView.setQtItemClickListener(new SlidingArcView.QTItemClickListener() {
            @Override
            public void onClick(View v, int index) {
                Toast.makeText(getApplicationContext(),"点击选中条目"+index,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
