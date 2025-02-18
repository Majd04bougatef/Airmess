package services;

import java.sql.*;

import interfaces.GlobalInterface;
import models.Users;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import util.MyDatabase;


public class UsersService implements GlobalInterface<Users> {

    private Connection con;

    public UsersService() {
        con = MyDatabase.getInstance().getCon();
    }





    @Override
    public void add(Users user) {
        String sql = "INSERT INTO users (name, prenom, email, password, roleUser, dateNaiss, phoneNumber, statut, diamond, deleteFlag, imagesU) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.getPassword());
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setString(5, user.getRoleUser());
            preparedStatement.setDate(6, user.getDateNaiss() != null ? Date.valueOf(user.getDateNaiss()) : null);
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getStatut());
            preparedStatement.setInt(9, user.getDiamond());
            preparedStatement.setInt(10, user.getDeleteFlag());
            preparedStatement.setString(11, user.getImagesU());
            preparedStatement.executeUpdate();
            System.out.println("User added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    @Override
    public void update(Users user) {
        String sql = "UPDATE users SET name = ?, prenom = ?, email = ?, password = ?, roleUser = ?, dateNaiss = ?, phoneNumber = ?, statut = ?, diamond = ?, deleteFlag = ?, imagesU = ? WHERE id_U = ?";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = user.getPassword() != null ? encoder.encode(user.getPassword()) : null;
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getEmail());
            if (hashedPassword != null) {
                preparedStatement.setString(4, hashedPassword);
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            preparedStatement.setString(5, user.getRoleUser());
            preparedStatement.setDate(6, user.getDateNaiss() != null ? Date.valueOf(user.getDateNaiss()) : null);
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getStatut());
            preparedStatement.setInt(9, user.getDiamond());
            preparedStatement.setInt(10, user.getDeleteFlag());
            preparedStatement.setString(11, user.getImagesU());
            preparedStatement.setInt(12, user.getId_U());
            preparedStatement.executeUpdate();
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(Users user) {
        String query = "DELETE FROM users WHERE id_U=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, user.getId_U());
            ps.executeUpdate();
            System.out.println("User deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<Users> getAll() {
        List<Users> list = new ArrayList<>();
        String query = "SELECT id_U, name, prenom, email, roleUser, dateNaiss, phoneNumber, statut, diamond, deleteFlag, imagesU FROM users";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Users user = new Users(
                        rs.getInt("id_U"),
                        rs.getString("name"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        null,
                        rs.getString("roleUser"),
                        rs.getDate("dateNaiss") != null ? rs.getDate("dateNaiss").toLocalDate() : null,
                        rs.getString("phoneNumber"),
                        rs.getString("statut"),
                        rs.getInt("diamond"),
                        rs.getInt("deleteFlag"),
                        rs.getString("imagesU")
                );
                list.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Users getById(int id) {
        String query = "SELECT * FROM users WHERE id_U=?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Users(
                            rs.getInt("id_U"),
                            rs.getString("name"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            null,
                            rs.getString("roleUser"),
                            rs.getDate("dateNaiss") != null ? rs.getDate("dateNaiss").toLocalDate() : null,
                            rs.getString("phoneNumber"),
                            rs.getString("statut"),
                            rs.getInt("diamond"),
                            rs.getInt("deleteFlag"),
                            rs.getString("imagesU")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
        }
        return null;
    }

    public Users login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    if (encoder.matches(password, hashedPassword)) {
                        return new Users(
                                rs.getInt("id_U"),
                                rs.getString("name"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                null,
                                rs.getString("roleUser"),
                                rs.getDate("dateNaiss") != null ? rs.getDate("dateNaiss").toLocalDate() : null,
                                rs.getString("phoneNumber"),
                                rs.getString("statut"),
                                rs.getInt("diamond"),
                                rs.getInt("deleteFlag"),
                                rs.getString("imagesU")
                        );
                    } else {
                        System.err.println("Invalid password.");
                    }
                } else {
                    System.err.println("Invalid email.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return null;
    }

    public int isTaken(String column, String value) {
        String sql = "SELECT COUNT(*) AS count FROM users WHERE " + column + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking uniqueness for " + column + ": " + e.getMessage());
        }
        return 0;
    }

}