package model;

import java.sql.Date;

public class AgendaCell {
    private int eid;
    private String ename;
    private Date day;
    private boolean onLeave;

    public AgendaCell(int eid, String ename, Date day, boolean onLeave) {
        this.eid = eid;
        this.ename = ename;
        this.day = day;
        this.onLeave = onLeave;
    }
    public int getEid() { return eid; }
    public String getEname() { return ename; }
    public Date getDay() { return day; }
    public boolean isOnLeave() { return onLeave; }
}
