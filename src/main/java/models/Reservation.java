package models;

public class Reservation {
    private int idR;
    private int idO;
    private String dateRes;
    private ModePaiement modePaiement;
    private int id_U;

    public Reservation(int idR, int idO, String dateRes, String modePaiement, int id_U) {
        this.idR = idR;
        this.idO = idO;
        this.dateRes = dateRes;
        this.modePaiement = ModePaiement.valueOf(modePaiement);
        this.id_U = id_U;
    }

    public Reservation(int idO, String dateRes, String modePaiement, int id_U) {
        this.idO = idO;
        this.dateRes = dateRes;
        this.modePaiement = ModePaiement.valueOf(modePaiement);
        this.id_U = id_U;
    }

    public Reservation() {

    }

    public int getIdR() {
        return idR;
    }

    public void setIdR(int idR) {
        this.idR = idR;
    }

    public int getIdO() {
        return idO;
    }

    public void setIdO(int idO) {
        this.idO = idO;
    }

    public String getDateRes() {
        return dateRes;
    }

    public void setDateRes(String dateRes) {
        this.dateRes = dateRes;
    }

    public String getModePaiement() {
        return String.valueOf(modePaiement);
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = ModePaiement.valueOf(modePaiement);
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idR=" + idR +
                ", idO=" + idO +
                ", dateRes='" + dateRes + '\'' +
                ", modePaiement=" + modePaiement +
                ", id_U=" + id_U +
                '}';
    }
}
