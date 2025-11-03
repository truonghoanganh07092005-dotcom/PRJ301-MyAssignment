package model;

import java.sql.Timestamp;

public class Notification extends BaseModel {
    private int nid;
    private int uidTarget;
    private String title;
    private String content;         // có thể là message hoặc URL
    private Timestamp created_time;
    private boolean is_read;
    private Integer rid;            // đơn liên quan (có thể null)

    public int getNid() { return nid; }
    public void setNid(int nid) { this.nid = nid; }

    public int getUidTarget() { return uidTarget; }
    public void setUidTarget(int uidTarget) { this.uidTarget = uidTarget; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreated_time() { return created_time; }
    public void setCreated_time(Timestamp created_time) { this.created_time = created_time; }

    public boolean isIs_read() { return is_read; }
    public void setIs_read(boolean is_read) { this.is_read = is_read; }

    public Integer getRid() { return rid; }
    public void setRid(Integer rid) { this.rid = rid; }
}
