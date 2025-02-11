package models;

import java.sql.Date;

public class reservation_transport {
    private int id,idU,idS;
    private Date dateRes,dateFin;
    private double prix;
    private String statut;

    public reservation_transport() {}
    public reservation_transport(int id,int idU, int idS, String statut, Date dateRes, Date dateFin, double prix) {
        this.id = id;
        this.idU = idU;
        this.idS = idS;
        this.dateRes = dateRes;
        this.dateFin = dateFin;
        this.prix = prix;
        this.statut = statut;
    }

    public reservation_transport(int idU, int idS, String statut, Date dateRes, Date dateFin, double prix) {
        this.idU = idU;
        this.idS = idS;
        this.dateRes = dateRes;
        this.dateFin = dateFin;
        this.prix = prix;
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

    public void setIdU(int idU) {
        this.idU = idU;
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public Date getDateRes() {
        return dateRes;
    }

    public void setDateRes(Date dateRes) {
        this.dateRes = dateRes;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public double getPrix() {
        return prix;
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
                ", statut='" + statut + '\'' +
                '}';
    }
}
