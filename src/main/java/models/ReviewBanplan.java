package models ;

import java.time.LocalDateTime;

public class ReviewBanplan {
    private int id_U;
    private int id_P;
    private int rating;
    private String comment;
    private LocalDateTime dateR;

    public ReviewBanplan() {}

    public ReviewBanplan(int id_U, int id_P, int rating, String comment, LocalDateTime dateR) {
        this.id_U = id_U;
        this.id_P = id_P;
        this.rating = rating;
        this.comment = comment;
        this.dateR = dateR;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public int getId_P() {
        return id_P;
    }

    public void setId_P(int id_P) {
        this.id_P = id_P;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateR() {
        return dateR;
    }

    public void setDateR(LocalDateTime dateR) {
        this.dateR = dateR;
    }

    @Override
    public String toString() {
        return "Reviewbonplan{" +
                "id_U=" + id_U +
                ", id_P=" + id_P +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", dateR=" + dateR +
                '}';
    }
}
