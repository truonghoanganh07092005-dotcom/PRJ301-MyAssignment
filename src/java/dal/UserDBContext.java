package dal;

import java.sql.*;
import java.util.ArrayList;
import model.Department;
import model.Employee;
import model.iam.User;

public class UserDBContext extends DBContext<User> {

    public User get(String username, String password) {
        try {
            String sql = """
                SELECT TOP 1
                    u.uid, u.username, u.displayname,

                    en.eid,
                    e.ename,
                    e.email, e.phone, e.emp_code, e.title, e.hire_date,

                    -- hỗ trợ cả dept_id (mới) và did (cũ)
                    COALESCE(e.dept_id, e.did) AS dept_id,
                    d.dept_name,

                    -- hỗ trợ cả manager_id (mới) và supervisorid (cũ)
                    mgr.eid   AS manager_id,
                    mgr.ename AS manager_name
                FROM [User] u
                LEFT JOIN Enrollment en ON en.uid = u.uid AND en.active = 1
                LEFT JOIN Employee   e  ON e.eid = en.eid
                LEFT JOIN Department d  ON d.dept_id = COALESCE(e.dept_id, e.did)
                LEFT JOIN Employee   mgr ON mgr.eid = COALESCE(e.manager_id, e.supervisorid)
                WHERE u.username = ? AND u.[password] = ?
            """;

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, username == null ? "" : username.trim());
            stm.setString(2, password == null ? "" : password.trim());

            ResultSet rs = stm.executeQuery();
            if (!rs.next()) return null;

            User u = new User();
            u.setId(rs.getInt("uid"));
            u.setUsername(rs.getString("username"));
            u.setDisplayname(rs.getString("displayname"));

            if (rs.getObject("eid") != null) {
                Employee e = new Employee();
                e.setId(rs.getInt("eid"));
                e.setName(rs.getString("ename"));
                e.setEmail(rs.getString("email"));
                e.setPhone(rs.getString("phone"));
                e.setEmpCode(rs.getString("emp_code"));
                e.setTitle(rs.getString("title"));
                e.setHireDate(rs.getDate("hire_date"));

                if (rs.getObject("dept_id") != null) {
                    Department d = new Department();
                    d.setId(rs.getInt("dept_id"));
                    d.setName(rs.getString("dept_name"));
                    e.setDept(d);
                }

                if (rs.getObject("manager_id") != null) {
                    Employee m = new Employee();
                    m.setId(rs.getInt("manager_id"));
                    m.setName(rs.getString("manager_name"));
                    e.setSupervisor(m);
                }

                u.setEmployee(e);
            }

            return u;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            closeConnection();
        }
    }

    @Override public ArrayList<User> list() { throw new UnsupportedOperationException(); }
    @Override public User get(int id)       { throw new UnsupportedOperationException(); }
    @Override public void insert(User m)    { throw new UnsupportedOperationException(); }
    @Override public void update(User m)    { throw new UnsupportedOperationException(); }
    @Override public void delete(User m)    { throw new UnsupportedOperationException(); }
}
