package model.iam;

import java.sql.Date;
import java.lang.reflect.Method;
import java.util.ArrayList;
import model.BaseModel;
import model.Employee;
import model.Department;     // nếu chưa có lớp này thì xóa import + getter tương ứng

public class User extends BaseModel {
    private String username;
    private String password;
    private String displayname;   // giữ nguyên theo model gốc
    private Employee employee;
    private ArrayList<Role> roles = new ArrayList<>();

    // ===== getters/setters gốc =====
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayname() { return displayname; }
    public void setDisplayname(String displayname) { this.displayname = displayname; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public ArrayList<Role> getRoles() { return roles; }
    public void setRoles(ArrayList<Role> roles) { this.roles = roles; }

    // ======================================================================
    // Convenience getters (an toàn NULL, ưu tiên lấy ở User, fallback Employee)
    // ======================================================================

    /** Tên hiển thị – ưu tiên displayname, fallback full name (employee.name), cuối cùng username */
    public String getDisplayName() {
        if (notBlank(displayname)) return displayname.trim();
        String full = getFullName();
        return notBlank(full) ? full : username;
    }

    /** Tên đầy đủ – đọc từ Employee.getName() nếu có */
    public String getFullName() {
        return fromEmpStr("getName");
    }

    /** Alias cho một số nơi gọi getName() trên User */
    public String getName() {
        String n = fromEmpStr("getName");
        return notBlank(n) ? n : getDisplayName();
    }

    /** Email của người dùng (ưu tiên ở User nếu bạn có bổ sung field sau này, hiện fallback Employee) */
    public String getEmail() {
        // nếu sau này bạn thêm field email ở User, chỉ cần mở comment 2 dòng dưới
        // if (notBlank(this.email)) return this.email.trim();
        return fromEmpStr("getEmail");
    }

    /** Số điện thoại */
    public String getPhone() {
        return fromEmpStr("getPhone");
    }

    /** Mã nhân viên */
    public String getCode() {
        return fromEmpStr("getCode");
    }

    /** Chức vụ / chức danh (tùy Employee đặt tên getTitle hay getPosition) */
    public String getTitle() {
        String t = fromEmpStr("getTitle");
        if (notBlank(t)) return t;
        return fromEmpStr("getPosition");
    }

    /** Phòng ban – trả về Department nếu Employee có */
    public Department getDepartment() {
        return fromEmpObj("getDepartment", Department.class);
    }

    /** Ngày vào làm */
    public Date getHireDate() {
        return fromEmpObj("getHireDate", Date.class);
    }

    /** Quản lý trực tiếp */
    public Employee getSupervisor() {
        return fromEmpObj("getSupervisor", Employee.class);
    }

    // ====================== helpers ======================
    private boolean notBlank(String s){ return s != null && !s.trim().isEmpty(); }

    /** Lấy String từ Employee bằng reflection (an toàn nếu method không tồn tại) */
    private String fromEmpStr(String method) {
        if (employee == null) return null;
        try {
            Method m = employee.getClass().getMethod(method);
            Object v = m.invoke(employee);
            return v == null ? null : v.toString();
        } catch (Exception ignore) { return null; }
    }

    /** Lấy đối tượng kiểu T từ Employee bằng reflection */
    @SuppressWarnings("unchecked")
    private <T> T fromEmpObj(String method, Class<T> type) {
        if (employee == null) return null;
        try {
            Method m = employee.getClass().getMethod(method);
            Object v = m.invoke(employee);
            return type.isInstance(v) ? (T) v : null;
        } catch (Exception ignore) { return null; }
    }
}
