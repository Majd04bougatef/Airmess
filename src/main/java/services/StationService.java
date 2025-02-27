package services;

import interfaces.GlobalInterface;
import models.*;
import util.MyDatabase;

import java.sql.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class StationService implements GlobalInterface<station> {

    Connection con ;

    public StationService(){
        con = MyDatabase.getInstance().getCon();

    }

    @Override
    public void add(station st) {
        String sql = "INSERT INTO station ( id_U, nom, latitude, longitude, capacite, nombreVelo, typeVelo, prixHeure,pays) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, st.getIdU());
            preparedStatement.setString(2, st.getNom());
            preparedStatement.setDouble(3, st.getLatitude());
            preparedStatement.setDouble(4, st.getLongitude());
            preparedStatement.setInt(5, st.getCapacite());
            preparedStatement.setInt(6, st.getNbVelo());
            preparedStatement.setString(7, st.getTypeVelo());
            preparedStatement.setDouble(8, st.getPrixheure());
            preparedStatement.setString(9, st.getPays());

            preparedStatement.executeUpdate();
            System.out.println("Station added successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(station station) {
        String sql= "DELETE FROM `station` WHERE idS = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, station.getIdS());
            preparedStatement.executeUpdate();
            System.out.println("Station deleted successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(station station) {
        String sql= "UPDATE station SET capacite=? , nombreVelo=? , typeVelo =? , prixHeure=?  WHERE idS =?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, station.getCapacite());
            preparedStatement.setInt(2, station.getNbVelo());
            preparedStatement.setString(3, station.getTypeVelo());
            preparedStatement.setDouble(4, station.getPrixheure());
            preparedStatement.setInt(5, station.getIdS());
            preparedStatement.executeUpdate();
            System.out.println("Station updated successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<station> getAll() {
        String query = "SELECT * FROM `station`";

        List<station> st = new ArrayList<>();

        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                station s = new station();

                s.setIdS(rs.getInt("idS"));
                s.setIdU(rs.getInt("id_U"));
                s.setNom(rs.getString("nom"));
                s.setLatitude(rs.getDouble("latitude"));
                s.setLongitude(rs.getDouble("longitude"));
                s.setCapacite(rs.getInt("capacite"));
                s.setNbVelo(rs.getInt("nombreVelo"));
                s.setTypeVelo(rs.getString("typeVelo"));
                s.setPrixheure(rs.getDouble("prixHeure"));
                s.setPays(rs.getString("pays"));
                st.add(s);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return st;

    }

    @Override
    public station getById(int id) {
        String query = "SELECT * FROM `station` WHERE idS = ?";
        station st = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();


            if (rs.next()) {
                st = new station();

                st.setIdS(id);
                st.setIdU(rs.getInt("id_U"));
                st.setNom(rs.getString("nom"));
                st.setLatitude(rs.getDouble("latitude"));
                st.setLongitude(rs.getDouble("longitude"));
                st.setCapacite(rs.getInt("capacite"));
                st.setNbVelo(rs.getInt("nombreVelo"));
                st.setTypeVelo(rs.getString("typeVelo"));
                st.setPrixheure(rs.getDouble("prixHeure"));
                st.setPays(rs.getString("pays"));


            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return st;
    }

    public void updateNombreVelo(int stationId, int nbVeloRes) {
        String sql = "UPDATE station SET nombreVelo = nombreVelo - ? WHERE idS = ? AND nombreVelo >= ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, nbVeloRes);
            preparedStatement.setInt(2, stationId);
            preparedStatement.setInt(3, nbVeloRes);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Le nombre de vélos a été mis à jour avec succès.");
            } else {
                System.out.println("Erreur lors de la mise à jour du nombre de vélos disponibles.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }

    public void updateRetourVelo(int stationId, int nbVeloRes) {
        String sql = "UPDATE station SET nombreVelo = nombreVelo + ? WHERE idS = ? ";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, nbVeloRes);
            preparedStatement.setInt(2, stationId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Le nombre de vélos a été mis à jour avec succès.");
            } else {
                System.out.println("Erreur lors de la mise à jour du nombre de vélos disponibles.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }

}
