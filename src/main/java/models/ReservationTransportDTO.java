package models;

import java.sql.Timestamp;

public class ReservationTransportDTO {
    private int idReservation;
    private String reference;
    private Timestamp dateRes;
    private Timestamp dateFin;
    private int nombreVelo;
    private String statut;
    private double prix;

    private int idUser;
    private String nomUser;
    private String prenomUser;

    private int idStation;
    private String nomStation;
    private String typeVelo;

    public ReservationTransportDTO(){}
    public ReservationTransportDTO(int idReservation, String reference, Timestamp dateRes, Timestamp dateFin, int nombreVelo, String statut, double prix, int idUser, String nomUser, String prenomUser, int idStation, String nomStation, String typeVelo) {
        this.idReservation = idReservation;
        this.reference = reference;
        this.dateRes = dateRes;
        this.dateFin = dateFin;
        this.nombreVelo = nombreVelo;
        this.statut = statut;
        this.prix = prix;
        this.idUser = idUser;
        this.nomUser = nomUser;
        this.prenomUser = prenomUser;
        this.idStation = idStation;
        this.nomStation = nomStation;
        this.typeVelo = typeVelo;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public int getNombreVelo() {
        return nombreVelo;
    }

    public void setNombreVelo(int nombreVelo) {
        this.nombreVelo = nombreVelo;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    public String getPrenomUser() {
        return prenomUser;
    }

    public void setPrenomUser(String prenomUser) {
        this.prenomUser = prenomUser;
    }

    public int getIdStation() {
        return idStation;
    }

    public void setIdStation(int idStation) {
        this.idStation = idStation;
    }

    public String getNomStation() {
        return nomStation;
    }

    public void setNomStation(String nomStation) {
        this.nomStation = nomStation;
    }

    public String getTypeVelo() {
        return typeVelo;
    }

    public void setTypeVelo(String typeVelo) {
        this.typeVelo = typeVelo;
    }
}
