package services;

import models.MessageM;
import util.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private Connection con;

    public MessageService() {
        con = MyDatabase.getInstance().getCon();
    }

    // Envoyer un message
    public void sendMessage(MessageM msg) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, dateM) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, msg.getSenderId());
            ps.setInt(2, msg.getReceiverId());
            ps.setString(3, msg.getContent());
            ps.setTimestamp(4, msg.getDateM());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
        }
    }

    public List<MessageM> getMessages(int user1, int user2) {
        String sql = "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY dateM ASC";
        List<MessageM> messages = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ps.setInt(3, user2);
            ps.setInt(4, user1);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MessageM msg = new MessageM();
                msg.setId(rs.getInt("id"));
                msg.setSenderId(rs.getInt("sender_id"));
                msg.setReceiverId(rs.getInt("receiver_id"));
                msg.setContent(rs.getString("content"));
                msg.setDateM(rs.getTimestamp("dateM"));

                messages.add(msg);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des messages : " + e.getMessage());
        }

        return messages;
    }
}
