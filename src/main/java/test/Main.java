package test;

import util.MyDatabase;

import java.sql.Connection;
import models.bonplan;
import services.BonPlanServices;
import models.ReviewBonplan;
import services.ReviewBonPlanServices;
import java.time.LocalDateTime;


/*public class Main {
    public static void main(String[] args) {
        BonPlanServices bonPlanService = new BonPlanServices();

        // Créer un nouvel objet bonplan
        bonplan bonPlan = new bonplan();
        bonPlan.setId_U(1);
        bonPlan.setIdP(101);
        bonPlan.setNomplace("La Bella Café");
        bonPlan.setLocalisation("Centre-ville, Tunis");
        bonPlan.setDescription("Un joli café avec une ambiance chaleureuse.");
        bonPlan.setTypePlace("cafe");

        // Appeler la méthode add
        bonPlanService.add(bonPlan);

    }
}

/*public class Main {
    public static void main(String[] args) {
        BonPlanServices service = new BonPlanServices();

        bonplan bonPlanToUpdate = new bonplan();
        bonPlanToUpdate.setIdP(101);
        bonPlanToUpdate.setId_U(1);
        bonPlanToUpdate.setNomplace("La Bella Café Update");
        bonPlanToUpdate.setLocalisation("La Marsa, Tunis");
        bonPlanToUpdate.setDescription("Un café cosy avec une vue magnifique.");
        bonPlanToUpdate.setTypePlace("café");


        service.update(bonPlanToUpdate);
    }
}*/
/*public class Main {
    public static void main(String[] args) {
        BonPlanServices service = new BonPlanServices();

        // Créer un bon plan avec un ID spécifique à supprimer
        int idPToDelete = 101;
        service.delete(idPToDelete);
    }
}*/

/*public class Main {
    public static void main(String[] args) {

        ReviewBonplan review = new ReviewBonplan();
        review.setIdP(101);
        review.setRating(5);
        review.setCommente("Super endroit, très agréable!");
        review.setDateR(LocalDateTime.now());

        // Créer un service de revue et ajouter la revue
        ReviewBonPlanServices reviewService = new ReviewBonPlanServices();
        reviewService.add(review);
    }
}*/


/*public class Main {
    public static void main(String[] args) {

        ReviewBonPlanServices reviewService = new ReviewBonPlanServices();


        ReviewBonplan reviewToUpdate = new ReviewBonplan();
        reviewToUpdate.setIdR(1);
        reviewToUpdate.setIdU(1);
        reviewToUpdate.setIdP(101);
        reviewToUpdate.setRating(4);
        reviewToUpdate.setCommente("Très bon endroit, service excellent!");
        reviewToUpdate.setDateR(LocalDateTime.now());


        reviewService.update(reviewToUpdate);

    }
}*/

public class Main {
    public static void main(String[] args) {
        ReviewBonPlanServices service = new ReviewBonPlanServices();

        // ID du review à supprimer
        int idRToDelete = 1;

        service.delete(idRToDelete);
    }
}