package model;

import java.sql.Timestamp;

public class Notification extends BaseModel {
    private Integer nid;
    private Integer uid;           // người nhận
    private String  content;       // nội dung ngắn
    private String  url;           // link điều hướng (có thể null)
    private Timestamp created_time;
    private Timestamp read_time;   // null = chưa đọc

    public Integer getNid() { return nid; }
    public void setNid(Integer nid) { this.nid = nid; }

    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Timestamp getCreated_time() { return created_time; }
    public void setCreated_time(Timestamp created_time) { this.created_time = created_time; }

    public Timestamp getRead_time() { return read_time; }
    public void setRead_time(Timestamp read_time) { this.read_time = read_time; }
}
