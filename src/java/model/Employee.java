package model;

import java.sql.Date;

public class Employee extends BaseModel {
    private String name;

    // NEW fields
    private String email;
    private String phone;
    private String empCode;   // mã nhân viên
    private String title;     // chức vụ / vị trí
    private Date hireDate;    // ngày vào làm (java.sql.Date)

    private Department dept;
    private Employee supervisor;

    // ---- getter/setter cũ ----
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Department getDept() { return dept; }
    public void setDept(Department dept) { this.dept = dept; }

    public Employee getSupervisor() { return supervisor; }
    public void setSupervisor(Employee supervisor) { this.supervisor = supervisor; }

    // ---- getter/setter mới ----
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
}
