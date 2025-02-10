package models;
import java.time.LocalDate;

public class Expense {

    private int idE;
    private int id_U;
    private String nameEX;
    private double amount;
    private String description;
    private String category;
    private LocalDate dateE;

    public Expense() {}

    public Expense(int idE, int id_U, String nameEX, double amount, String description, String category, LocalDate dateE) {
        this.idE = idE;
        this.id_U = id_U;
        this.nameEX = nameEX;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateE = dateE;
    }

    public Expense(int id_U, String nameEX, double amount, String description, String category, LocalDate dateE) {
        this.id_U = id_U;
        this.nameEX = nameEX;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateE = dateE;
    }

    public int getIdE() {
        return idE;
    }

    public void setIdE(int idE) {
        this.idE = idE;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public String getNameEX() {
        return nameEX;
    }

    public void setNameEX(String nameEX) {
        this.nameEX = nameEX;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDateE() {
        return dateE;
    }

    public void setDateE(LocalDate dateE) {
        this.dateE = dateE;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "idE=" + idE +
                ", id_U=" + id_U +
                ", nameEX='" + nameEX + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", dateE=" + dateE +
                '}';
    }
}
