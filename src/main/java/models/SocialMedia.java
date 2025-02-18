// Fichier SocialMedia.java
package models;

import java.util.Date;

public class SocialMedia {
    private int idEB;
    private String titre;
    private String contenu;
    private int id_U;
    private Date publicationDate;
    private String lieu;
    private int like;
    private int dislike;
    private String imagemedia;


    // Constructeur par défaut
    public SocialMedia() {
    }

    // Constructeur avec tous les paramètres
    public SocialMedia(int idEB, String titre, String contenu, int id_U,  Date publicationDate, String lieu, int like, int dislike,String imagemedia) {
        this.idEB = idEB;
        this.titre = titre;
        this.contenu = contenu;
        this.id_U = id_U;
        this.publicationDate = publicationDate;
        this.lieu = lieu;
        this.like = like;
        this.dislike = dislike;
        this.imagemedia =imagemedia;
    }

    // Constructeur sans idEB
    public SocialMedia(String titre, String contenu, int id_U, Date publicationDate, String lieu, int like, int dislike,String imagemedia) {
        this.titre = titre;
        this.contenu = contenu;
        this.id_U = id_U;
        this.publicationDate = publicationDate;
        this.lieu = lieu;
        this.like = like;
        this.dislike = dislike;
        this.imagemedia =imagemedia;
    }

    // Getters et Setters
    public int getIdEB() {
        return idEB;
    }

    public void setIdEB(int idEB) {
        this.idEB = idEB;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public String getImagemedia() {
        return imagemedia;
    }
    public void setImagemedia(String imagemedia) {
        this.imagemedia = imagemedia;
    }


    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    @Override
    public String toString() {
        return "SocialMedia{" +
                "idEB=" + idEB +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", idU=" + id_U +
                ", publicationDate=" + publicationDate +
                ", lieu='" + lieu + '\'' +
                ", like=" + like +
                ", dislike=" + dislike +
                ", imagemedia='" + imagemedia + '\'' +
                '}';
    }
}
