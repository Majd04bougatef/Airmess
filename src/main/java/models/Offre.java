package models;

public class Offre {

    private int idO;
    private int id_U;
    private double priceInit;
    private double priceAfter;
    private String startDate;
    private String endDate;
    private int numberLimit;
    private String description;
    private String place;
    private String image;

    public Offre(int idO, int id_U, double priceInit, double priceAfter, String startDate, String endDate, int numberLimit, String description, String place, String image) {
        this.idO = idO;
        this.id_U = id_U;
        this.priceInit = priceInit;
        this.priceAfter = priceAfter;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberLimit = numberLimit;
        this.description = description;
        this.place = place;
    }

    public Offre(int id_U, double priceInit, double priceAfter, String startDate, String endDate, int numberLimit, String description, String place, String image) {
        this.id_U = id_U;
        this.priceInit = priceInit;
        this.priceAfter = priceAfter;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberLimit = numberLimit;
        this.description = description;
        this.place = place;
    }

    public Offre() {
    }

    public int getIdO() {
        return idO;
    }

    public void setIdO(int idO) {
        this.idO = idO;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public double getPriceInit() {
        return priceInit;
    }

    public void setPriceInit(double priceInit) {
        this.priceInit = priceInit;
    }

    public double getPriceAfter() {
        return priceAfter;
    }

    public void setPriceAfter(double priceAfter) {
        this.priceAfter = priceAfter;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getNumberLimit() {
        return numberLimit;
    }

    public void setNumberLimit(int numberLimit) {
        this.numberLimit = numberLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImage() { return image; }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "idO=" + idO +
                ", id_U=" + id_U +
                ", priceInit=" + priceInit +
                ", priceAfter=" + priceAfter +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", numberLimit=" + numberLimit +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                '}';
    }
}
