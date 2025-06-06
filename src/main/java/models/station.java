package models;

public class station {

    private int idS,idU,capacite,nbVelo,numberRaters;
    private String nom,typeVelo,pays,statut;
    private double latitude,longitude,prixheure,rating;

    public station() {}
    public station(int id , int idU , String nom , double latitude , double longitude , double prixheure,int capacite,int nbV,String typeV,String pays) {
        this.idS = id;
        this.idU = idU;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.prixheure = prixheure;
        this.capacite = capacite;
        this.nbVelo = nbV;
        this.typeVelo = typeV;
        this.pays = pays;
        this.numberRaters = 0;
        this.rating = 0.0;
        this.statut = "inactive";
    }

    public station(int idU , String nom , double latitude , double longitude , double prixheure,int capacite,int nbV,String typeV,String pays) {
        this.idU = idU;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.prixheure = prixheure;
        this.capacite = capacite;
        this.nbVelo = nbV;
        this.typeVelo = typeV;
        this.pays = pays;
        this.numberRaters = 0;
        this.rating = 0.0;
        this.statut = "inactive";
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public int getIdU() {
        return idU;
    }

    public void setIdU(int idU) {
        this.idU = idU;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public int getNbVelo() {
        return nbVelo;
    }

    public void setNbVelo(int nbVelo) {
        this.nbVelo = nbVelo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypeVelo() {
        return typeVelo;
    }

    public void setTypeVelo(String typeVelo) {
        this.typeVelo = typeVelo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPrixheure() {
        return prixheure;
    }

    public void setPrixheure(double prixheure) {
        this.prixheure = prixheure;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public int getNumberRaters() {
        return numberRaters;
    }

    public void setNumberRaters(int numberRaters) {
        this.numberRaters = numberRaters;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "station{" +
                "idS=" + idS +
                ", idU=" + idU +
                ", capacite=" + capacite +
                ", nbVelo=" + nbVelo +
                ", numberRaters=" + numberRaters +
                ", nom='" + nom + '\'' +
                ", typeVelo='" + typeVelo + '\'' +
                ", pays='" + pays + '\'' +
                ", statut='" + statut + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", prixheure=" + prixheure +
                ", rating=" + rating +
                '}';
    }
}
