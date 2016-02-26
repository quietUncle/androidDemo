package quietuncle.logutli;

/**
 * 作者： quietUncle on 2016/2/26
 */
public class Test {
    public static String getInfo(){
        String info="";
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        info+="TEST  ------stacktrace len :"+stacktrace.length;
        for (int i = 0; i <stacktrace.length; i++) {
            info+="\n\t"+"----  the " + i + " element  ----";
            info+="\n\t"+"toString: " + stacktrace[i].toString();
            info+="\n\t"+"ClassName: " + stacktrace[i].getClassName();
            info+="\n\t"+"FileName: " + stacktrace[i].getFileName();
            info+="\n\t"+"LineNumber: " + stacktrace[i].getLineNumber();
            info+="\n\t"+"MethodName: " + stacktrace[i].getMethodName();
        }
        return  info;
    }
}
