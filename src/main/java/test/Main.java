package test;

import util.MyDatabase;

import java.sql.Connection;
import models.TypeEB;


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
    }
}
