package models;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class review_transport {
    private int id,idU,idS,rating;
    private String commentt;
    private Timestamp date_RT;

    public review_transport() {}
    public review_transport(int id,int idU, int idS, int rating, String commentt) {
        this.id = id;
        this.idU = idU;
        this.idS = idS;
        this.rating = rating;
        this.commentt = commentt;
        this.date_RT = new Timestamp(System.currentTimeMillis());
    }

    public review_transport(int idU, int idS, int rating, String commentt) {
        this.idU = idU;
        this.idS = idS;
        this.rating = rating;
        this.commentt = commentt;
        this.date_RT = new Timestamp(System.currentTimeMillis());
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCommentt() {
        return commentt;
    }

    public void setCommentt(String commentt) {
        this.commentt = commentt;
    }

    public Timestamp getDate_RT() {
        return date_RT;
    }

    public void setDate_RT(Timestamp date_RT) {
        this.date_RT = date_RT;
    }

    @Override
    public String toString() {
        return "review_transport{" +
                "id=" + id +
                ", idU=" + idU +
                ", idS=" + idS +
                ", rating=" + rating +
                ", commentt='" + commentt + '\'' +
                ", date_RT=" + date_RT +
                '}';
    }
}
