package services;

import interfaces.GlobalInterface;
import models.*;
import util.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class ReviewBonPlanServices implements GlobalInterface<ReviewBonplan> {

    private Connection con;

    public ReviewBonPlanServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(ReviewBonplan reviewBonplan) {
        int idP = reviewBonplan.getIdP();
        String checkIfBonplanExists = "SELECT idP FROM bonplan WHERE idP = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkIfBonplanExists)) {
            checkStmt.setInt(1, idP);

            var rs = checkStmt.executeQuery();
            if (rs.next()) {  // Si le bon plan existe dans la table bonplan

                String sql = "INSERT INTO reviewbonplan (id_U, idP, rating, commente, dateR) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

                    preparedStatement.setInt(1, reviewBonplan.getIdU());
                    preparedStatement.setInt(2, reviewBonplan.getIdP());
                    preparedStatement.setInt(3, reviewBonplan.getRating());
                    preparedStatement.setString(4, reviewBonplan.getCommente());
                    preparedStatement.setObject(5, reviewBonplan.getDateR());

                    preparedStatement.executeUpdate();
                    System.out.println("Review added successfully!");
                } catch (SQLException e) {
                    System.err.println("Error adding review: " + e.getMessage());
                }
            } else {
                System.err.println("Error: Bon plan with idP " + idP + " not found!");
            }
        } catch (SQLException e) {
            System.err.println("Error checking bonplan existence: " + e.getMessage());
        }
    }

    @Override
    public void update(ReviewBonplan reviewBonplan) {
        String sql = "UPDATE reviewbonplan SET  rating = ?, commente = ?, dateR = ? WHERE idR = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            // Assigner les valeurs aux paramètres

            preparedStatement.setInt(1, reviewBonplan.getRating());
            preparedStatement.setString(2, reviewBonplan.getCommente());
            preparedStatement.setObject(3, reviewBonplan.getDateR());
            preparedStatement.setInt(4, reviewBonplan.getIdR());

            preparedStatement.executeUpdate();
            System.out.println("Review updated successfully!");

        } catch (SQLException e) {
            System.err.println("Error updating Review: " + e.getMessage());
        }
    }

    @Override
    public void delete(ReviewBonplan reviewBonplan) {
        String sql = "DELETE FROM reviewbonplan WHERE idR = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            preparedStatement.setInt(1, reviewBonplan.getIdR());
            preparedStatement.executeUpdate();
            System.out.println(" deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting Review: " + e.getMessage());
        }
    }

    @Override
    public List<ReviewBonplan> getAll() {
        String query = "SELECT * FROM reviewbonplan";

        List<ReviewBonplan> rt = new ArrayList<>();

        try {
            Statement statement = con.createStatement();
            ResultSet bp = statement.executeQuery(query);
            while (bp.next()){
                ReviewBonplan r = new ReviewBonplan();

                r.setIdR(bp.getInt("idR"));
                r.setIdU(bp.getInt("id_U"));
                r.setIdP(bp.getInt("idP"));
                r.setRating(bp.getInt("rating"));
                r.setCommente(bp.getString("commente"));
                Timestamp timestamp = bp.getTimestamp("dateR");
                if (timestamp != null) {
                    r.setDateR(timestamp.toLocalDateTime());
                }

                rt.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rt;
    }

    @Override
    public ReviewBonplan getById(int id) {
        String query = "SELECT * FROM reviewbonplan WHERE idR = ?";
         ReviewBonplan b = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();


            if (rs.next()) {
                b = new ReviewBonplan();

                b.setIdP(id);
               b.setIdU(rs.getInt("id_U"));
               b.setIdP(rs.getInt("idP"));
               b.setRating(rs.getInt("rating"));
               b.setCommente(rs.getString("commente"));
                Timestamp timestamp = rs.getTimestamp("dateR");
                if (timestamp != null) {
                    b.setDateR(timestamp.toLocalDateTime());
                }



            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return b;
    }

}