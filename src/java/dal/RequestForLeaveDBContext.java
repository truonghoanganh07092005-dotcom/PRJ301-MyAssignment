package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Department;
import model.Employee;
import model.RequestForLeave;

public class RequestForLeaveDBContext extends DBContext<RequestForLeave> {

    /* ============ Helpers ============ */

    private Integer getIntOrNull(ResultSet rs, String col) throws SQLException {
        Object o = rs.getObject(col);
        return (o == null) ? null : ((Number)o).intValue();
    }
    private String nz(String s){ return s == null ? "" : s; }

    private RequestForLeave mapRow(ResultSet rs) throws SQLException {
        RequestForLeave r = new RequestForLeave();
        r.setRid(rs.getInt("rid"));
        r.setTitle(nz(rs.getString("title")));
        r.setReason(nz(rs.getString("reason")));
        r.setFrom(rs.getDate("from"));
        r.setTo(rs.getDate("to"));
        r.setCreated_time(rs.getTimestamp("created_time"));
        r.setStatus(rs.getInt("status"));

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

    /* ============ CRUD/Business ============ */

    /** Lấy chi tiết 1 đơn theo rid */
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
            SELECT SCOPE_IDENTITY();
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, r.getCreated_by().getId());
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

    /** Top N đơn gần đây của chính nhân viên (theo EID) */
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

    /** Top N đơn cấp dưới của 1 EID quản lý (tương thích cả manager_id/supervisorid) */
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

    /** Top N đơn cấp dưới: nhận UID quản lý, tự map → EID, tương thích dữ liệu cũ (created_by=UID) */
    public List<RequestForLeave> recentOfSubordinatesByUid(int managerUid, int limit) {
        List<RequestForLeave> list = new ArrayList<>();
        String sql = """
            WITH mgr AS (
                SELECT TOP 1 en.eid AS manager_eid
                FROM Enrollment en
                WHERE en.uid = ? AND en.active = 1
            ),
            S AS (
                -- created_by = EID (đúng schema)
                SELECT r.rid, r.title, r.reason, r.[from], r.[to],
                       r.created_time, r.status,
                       e.eid AS created_id, e.ename AS created_name,
                       COALESCE(e.dept_id, e.did) AS dept_id,
                       d.dept_name,
                       COALESCE(e.manager_id, e.supervisorid) AS manager_eid
                FROM RequestForLeave r
                JOIN Employee e ON e.eid = r.created_by
                LEFT JOIN Department d ON d.dept_id = COALESCE(e.dept_id, e.did)

                UNION ALL

                -- created_by = UID (dữ liệu cũ) → quy chiếu qua Enrollment
                SELECT r.rid, r.title, r.reason, r.[from], r.[to],
                       r.created_time, r.status,
                       e2.eid AS created_id, e2.ename AS created_name,
                       COALESCE(e2.dept_id, e2.did) AS dept_id,
                       d2.dept_name,
                       COALESCE(e2.manager_id, e2.supervisorid) AS manager_eid
                FROM RequestForLeave r
                JOIN Enrollment en2 ON en2.uid = r.created_by AND en2.active = 1
                JOIN Employee   e2  ON e2.eid = en2.eid
                LEFT JOIN Department d2 ON d2.dept_id = COALESCE(e2.dept_id, e2.did)
            )
            SELECT TOP (?) rid, title, reason, [from], [to], created_time, status,
                           created_id, created_name, dept_id, dept_name
            FROM S
            WHERE manager_eid = (SELECT manager_eid FROM mgr)
            ORDER BY created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, managerUid);
            stm.setInt(2, limit);
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

    /** Chỉ chủ đơn (UID) và status=0 mới được xoá */
    public boolean deleteByOwnerIfInProgress(int rid, int uid) {
        String sql = """
            DELETE FROM RequestForLeave
            WHERE rid = ? AND status = 0
              AND created_by = (SELECT en.eid FROM Enrollment en WHERE en.uid = ? AND en.active = 1)
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
              AND ( r.created_by = ? OR COALESCE(e.manager_id, e.supervisorid) = ? )
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
/** Chủ đơn huỷ khi đang In Progress (status=0) -> set status=3 (Cancelled) */
public boolean cancelByOwnerIfInProgress(int rid, int uid) {
    String sql = """
        UPDATE r
        SET r.status = 3
        FROM RequestForLeave r
        JOIN Enrollment en ON en.eid = r.created_by AND en.active = 1
        WHERE r.rid = ? AND en.uid = ? AND r.status = 0
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, rid);
        stm.setInt(2, uid);
        return stm.executeUpdate() > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    } finally {
        closeConnection();
    }
}


    /* not used */
    @Override public ArrayList<RequestForLeave> list() { throw new UnsupportedOperationException(); }
    @Override public void insert(RequestForLeave model) { throw new UnsupportedOperationException(); }
    @Override public void update(RequestForLeave model) { throw new UnsupportedOperationException(); }
    @Override public void delete(RequestForLeave model) { throw new UnsupportedOperationException(); }

  public List<RequestForLeave> listMineByTitle(Integer eid, String q, int limit) {
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
        WHERE r.created_by = ? AND r.title LIKE ?
        ORDER BY r.created_time DESC
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, limit);
        stm.setInt(2, eid);
        stm.setString(3, "%" + (q == null ? "" : q.trim()) + "%");
        ResultSet rs = stm.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}

/** Chủ đơn sửa khi đang In Progress (status=0) */
public boolean updateByOwnerIfInProgress(int rid, int uid, String title,
                                         java.sql.Date from, java.sql.Date to, String reason) {
    String sql = """
        UPDATE r
           SET r.title = ?, r.[from] = ?, r.[to] = ?, r.reason = ?
        FROM RequestForLeave r
        JOIN Enrollment en ON en.eid = r.created_by AND en.active = 1
        WHERE r.rid = ? AND en.uid = ? AND r.status = 0
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setString(1, title);
        stm.setDate(2, from);
        stm.setDate(3, to);
        stm.setString(4, reason);
        stm.setInt(5, rid);
        stm.setInt(6, uid);
        return stm.executeUpdate() > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    } finally { closeConnection(); }
}

/** Chủ đơn khôi phục khi đang Cancelled (status=3) -> set về 0 */
public boolean uncancelByOwnerIfCancelled(int rid, int uid) {
    String sql = """
        UPDATE r
           SET r.status = 0
        FROM RequestForLeave r
        JOIN Enrollment en ON en.eid = r.created_by AND en.active = 1
        WHERE r.rid = ? AND en.uid = ? AND r.status = 3
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, rid);
        stm.setInt(2, uid);
        return stm.executeUpdate() > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    } finally { closeConnection(); }
}
public boolean approveByManagerIfInProgress(int rid, int managerUid) {
    String sql = """
        UPDATE r
           SET r.status = 1,
               r.processed_by   = en.eid,
               r.processed_time = GETDATE()
        FROM RequestForLeave r
        JOIN Employee e ON e.eid = r.created_by
        JOIN Enrollment en ON en.uid = ? AND en.active = 1
        WHERE r.rid = ?
          AND r.status = 0
          AND COALESCE(e.manager_id, e.supervisorid) = en.eid
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setInt(2, rid);
        return stm.executeUpdate() > 0;
    } catch (SQLException ex) { ex.printStackTrace(); return false; }
    finally { closeConnection(); }
}

