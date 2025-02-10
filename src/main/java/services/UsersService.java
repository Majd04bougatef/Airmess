package services;
import java.sql.Date;
import interfaces.GlobalInterface;
import models.Users;
import util.MyDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersService implements GlobalInterface<Users> {

    private Connection con;
    public UsersService() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Users user) {
        String sql = "INSERT INTO users (name, prenom, email, password, roleUser, dateNaiss, phoneNumber, statut, diamond, deleteFlag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getRoleUser());
            preparedStatement.setDate(6, Date.valueOf(user.getDateNaiss()));
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getStatut());
            preparedStatement.setInt(9, user.getDiamond());
            preparedStatement.setInt(10, user.getDeleteFlag());
            preparedStatement.executeUpdate();
            System.out.println("User added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    @Override
    public void update(Users user) {
        String sql = "UPDATE users SET name = ?, prenom = ?, email = ?, password = ?, roleUser = ?, dateNaiss = ?, phoneNumber = ?, statut = ?, diamond = ?, deleteFlag = ? WHERE id_U = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getRoleUser());
            preparedStatement.setDate(6, Date.valueOf(user.getDateNaiss()));
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getStatut());
            preparedStatement.setInt(9, user.getDiamond());
            preparedStatement.setInt(10, user.getDeleteFlag());
            preparedStatement.setInt(11, user.getId_U());
            preparedStatement.executeUpdate();
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(Users t) {
        try {
            Connection con = MyDatabase.getInstance().getCon();
            String query = "DELETE FROM users WHERE id_U=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, t.getId_U());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Users> getAll() {
        List<Users> list = new ArrayList<>();
        try {
            Connection con = MyDatabase.getInstance().getCon();
            String query = "SELECT * FROM users";
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                Users user = new Users(
                        rs.getInt("id_U"),
                        rs.getString("name"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("roleUser"),
                        rs.getDate("dateNaiss").toLocalDate(),
                        rs.getString("phoneNumber"),
                        rs.getString("statut"),
                        rs.getInt("diamond"),
                        rs.getInt("deleteFlag")
                );
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Users> getById(int id) {
        List<Users> list = new ArrayList<>();
        try {
            Connection con = MyDatabase.getInstance().getCon();
            String query = "SELECT * FROM users WHERE id_U=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Users user = new Users(
                        rs.getInt("id_U"),
                        rs.getString("name"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("roleUser"),
                        rs.getDate("dateNaiss").toLocalDate(),
                        rs.getString("phoneNumber"),
                        rs.getString("statut"),
                        rs.getInt("diamond"),
                        rs.getInt("deleteFlag")
                );
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}