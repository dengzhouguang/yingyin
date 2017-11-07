package com.dzg.yingyin.bean;

/**
 * Created by dengzhouguang on 2017/11/3.
 */

public class VideoBean {
    public VideoBean() {
    }

    public VideoBean(String url, String title, String time, String size, String filePath) {
        this.url = url;
        this.title = title;
        this.time = time;
        this.size = size;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    private String url;
    private String title;
    private String time;
    private String size;
    private String filePath;

    @Override
    public String toString() {
        return "VideoBean{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", size='" + size + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
