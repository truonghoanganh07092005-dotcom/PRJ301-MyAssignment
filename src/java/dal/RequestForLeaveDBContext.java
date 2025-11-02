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
/* ========== AUTH CHECKS dùng cho BaseRequiredAuthorizationController ========== */

/** Quản lý trực tiếp được quyền DUYỆT khi đơn đang In-Progress (status=0). */
public boolean canApprove(int rid, int managerUid) {
    String sql = """
        SELECT 1
        FROM RequestForLeave r
        JOIN Employee   e  ON e.eid = r.created_by
        JOIN Enrollment en ON en.uid = ? AND en.active = 1   -- chuyển uid -> eid
        WHERE r.rid = ?
          AND r.status = 0                                    -- In-Progress
          AND COALESCE(e.manager_id, e.supervisorid) = en.eid -- đúng cấp quản lý
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setInt(2, rid);
        try (ResultSet rs = stm.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return false;
}

/** Quyền TỪ CHỐI giống điều kiện DUYỆT. */
public boolean canReject(int rid, int managerUid) {
    return canApprove(rid, managerUid);
}

/** Quản lý trực tiếp HỦY DUYỆT/TỪ CHỐI trong 24h và chính người đã xử lý. */
public boolean canUnapprove(int rid, int managerUid) {
    String sql = """
        SELECT 1
        FROM RequestForLeave r
        JOIN Enrollment en ON en.uid = ? AND en.active = 1
        WHERE r.rid = ?
          AND r.status IN (1,2)               -- APPROVED/REJECTED
          AND r.processed_by = en.eid         -- đúng người đã xử lý
          AND DATEDIFF(HOUR, r.processed_time, GETDATE()) <= 24
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setInt(2, rid);
        try (ResultSet rs = stm.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return false;
}

/* Danh sách cấp dưới theo EID của quản lý (manager/supervisor) */
public ArrayList<model.Employee> subordinatesOfManagerEid(int managerEid) {
    ArrayList<model.Employee> list = new ArrayList<>();
    String sql = """
        SELECT e.eid, e.ename
        FROM Employee e
        WHERE COALESCE(e.manager_id, e.supervisorid) = ?
        ORDER BY e.ename
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerEid);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            model.Employee e = new model.Employee();
            e.setId(rs.getInt("eid"));
            e.setName(rs.getString("ename"));
            list.add(e);
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}

/* Các khoảng nghỉ (đơn đã DUYỆT) của cấp dưới trong khoảng [from..to] */
public static final class SubLeave {
    public int eid;
    public java.sql.Date from;
    public java.sql.Date to;
}

public ArrayList<SubLeave> approvedLeavesOfSubs(int managerEid, java.sql.Date from, java.sql.Date to) {
    ArrayList<SubLeave> list = new ArrayList<>();
    String sql = """
        SELECT r.created_by AS eid, r.[from], r.[to]
        FROM RequestForLeave r
        JOIN Employee e ON e.eid = r.created_by
        WHERE r.status = 1
          AND COALESCE(e.manager_id, e.supervisorid) = ?
          AND r.[to]   >= ?      -- giao nhau với khoảng cần xem
          AND r.[from] <= ?
        ORDER BY r.created_by, r.[from]
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerEid);
        stm.setDate(2, from);
        stm.setDate(3, to);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            SubLeave sl = new SubLeave();
            sl.eid  = rs.getInt("eid");
            sl.from = rs.getDate("from");
            sl.to   = rs.getDate("to");
            list.add(sl);
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}
public java.util.List<model.LeaveSpan> listLeavesOfTeamBetween(
        int managerUid, java.sql.Date from, java.sql.Date to) {
    java.util.List<model.LeaveSpan> list = new java.util.ArrayList<>();
    String sql = """
        WITH mgr AS (
            SELECT TOP 1 en.eid AS manager_eid
            FROM Enrollment en
            WHERE en.uid = ? AND en.active = 1
        ),
        S AS (
            -- Case A: created_by = EID (đúng schema mới)
            SELECT e.eid, e.ename, r.[from], r.[to], r.status
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by

            UNION ALL

            -- Case B: created_by = UID (schema cũ) -> map sang EID của nhân viên
            SELECT e2.eid, e2.ename, r.[from], r.[to], r.status
            FROM RequestForLeave r
            JOIN Enrollment en2 ON en2.uid = r.created_by AND en2.active = 1
            JOIN Employee   e2  ON e2.eid = en2.eid
        )
        SELECT s.eid, s.ename, s.[from], s.[to]
        FROM S s
        WHERE s.status IN (0,1)                             -- cho thấy In-Progress & Approved
          AND (s.[from] <= ? AND s.[to] >= ?)               -- giao với [from..to]
          AND COALESCE(
                (SELECT COALESCE(e.manager_id, e.supervisorid) 
                 FROM Employee e WHERE e.eid = s.eid), -1
              ) = (SELECT manager_eid FROM mgr)
        ORDER BY s.ename, s.[from]
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setDate(2, to);
        stm.setDate(3, from);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            list.add(new model.LeaveSpan(
                rs.getInt("eid"),
                rs.getString("ename"),
                rs.getDate("from"),
                rs.getDate("to")
            ));
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}
/** Lưới Agenda: mọi cấp dưới của managerUid × mọi ngày [from..to], đánh dấu có/không nghỉ
 *  - Hỗ trợ created_by = EID (mới) và created_by = UID (cũ)
 *  - Tính nghỉ khi có bản ghi status IN (0,1) & (from<=day<=to)
 */
public java.util.List<model.AgendaCell> listAgendaCellsForManager(
        int managerUid, java.sql.Date from, java.sql.Date to) {

    java.util.List<model.AgendaCell> list = new java.util.ArrayList<>();
    String sql = """
        -- manager EID từ UID
        WITH mgr AS (
            SELECT TOP 1 en.eid AS manager_eid
            FROM Enrollment en
            WHERE en.uid = ? AND en.active = 1
        ),
        -- dãy ngày [from..to]
        dates AS (
            SELECT CAST(? AS date) AS d
            UNION ALL
            SELECT DATEADD(day, 1, d) FROM dates WHERE d < ?
        ),
        -- danh sách cấp dưới (bằng EID)
        subs AS (
            SELECT e.eid, e.ename
            FROM Employee e
            WHERE COALESCE(e.manager_id, e.supervisorid) = (SELECT manager_eid FROM mgr)
        ),
        -- chuẩn hoá bảng đơn: mọi dòng = (eid, from, to, status)
        req_norm AS (
            -- created_by = EID (mới)
            SELECT r.created_by AS eid, r.[from], r.[to], r.status
            FROM RequestForLeave r
            WHERE r.created_by IS NOT NULL

            UNION ALL
            -- created_by = UID (cũ) → map sang EID
            SELECT en2.eid AS eid, r.[from], r.[to], r.status
            FROM RequestForLeave r
            JOIN Enrollment en2 ON en2.uid = r.created_by AND en2.active = 1
        ),
        -- các ngày có nghỉ (chỉ lấy trạng thái 0-InProgress hoặc 1-Approved)
        spans AS (
            SELECT rn.eid, d.d AS day
            FROM req_norm rn
            JOIN dates d ON rn.[from] <= d.d AND rn.[to] >= d.d
            WHERE rn.status IN (0,1)
        )
        SELECT s.eid, s.ename, d.d,
               CASE WHEN sp.day IS NOT NULL THEN 1 ELSE 0 END AS on_leave
        FROM subs s
        CROSS JOIN dates d
        LEFT JOIN spans sp ON sp.eid = s.eid AND sp.day = d.d
        ORDER BY s.ename, d.d
        OPTION (MAXRECURSION 32767);
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setDate(2, from);
        stm.setDate(3, to);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            list.add(new model.AgendaCell(
                rs.getInt("eid"),
                rs.getString("ename"),
                rs.getDate("d"),
                rs.getInt("on_leave") == 1
            ));
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}
// ===================== AGENDA (SINGLE, CANONICAL) =====================

