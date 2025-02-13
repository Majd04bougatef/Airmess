package services;

import models.ReviewBonplan;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;


public class ReviewBonPlanServices {

    private Connection con;

    public ReviewBonPlanServices() {
        con = MyDatabase.getInstance().getCon();
    }

    public void add(ReviewBonplan review) {
        // Fixer un id_U fictif (par exemple 1 si la table users est vide et que tu n'as pas encore de données utilisateurs)
        int fixedIdU = 1;

        int idP = review.getIdP();

        String checkIfBonplanExists = "SELECT idP FROM bonplan WHERE idP = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkIfBonplanExists)) {
            checkStmt.setInt(1, idP);

            var rs = checkStmt.executeQuery();
            if (rs.next()) {  // Si le bon plan existe dans la table bonplan

                String sql = "INSERT INTO reviewbonplan (id_U, idP, rating, commente, dateR) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

                    preparedStatement.setInt(1, fixedIdU);
                    preparedStatement.setInt(2, idP);
                    preparedStatement.setInt(3, review.getRating());
                    preparedStatement.setString(4, review.getCommente());
                    preparedStatement.setObject(5, review.getDateR());

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
    public void update(ReviewBonplan reviewBonplan) {
        String sql = "UPDATE reviewbonplan SET id_U = ?, idP = ?, rating = ?, commente = ?, dateR = ? WHERE idR = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            // Assigner les valeurs aux paramètres
            preparedStatement.setInt(1, reviewBonplan.getIdU());
            preparedStatement.setInt(2, reviewBonplan.getIdP());
            preparedStatement.setInt(3, reviewBonplan.getRating());
            preparedStatement.setString(4, reviewBonplan.getCommente());
            preparedStatement.setObject(5, reviewBonplan.getDateR());
            preparedStatement.setInt(6, reviewBonplan.getIdR());


            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Review updated successfully!");
            } else {
                System.out.println("No Review found with idR: " + reviewBonplan.getIdR());
            }
        } catch (SQLException e) {
            System.err.println("Error updating Review: " + e.getMessage());
        }
    }
    public void delete(int idR) {
        String sql = "DELETE FROM reviewbonplan WHERE idR = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            preparedStatement.setInt(1, idR);

            // la requête de suppression
            preparedStatement.executeUpdate();
            System.out.println("Review with idR " + idR + " deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting Review: " + e.getMessage());
        }
    }


}