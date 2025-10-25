package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Employee;
import model.RequestForLeave;

public class RequestForLeaveDBContext extends DBContext<RequestForLeave> {

    // ========================= SELECT =========================

    /** Top N đơn gần đây của 1 employee (đổ ra mục "Đơn gần đây"). */
    public List<RequestForLeave> recentOfEmployee(int eid, int top) {
        top = Math.max(1, Math.min(top, 50));
        String sql =
              "SELECT TOP " + top + " "
            + "       r.rid, r.created_by, e.ename AS created_name, "
            + "       r.created_time, r.[from], r.[to], r.[reason], r.[status], "
            + "       r.processed_by, p.ename AS processed_name, r.title "
            + "FROM RequestForLeave r "
            + "JOIN Employee e ON e.eid = r.created_by "
            + "LEFT JOIN Employee p ON p.eid = r.processed_by "
            + "WHERE r.created_by = ? "
            + "ORDER BY r.created_time DESC, r.rid DESC";

        List<RequestForLeave> list = new ArrayList<>();
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, eid);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName())
                  .log(Level.SEVERE, null, ex);
        } finally { closeConnection(); }
        return list;
    }

    /** Danh sách tất cả "Đơn của tôi" (đổ trang /request/my). */
    public List<RequestForLeave> listByEmployee(int eid) {
        String sql =
              "SELECT r.rid, r.created_by, e.ename AS created_name, "
            + "       r.created_time, r.[from], r.[to], r.[reason], r.[status], "
            + "       r.processed_by, p.ename AS processed_name, r.title "
            + "FROM RequestForLeave r "
            + "JOIN Employee e ON e.eid = r.created_by "
            + "LEFT JOIN Employee p ON p.eid = r.processed_by "
            + "WHERE r.created_by = ? "
            + "ORDER BY r.created_time DESC, r.rid DESC";

        List<RequestForLeave> list = new ArrayList<>();
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, eid);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName())
                  .log(Level.SEVERE, null, ex);
        } finally { closeConnection(); }
        return list;
    }

    /** Top N đơn gần đây của cấp dưới (đã loại chính mình bằng WHERE o.lvl > 0). */
    public List<RequestForLeave> recentOfSubordinates(int managerEid, int top) {
        top = Math.max(1, Math.min(top, 50));
        String sql =
              "WITH Org AS (                                                   \n"
            + "   SELECT eid, 0 AS lvl FROM Employee WHERE eid = ?              \n"
            + "   UNION ALL                                                     \n"
            + "   SELECT c.eid, o.lvl + 1 FROM Employee c                       \n"
            + "   JOIN Org o ON c.supervisorid = o.eid                          \n"
            + ")                                                                 \n"
            + "SELECT TOP " + top + "                                          \n"
            + "   r.rid, r.created_by, ce.ename AS created_name,                \n"
            + "   r.created_time, r.[from], r.[to], r.[reason], r.[status],     \n"
            + "   r.processed_by, pe.ename AS processed_name, r.title           \n"
            + "FROM Org o                                                       \n"
            + "JOIN RequestForLeave r  ON r.created_by = o.eid                  \n"
            + "JOIN Employee       ce  ON ce.eid = r.created_by                 \n"
            + "LEFT JOIN Employee  pe  ON pe.eid = r.processed_by               \n"
            + "WHERE o.lvl > 0                                                  \n"
            + "ORDER BY r.created_time DESC, r.rid DESC;                         \n";

        List<RequestForLeave> list = new ArrayList<>();
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, managerEid);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName())
                  .log(Level.SEVERE, null, ex);
        } finally { closeConnection(); }
        return list;
    }

    /** (Tuỳ chọn) Lấy cả tôi + cấp dưới (nếu bạn cần) */
    public ArrayList<RequestForLeave> getByEmployeeAndSubodiaries(int eid) {
        ArrayList<RequestForLeave> rfls = new ArrayList<>();
        String sql = """
            WITH Org AS (
               SELECT *, 0 AS lvl FROM Employee e WHERE e.eid = ?
               UNION ALL
               SELECT c.*, o.lvl + 1 AS lvl
               FROM Employee c JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT r.rid, r.created_by, ce.ename AS created_name, r.created_time,
                   r.[from], r.[to], r.[reason], r.[status],
                   r.processed_by, pe.ename AS processed_name, r.title
            FROM Org o
            JOIN RequestForLeave r  ON r.created_by = o.eid
            JOIN Employee       ce  ON ce.eid = r.created_by
            LEFT JOIN Employee  pe  ON pe.eid = r.processed_by
            ORDER BY r.created_time DESC, r.rid DESC
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, eid);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) rfls.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName())
                  .log(Level.SEVERE, null, ex);
        } finally { closeConnection(); }
        return rfls;
    }

    // ========================= INSERT =========================

    /** Insert và trả về rid (tiện redirect / highlight đơn vừa tạo). */
    public int insertReturningId(RequestForLeave m) {
        String sql = "INSERT INTO RequestForLeave("
                   + " created_by, created_time, [from], [to], reason, status, processed_by, title)"
                   + " VALUES(?, GETDATE(), ?, ?, ?, ?, ?, ?)";
        int rid = -1;
        try (PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, m.getCreated_by().getId());
            stm.setDate(2, m.getFrom());
            stm.setDate(3, m.getTo());
            stm.setString(4, m.getReason());
            stm.setInt(5, m.getStatus()); // 0=InProgress
            if (m.getProcessed_by() == null) stm.setNull(6, Types.INTEGER);
            else stm.setInt(6, m.getProcessed_by().getId());
            try { stm.setString(7, (String) m.getClass().getMethod("getTitle").invoke(m)); }
            catch (Exception ignore) { stm.setNull(7, Types.NVARCHAR); }

            stm.executeUpdate();
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) rid = rs.getInt(1);
            }
        } catch (SQLException e) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, e);
        } finally { closeConnection(); }
        return rid;
    }

    /** Implement interface – vẫn hỗ trợ cũ (gọi insertReturningId bên trên). */
    @Override
    public void insert(RequestForLeave m) {
        int rid = insertReturningId(m);
        try { m.setId(rid); } catch (Exception ignore) {} // nếu BaseModel có setId
        try { m.getClass().getMethod("setRid", int.class).invoke(m, rid); } catch (Exception ignore) {}
    }

    // ========================= MAPPING =========================

    private RequestForLeave mapRow(ResultSet rs) throws SQLException {
        RequestForLeave rfl = new RequestForLeave();
        // rid / created_time …
        try { rfl.getClass().getMethod("setRid", int.class).invoke(rfl, rs.getInt("rid")); }
        catch (Exception ignore) { rfl.setId(rs.getInt("rid")); }
        rfl.setCreated_time(rs.getTimestamp("created_time"));
        rfl.setFrom(rs.getDate("from"));
        rfl.setTo(rs.getDate("to"));
        rfl.setReason(rs.getString("reason"));
        rfl.setStatus(rs.getInt("status"));
        try { rfl.getClass().getMethod("setTitle", String.class).invoke(rfl, rs.getString("title")); } catch (Exception ignore){}

        Employee created = new Employee();
        created.setId(rs.getInt("created_by"));
        created.setName(rs.getString("created_name"));
        rfl.setCreated_by(created);

        Integer processedId = (Integer) rs.getObject("processed_by");
        if (processedId != null) {
            Employee processed = new Employee();
            processed.setId(processedId);
            processed.setName(rs.getString("processed_name"));
            rfl.setProcessed_by(processed);
        }
        return rfl;
    }
public RequestForLeave getByRid(int rid){
    String sql = """
      SELECT r.rid, r.created_by, e.ename created_name, r.created_time, r.[from], r.[to],
             r.reason, r.status, r.processed_by, p.ename processed_name, r.title
      FROM RequestForLeave r
      JOIN Employee e ON e.eid = r.created_by
      LEFT JOIN Employee p ON p.eid = r.processed_by
      WHERE r.rid = ?
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, rid);
        try (ResultSet rs = stm.executeQuery()) {
            if (rs.next()) return mapRow(rs); // đã có mapRow trước đó
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    finally { closeConnection(); }
    return null;
}
public boolean deleteByOwnerIfInProgress(int rid, int ownerId) {
    String sql = "DELETE FROM RequestForLeave WHERE rid=? AND created_by=? AND status=0";
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, rid);
        stm.setInt(2, ownerId);
        int affected = stm.executeUpdate();
        return affected > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    } finally {
        closeConnection();
    }
}
    // ========================= NOT USED =========================
    @Override public ArrayList<RequestForLeave> list()            { throw new UnsupportedOperationException(); }
    @Override public RequestForLeave get(int id)                  { throw new UnsupportedOperationException(); }
    @Override public void update(RequestForLeave model)           { throw new UnsupportedOperationException(); }
    @Override public void delete(RequestForLeave model)           { throw new UnsupportedOperationException(); }
}
