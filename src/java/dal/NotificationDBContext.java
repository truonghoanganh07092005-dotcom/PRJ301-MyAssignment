package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Notification;

public class NotificationDBContext extends DBContext<Notification> {

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNid(rs.getInt("nid"));
        n.setUidTarget(rs.getInt("uid_target"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setCreated_time(rs.getTimestamp("created_time"));
        n.setIs_read(rs.getBoolean("is_read"));
        Object o = rs.getObject("rid");
        n.setRid((o == null) ? null : ((Number) o).intValue());
        return n;
    }

    /** Tạo 1 thông báo mới (rid có thể null) */
    public void push(int uidTarget, String title, String content, Integer rid) {
        String sql = """
            INSERT INTO Notification(uid_target,title,content,rid)
            VALUES(?,?,?,?)
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uidTarget);
            stm.setString(2, title);
            stm.setString(3, content);
            if (rid == null) stm.setNull(4, Types.INTEGER); else stm.setInt(4, rid);
            stm.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
    }

    public int countUnread(int uid) {
        String sql = "SELECT COUNT(*) FROM Notification WHERE uid_target=? AND is_read=0";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uid);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return 0;
    }

    public void markAllRead(int uid) {
        String sql = "UPDATE Notification SET is_read=1 WHERE uid_target=? AND is_read=0";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uid);
            stm.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
    }

    public List<Notification> listByUid(int uid, int limit) {
        List<Notification> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) nid, uid_target, title, content, created_time, is_read, rid
            FROM Notification
            WHERE uid_target=?
            ORDER BY created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, limit);
            stm.setInt(2, uid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    // Unused abstract methods
    @Override public ArrayList<Notification> list() { throw new UnsupportedOperationException(); }
    @Override public Notification get(int id) { throw new UnsupportedOperationException(); }
    @Override public void insert(Notification m) { throw new UnsupportedOperationException(); }
    @Override public void update(Notification m) { throw new UnsupportedOperationException(); }
    @Override public void delete(Notification m) { throw new UnsupportedOperationException(); }
    // Giữ nguyên push(...). Thêm alias để tương thích controller cũ:
public void create(int uidTarget, String title, String content) {
    push(uidTarget, title, content, null);
}
public void create(int uidTarget, String title, String content, Integer rid) {
    push(uidTarget, title, content, rid);
}

}
