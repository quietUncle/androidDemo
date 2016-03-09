package quietuncle.qtscrollview;

/**
 * 作者： quietUncle on 2016/3/2
 */
public class DayBean {

    private String src[];
    private String content;
    private String time;

    public DayBean(String src[], String content,String time) {
        this.src = src;
        this.time=time;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String[] getSrc() {
        return src;
    }

    public String getTime() {
        return time;
    }
}
