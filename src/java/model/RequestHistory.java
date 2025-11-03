package model;

import java.sql.Timestamp;

public class RequestHistory extends BaseModel {
    private int hid;
    private int rid;
    private Integer actorUid;      // <- có thể null
    private String action;
    private String note;
    private Timestamp created_time;

    public int getHid() { return hid; }
    public void setHid(int hid) { this.hid = hid; }

    public int getRid() { return rid; }
    public void setRid(int rid) { this.rid = rid; }

    public Integer getActorUid() { return actorUid; }
    public void setActorUid(Integer actorUid) { this.actorUid = actorUid; } // <- MỚI

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Timestamp getCreated_time() { return created_time; }
    public void setCreated_time(Timestamp created_time) { this.created_time = created_time; }
}
