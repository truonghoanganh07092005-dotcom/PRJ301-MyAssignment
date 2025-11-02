package dal;

import java.sql.*;
import java.util.ArrayList;
import model.Notification;

public class NotificationDBContext extends DBContext<Notification> {

    public void create(int uid, String title, String content, String url){
        String sql = "INSERT INTO Notification(uid,title,content,url) VALUES(?,?,?,?)";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, uid);
            stm.setString(2, title);
            stm.setString(3, content);
            stm.setString(4, url);
            stm.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
    }

    public ArrayList<Notification> listUnreadByUid(int uid, int limit){
        ArrayList<Notification> list = new ArrayList<>();
        String sql = "SELECT TOP (?) nid,uid,title,content,url,is_read,created_time "
                   + "FROM Notification WHERE uid=? ORDER BY created_time DESC";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, limit);
            stm.setInt(2, uid);
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                Notification n = new Notification();
                n.setId(rs.getInt("nid"));
                n.setUid(rs.getInt("uid"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                n.setUrl(rs.getString("url"));
                n.setRead(rs.getBoolean("is_read"));
                n.setCreatedTime(rs.getTimestamp("created_time"));
                list.add(n);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
        return list;
    }

    public void markRead(int nid){
        String sql = "UPDATE Notification SET is_read=1 WHERE nid=?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, nid);
            stm.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
        finally { closeConnection(); }
    }

    @Override public ArrayList<Notification> list(){ throw new UnsupportedOperationException(); }
    @Override public Notification get(int id){ throw new UnsupportedOperationException(); }
    @Override public void insert(Notification m){ throw new UnsupportedOperationException(); }
    @Override public void update(Notification m){ throw new UnsupportedOperationException(); }
    @Override public void delete(Notification m){ throw new UnsupportedOperationException(); }
}
