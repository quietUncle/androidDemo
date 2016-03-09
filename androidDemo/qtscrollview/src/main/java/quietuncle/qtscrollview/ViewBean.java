package quietuncle.qtscrollview;

import java.util.List;

/**
 * 作者： quietUncle on 2016/3/2
 */
public class ViewBean {
    private List<DayBean> content;
    private String title;
    private String time;
    private String auth;
    private String place;
    private String bg;
    private String headSrc;

    public ViewBean(List<DayBean> content, String time, String title, String auth, String place, String bg, String headSrc) {
        this.content = content;
        this.time = time;
        this.title = title;
        this.auth = auth;
        this.place = place;
        this.bg = bg;
        this.headSrc = headSrc;
    }

    public List<DayBean> getContent() {
        return content;
    }
}
