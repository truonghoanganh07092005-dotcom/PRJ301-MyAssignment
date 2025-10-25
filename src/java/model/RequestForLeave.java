package model;

import java.util.Date;

public class RequestForLeave extends BaseModel {
    private int rid;                    // <-- nếu BaseModel không dùng id
    private Employee created_by;
    private Date created_time;
    private java.sql.Date from;
    private java.sql.Date to;
    private String reason;
    private int status;                 // 0: InProgress, 1: Approved, 2: Rejected
    private Employee processed_by;
    private String title;               // optional: tiêu đề ngắn

    public int getRid() { return rid; }
    public void setRid(int rid) { this.rid = rid; }

    public Employee getCreated_by() { return created_by; }
    public void setCreated_by(Employee created_by) { this.created_by = created_by; }

    public Date getCreated_time() { return created_time; }
    public void setCreated_time(Date created_time) { this.created_time = created_time; }

    public java.sql.Date getFrom() { return from; }
    public void setFrom(java.sql.Date from) { this.from = from; }

    public java.sql.Date getTo() { return to; }
    public void setTo(java.sql.Date to) { this.to = to; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Employee getProcessed_by() { return processed_by; }
    public void setProcessed_by(Employee processed_by) { this.processed_by = processed_by; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
}