/** Hàng nhân sự để vẽ lưới Agenda */
public static class SimpleEmp {
    public final int eid;
    public final String ename;
    public SimpleEmp(int eid, String ename) {
        this.eid = eid;
        this.ename = ename;
    }
}

/** Một khoảng NGHỈ đã duyệt của 1 nhân sự */
public static class AgendaItem {
    public final int eid;
    public final String ename;
    public final java.sql.Date from;
    public final java.sql.Date to;
    public AgendaItem(int eid, String ename, java.sql.Date from, java.sql.Date to) {
        this.eid = eid;
        this.ename = ename;
        this.from = from;
        this.to = to;
    }
}

/** Danh sách cấp dưới trực tiếp (managerUid → EID con) */
public java.util.List<SimpleEmp> subordinatesOfManaged(int managerUid) {
    String sql = """
        SELECT e.eid, e.ename
        FROM Enrollment en
        JOIN Employee m ON m.eid = en.eid
        JOIN Employee e ON COALESCE(e.manager_id, e.supervisorid) = m.eid
        WHERE en.uid = ? AND en.active = 1
        ORDER BY e.ename
    """;
    java.util.List<SimpleEmp> list = new java.util.ArrayList<>();
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        try (ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                list.add(new SimpleEmp(rs.getInt("eid"), rs.getString("ename")));
            }
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}

/** Các khoảng NGHỈ ĐÃ DUYỆT (status=1) của cấp dưới trong [from, to] – hỗ trợ cả dữ liệu cũ/new */
public java.util.List<AgendaItem> agendaOfSubordinates(int managerUid,
                                                       java.sql.Date from,
                                                       java.sql.Date to) {
    String sql = """
        WITH mgr AS (
            SELECT TOP 1 en.eid AS manager_eid
            FROM Enrollment en
            WHERE en.uid = ? AND en.active = 1
        ),
        S AS (
            -- Schema mới: created_by = EID
            SELECT r.created_by AS eid, e.ename, r.[from], r.[to], r.status
            FROM RequestForLeave r
            JOIN Employee e ON e.eid = r.created_by
            WHERE COALESCE(e.manager_id, e.supervisorid) = (SELECT manager_eid FROM mgr)

            UNION ALL

            -- Schema cũ: created_by = UID -> map UID -> EID
            SELECT en2.eid AS eid, e2.ename, r.[from], r.[to], r.status
            FROM RequestForLeave r
            JOIN Enrollment en2 ON en2.uid = r.created_by AND en2.active = 1
            JOIN Employee   e2  ON e2.eid = en2.eid
            WHERE COALESCE(e2.manager_id, e2.supervisorid) = (SELECT manager_eid FROM mgr)
        )
        SELECT eid, ename, [from], [to]
        FROM S
        WHERE status = 1                                -- chỉ đơn ĐÃ DUYỆT
          AND NOT ([to] < ? OR [from] > ?)             -- giao với [from..to]
        ORDER BY ename, [from]
    """;

    java.util.List<AgendaItem> list = new java.util.ArrayList<>();
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, managerUid);
        stm.setDate(2, from);
        stm.setDate(3, to);
        try (ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                list.add(new AgendaItem(
                        rs.getInt("eid"),
                        rs.getString("ename"),
                        rs.getDate("from"),
                        rs.getDate("to")
                ));
            }
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return list;
}
// ===================== END AGENDA (SINGLE) =====================


}