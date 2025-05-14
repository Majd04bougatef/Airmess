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
        System.out.println("Démarrage de l'ajout d'une publication: " + socialMedia);
        
        // Vérifier la connexion à la base de données
        if (con == null) {
            System.err.println("Erreur: Connexion à la base de données null!");
            con = MyDatabase.getInstance().getCon();
            if (con == null) {
                System.err.println("Erreur fatale: Impossible d'établir une connexion à la base de données!");
                return;
            }
        }
        
        // Définir la requête SQL
        String sql = "INSERT INTO socialmedia (titre, contenu, id_U, publicationDate, lieu, imagemedia, likee, dislike) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            // Mettre les valeurs dans la requête préparée
            preparedStatement.setString(1, socialMedia.getTitre());
            preparedStatement.setString(2, socialMedia.getContenu());
            preparedStatement.setInt(3, socialMedia.getId_U());
            preparedStatement.setDate(4, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
            preparedStatement.setString(5, socialMedia.getLieu());
            preparedStatement.setString(6, socialMedia.getImagemedia());
            preparedStatement.setInt(7, socialMedia.getLikee());
            preparedStatement.setInt(8, socialMedia.getDislike());
            
            // Exécuter la requête
            int rowsInserted = preparedStatement.executeUpdate();
            
            // Vérifier le résultat
            if (rowsInserted > 0) {
                System.out.println("Publication ajoutée avec succès! Nombre de lignes insérées: " + rowsInserted);
            } else {
                System.err.println("Avertissement: aucune ligne n'a été insérée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            
            // Récupérer plus d'informations sur l'erreur SQL
            try {
                if (e.getErrorCode() == 1062) {
                    System.err.println("Erreur: Duplication de clé unique");
                } else if (e.getErrorCode() == 1054) {
                    System.err.println("Erreur: Colonne inconnue dans la requête SQL");
                } else if (e.getErrorCode() == 1146) {
                    System.err.println("Erreur: Table 'socialmedia' n'existe pas");
                } else {
                    System.err.println("Code d'erreur SQL: " + e.getErrorCode());
                }
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'analyse de l'erreur SQL: " + ex.getMessage());
            }
            
            // Essayer de reconnecter et réessayer
            try {
                System.out.println("Tentative de reconnexion à la base de données...");
                MyDatabase.getInstance().reconnect();
                con = MyDatabase.getInstance().getCon();
                
                if (con != null) {
                    // Réessayer l'opération après reconnexion
                    try (PreparedStatement retryPs = con.prepareStatement(sql)) {
                        retryPs.setString(1, socialMedia.getTitre());
                        retryPs.setString(2, socialMedia.getContenu());
                        retryPs.setInt(3, socialMedia.getId_U());
                        retryPs.setDate(4, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
                        retryPs.setString(5, socialMedia.getLieu());
                        retryPs.setString(6, socialMedia.getImagemedia());
                        retryPs.setInt(7, socialMedia.getLikee());
                        retryPs.setInt(8, socialMedia.getDislike());
                        
                        int retryResult = retryPs.executeUpdate();
                        if (retryResult > 0) {
                            System.out.println("Publication ajoutée avec succès après reconnexion!");
                        } else {
                            System.err.println("Échec de l'ajout après reconnexion.");
                        }
                    } catch (SQLException retryEx) {
                        System.err.println("Échec de la deuxième tentative d'ajout: " + retryEx.getMessage());
                    }
                }
            } catch (Exception reconnectEx) {
                System.err.println("Échec de la tentative de reconnexion: " + reconnectEx.getMessage());
            }
        }
    }

    @Override
    public void update(SocialMedia socialMedia) {
        String sql = "UPDATE socialmedia SET titre = ?, contenu = ?, id_U = ?, publicationDate = ?, lieu = ? ,imagemedia = ? WHERE idEB = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, socialMedia.getTitre());
            preparedStatement.setString(2, socialMedia.getContenu());
            preparedStatement.setInt(3, socialMedia.getId_U());
            preparedStatement.setDate(4, new java.sql.Date(socialMedia.getPublicationDate().getTime()));
            preparedStatement.setString(5, socialMedia.getLieu());
            preparedStatement.setString(6, socialMedia.getImagemedia());
            preparedStatement.setInt(7, socialMedia.getIdEB());

            preparedStatement.executeUpdate();
            System.out.println(" Publication mise à jour avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(SocialMedia socialMedia) {
        delete(socialMedia.getIdEB());
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
        String query = "SELECT * FROM socialmedia ORDER BY publicationDate DESC";
        List<SocialMedia> socialMedias = new ArrayList<>();

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                SocialMedia socialMedia = new SocialMedia();
                socialMedia.setIdEB(rs.getInt("idEB"));
                socialMedia.setTitre(rs.getString("titre"));
                socialMedia.setContenu(rs.getString("contenu"));
                socialMedia.setId_U(rs.getInt("id_U"));
                socialMedia.setPublicationDate(rs.getDate("publicationDate"));
                socialMedia.setLieu(rs.getString("lieu"));
                socialMedia.setLikee(rs.getInt("likee"));
                socialMedia.setDislike(rs.getInt("dislike"));
                socialMedia.setImagemedia(rs.getString("imagemedia"));
                socialMedias.add(socialMedia);
            }
            System.out.println("Nombre de publications récupérées: " + socialMedias.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des publications : " + e.getMessage());
            e.printStackTrace();
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

                socialMedia.setPublicationDate(rs.getDate("publicationDate"));
                socialMedia.setLieu(rs.getString("lieu"));

                socialMedia.setLikee(rs.getInt("likee"));
                socialMedia.setDislike(rs.getInt("dislike"));
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération de la publication : " + e.getMessage());
        }
        return socialMedia;
    }

    public void updateLikes(SocialMedia post) {
        String query = "UPDATE socialmedia SET likee = ? WHERE idEB = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, post.getLikee());
            stmt.setInt(2, post.getIdEB());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Mise à jour réussie pour l'ID : " + post.getIdEB());
            } else {
                System.out.println("Erreur: la mise à jour a échoué pour l'ID : " + post.getIdEB());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
