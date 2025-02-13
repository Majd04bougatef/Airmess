package services;
import models.Commentaire;
import util.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireServices {

    private Connection con;

    public CommentaireServices() {
        con = MyDatabase.getInstance().getCon();
    }

    // 📝 Ajouter un commentaire
    public void add(Commentaire commentaire) {
        String sql = "INSERT INTO commentaire (idEB, id_U, description, numberlike, numberdislike) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, commentaire.getIdEB());
            preparedStatement.setInt(2, commentaire.getId_U());
            preparedStatement.setString(3, commentaire.getDescription());  // Correction du champ
            preparedStatement.setInt(4, commentaire.getNumberLike());
            preparedStatement.setInt(5, commentaire.getNumberDislike());

            preparedStatement.executeUpdate();
            System.out.println(" Commentaire ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    // ✏️ Mettre à jour un commentaire
    public void update(Commentaire commentaire) {
        String sql = "UPDATE commentaire SET idEB = ?, id_U = ?, description = ?, numberlike = ?, numberdislike = ? WHERE idC = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, commentaire.getIdEB());
            preparedStatement.setInt(2, commentaire.getId_U());
            preparedStatement.setString(3, commentaire.getDescription()); // Correction du champ
            preparedStatement.setInt(4, commentaire.getNumberLike());
            preparedStatement.setInt(5, commentaire.getNumberDislike());
            preparedStatement.setInt(6, commentaire.getIdC());

            preparedStatement.executeUpdate();
            System.out.println(" Commentaire mis à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    // 🗑️ Supprimer un commentaire
    public void delete(int idC) {
        String sql = "DELETE FROM commentaire WHERE idC = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, idC);
            preparedStatement.executeUpdate();
            System.out.println("Commentaire supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // 📋 Afficher tous les commentaires
    public List<Commentaire> display() {
        String query = "SELECT * FROM commentaire";
        List<Commentaire> commentaires = new ArrayList<>();

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setIdC(rs.getInt("idC"));
                commentaire.setIdEB(rs.getInt("idEB"));
                commentaire.setId_U(rs.getInt("id_U"));
                commentaire.setDescription(rs.getString("description"));  // Correction du champ
                commentaire.setNumberLike(rs.getInt("numberlike"));
                commentaire.setNumberDislike(rs.getInt("numberdislike"));

                commentaires.add(commentaire);
            }

            System.out.println( commentaires.size() );
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires : " + e.getMessage());
        }

        return commentaires;
    }
}