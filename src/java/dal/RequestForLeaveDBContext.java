package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Department;
import model.Employee;
import model.RequestForLeave;

public class RequestForLeaveDBContext extends DBContext<RequestForLeave> {

    /* ===================== Helpers ===================== */

    private Integer getIntOrNull(ResultSet rs, String col) throws SQLException {
        Object o = rs.getObject(col);
        return (o == null) ? null : ((Number) o).intValue();
    }

    private String nz(String s) { return s == null ? "" : s; }

    private RequestForLeave mapRow(ResultSet rs) throws SQLException {
        RequestForLeave r = new RequestForLeave();
        r.setRid(rs.getInt("rid"));
        r.setTitle(nz(rs.getString("title")));
        r.setReason(nz(rs.getString("reason")));
        r.setFrom(rs.getDate("from"));
        r.setTo(rs.getDate("to"));
        r.setCreated_time(rs.getTimestamp("created_time"));
        r.setStatus(rs.getInt("status"));

        // Người tạo
        Employee e = new Employee();
        e.setId(rs.getInt("created_id"));
        e.setName(nz(rs.getString("created_name")));
        Integer deptId = getIntOrNull(rs, "dept_id");
        if (deptId != null) {
            Department d = new Department();
            d.setId(deptId);
            d.setName(nz(rs.getString("dept_name")));
            e.setDept(d);
        }
        r.setCreated_by(e);

        return r;
    }

    /* ===================== CRUD nhỏ + Business ===================== */

    @Override
    public RequestForLeave get(int rid) {
        String sql = """
            SELECT r.rid, r.title, r.reason, r.[from], r.[to],
                   r.created_time, r.status,
                   e.eid AS created_id, e.ename AS created_name,
                   COALESCE(e.dept_id, e.did) AS dept_id,
                   d.dept_name
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Department d ON d.dept_id = COALESCE(e.dept_id, e.did)
            WHERE r.rid = ?
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally { closeConnection(); }
        return null;
    }

    /** Thêm đơn và trả về id (created_by = EID) */
    public int insertReturningId(RequestForLeave r) {
        String sql = """
            INSERT INTO RequestForLeave (created_by, [from], [to], reason, status, title, created_time)
            VALUES (?, ?, ?, ?, ?, ?, GETDATE());
            SELECT SCOPE_IDENTITY() AS rid;
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, r.getCreated_by().getId());             // EID
            stm.setDate(2, new java.sql.Date(r.getFrom().getTime()));
            stm.setDate(3, new java.sql.Date(r.getTo().getTime()));
            stm.setString(4, r.getReason());
            stm.setInt(5, r.getStatus());
            stm.setString(6, r.getTitle());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return -1;
    }

    /** Top N đơn gần đây của chính nhân viên */
    public List<RequestForLeave> recentOfEmployee(int eid, int limit) {
        List<RequestForLeave> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) r.rid, r.title, r.reason, r.[from], r.[to],
                           r.created_time, r.status,
                           e.eid AS created_id, e.ename AS created_name,
                           COALESCE(e.dept_id, e.did) AS dept_id,
                           d.dept_name
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Department d ON d.dept_id = COALESCE(e.dept_id, e.did)
            WHERE r.created_by = ?
            ORDER BY r.created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, limit);
            stm.setInt(2, eid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    /** Top N đơn của cấp dưới (manager_id | supervisorid) */
    public List<RequestForLeave> recentOfSubordinates(int managerEid, int limit) {
        List<RequestForLeave> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) r.rid, r.title, r.reason, r.[from], r.[to],
                           r.created_time, r.status,
                           e.eid AS created_id, e.ename AS created_name,
                           COALESCE(e.dept_id, e.did) AS dept_id,
                           d.dept_name
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Department d ON d.dept_id = COALESCE(e.dept_id, e.did)
            WHERE COALESCE(e.manager_id, e.supervisorid) = ?
            ORDER BY r.created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, limit);
            stm.setInt(2, managerEid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    /** Tìm kiếm đơn của chính nhân viên */
    public List<RequestForLeave> searchOfEmployee(int eid, String q, int limit) {
        List<RequestForLeave> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) r.rid, r.title, r.reason, r.[from], r.[to],
                           r.created_time, r.status,
                           e.eid AS created_id, e.ename AS created_name,
                           COALESCE(e.dept_id, e.did) AS dept_id,
                           d.dept_name
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Department d ON d.dept_id = COALESCE(e.dept_id, e.did)
            WHERE r.created_by = ?
              AND (
                    r.title  LIKE ? OR
                    r.reason LIKE ? OR
                    CONVERT(varchar(10), r.[from], 120) LIKE ? OR
                    CONVERT(varchar(10), r.[to],   120) LIKE ?
                  )
            ORDER BY r.created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            String like = "%" + q + "%";
            stm.setInt(1, limit);
            stm.setInt(2, eid);
            stm.setString(3, like);
            stm.setString(4, like);
            stm.setString(5, like);
            stm.setString(6, like);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    /** Chủ đơn xóa khi đang In Progress */
    public boolean deleteByOwnerIfInProgress(int rid, int uid) {
        String sql = """
            DELETE FROM RequestForLeave
            WHERE rid = ? AND status = 0
              AND created_by = (
                SELECT en.eid FROM Enrollment en
                WHERE en.uid = ? AND en.active = 1
              )
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            stm.setInt(2, uid);
            int n = stm.executeUpdate();
            return n > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return false;
    }

    /** Viewer có quyền xem đơn không? (chính chủ hoặc quản lý trực tiếp) */
    public boolean canViewRequest(int rid, Integer viewerEid) {
        if (viewerEid == null) return false;
        String sql = """
            SELECT 1
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by
            WHERE r.rid = ?
              AND (
                    r.created_by = ? OR
                    COALESCE(e.manager_id, e.supervisorid) = ?
                  )
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            stm.setInt(2, viewerEid);
            stm.setInt(3, viewerEid);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return false;
    }

    /* ====== Not used/required ====== */
    @Override public ArrayList<RequestForLeave> list() { throw new UnsupportedOperationException(); }
    @Override public void insert(RequestForLeave model) { throw new UnsupportedOperationException(); }
    @Override public void update(RequestForLeave model) { throw new UnsupportedOperationException(); }
    @Override public void delete(RequestForLeave model) { throw new UnsupportedOperationException(); }
}
