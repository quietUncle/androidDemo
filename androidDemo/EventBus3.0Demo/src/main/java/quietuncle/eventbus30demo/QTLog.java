package quietuncle.eventbus30demo;

import android.text.TextUtils;
import android.util.Log;

/**
 * 作者： quietUncle on 2016/2/26
 * 自定义 Log工具， tag自动产生，格式:
 * tagPrefix:className.methodName(Line:lineNumber),
 * tagPrefix为空时只输出：className.methodName(Line:lineNumber)。
 */
public class QTLog {
    public static String tagPrefix = "QT";//log前缀
    public static boolean debug = true;

    public static void d(Object o) {
        logger("d", o,getCallerStackTraceElement());
    }
    public static void e(Object o) {
        logger("e", o,getCallerStackTraceElement());
    }
    public static void i(Object o) {
        logger("i", o,getCallerStackTraceElement());
    }
    public static void w(Object o) {
        logger("w", o,getCallerStackTraceElement());
    }

    /**
     *
     * @param type logger级别
     * @param o   logger内容
     * @param caller 方法调用类信息,只能在最外层方法传入，不然会变成当前类
     */
    private static void logger(String type, Object o,StackTraceElement caller) {
        if (!debug) {
            return;
        }
        String msg=o+"";
        String tag = getTag(caller);
            switch (type){
                case  "i":
                    Log.i(tag,msg);
                case  "d":
                    Log.d(tag,msg);
                    break;
                case  "e":
                    Log.e(tag,msg);
                    break;
                case  "w":
                    Log.w(tag,msg);
                    break;
            }
    }


    private static String getTag(StackTraceElement element) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = element.getClassName(); // 获取到类名

        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf("") + 1);
        tag = String.format(tag, callerClazzName, element.getMethodName(),
                element.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":"
                + tag;
        return tag;
    }

    /**
     * 获取线程状态
     * @return
     */
    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }
}
