package dal;

import java.sql.*;
import java.util.ArrayList;
import model.Notification;

public class NotificationDBContext extends DBContext<Notification> {

    /** Tạo 1 thông báo cho 1 user (uid). Url có thể null. */
    public void create(int uid, String content, String url) {
        String sql = """
            INSERT INTO Notification(uid, content, url, created_time)
            VALUES (?, ?, ?, GETDATE())
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uid);
            stm.setString(2, content);
            if (url == null || url.isBlank()) stm.setNull(3, Types.NVARCHAR);
            else stm.setString(3, url);
            stm.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { closeConnection(); }
    }

    /** Danh sách CHƯA ĐỌC (mới→cũ). */
    public ArrayList<Notification> unread(int uid) {
        ArrayList<Notification> list = new ArrayList<>();
        String sql = """
            SELECT nid, uid, content, url, created_time, read_time
            FROM Notification
            WHERE uid = ? AND read_time IS NULL
            ORDER BY created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    /** Lấy N thông báo gần đây (đã/ chưa đọc). */
    public ArrayList<Notification> recent(int uid, int limit) {
        ArrayList<Notification> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) nid, uid, content, url, created_time, read_time
            FROM Notification
            WHERE uid = ?
            ORDER BY created_time DESC
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, limit);
            stm.setInt(2, uid);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    /** Đánh dấu đã đọc tất cả. */
    public void readAll(int uid) {
        String sql = """
            UPDATE Notification
            SET read_time = COALESCE(read_time, GETDATE())
            WHERE uid = ? AND read_time IS NULL
        """;
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uid);
            stm.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { closeConnection(); }
    }

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNid(rs.getInt("nid"));
        n.setUid(rs.getInt("uid"));
        n.setContent(rs.getString("content"));
        n.setUrl(rs.getString("url"));
        n.setCreated_time(rs.getTimestamp("created_time"));
        n.setRead_time(rs.getTimestamp("read_time"));
        return n;
    }

    @Override public ArrayList<Notification> list(){ throw new UnsupportedOperationException(); }
    @Override public Notification get(int id){ throw new UnsupportedOperationException(); }
    @Override public void insert(Notification m){ throw new UnsupportedOperationException(); }
    @Override public void update(Notification m){ throw new UnsupportedOperationException(); }
    @Override public void delete(Notification m){ throw new UnsupportedOperationException(); }
}
