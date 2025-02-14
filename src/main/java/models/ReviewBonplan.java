package models;

import java.sql.Date;
import java.time.LocalDateTime;

public class ReviewBonplan {
    private int idR;
    private int idU;
    private int idP;
    private Integer rating;
    private String commente;
    private LocalDateTime dateR;

    public ReviewBonplan() {}

    public ReviewBonplan( int idU, int idP, Integer rating, String commente, LocalDateTime dateR) {
        this.idU = idU;
        this.idP = idP;
        this.rating = rating;
        this.commente = commente;
        this.dateR = dateR;
    }

    public ReviewBonplan(int idR, int idU, int idP, Integer rating, String commente, LocalDateTime dateR) {
        this.idR = idR;
        this.idU = idU;
        this.idP = idP;
        this.rating = rating;
        this.commente = commente;
        this.dateR = dateR;
    }

    public int getIdR() {
        return idR;
    }

    public void setIdR(int idR) {
        this.idR = idR;
    }

    public int getIdU() {
        return idU;
    }

    public void setIdU(int idU) {
        this.idU = idU;
    }

    public int getIdP() {
        return idP;
    }

    public void setIdP(int idP) {
        this.idP = idP;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCommente() {
        return commente;
    }

    public void setCommente(String comment) {
        this.commente = comment;
    }

    public LocalDateTime getDateR() {
        return dateR;
    }

    public void setDateR(LocalDateTime dateR) {
        this.dateR = dateR;
    }

    @Override
    public String toString() {
        return "ReviewBonplan{" +
                "idR=" + idR +
                ", idU=" + idU +
                ", idP=" + idP +
                ", rating=" + rating +
                ", comment='" + commente + '\'' +
                ", dateR=" + dateR +
                '}';
    }
}
