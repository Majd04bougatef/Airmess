package services;

import interfaces.GlobalInterface;
import models.Offre;
import models.Reservation;
import util.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements GlobalInterface<Reservation> {
    Connection connection;

    public ReservationService() {
        connection = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Reservation reservation) {
        // Add reservation to database
        String query = "INSERT INTO reservation (id_U, idO, date_res, mode_paiement) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, reservation.getId_U());
            preparedStatement.setInt(2, reservation.getIdO().getIdO());
            preparedStatement.setString(3, reservation.getDateRes());
            preparedStatement.setString(4, reservation.getModePaiement());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Reservation reservation) {
        // Update reservation in database
        String query = "UPDATE reservation SET id_U = ?, idO = ?, date_res = ?, mode_paiement = ? WHERE idR = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, reservation.getId_U());
            preparedStatement.setInt(2, reservation.getIdO().getIdO());
            preparedStatement.setString(3, reservation.getDateRes());
            preparedStatement.setString(4, reservation.getModePaiement());
            preparedStatement.setInt(5, reservation.getIdR());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Reservation reservation) {
        // Delete reservation from database
        String query = "DELETE FROM reservation WHERE idR = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, reservation.getIdR());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reservation> getAll() {
        // Get all reservations from database
        String query = "SELECT * FROM reservation";
        List<Reservation> reservations = new ArrayList<>();
        try {
            System.out.println("tru ID: ");

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet res = preparedStatement.executeQuery();
            System.out.println("tru ID 2: ");
            while (res.next()) {
                System.out.println("tru ID 3: ");
                Reservation reservation = new Reservation();
                reservation.setIdR(res.getInt("idR"));
                reservation.setId_U(res.getInt("id_U"));
                Offre offre = new Offre();
                offre.setIdO(res.getInt("idO"));
                reservation.setIdO(offre);
                reservation.setDateRes(res.getString("date_res"));
                reservation.setModePaiement(res.getString("mode_paiement"));

                reservations.add(reservation);
                System.out.println("Reservation IDdddddd: " + reservation.getIdR());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        return reservations;
    }

    @Override
    public Reservation getById(int id) {
        // Get reservation by id from database
        String query = "SELECT * FROM reservation WHERE idR = ?";
        Reservation reservation = new Reservation();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet res = preparedStatement.executeQuery();
            if (res.next()) {
                reservation.setIdR(res.getInt("idR"));
                reservation.setId_U(res.getInt("id_U"));
                Offre offre = new Offre();
                offre.setIdO(res.getInt("idO"));
                reservation.setIdO(offre);
                reservation.setDateRes(res.getString("date_res"));
                reservation.setModePaiement(res.getString("mode_paiement"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return reservation;
    }
}
