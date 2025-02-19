package services;

import interfaces.GlobalInterface;
import models.Commentaire;
import util.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CommentaireServices implements GlobalInterface<Commentaire> {

    private Connection con;

    public CommentaireServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Commentaire commentaire) {
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
                        System.out.println("Commentaire ajouté avec ID : " + commentaire.getIdC());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'ajout : " + e.getMessage());
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

    public List<Commentaire> getAllWithPostDetails() {
        String query = "SELECT c.idC, c.idEB, c.id_U, c.description, c.numberlike, c.numberdislike, " +
                "s.titre, s.contenu, s.imagemedia " +
                "FROM commentaire c " +
                "JOIN socialmedia s ON c.idEB = s.idEB";

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

                String postTitre = rs.getString("titre");
                String postContenu = rs.getString("contenu");
                String postImagemedia = rs.getString("imagemedia");

                System.out.println(" Commentaire ID: " + commentaire.getIdC() +
                        " | Post: " + postTitre +
                        " | Contenu: " + postContenu);

                commentaires.add(commentaire);
            }

        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération des commentaires avec posts : " + e.getMessage());
        }

        return commentaires;
    }




}
