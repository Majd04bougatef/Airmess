package services;

import models.bonplan;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BonPlanServices {

    private Connection con;

    public BonPlanServices() {
        con = MyDatabase.getInstance().getCon();
    }

    public void add(bonplan bonPlan) {
        String sql = "INSERT INTO bonplan (id_U, idP, nomplace, localisation, description, typePlace) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, bonPlan.getId_U());
            preparedStatement.setInt(2, bonPlan.getIdP());
            preparedStatement.setString(3, bonPlan.getNomplace());
            preparedStatement.setString(4, bonPlan.getLocalisation());
            preparedStatement.setString(5, bonPlan.getDescription());
            preparedStatement.setString(6, bonPlan.getTypePlace());

            preparedStatement.executeUpdate();
            System.out.println("Bon Plan added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding Bon Plan: " + e.getMessage());
        }
    }

    public void update(bonplan bonPlan) {
        String sql = "UPDATE bonplan SET id_U = ?, nomplace = ?, localisation = ?, description = ?, typePlace = ? WHERE idP = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, bonPlan.getId_U());
            preparedStatement.setString(2, bonPlan.getNomplace());
            preparedStatement.setString(3, bonPlan.getLocalisation());
            preparedStatement.setString(4, bonPlan.getDescription());
            preparedStatement.setString(5, bonPlan.getTypePlace());
            preparedStatement.setInt(6, bonPlan.getIdP());  // idP est utilisé comme identifiant unique ici

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Bon Plan updated successfully!");
            } else {
                System.out.println("No Bon Plan found with idP: " + bonPlan.getIdP());
            }
        } catch (SQLException e) {
            System.err.println("Error updating Bon Plan: " + e.getMessage());
        }
    }
    public void delete(int idP) {
        String sql = "DELETE FROM bonplan WHERE idP = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, idP);
            preparedStatement.executeUpdate();
            System.out.println("Bon Plan with idP " + idP + " deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting Bon Plan: " + e.getMessage());
        }
    }

}



