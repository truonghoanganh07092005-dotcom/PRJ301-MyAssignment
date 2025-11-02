package dal;

import java.sql.*;
import java.util.ArrayList;
import model.RequestActionHistoryRow;

public class RequestHistoryDBContext extends DBContext<RequestActionHistoryRow> {

    /** Ghi 1 dòng lịch sử (nullable fields truyền null nếu không có). */
    public void add(int rid, String action,
                    Integer actorUid, Integer actorEid,
                    Integer prevStatus, Integer newStatus,
                    String note) {
        String sql = """
            INSERT INTO RequestHistory(rid, action, actor_uid, actor_eid,
                                       prev_status, new_status, note, created_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 1;
            stm.setInt(i++, rid);
            stm.setString(i++, action);
            if (actorUid == null) stm.setNull(i++, Types.INTEGER); else stm.setInt(i++, actorUid);
            if (actorEid == null) stm.setNull(i++, Types.INTEGER); else stm.setInt(i++, actorEid);
            if (prevStatus == null) stm.setNull(i++, Types.INTEGER); else stm.setInt(i++, prevStatus);
            if (newStatus == null) stm.setNull(i++, Types.INTEGER); else stm.setInt(i++, newStatus);
            if (note == null) stm.setNull(i++, Types.NVARCHAR); else stm.setString(i++, note);
            stm.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { closeConnection(); }
    }

    /** Lịch sử theo rid (mới nhất trước). */
    public ArrayList<RequestActionHistoryRow> listByRid(int rid) {
        ArrayList<RequestActionHistoryRow> list = new ArrayList<>();
        String sql = """
            SELECT h.hid, h.rid, h.action, h.actor_uid, h.actor_eid,
                   h.prev_status, h.new_status, h.note, h.created_time,
                   e.ename AS actor_name
            FROM RequestHistory h
            LEFT JOIN Employee e ON e.eid = h.actor_eid
            WHERE h.rid = ?
            ORDER BY h.created_time DESC, h.hid DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    private RequestActionHistoryRow map(ResultSet rs) throws SQLException {
        RequestActionHistoryRow r = new RequestActionHistoryRow();
        r.setHid(rs.getInt("hid"));
        r.setRid(rs.getInt("rid"));
        r.setAction(rs.getString("action"));
        Object o;
        o = rs.getObject("actor_uid");  r.setActorUid(o==null? null : ((Number)o).intValue());
        o = rs.getObject("actor_eid");  r.setActorEid(o==null? null : ((Number)o).intValue());
        o = rs.getObject("prev_status");r.setPrevStatus(o==null? null : ((Number)o).intValue());
        o = rs.getObject("new_status"); r.setNewStatus(o==null? null : ((Number)o).intValue());
        r.setNote(rs.getString("note"));
        r.setCreatedTime(rs.getTimestamp("created_time"));
        r.setActorName(rs.getString("actor_name"));
        return r;
    }

    @Override public ArrayList<RequestActionHistoryRow> list(){ throw new UnsupportedOperationException(); }
    @Override public RequestActionHistoryRow get(int id){ throw new UnsupportedOperationException(); }
    @Override public void insert(RequestActionHistoryRow m){ throw new UnsupportedOperationException(); }
    @Override public void update(RequestActionHistoryRow m){ throw new UnsupportedOperationException(); }
    @Override public void delete(RequestActionHistoryRow m){ throw new UnsupportedOperationException(); }
}
