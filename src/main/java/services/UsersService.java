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
        // Updated SQL query without password field
        String sql = "UPDATE users SET "
                + "name = ?, prenom = ?, email = ?, roleUser = ?, "
                + "dateNaiss = ?, phoneNumber = ?, statut = ?, "
                + "diamond = ?, deleteFlag = ?, imagesU = ? "
                + "WHERE id_U = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            // Set parameters for the updated fields
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRoleUser());
            preparedStatement.setDate(5, user.getDateNaiss() != null ?
                    Date.valueOf(user.getDateNaiss()) : null);
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setString(7, user.getStatut());
            preparedStatement.setInt(8, user.getDiamond());
            preparedStatement.setInt(9, user.getDeleteFlag());
            preparedStatement.setString(10, user.getImagesU());
            preparedStatement.setInt(11, user.getId_U());

            preparedStatement.executeUpdate();
            System.out.println("User updated successfully (password unchanged)!");
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
        String query = "SELECT * FROM users WHERE email = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int deleteFlag = rs.getInt("deleteFlag");
                String hashedPassword = rs.getString("password");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                if (encoder.matches(password, hashedPassword)) {
                    if (deleteFlag == 1) {
                        // Reactivate user
                        String reactivateQuery = "UPDATE users SET deleteFlag = 0 WHERE email = ?";
                        PreparedStatement updateStmt = con.prepareStatement(reactivateQuery);
                        updateStmt.setString(1, email);
                        updateStmt.executeUpdate();
                        updateStmt.close();
                        System.out.println("User account reactivated: " + email);
                    }

                    Users user = new Users();
                    user.setId_U(rs.getInt("id_U"));
                    user.setName(rs.getString("name"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(null);
                    user.setRoleUser(rs.getString("roleUser"));

                    java.sql.Date dateNaiss = rs.getDate("dateNaiss");
                    if (dateNaiss != null) {
                        user.setDateNaiss(dateNaiss.toLocalDate());
                    }

                    user.setPhoneNumber(rs.getString("phoneNumber"));
                    user.setStatut(rs.getString("statut"));
                    user.setDiamond(rs.getInt("diamond"));
                    user.setDeleteFlag(0); // Ensure deleteFlag is reset
                    user.setImagesU(rs.getString("imagesU"));

                    System.out.println("Successful login for user: " + email);

                    rs.close();
                    pstmt.close();
                    return user;
                } else {
                    System.err.println("Invalid password for user: " + email);
                    rs.close();
                    pstmt.close();
                    return null;
                }
            } else {
                System.err.println("No user found with email: " + email);
                rs.close();
                pstmt.close();
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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


    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }
    public boolean verifyPassword(Users user, String currentPassword) {
        String sql = "SELECT password FROM users WHERE id_U = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId_U());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    return encoder.matches(currentPassword, hashedPassword);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying password: " + e.getMessage());
        }

        return false;
    }
    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(newPassword);

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setString(2, email);
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }



    public void updatepassword(Users user) {
        String sql = "UPDATE users SET password = ? WHERE id_U = ?";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = user.getPassword() != null ? encoder.encode(user.getPassword()) : null;
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            if (hashedPassword != null) {
                preparedStatement.setString(1, hashedPassword);
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            preparedStatement.setInt(2, user.getId_U());
            preparedStatement.executeUpdate();
            System.out.println("User password updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating user password: " + e.getMessage());
        }
    }

    // Add the new method to fetch user by email
    public Users getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Users(
                            rs.getInt("id_U"),
                            rs.getString("name"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            null, // password is null as you are not retrieving it here
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
            System.err.println("Error retrieving user by email: " + e.getMessage());
        }
        return null;
    }
}