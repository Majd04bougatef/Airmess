package models;

import java.sql.Timestamp;

public class reservation_transport {
    private int id,idU,idS,nombreVelo;
    private Timestamp dateRes,dateFin;
    private double prix;
    private String statut;

    public reservation_transport() {}
    public reservation_transport(int id,int idU, int idS, String statut, Timestamp dateRes, Timestamp dateFin, double prix,int nombreVelo) {
        this.id = id;
        this.idU = idU;
        this.idS = idS;
        this.dateRes = dateRes;
        this.dateFin = dateFin;
        this.prix = prix;
        this.nombreVelo = nombreVelo;
        this.statut = statut;
    }

    public reservation_transport(int idU, int idS, String statut, Timestamp dateRes, Timestamp dateFin, double prix,int nombreVelo) {
        this.idU = idU;
        this.idS = idS;
        this.dateRes = dateRes;
        this.dateFin = dateFin;
        this.prix = prix;
        this.nombreVelo = nombreVelo;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdU() {
        return idU;
    }

    public int getNombreVelo() {
        return nombreVelo;
    }

    public void setIdU(int idU) {
        this.idU = idU;
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public Timestamp getDateRes() {
        return dateRes;
    }

    public void setDateRes(Timestamp dateRes) {
        this.dateRes = dateRes;
    }

    public Timestamp getDateFin() {
        return dateFin;
    }

    public void setDateFin(Timestamp dateFin) {
        this.dateFin = dateFin;
    }

    public double getPrix() {
        return prix;
    }

    public void setNombreVelo(int nombreVelo) {
        this.nombreVelo = nombreVelo;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "reservation_transport{" +
                "id=" + id +
                ", idU=" + idU +
                ", idS=" + idS +
                ", dateRes=" + dateRes +
                ", dateFin=" + dateFin +
                ", prix=" + prix +
                ", nombre Velo =" + nombreVelo +
                ", statut='" + statut + '\'' +
                '}';
    }
}
