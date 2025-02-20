package models;

import java.time.LocalDate;
import java.util.Objects;

public class Expense {

    private int idE;
    private int id_U;
    private String nameEX;
    private double amount;
    private String description;
    private String category;
    private LocalDate dateE;
    private String Imagedepense;

    public Expense() {
    }

    public Expense(int idE, int id_U, String nameEX, double amount, String description, String category, LocalDate dateE, String Imagedepense) {
        this.idE = idE;
        this.id_U = id_U;
        this.nameEX = nameEX;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateE = dateE;
        this.Imagedepense = Imagedepense;
    }

    public Expense(int id_U, String nameEX, double amount, String description, String category, LocalDate dateE, String Imagedepense) {

        this.id_U = id_U;
        this.nameEX = nameEX;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateE = dateE;
        this.Imagedepense = Imagedepense;
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

    public String getImagedepense() {
        return Imagedepense;
    }

    public void setImagedepense(String imagedepense) {
        Imagedepense = imagedepense;
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
                ", Imagedepense='" + Imagedepense + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Expense expense)) return false;
        return idE == expense.idE && id_U == expense.id_U && Double.compare(amount, expense.amount) == 0 && Objects.equals(nameEX, expense.nameEX) && Objects.equals(description, expense.description) && Objects.equals(category, expense.category) && Objects.equals(dateE, expense.dateE) && Objects.equals(Imagedepense, expense.Imagedepense);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idE, id_U, nameEX, amount, description, category, dateE, Imagedepense);
    }
}
