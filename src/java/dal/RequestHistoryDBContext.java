package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.RequestHistory;

public class RequestHistoryDBContext extends DBContext<RequestHistory> {

    private RequestHistory map(ResultSet rs) throws SQLException {
        RequestHistory h = new RequestHistory();
        h.setHid(rs.getInt("hid"));
        h.setRid(rs.getInt("rid"));
        Object o = rs.getObject("actor_uid");
        h.setActorUid((o==null) ? null : ((Number)o).intValue());
        h.setAction(rs.getString("action"));
        h.setNote(rs.getString("note"));
        h.setCreated_time(rs.getTimestamp("created_time"));
        return h;
    }

    /** Ghi lịch sử ngắn gọn (note có thể null) */
    public void add(int rid, int actorUid, String action, String note) {
        String sql = """
            INSERT INTO RequestHistory(rid,actor_uid,action,note)
            VALUES(?,?,?,?)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            stm.setInt(2, actorUid);
            stm.setString(3, action);
            if (note == null) stm.setNull(4, Types.NVARCHAR); else stm.setString(4, note);
            stm.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
    }

    public List<RequestHistory> listByRid(int rid, int limit) {
        List<RequestHistory> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) hid, rid, actor_uid, action, note, created_time
            FROM RequestHistory
            WHERE rid=?
            ORDER BY created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, limit);
            stm.setInt(2, rid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    // Unused
    @Override public ArrayList<RequestHistory> list() { throw new UnsupportedOperationException(); }
    @Override public RequestHistory get(int id) { throw new UnsupportedOperationException(); }
    @Override public void insert(RequestHistory m) { throw new UnsupportedOperationException(); }
    @Override public void update(RequestHistory m) { throw new UnsupportedOperationException(); }
    @Override public void delete(RequestHistory m) { throw new UnsupportedOperationException(); }
}
