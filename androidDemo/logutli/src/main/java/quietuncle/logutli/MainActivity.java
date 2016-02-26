package quietuncle.logutli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView content = (TextView) findViewById(R.id.content);
//        String info="";
//        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
//        info+="stacktrace len :"+stacktrace.length;
//        for (int i = 0; i < stacktrace.length; i++) {
//            info+="\n\t"+"----  the " + i + " element  ----";
//            info+="\n\t"+"toString: " + stacktrace[i].toString();
//            info+="\n\t"+"ClassName: " + stacktrace[i].getClassName();
//            info+="\n\t"+"FileName: " + stacktrace[i].getFileName();
//            info+="\n\t"+"LineNumber: " + stacktrace[i].getLineNumber();
//            info+="\n\t"+"MethodName: " + stacktrace[i].getMethodName();
//        }
        String info = Test.getInfo();
        content.setText(info);

    }

    public void sendLog(View v) {
        QTLog.i("info");
        QTLog.d("debug");
        QTLog.w("warm");
        QTLog.e("error");
    }
}
