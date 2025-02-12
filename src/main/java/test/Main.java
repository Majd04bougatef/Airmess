package test;

import util.MyDatabase;

import java.sql.Connection;
import models.bonplan;
import services.BonPlanServices;

/*public class Main {
    public static void main(String[] args) {
        BonPlanServices bonPlanService = new BonPlanServices();

        /* Créer un nouvel objet bonplan
        bonplan bonPlan = new bonplan();
        bonPlan.setId_U(1);  // L'ID de l'utilisateur 'John Doe' dans la table 'users'
        bonPlan.setIdP(101); // Exemple d'ID du bon plan
        bonPlan.setNomplace("La Bella Café");
        bonPlan.setLocalisation("Centre-ville, Tunis");
        bonPlan.setDescription("Un joli café avec une ambiance chaleureuse.");
        bonPlan.setTypePlace("cafe"); // Assurez-vous que 'cafe' est une valeur valide

        // Appeler la méthode add pour insérer dans la base de données
        //bonPlanService.add(bonPlan);

    }
}*/

/*public class Main {
    public static void main(String[] args) {
        BonPlanServices service = new BonPlanServices();

        // Créer un bon plan avec les nouvelles valeurs pour la mise à jour
        bonplan bonPlanToUpdate = new bonplan();
        bonPlanToUpdate.setIdP(101);  // idP = 101 pour identifier le bon plan à mettre à jour
        bonPlanToUpdate.setId_U(1);   // id utilisateur
        bonPlanToUpdate.setNomplace("La Bella Café Update");
        bonPlanToUpdate.setLocalisation("La Marsa, Tunis");
        bonPlanToUpdate.setDescription("Un café cosy avec une vue magnifique.");
        bonPlanToUpdate.setTypePlace("café");

        // Appeler la méthode update pour mettre à jour l'enregistrement
        service.update(bonPlanToUpdate);
    }
}*/
public class Main {
    public static void main(String[] args) {
        BonPlanServices service = new BonPlanServices();

        // Créer un bon plan avec un ID spécifique à supprimer
        int idPToDelete = 101;  // ID du bon plan à supprimer (par exemple)

        // Appeler la méthode delete pour supprimer le bon plan
        service.delete(idPToDelete);  // Suppression du bon plan avec l'ID 101
    }
}