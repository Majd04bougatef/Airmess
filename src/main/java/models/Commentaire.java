package models;

import java.util.Date;

public class Commentaire {
    private int idC;
    private int idEB;
    private int id_U;
    private String description; // Original description
    private String proposedDescription; // AI-moderated description
    private int numberLike;
    private int numberDislike;
    private boolean isApproved; // NEW: Approval status

    private String postTitre;
    private String postContenu;
    private String postImagemedia;

    //  Constructeur par défaut
    public Commentaire() {
        this.isApproved = false;
    }

    //Constructeur avec tous les paramètres
    public Commentaire(int idC, int idEB, int id_U, String description, String proposedDescription, int numberLike, int numberDislike, boolean isApproved) {
        this.idC = idC;
        this.idEB = idEB;
        this.id_U = id_U;
        this.description = description;
        this.proposedDescription = proposedDescription;
        this.numberLike = numberLike;
        this.numberDislike = numberDislike;
        this.isApproved = isApproved;
    }

    //  Constructeur sans idC
    public Commentaire(int idEB, int id_U, String description, String proposedDescription, int numberLike, int numberDislike, boolean isApproved) {
        this.idEB = idEB;
        this.id_U = id_U;
        this.description = description;
        this.proposedDescription = proposedDescription;
        this.numberLike = numberLike;
        this.numberDislike = numberDislike;
        this.isApproved = isApproved;
    }

    public int getIdC() {
        return idC;
    }

    public void setIdC(int idC) {
        this.idC = idC;
    }

    public int getIdEB() {
        return idEB;
    }

    public void setIdEB(int idEB) {
        this.idEB = idEB;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProposedDescription() {
        return proposedDescription;
    }

    public void setProposedDescription(String proposedDescription) {
        this.proposedDescription = proposedDescription;
    }


    public int getNumberLike() {
        return numberLike;
    }

    public void setNumberLike(int numberLike) {
        this.numberLike = numberLike;
    }

    public int getNumberDislike() {
        return numberDislike;
    }

    public void setNumberDislike(int numberDislike) {
        this.numberDislike = numberDislike;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getPostTitre() {
        return postTitre;
    }

    public void setPostTitre(String postTitre) {
        this.postTitre = postTitre;
    }

    public String getPostContenu() {
        return postContenu;
    }

    public void setPostContenu(String postContenu) {
        this.postContenu = postContenu;
    }

    public String getPostImagemedia() {
        return postImagemedia;
    }

    public void setPostImagemedia(String postImagemedia) {
        this.postImagemedia = postImagemedia;
    }



    @Override
    public String toString() {
        return "Commentaire{" +
                "idC=" + idC +
                ", idEB=" + idEB +
                ", id_U=" + id_U +
                ", description='" + description + '\'' +
                ", proposedDescription='" + proposedDescription + '\'' +
                ", numberLike=" + numberLike +
                ", numberDislike=" + numberDislike +
                ", isApproved=" + isApproved +
                '}';
    }
}