package services;

import models.SocialMedia;
import util.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocialMediaServices {

    private Connection con;

    public SocialMediaServices() {
        con = MyDatabase.getInstance().getCon();
    }

    // add
    public void add(SocialMedia socialMedia) {
        String sql = "INSERT INTO socialmedia (titre, contenu, id_U, category, publicationDate, lieu, `like`, dislike) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, socialMedia.getTitre());
            preparedStatement.setString(2, socialMedia.getContenu());
            preparedStatement.setInt(3, socialMedia.getId_U());
            preparedStatement.setString(4, socialMedia.getCategory().toString());
            preparedStatement.setDate(5, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
            preparedStatement.setString(6, socialMedia.getLieu());
            preparedStatement.setInt(7, socialMedia.getLike());
            preparedStatement.setInt(8, socialMedia.getDislike());
            preparedStatement.executeUpdate();
            System.out.println("Publication ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    //update
    public void update(SocialMedia socialMedia) {
        String sql = "UPDATE socialmedia SET titre = ?, contenu = ?, id_U = ?, category = ?, publicationDate = ?, lieu = ?, `like` = ?, dislike = ? WHERE idEB = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, socialMedia.getTitre());
            preparedStatement.setString(2, socialMedia.getContenu());
            preparedStatement.setInt(3, socialMedia.getId_U());
            preparedStatement.setString(4, socialMedia.getCategory().toString());
            preparedStatement.setDate(5, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
            preparedStatement.setString(6, socialMedia.getLieu());
            preparedStatement.setInt(7, socialMedia.getLike());
            preparedStatement.setInt(8, socialMedia.getDislike());
            preparedStatement.setInt(9, socialMedia.getIdEB());
            preparedStatement.executeUpdate();
            System.out.println("Publication mise à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    // delete
    public void delete(int id) {
        String sql = "DELETE FROM socialmedia WHERE idEB = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Publication supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // Afficher
    public List<SocialMedia> display() {
        String query = "SELECT * FROM socialmedia";
        List<SocialMedia> socialMedias = new ArrayList<>();

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                SocialMedia socialMedia = new SocialMedia();
                socialMedia.setIdEB(rs.getInt("idEB"));
                socialMedia.setTitre(rs.getString("titre"));
                socialMedia.setContenu(rs.getString("contenu"));
                socialMedia.setId_U(rs.getInt("id_U"));
               // socialMedia.setCategory(TypeEB.valueOf(rs.getString("category").toUpperCase()));
                socialMedia.setPublicationDate(rs.getDate("publicationDate"));
                socialMedia.setLieu(rs.getString("lieu"));
                socialMedia.setLike(rs.getInt("like"));
                socialMedia.setDislike(rs.getInt("dislike"));
                socialMedias.add(socialMedia);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des publications : " + e.getMessage());
        }

        return socialMedias;
    }
}
