package services;

import interfaces.GlobalInterface;
import models.*;
import util.MyDatabase;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class ReservationTransportService implements GlobalInterface<reservation_transport> {

    Connection con ;

    public ReservationTransportService(){
        con = MyDatabase.getInstance().getCon();

    }

    @Override
    public void add(reservation_transport rt) {
        String sql = "INSERT INTO `reservation_transport`(`id_U`, `idS`,`reference`, `dateRes`, `dateFin`, `prix`,`nombreVelo`, `statut`) VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, rt.getIdU());
            preparedStatement.setInt(2, rt.getIdS());
            preparedStatement.setString(3, rt.getReference());
            preparedStatement.setTimestamp(4, rt.getDateRes());
            preparedStatement.setTimestamp(5, rt.getDateFin());
            preparedStatement.setDouble(6, rt.getPrix());
            preparedStatement.setDouble(7, rt.getNombreVelo());
            preparedStatement.setString(8, rt.getStatut());

            preparedStatement.executeUpdate();
            System.out.println("Reservation added successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(reservation_transport reservationTransport) {
        String sql = "UPDATE reservation_transport SET dateFin=?, prix=?, statut = 'terminée' WHERE id = ? AND statut='en cours'";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, reservationTransport.getDateFin());
            preparedStatement.setDouble(2, reservationTransport.getPrix());
            preparedStatement.setInt(3, reservationTransport.getId());
            preparedStatement.executeUpdate();
            System.out.println("reseration updated successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(reservation_transport reservationTransport) {
        String sql= "DELETE FROM `reservation_transport` WHERE id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationTransport.getId());
            preparedStatement.executeUpdate();
            System.out.println("reservation deleted successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<reservation_transport> getAll() {
        String query = "SELECT * FROM `reservation_transport`";

        List<reservation_transport> rt = new ArrayList<>();

        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                reservation_transport r = new reservation_transport();

                r.setId(rs.getInt("id"));
                r.setIdS(rs.getInt("idS"));
                r.setIdU(rs.getInt("id_U"));
                r.setDateRes(rs.getTimestamp("dateRes"));
                r.setDateFin(rs.getTimestamp("dateFin"));
                r.setPrix(rs.getDouble("prix"));
                r.setStatut(rs.getString("statut"));
                r.setNombreVelo(rs.getInt("nombreVelo"));
                r.setReference(rs.getString("reference"));
                rt.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rt;
    }

    @Override
    public reservation_transport getById(int id) {
        String query = "SELECT * FROM `reservation_transport` WHERE id = ?";
        reservation_transport rt = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();


            if (rs.next()) {
                rt = new reservation_transport();

                rt.setId(id);
                rt.setIdS(rs.getInt("idS"));
                rt.setIdU(rs.getInt("id_U"));
                rt.setDateRes(rs.getTimestamp("dateRes"));
                rt.setDateFin(rs.getTimestamp("dateFin"));
                rt.setPrix(rs.getDouble("prix"));
                rt.setStatut(rs.getString("statut"));
                rt.setNombreVelo(rs.getInt("nombreVelo"));
                rt.setReference(rs.getString("reference"));

            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return rt;
    }

    public reservation_transport CalculPrix (reservation_transport rs){

        String query = "SELECT prixHeure FROM `station`,`reservation_transport` WHERE station.idS = reservation_transport.idS AND id = ?";
        reservation_transport rt = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, rs.getId());

            ResultSet r = preparedStatement.executeQuery();


            if (r.next()) {
                double prixHeure=r.getDouble("prixHeure");
                Timestamp dateFin = Timestamp.from(Instant.now());
                rs.setDateFin(dateFin);

                Timestamp dateDebut = rs.getDateRes();

                long differenceInHours = ChronoUnit.HOURS.between(dateDebut.toInstant(), dateFin.toInstant());
                long differenceInMinutes = ChronoUnit.MINUTES.between(dateDebut.toInstant(), dateFin.toInstant()) % 60;

                double totalTime = differenceInHours + (differenceInMinutes / 60.0);
                rs.setPrix((differenceInHours + (differenceInMinutes / 60.0))*prixHeure);

            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return rs;
    }


    public void deletee(int id) {
        String sql= "DELETE FROM `reservation_transport` WHERE id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("reservation deleted successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
