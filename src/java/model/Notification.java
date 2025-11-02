package model;

import java.util.Date;

public class Notification extends BaseModel {
    private int uid;
    private String title;
    private String content;
    private String url;
    private boolean read;
    private Date createdTime;

    public int getUid(){ return uid; }
    public void setUid(int uid){ this.uid = uid; }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    public String getContent(){ return content; }
    public void setContent(String content){ this.content = content; }
    public String getUrl(){ return url; }
    public void setUrl(String url){ this.url = url; }
    public boolean isRead(){ return read; }
    public void setRead(boolean read){ this.read = read; }
    public Date getCreatedTime(){ return createdTime; }
    public void setCreatedTime(Date createdTime){ this.createdTime = createdTime; }
}
