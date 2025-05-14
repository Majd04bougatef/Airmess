package services;

import interfaces.GlobalInterface;
import models.Offre;
import test.Session;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OffreService implements GlobalInterface<Offre> {
    Connection connection;

    public OffreService() {
        connection = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Offre offre) {
        String query = "INSERT INTO offre (id_U, price_init, price_after, start_date, end_date, number_limit, description, place, image_path, aidesc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Session.getInstance().getId_U());
            preparedStatement.setDouble(2, offre.getPriceInit());
            preparedStatement.setDouble(3, offre.getPriceAfter());
            preparedStatement.setString(4, offre.getStartDate());
            preparedStatement.setString(5, offre.getEndDate());
            preparedStatement.setInt(6, offre.getNumberLimit());
            preparedStatement.setString(7, offre.getDescription());
            preparedStatement.setString(8, offre.getPlace());
            preparedStatement.setString(9, offre.getImage());
            preparedStatement.setString(10, offre.getAiDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Offre offre) {
        // TODO Auto-generated method stub
        String query = "UPDATE offre SET id_U = ?, price_init = ?, price_after = ?, start_date = ?, end_date = ?, number_limit = ?, description = ?, place = ?, image_path = ? WHERE idO = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, offre.getId_U());
            preparedStatement.setDouble(2, offre.getPriceInit());
            preparedStatement.setDouble(3, offre.getPriceAfter());
            preparedStatement.setString(4, offre.getStartDate());
            preparedStatement.setString(5, offre.getEndDate());
            preparedStatement.setInt(6, offre.getNumberLimit());
            preparedStatement.setString(7, offre.getDescription());
            preparedStatement.setString(8, offre.getPlace());
            preparedStatement.setString(9, offre.getImage());
            preparedStatement.setInt(10, offre.getIdO());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Offre offre) {
        // TODO Auto-generated method stub
        String query = "DELETE FROM offre WHERE idO = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, offre.getIdO());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Offre> getAll() {
        String query = "SELECT * FROM offre";
        List<Offre> offres = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Offre offre = new Offre();
                offre.setIdO(resultSet.getInt("idO"));
                offre.setId_U(resultSet.getInt("id_U"));
                offre.setPriceInit(resultSet.getDouble("price_init"));
                offre.setPriceAfter(resultSet.getDouble("price_after"));
                offre.setStartDate(resultSet.getString("start_date"));
                offre.setEndDate(resultSet.getString("end_date"));
                offre.setNumberLimit(resultSet.getInt("number_limit"));
                offre.setDescription(resultSet.getString("description"));
                offre.setPlace(resultSet.getString("place"));
                offre.setImage(resultSet.getString("image_path"));
                offre.setAiDescription(resultSet.getString("aidesc"));
                offres.add(offre);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return offres;
    }

    @Override
    public Offre getById(int id) {
        String query = "SELECT * FROM offre WHERE idO = ?";
        Offre offre = new Offre();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                offre.setIdO(resultSet.getInt("idO"));
                offre.setId_U(resultSet.getInt("id_U"));
                offre.setPriceInit(resultSet.getDouble("price_init"));
                offre.setPriceAfter(resultSet.getDouble("price_after"));
                offre.setStartDate(resultSet.getString("start_date"));
                offre.setEndDate(resultSet.getString("end_date"));
                offre.setNumberLimit(resultSet.getInt("number_limit"));
                offre.setDescription(resultSet.getString("description"));
                offre.setPlace(resultSet.getString("place"));
                offre.setImage(resultSet.getString("image_path"));
                offre.setAiDescription(resultSet.getString("aidesc"));

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return offre;
    }

}
