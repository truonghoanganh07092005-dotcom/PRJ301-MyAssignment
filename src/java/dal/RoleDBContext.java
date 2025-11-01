/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import model.iam.Role;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.iam.Feature;

/**
 *
 * @author sonnt
 */
public class RoleDBContext extends DBContext<Role> {

  // dal/RoleDBContext.java  (thay method cũ)
public ArrayList<Role> getByUserId(int uid) {
    ArrayList<Role> roles = new ArrayList<>();
    String sql = """
        SELECT r.rid, r.rname, f.fid, f.url
        FROM [User] u
        JOIN [UserRole]    ur ON u.uid = ur.uid
        JOIN [Role]         r ON r.rid = ur.rid
        JOIN [RoleFeature] rf ON rf.rid = r.rid
        JOIN [Feature]      f ON f.fid = rf.fid
        WHERE u.uid = ?
        ORDER BY r.rid, f.fid
    """;
    try (PreparedStatement stm = connection.prepareStatement(sql)) {
        stm.setInt(1, uid);
        try (ResultSet rs = stm.executeQuery()) {
            Role current = null;
            Integer lastRid = null;
            while (rs.next()) {
                int rid = rs.getInt("rid");
                if (lastRid == null || rid != lastRid) {
                    current = new Role();
                    current.setId(rid);                 // <-- PHẢI là rid
                    current.setName(rs.getString("rname"));
                    roles.add(current);
                    lastRid = rid;
                }
                Feature f = new Feature();
                f.setId(rs.getInt("fid"));
                f.setUrl(rs.getString("url"));
                current.getFeatures().add(f);
            }
        }
    } catch (SQLException ex) {
        Logger.getLogger(RoleDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        closeConnection();
    }
    return roles;
}
    @Override
    public ArrayList<Role> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Role get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void insert(Role model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Role model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Role model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
