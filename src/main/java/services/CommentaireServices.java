package services;

import interfaces.GlobalInterface;
import models.Commentaire;
import util.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CommentaireServices implements GlobalInterface<Commentaire> {

    private Connection con;
    private AiServices aiServices;

    public CommentaireServices() {
        con = MyDatabase.getInstance().getCon();
        aiServices = new AiServices();
    }

    @Override
    public void add(Commentaire commentaire) {
        try {
            // 🔥 Modération de la description avant insertion
            String descriptionModeree = aiServices.moderateContent(commentaire.getDescription());

            // Vérifie si le contenu est acceptable après modération
            if (descriptionModeree == null || descriptionModeree.trim().isEmpty()) {
                System.out.println("❌ Le commentaire contient du contenu inapproprié et n'a pas été ajouté.");
                return;  // Ne pas insérer en base de données
            }

            // Vérifie si le commentaire a été modifié par l'IA
            if (descriptionModeree.equals(commentaire.getDescription())) {
                System.out.println("❌ Le commentaire n'a pas été modifié par l'IA, donc il est rejeté.");
                return;
            }

            // Met à jour la description avec la version modérée
            commentaire.setDescription(descriptionModeree);

            // Vérification de la connexion à la base de données
            if (con == null) {
                System.err.println("❌ La connexion à la base de données est nulle.");
                return;
            }

            // Insertion du commentaire dans la base de données
            String sql = "INSERT INTO commentaire (idEB, id_U, description, numberlike, numberdislike) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, commentaire.getIdEB());
                preparedStatement.setInt(2, commentaire.getId_U());
                preparedStatement.setString(3, commentaire.getDescription());
                preparedStatement.setInt(4, commentaire.getNumberLike());
                preparedStatement.setInt(5, commentaire.getNumberDislike());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            commentaire.setIdC(generatedKeys.getInt(1));
                            System.out.println("✅ Commentaire ajouté avec IA : " + commentaire.getDescription());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du commentaire : " + e.getMessage());
            e.printStackTrace();  // Afficher la stack trace pour déboguer
        } catch (Exception e) {
            System.err.println("⚠️ Erreur IA : " + e.getMessage());
            e.printStackTrace();  // Afficher la stack trace pour déboguer
        }
    }




    @Override
    public void update(Commentaire commentaire) {
        String sql = "UPDATE commentaire SET description = ?, numberlike = ?, numberdislike = ? WHERE idC = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, commentaire.getDescription());
            preparedStatement.setInt(2, commentaire.getNumberLike());
            preparedStatement.setInt(3, commentaire.getNumberDislike());
            preparedStatement.setInt(4, commentaire.getIdC());

            preparedStatement.executeUpdate();
            System.out.println(" Commentaire mis à jour avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Commentaire commentaire) {
        String sql = "DELETE FROM commentaire WHERE idC = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, commentaire.getIdC());
            preparedStatement.executeUpdate();
            System.out.println( "Commentaire supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Commentaire> getAll() {
        String query = "SELECT * FROM commentaire";
        List<Commentaire> commentaires = new ArrayList<>();

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setIdC(rs.getInt("idC"));
                commentaire.setIdEB(rs.getInt("idEB"));
                commentaire.setId_U(rs.getInt("id_U"));
                commentaire.setDescription(rs.getString("description"));
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));

                commentaires.add(commentaire);
            }

            System.out.println( commentaires.size() + " commentaires récupérés.");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération des commentaires : " + e.getMessage());
        }

        return commentaires;
    }

    @Override
    public Commentaire getById(int id) {
        String query = "SELECT * FROM commentaire WHERE idC = ?";
        Commentaire commentaire = null;

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                commentaire = new Commentaire();
                commentaire.setIdC(rs.getInt("idC"));
                commentaire.setIdEB(rs.getInt("idEB"));
                commentaire.setId_U(rs.getInt("id_U"));
                commentaire.setDescription(rs.getString("description"));
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));

                System.out.println(" Commentaire trouvé : " + commentaire);
            } else {
                System.out.println(" Aucun commentaire trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération du commentaire : " + e.getMessage());
        }

        return commentaire;
    }

    public List<Commentaire> getAllWithPostDetails(int postId) {
        String query = "SELECT c.idC, c.idEB, c.id_U, c.description, c.numberlike, c.numberdislike, " +
                "s.titre, s.contenu, s.imagemedia " +
                "FROM commentaire c " +
                "JOIN socialmedia s ON c.idEB = s.idEB "  +
                "WHERE c.idEB = ?"; ;

        List<Commentaire> commentaires = new ArrayList<>();

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setIdC(rs.getInt("idC"));
                commentaire.setIdEB(rs.getInt("idEB"));
                commentaire.setId_U(rs.getInt("id_U"));
                commentaire.setDescription(rs.getString("description"));
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));

                commentaire.setPostTitre(rs.getString("titre"));
                commentaire.setPostContenu(rs.getString("contenu"));
                commentaire.setPostImagemedia(rs.getString("imagemedia"));

                System.out.println("Commentaire ID: " + commentaire.getIdC() +
                        " | Post: " + commentaire.getPostTitre() +
                        " | Contenu: " + commentaire.getPostContenu());

                commentaires.add(commentaire);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires avec posts : " + e.getMessage());
        }

        return commentaires;
    }
}
