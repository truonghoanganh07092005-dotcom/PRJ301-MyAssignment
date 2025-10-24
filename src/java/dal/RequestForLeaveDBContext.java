package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Employee;
import model.RequestForLeave;

public class RequestForLeaveDBContext extends DBContext<RequestForLeave> {

    /**
     * Lấy Top N đơn gần đây của 1 employee (đổ ra mục "Đơn gần đây").
     */
    public List<RequestForLeave> recentOfEmployee(int eid, int top) {
        top = Math.max(1, Math.min(top, 50)); // an toàn
        String sql =
              "SELECT TOP " + top + " "
            + "       r.rid, r.created_by, e.ename AS created_name, "
            + "       r.created_time, r.[from], r.[to], r.[reason], r.[status], "
            + "       r.processed_by, p.ename AS processed_name, r.title "
            + "FROM RequestForLeave r "
            + "JOIN Employee e ON e.eid = r.created_by "
            + "LEFT JOIN Employee p ON p.eid = r.processed_by "
            + "WHERE r.created_by = ? "
            + "ORDER BY r.created_time DESC";

        List<RequestForLeave> list = new ArrayList<>();
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, eid);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName())
                  .log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return list;
    }

    /**
     * Lấy tất cả đơn của employee hiện tại và toàn bộ cấp dưới (đệ quy),
     * sắp xếp mới nhất trước.
     */
    public ArrayList<RequestForLeave> getByEmployeeAndSubodiaries(int eid) {
        ArrayList<RequestForLeave> rfls = new ArrayList<>();
        String sql = """
            WITH Org AS (
               SELECT *, 0 AS lvl FROM Employee e WHERE e.eid = ?
               UNION ALL
               SELECT c.*, o.lvl + 1 AS lvl
               FROM Employee c JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT
                   r.rid,
                   r.created_by,
                   ce.ename AS created_name,
                   r.created_time,
                   r.[from],
                   r.[to],
                   r.[reason],
                   r.[status],
                   r.processed_by,
                   pe.ename AS processed_name,
                   r.title
            FROM Org o
            JOIN RequestForLeave r  ON r.created_by = o.eid
            JOIN Employee       ce  ON ce.eid = r.created_by
            LEFT JOIN Employee  pe  ON pe.eid = r.processed_by
            ORDER BY r.created_time DESC
            """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, eid);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    rfls.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequestForLeaveDBContext.class.getName())
                  .log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
        return rfls;
    }

    /**
     * Map 1 hàng ResultSet -> RequestForLeave (NULL-safe).
     */
    private RequestForLeave mapRow(ResultSet rs) throws SQLException {
        RequestForLeave rfl = new RequestForLeave();

        rfl.setRid(rs.getInt("rid"));
        rfl.setCreated_time(rs.getTimestamp("created_time"));
        rfl.setFrom(rs.getDate("from"));
        rfl.setTo(rs.getDate("to"));
        rfl.setReason(rs.getString("reason"));
        rfl.setStatus(rs.getInt("status"));
        try {
            // nếu có cột title trong DB
            rfl.setTitle(rs.getString("title"));
        } catch (SQLException ignore) { /* title không tồn tại thì bỏ qua */ }

        // created_by
        Employee created = new Employee();
        created.setId(rs.getInt("created_by"));
        created.setName(rs.getString("created_name"));
        rfl.setCreated_by(created);

        // processed_by (NULL-safe)
        Integer processedId = (Integer) rs.getObject("processed_by"); // null nếu DB là NULL
        if (processedId != null) {
            Employee processed = new Employee();
            processed.setId(processedId);
            processed.setName(rs.getString("processed_name"));
            rfl.setProcessed_by(processed);
        }

        return rfl;
    }

    /* ====== Các method CRUD chưa dùng có thể để trống hoặc implement sau ====== */

    @Override
    public ArrayList<RequestForLeave> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RequestForLeave get(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(RequestForLeave model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(RequestForLeave model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(RequestForLeave model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
