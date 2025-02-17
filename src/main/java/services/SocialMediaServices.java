package services;

import models.SocialMedia;
import interfaces.GlobalInterface;
import util.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SocialMediaServices implements GlobalInterface<SocialMedia> {
    Connection con;

    public SocialMediaServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(SocialMedia socialMedia) {
        String sql = "INSERT INTO socialmedia (titre, contenu, id_U, category, publicationDate, lieu, `like`, dislike) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, socialMedia.getTitre());
            preparedStatement.setString(2, socialMedia.getContenu());
            preparedStatement.setInt(3, socialMedia.getId_U());
            preparedStatement.setString(4, socialMedia.getCategory().name()); // `.name()` pour convertir l'Enum
            preparedStatement.setDate(5, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
            preparedStatement.setString(6, socialMedia.getLieu());
            preparedStatement.setInt(7, socialMedia.getLike());
            preparedStatement.setInt(8, socialMedia.getDislike());

            preparedStatement.executeUpdate();
            System.out.println(" Publication ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(SocialMedia socialMedia) {
        String sql = "UPDATE socialmedia SET titre = ?, contenu = ?, id_U = ?, category = ?, publicationDate = ?, lieu = ?, `like` = ?, dislike = ? WHERE idEB = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, socialMedia.getTitre());
            preparedStatement.setString(2, socialMedia.getContenu());
            preparedStatement.setInt(3, socialMedia.getId_U());
            preparedStatement.setString(4, socialMedia.getCategory().name());
            preparedStatement.setDate(5, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
            preparedStatement.setString(6, socialMedia.getLieu());
            preparedStatement.setInt(7, socialMedia.getLike());
            preparedStatement.setInt(8, socialMedia.getDislike());
            preparedStatement.setInt(9, socialMedia.getIdEB());

            preparedStatement.executeUpdate();
            System.out.println(" Publication mise à jour avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(SocialMedia socialMedia) {
        delete(socialMedia.getIdEB()); // Appelle la version delete(int idEB)
    }


    public void delete(int idEB) {
        String sql = "DELETE FROM socialmedia WHERE idEB = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, idEB);
            preparedStatement.executeUpdate();
            System.out.println(" Publication supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<SocialMedia> getAll() {
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


                try {
                    socialMedia.setCategory(models.TypeEB.valueOf(rs.getString("category").toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.err.println(" Catégorie inconnue : " + rs.getString("category"));
                    socialMedia.setCategory(models.TypeEB.ARTICLE); // Valeur par défaut
                }

                socialMedia.setPublicationDate(rs.getDate("publicationDate"));
                socialMedia.setLieu(rs.getString("lieu"));
                socialMedia.setLike(rs.getInt("like"));
                socialMedia.setDislike(rs.getInt("dislike"));

                socialMedias.add(socialMedia);
            }

        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération des publications : " + e.getMessage());
        }

        return socialMedias;
    }

    @Override
    public SocialMedia getById(int id) {
        String query = "SELECT * FROM socialmedia WHERE idEB = ?";
        SocialMedia socialMedia = null;
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                socialMedia = new SocialMedia();
                socialMedia.setIdEB(id);
                socialMedia.setTitre(rs.getString("titre"));
                socialMedia.setContenu(rs.getString("contenu"));
                socialMedia.setId_U(rs.getInt("id_U"));

                // ✅ Vérification de `TypeEB`
                try {
                    socialMedia.setCategory(models.TypeEB.valueOf(rs.getString("category").toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Catégorie inconnue pour ID " + id + " : " + rs.getString("category"));
                    socialMedia.setCategory(models.TypeEB.ARTICLE);
                }

                socialMedia.setPublicationDate(rs.getDate("publicationDate"));
                socialMedia.setLieu(rs.getString("lieu"));
                socialMedia.setLike(rs.getInt("like"));
                socialMedia.setDislike(rs.getInt("dislike"));
            }

        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération de la publication : " + e.getMessage());
        }
        return socialMedia;
    }
}
