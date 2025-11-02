package model;

import java.sql.Date;

public class LeaveSpan {
    private int eid;
    private String ename;
    private Date from;
    private Date to;

    public LeaveSpan() { }
    public LeaveSpan(int eid, String ename, Date from, Date to) {
        this.eid = eid; this.ename = ename; this.from = from; this.to = to;
    }
    public int getEid() { return eid; }
    public void setEid(int eid) { this.eid = eid; }
    public String getEname() { return ename; }
    public void setEname(String ename) { this.ename = ename; }
    public Date getFrom() { return from; }
    public void setFrom(Date from) { this.from = from; }
    public Date getTo() { return to; }
    public void setTo(Date to) { this.to = to; }
}
