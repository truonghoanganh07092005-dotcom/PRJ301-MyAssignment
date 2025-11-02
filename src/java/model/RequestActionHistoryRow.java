package model;

import java.sql.Timestamp;

/** Dòng lịch sử hiển thị cho 1 đơn */
public class RequestActionHistoryRow extends BaseModel {   // << thêm extends BaseModel

    private int hid;
    private int rid;
    private String action;       // CREATE / EDIT / APPROVE / REJECT / UNAPPROVE / CANCEL / UN_CANCEL / DELETE
    private Integer actorUid;    // có thể null
    private Integer actorEid;    // có thể null
    private Integer prevStatus;  // có thể null
    private Integer newStatus;   // có thể null
    private String note;         // có thể null
    private Timestamp createdTime;

    // view-only
    private String actorName;

    public int getHid(){ return hid; }
    public void setHid(int hid){ this.hid = hid; }
    public int getRid(){ return rid; }
    public void setRid(int rid){ this.rid = rid; }
    public String getAction(){ return action; }
    public void setAction(String action){ this.action = action; }
    public Integer getActorUid(){ return actorUid; }
    public void setActorUid(Integer actorUid){ this.actorUid = actorUid; }
    public Integer getActorEid(){ return actorEid; }
    public void setActorEid(Integer actorEid){ this.actorEid = actorEid; }
    public Integer getPrevStatus(){ return prevStatus; }
    public void setPrevStatus(Integer prevStatus){ this.prevStatus = prevStatus; }
    public Integer getNewStatus(){ return newStatus; }
    public void setNewStatus(Integer newStatus){ this.newStatus = newStatus; }
    public String getNote(){ return note; }
    public void setNote(String note){ this.note = note; }
    public Timestamp getCreatedTime(){ return createdTime; }
    public void setCreatedTime(Timestamp createdTime){ this.createdTime = createdTime; }
    public String getActorName(){ return actorName; }
    public void setActorName(String actorName){ this.actorName = actorName; }
}
