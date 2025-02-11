package services;

import interfaces.GlobalInterface;
import models.*;
import util.MyDatabase;

import java.sql.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class StationService implements GlobalInterface<station> {

    Connection con ;

    public StationService(){
        con = MyDatabase.getInstance().getCon();

    }

    @Override
    public void add(station st) {
        String sql = "INSERT INTO station ( id_U, nom, latitude, longitude, capacite, nombreVelo, typeVelo, prixHeure) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, st.getIdU());
            preparedStatement.setString(2, st.getNom());
            preparedStatement.setDouble(3, st.getLatitude());
            preparedStatement.setDouble(4, st.getLongitude());
            preparedStatement.setInt(5, st.getCapacite());
            preparedStatement.setInt(6, st.getNbVelo());
            preparedStatement.setString(7, st.getTypeVelo());
            preparedStatement.setDouble(8, st.getPrixheure());

            preparedStatement.executeUpdate();
            System.out.println("Station added successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(station station) {

    }

    @Override
    public void update(station station) {

    }

    @Override
    public List<station> getAll() {
        return List.of();
    }

    @Override
    public List<station> getById(int id) {
        return List.of();
    }
}
