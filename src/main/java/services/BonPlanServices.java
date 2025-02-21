package services;

import interfaces.GlobalInterface;
import models.*;
import util.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class BonPlanServices implements GlobalInterface<bonplan> {

    private Connection con;

    public BonPlanServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(bonplan bonplan) {
        String sql = "INSERT INTO bonplan (id_U, idP, nomplace, localisation, description, typePlace , imageBP) VALUES (?, ?, ?, ?, ?, ? ,?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, bonplan.getId_U());
            preparedStatement.setInt(2, bonplan.getIdP());
            preparedStatement.setString(3, bonplan.getNomplace());
            preparedStatement.setString(4, bonplan.getLocalisation());
            preparedStatement.setString(5, bonplan.getDescription());
            preparedStatement.setString(6, bonplan.getTypePlace());
            preparedStatement.setString(7, bonplan.getImageBP());

            preparedStatement.executeUpdate();
            System.out.println("Bon Plan added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding Bon Plan: " + e.getMessage());
        }
    }

    @Override
    public void update(bonplan bonPlan) {
        String sql = "UPDATE bonplan SET  nomplace = ?, localisation = ?, description = ?, typePlace = ?,imageBP = ? WHERE idP = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, bonPlan.getNomplace());
            preparedStatement.setString(2, bonPlan.getLocalisation());
            preparedStatement.setString(3, bonPlan.getDescription());
            preparedStatement.setString(4, bonPlan.getTypePlace());
            preparedStatement.setString(5, bonPlan.getImageBP());
            preparedStatement.setInt(6, bonPlan.getIdP());  // idP est utilisé comme identifiant unique ici

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {// bon plan modifiée
                System.out.println("Bon Plan updated successfully!");
            } else {
                System.out.println("No Bon Plan found with idP: " + bonPlan.getIdP());
            }
        } catch (SQLException e) {
            System.err.println("Error updating Bon Plan: " + e.getMessage());
        }
    }

    @Override
    public void delete(bonplan bonPlan) {
        String sql = "DELETE FROM bonplan WHERE idP = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, bonPlan.getIdP());
            preparedStatement.executeUpdate();
            System.out.println(" deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting Bon Plan: " + e.getMessage());
        }
    }

    @Override
    public List<bonplan> getAll() {
            String query = "SELECT * FROM bonplan";

            List<bonplan> rt = new ArrayList<>();

            try {
                Statement statement = con.createStatement();
                ResultSet bp = statement.executeQuery(query);
                while (bp.next()){
                    bonplan r = new bonplan();

                    r.setIdP(bp.getInt("idP"));
                    r.setId_U(bp.getInt("id_U"));
                    r.setNomplace(bp.getString("nomplace"));
                    r.setLocalisation(bp.getString("localisation"));
                    r.setDescription(bp.getString("description"));
                    r.setTypePlace(bp.getString("typePlace"));
                    r.setImageBP(bp.getString("imageBP"));

                    rt.add(r);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return rt;

    }

    @Override
    public bonplan getById(int id) {
        String query = "SELECT * FROM bonplan WHERE idP = ?";
        bonplan b = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();


            if (rs.next()) {
                b = new bonplan();

                b.setIdP(id);
                b.setId_U(rs.getInt("id_U"));
                b.setNomplace(rs.getString("nomplace"));
                b.setLocalisation(rs.getString("localisation"));
                b.setDescription(rs.getString("description"));
                b.setTypePlace(rs.getString("typePlace"));
                b.setImageBP(rs.getString("imageBP"));

            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return b;
    }
    public boolean existsByName(String nom) {
        String query = "SELECT COUNT(*) FROM bonplan WHERE nomplace = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}



