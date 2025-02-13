package test;
import util.MyDatabase;
import java.sql.Connection;
import models.TypeEB;
import interfaces.GlobalInterface;
import models.SocialMedia;
import services.SocialMediaServices;
import models.Commentaire;
import services.CommentaireServices;
import java.util.Date;
import java.util.List;




public class Main {
    public static void main(String[] args) {
        SocialMediaServices socialMediaServices = new SocialMediaServices();
        CommentaireServices commentaireServices = new CommentaireServices();


        SocialMedia newSocialMedia = new SocialMedia(
                "Titre de la publication",
                "Voici le contenu de la publication.",
                1,
                TypeEB.ARTICLE,
                new java.util.Date(),
                "Tunis",
                100,
                 5
        );

       /* // add
       socialMediaServices.add(newSocialMedia);

        // Affichage add
        System.out.println("Liste des publications après ajout:");
        socialMediaServices.display().forEach(socialMedia -> System.out.println(socialMedia)); */



       /* update
        newSocialMedia.setIdEB(3);
        newSocialMedia.setTitre("blog");
        //Titre mis à jour
       newSocialMedia.setContenu("Contenu mis à jour pour tester.");
        socialMediaServices.update(newSocialMedia);

        // Affichage update
        System.out.println("\nListe des publications après mise à jour:");
   socialMediaServices.display().forEach(socialMedia -> System.out.println(socialMedia)); */

       /* //  delete
        newSocialMedia.setIdEB(4);

        socialMediaServices.delete(newSocialMedia.getIdEB());

        // Affichage delete
       System.out.println("\nListe des publications après suppression:");
      socialMediaServices.display().forEach(socialMedia -> System.out.println(socialMedia)); */



        Commentaire newCommentaire = new Commentaire(
                2,
                1,
                "Oui je valide !",
                10,
                2
        );

        //addcommentaire
      //  commentaireServices.add(newCommentaire);

//affichage
        System.out.println(" Liste des commentaires :");
        List<Commentaire> commentaires = commentaireServices.display();
        for (Commentaire c : commentaires) {
            System.out.println(c);
        }

       /* update commentaire
        newCommentaire.setIdC(1);
        newCommentaire.setDescription("Contenu du commentaire mis à jour.");
        commentaireServices.update(newCommentaire);

       Affichage après update
        System.out.println(" Liste des commentaires APRÈS MISE À JOUR :");
        List<Commentaire> commentairesApresUpdate = commentaireServices.display();
        for (Commentaire c : commentairesApresUpdate) {
            System.out.println(c);
        }*/

       /* delete commentaire
        commentaireServices.delete(2);
// Affichage après delete
        System.out.println(" Liste des commentaires APRÈS SUPPRESSION :");
        List<Commentaire> commentairesApresDelete = commentaireServices.display();
        for (Commentaire c : commentairesApresDelete) {
            System.out.println(c);
        } */




    }


}
