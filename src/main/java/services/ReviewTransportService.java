package services;

import interfaces.GlobalInterface;
import models.*;
import util.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ReviewTransportService implements GlobalInterface<review_transport> {

    Connection con ;

    public ReviewTransportService(){
        con = MyDatabase.getInstance().getCon();

    }

    @Override
    public void add(review_transport reviewTransport) {
        String sql = "INSERT INTO `review_transport`(`id_U`, `idS`, `rating`, `commentt`, `date_RT`) VALUES (?,?,?,?,?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, reviewTransport.getIdU());
            preparedStatement.setInt(2, reviewTransport.getIdS());
            preparedStatement.setInt(3, reviewTransport.getRating());
            preparedStatement.setString(4, reviewTransport.getCommentt());
            preparedStatement.setTimestamp(5, reviewTransport.getDate_RT());


            preparedStatement.executeUpdate();
            System.out.println("Review added successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(review_transport reviewTransport) {
        String sql = "UPDATE review_transport SET rating=?, commentt=? WHERE id = ? ";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, reviewTransport.getRating());
            preparedStatement.setString(2, reviewTransport.getCommentt());
            preparedStatement.setInt(3, reviewTransport.getId());
            preparedStatement.executeUpdate();
            System.out.println("review updated successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(review_transport reviewTransport) {
        String sql= "DELETE FROM `review_transport` WHERE id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, reviewTransport.getId());
            preparedStatement.executeUpdate();
            System.out.println("review deleted successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<review_transport> getAll() {
        String query = "SELECT * FROM `review_transport`";

        List<review_transport> rt = new ArrayList<>();

        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                review_transport r = new review_transport();

                r.setId(rs.getInt("id"));
                r.setIdS(rs.getInt("idS"));
                r.setIdU(rs.getInt("id_U"));
                r.setRating(rs.getInt("rating"));
                r.setCommentt(rs.getString("commentt"));
                r.setDate_RT(rs.getTimestamp("date_RT"));
                rt.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rt;
    }

    @Override
    public review_transport getById(int id) {
        String query = "SELECT * FROM `review_transport` WHERE id = ?";
        review_transport rt = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();


            if (rs.next()) {
                rt = new review_transport();

                rt.setId(id);
                rt.setIdS(rs.getInt("idS"));
                rt.setIdU(rs.getInt("id_U"));
                rt.setRating(rs.getInt("rating"));
                rt.setCommentt(rs.getString("commentt"));
                rt.setDate_RT(rs.getTimestamp("date_RT"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return rt;
    }
}
