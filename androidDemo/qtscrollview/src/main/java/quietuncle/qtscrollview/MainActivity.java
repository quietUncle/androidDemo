package quietuncle.qtscrollview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String src[] = {"http://f.hiphotos.baidu.com/image/h%3D200/sign=9481c6e2b4fd5266b82b3b149b189799/8601a18b87d6277f8b65f8312a381f30e924fc40.jpg", "http://a.hiphotos.baidu.com/image/pic/item/4d086e061d950a7b224284cd08d162d9f2d3c940.jpg"};
    String src1[]={"http://a.hiphotos.baidu.com/image/pic/item/267f9e2f070828385f623e10ba99a9014c08f143.jpg"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.initScreen(this);
        setContentView(R.layout.activity_main);
        final ImageView bg= (ImageView) findViewById(R.id.bg);
        QTScrollView qtScrollView = (QTScrollView) findViewById(R.id.qtView);
        qtScrollView.setDatas(initData());

        qtScrollView.setOnPageSelectItem(new QTScrollView.onPageSelectItemListener() {
            @Override
            public void onSelect(QTScrollView.PageView view) {
                Glide.with(MainActivity.this).load(view.getBean().getSrc()[0]).into(bg);
            }
        });

    }

    private ViewBean initData() {
        ViewBean bean;
        List<DayBean> list = new ArrayList<>();
        for (int i = 0, len = 10; i < len; i++) {
            DayBean b = new DayBean(i%2==0?src:src1, "画好月月", i > 5 ? "2016.03.03" : "2016.03.02");
            list.add(b);
        }
        bean = new ViewBean(list, "2015", "测试", "quietUncle", "上海", "...", "...");
        return bean;
    }
}
