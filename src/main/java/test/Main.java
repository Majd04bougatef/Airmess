package test;

import util.MyDatabase;

import java.sql.Connection;
import models.TypeEB;  // Assurez-vous que le package est correct


import models.SocialMedia;
import services.SocialMediaServices;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Création des services
        SocialMediaServices socialMediaServices = new SocialMediaServices();

        // Exemple de création d'une nouvelle publication
        SocialMedia newSocialMedia = new SocialMedia(
                "Titre de la publication",
                "Voici le contenu de la publication.",
                1,  // ID de l'utilisateur
                TypeEB.ARTICLE,
                new java.util.Date(),
                "Tunis",
                100,  // Nombre de likes
                5     // Nombre de dislikes
        );

        // Ajout de la publication
        socialMediaServices.add(newSocialMedia);

        // Affichage de toutes les publications
        System.out.println("Liste des publications après ajout:");
        socialMediaServices.display().forEach(socialMedia -> System.out.println(socialMedia));

        // Exemple de mise à jour d'une publication (ID = 1 ici)
       // newSocialMedia.setTitre("Titre mis à jour");
       // newSocialMedia.setContenu("Contenu mis à jour pour tester.");
       // socialMediaServices.update(newSocialMedia);

        // Affichage des publications après mise à jour
      //  System.out.println("\nListe des publications après mise à jour:");
//        socialMediaServices.display().forEach(socialMedia -> System.out.println(socialMedia));

        // Exemple de suppression de la publication (ID = 1 ici)
       // socialMediaServices.delete(newSocialMedia.getIdEB());

        // Affichage des publications après suppression
      //  System.out.println("\nListe des publications après suppression:");
      //  socialMediaServices.display().forEach(socialMedia -> System.out.println(socialMedia));
    }
}
