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

    public Commentaire processPost(Commentaire commentaire) {
        try {
            return moderateAndPropose(commentaire);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modération du commentaire : " + e.getMessage());
            Commentaire proposedComment = new Commentaire(
                    commentaire.getIdEB(),
                    commentaire.getId_U(),
                    commentaire.getDescription(),
                    commentaire.getDescription(),
                    commentaire.getNumberLike(),
                    commentaire.getNumberDislike(),
                    true
            );


            return proposedComment;
        }
    }

    private Commentaire moderateAndPropose(Commentaire commentaire) throws Exception {
        String descriptionModeree = aiServices.moderateContent(commentaire.getDescription());

        if (descriptionModeree == null || descriptionModeree.trim().isEmpty()) {
            System.out.println(" Le commentaire contient du contenu inapproprié et n'a pas été ajouté.");
            return null;
        }
        Commentaire proposedComment = new Commentaire(
                commentaire.getIdEB(),
                commentaire.getId_U(),
                commentaire.getDescription(),
                descriptionModeree,
                commentaire.getNumberLike(),
                commentaire.getNumberDislike(),
                false
        );


        return proposedComment;
    }
    public void add(Commentaire commentaire) {
        try {

            if (!commentaire.isApproved()) {
                System.out.println(" Cannot add unapproved comment.");
                return;
            }

            if (con == null) {
                System.err.println(" La connexion à la base de données est nulle.");
                return;
            }

            String sql = "INSERT INTO commentaire (idEB, id_U, description, proposedDescription, numberlike, numberdislike, isApproved) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, commentaire.getIdEB());
                preparedStatement.setInt(2, commentaire.getId_U());
                preparedStatement.setString(3, commentaire.getDescription());
                preparedStatement.setString(4, commentaire.getProposedDescription());
                preparedStatement.setInt(5, commentaire.getNumberLike());
                preparedStatement.setInt(6, commentaire.getNumberDislike());
                preparedStatement.setBoolean(7, commentaire.isApproved());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            commentaire.setIdC(generatedKeys.getInt(1));
                            System.out.println(" Commentaire ajouté avec succes : " + commentaire.getDescription());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'ajout du commentaire : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void approveCommentaire(Commentaire commentaire) {
        String sql = "UPDATE commentaire SET isApproved = ? WHERE idC = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, commentaire.getIdC());
            preparedStatement.executeUpdate();
            System.out.println(" Commentaire approuvé avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'approbation du commentaire : " + e.getMessage());
        }
    }

    @Override
    public void update(Commentaire commentaire) {
        String sql = "UPDATE commentaire SET description = ?, proposedDescription = ?, numberlike = ?, numberdislike = ?, isApproved = ? WHERE idC = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, commentaire.getDescription());
            preparedStatement.setString(2, commentaire.getProposedDescription());
            preparedStatement.setInt(3, commentaire.getNumberLike());
            preparedStatement.setInt(4, commentaire.getNumberDislike());
            preparedStatement.setBoolean(5, commentaire.isApproved());
            preparedStatement.setInt(6, commentaire.getIdC());

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
                commentaire.setProposedDescription(rs.getString("proposedDescription"));
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));
                commentaire.setApproved(rs.getBoolean("isApproved"));

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
                commentaire.setProposedDescription(rs.getString("proposedDescription"));
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));
                commentaire.setApproved(rs.getBoolean("isApproved"));

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
        String query = "SELECT c.idC, c.idEB, c.id_U, c.description, c.proposedDescription, c.numberlike, c.numberdislike,c.isApproved, " +
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
                commentaire.setProposedDescription(rs.getString("proposedDescription"));
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));
                commentaire.setApproved(rs.getBoolean("isApproved"));

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