// === REJECT ===
public boolean rejectByManagerIfInProgress(int rid, int managerUid) {
    String sql = """
        UPDATE r
           SET r.status = 2,
               r.processed_by   = en.eid,
               r.processed_time = GETDATE()
        FROM RequestForLeave r
        JOIN Employee e ON e.eid = r.created_by
        JOIN Enrollment en ON en.uid = ? AND en.active = 1
        WHERE r.rid = ?
          AND r.status = 0
          AND COALESCE(e.manager_id, e.supervisorid) = en.eid
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setInt(2, rid);
        return stm.executeUpdate() > 0;
    } catch (SQLException ex) { ex.printStackTrace(); return false; }
    finally { closeConnection(); }
}

// === UNAPPROVE (HỦY DUYỆT/TỪ CHỐI) TRONG 24H ===
public boolean unapproveWithin24h(int rid, int managerUid) {
    String sql = """
        UPDATE r
           SET r.status = 0,
               r.processed_by   = NULL,
               r.processed_time = NULL
        FROM RequestForLeave r
        JOIN Enrollment en ON en.uid = ? AND en.active = 1
        WHERE r.rid = ?
          AND r.status IN (1,2)           -- đã duyệt hoặc đã từ chối
          AND r.processed_by = en.eid     -- chính người xử lý
          AND DATEDIFF(HOUR, r.processed_time, GETDATE()) <= 24
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setInt(2, rid);
        return stm.executeUpdate() > 0;
    } catch (SQLException ex) { ex.printStackTrace(); return false; }
    finally { closeConnection(); }
}
public Integer ownerUidByRid(int rid){
    String sql = """
        SELECT TOP 1 en.uid
        FROM RequestForLeave r
        JOIN Enrollment en ON en.eid = r.created_by AND en.active = 1
        WHERE r.rid = ?
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)){
        stm.setInt(1, rid);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (SQLException ex){ ex.printStackTrace(); }
    finally { closeConnection(); }
    return null;
}

/** Lấy UID của quản lý trực tiếp của chủ đơn (nếu có) */
public Integer managerUidOfOwnerByRid(int rid){
    String sql = """
        SELECT TOP 1 enmgr.uid
        FROM RequestForLeave r
        JOIN Employee e    ON e.eid = r.created_by
        JOIN Enrollment en ON en.eid = COALESCE(e.manager_id, e.supervisorid) AND en.active = 1
        JOIN Enrollment enmgr ON enmgr.eid = en.eid AND enmgr.active = 1
        WHERE r.rid = ?
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)){
        stm.setInt(1, rid);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (SQLException ex){ ex.printStackTrace(); }
    finally { closeConnection(); }
    return null;
}

/** Lấy trạng thái hiện tại của đơn */
public Integer getStatus(int rid){
    String sql = "SELECT status FROM RequestForLeave WHERE rid=?";
    try (PreparedStatement stm = connection.prepareStatement(sql)){
        stm.setInt(1, rid);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) return (Integer) rs.getObject(1);
    } catch (SQLException ex){ ex.printStackTrace(); }
    finally { closeConnection(); }
    return null;
}

}