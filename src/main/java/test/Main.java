package test;

import models.Offre;
import services.OffreService;
import util.MyDatabase;
import java.util.List.*;

import java.sql.Connection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        MyDatabase db = MyDatabase.getInstance();
        Connection con = db.getCon();
        OffreService offreService = new OffreService();
        Offre offre1 = new Offre(1, 100, 50, "2021-01-01", "2021-01-02", 10, "Description", "Place");
        System.out.println("Ajouter");
        offreService.add(offre1);
        Offre offre = offreService.getAll().get(0);
        System.out.println(offre);
        System.out.println("Modifier");
        offre.setPriceAfter(70);
        offreService.update(offre);
        System.out.println(offreService.getAll().get(0));
        System.out.println("Supprimer");
        offreService.delete(offre);



    }
}