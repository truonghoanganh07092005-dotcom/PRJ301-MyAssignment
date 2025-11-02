package model;

import java.util.Date;
import model.Employee;

public class RequestActionHistory extends BaseModel {
    private int rid;
    private String action;
    private Integer actorUid;
    private Employee actor;   // để hiện tên
    private String note;
    private Integer prevStatus;
    private Integer newStatus;
    private Date createdTime;

    public int getRid(){ return rid; }
    public void setRid(int rid){ this.rid = rid; }
    public String getAction(){ return action; }
    public void setAction(String action){ this.action = action; }
    public Integer getActorUid(){ return actorUid; }
    public void setActorUid(Integer actorUid){ this.actorUid = actorUid; }
    public Employee getActor(){ return actor; }
    public void setActor(Employee actor){ this.actor = actor; }
    public String getNote(){ return note; }
    public void setNote(String note){ this.note = note; }
    public Integer getPrevStatus(){ return prevStatus; }
    public void setPrevStatus(Integer prevStatus){ this.prevStatus = prevStatus; }
    public Integer getNewStatus(){ return newStatus; }
    public void setNewStatus(Integer newStatus){ this.newStatus = newStatus; }
    public Date getCreatedTime(){ return createdTime; }
    public void setCreatedTime(Date createdTime){ this.createdTime = createdTime; }
}
