package dal;

import java.sql.*;
import java.util.ArrayList;
import model.Employee;
import model.RequestActionHistory;

public class RequestHistoryDBContext extends DBContext<RequestActionHistory> {

    private RequestActionHistory map(ResultSet rs) throws SQLException {
        RequestActionHistory h = new RequestActionHistory();
        h.setId(rs.getInt("hid"));
        h.setRid(rs.getInt("rid"));
        h.setAction(rs.getString("action"));
        Object ou = rs.getObject("actor_uid");
        h.setActorUid(ou==null? null : ((Number)ou).intValue());
        h.setNote(rs.getString("note"));
        h.setPrevStatus((Integer) rs.getObject("prev_status"));
        h.setNewStatus((Integer) rs.getObject("new_status"));
        h.setCreatedTime(rs.getTimestamp("created_time"));
        Employee e = new Employee();
        Object oeid = rs.getObject("actor_eid");
        if (oeid!=null) e.setId(((Number)oeid).intValue());
        e.setName(rs.getString("actor_name"));
        h.setActor(e);
        return h;
    }

    public void add(int rid, String action, Integer actorUid, Integer actorEid,
                    Integer prevStatus, Integer newStatus, String note){
        String sql = """
            INSERT INTO RequestActionHistory(rid,action,actor_uid,actor_eid,prev_status,new_status,note)
            VALUES(?,?,?,?,?,?,?)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            stm.setString(2, action);
            if (actorUid==null) stm.setNull(3, Types.INTEGER); else stm.setInt(3, actorUid);
            if (actorEid==null) stm.setNull(4, Types.INTEGER); else stm.setInt(4, actorEid);
            if (prevStatus==null) stm.setNull(5, Types.INTEGER); else stm.setInt(5, prevStatus);
            if (newStatus==null) stm.setNull(6, Types.INTEGER); else stm.setInt(6, newStatus);
            stm.setString(7, note);
            stm.executeUpdate();
        } catch (SQLException ex){ ex.printStackTrace(); }
        finally { closeConnection(); }
    }

    public ArrayList<RequestActionHistory> listByRid(int rid){
        ArrayList<RequestActionHistory> list = new ArrayList<>();
        String sql = """
            SELECT h.hid,h.rid,h.action,h.actor_uid,h.actor_eid,h.prev_status,h.new_status,h.note,h.created_time,
                   e.ename AS actor_name
            FROM RequestActionHistory h
            LEFT JOIN Employee e ON e.eid = h.actor_eid
            WHERE h.rid=? ORDER BY h.created_time DESC, h.hid DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, rid);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) list.add(map(rs));
        } catch (SQLException ex){ ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    @Override public ArrayList<RequestActionHistory> list(){ throw new UnsupportedOperationException(); }
    @Override public RequestActionHistory get(int id){ throw new UnsupportedOperationException(); }
    @Override public void insert(RequestActionHistory m){ throw new UnsupportedOperationException(); }
    @Override public void update(RequestActionHistory m){ throw new UnsupportedOperationException(); }
    @Override public void delete(RequestActionHistory m){ throw new UnsupportedOperationException(); }
}
