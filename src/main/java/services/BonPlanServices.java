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
}
