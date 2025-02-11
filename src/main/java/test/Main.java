package test;

import models.Offre;
import models.Reservation;
import services.OffreService;
import services.ReservationService;
import util.MyDatabase;
import java.util.List.*;

import java.sql.Connection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        MyDatabase db = MyDatabase.getInstance();
        Connection con = db.getCon();
        //OffreService offreService = new OffreService();
        //Offre offre1 = new Offre(1, 100, 50, "2021-01-01", "2021-01-02", 10, "Description", "Place");
        //System.out.println("Ajouter");
        //offreService.add(offre1);
        //Offre offre = offreService.getById(5);
        //System.out.println(offre);
        //System.out.println("Modifier");
        //offre.setPriceAfter(70);
        //offreService.update(offre);
        //System.out.println(offreService.getById(5));
        //System.out.println("Supprimer");
        //offreService.delete(offre);

        ReservationService reservationService = new ReservationService();
        Reservation reservation = new Reservation(6, "2021-01-01", "carte", 1);
        System.out.println("Ajouter");
        reservationService.add(reservation);
        Reservation reservation1 = reservationService.getById(2);
        System.out.println(reservation1);
        System.out.println("Modifier");
        reservation1.setModePaiement("espece");
        reservationService.update(reservation1);
        System.out.println(reservationService.getById(2));
        System.out.println("Supprimer");
        reservationService.delete(reservation1);



    }
}