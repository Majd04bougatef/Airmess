package models;
import java.time.LocalDate;

public class Users {
    private int id_U; // id_U
    private String name;
    private String prenom;
    private String email;
    private String password;
    private String roleUser; // Enums represented as String
    private LocalDate dateNaiss;
    private String phoneNumber;
    private String statut; // Enums represented as String
    private int diamond;
    private int deleteFlag;
    private double prixtotal;

    // Default Constructor
    public Users() {
    }

    // Parameterized Constructor
    public Users(int id_U, String name, String prenom, String email, String password, String roleUser, LocalDate dateNaiss, String phoneNumber, String statut, int diamond, int deleteFlag, double prixtotal) {
        this.id_U = id_U;
        this.name = name;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roleUser = roleUser;
        this.dateNaiss = dateNaiss;
        this.phoneNumber = phoneNumber;
        this.statut = statut;
        this.diamond = diamond;
        this.deleteFlag = deleteFlag;
        this.prixtotal = prixtotal;
    }
    public Users( String name, String prenom, String email, String password, String roleUser, LocalDate dateNaiss, String phoneNumber, String statut, int diamond, int deleteFlag, double prixtotal) {

        this.name = name;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roleUser = roleUser;
        this.dateNaiss = dateNaiss;
        this.phoneNumber = phoneNumber;
        this.statut = statut;
        this.diamond = diamond;
        this.deleteFlag = deleteFlag;
        this.prixtotal = prixtotal;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleUser() {
        return roleUser;
    }

    public void setRoleUser(String roleUser) {
        this.roleUser = roleUser;
    }

    public LocalDate getDateNaiss() {
        return dateNaiss;
    }

    public void setDateNaiss(LocalDate dateNaiss) {
        this.dateNaiss = dateNaiss;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public double getPrixtotal() {
        return prixtotal;
    }

    public void setPrixtotal(double prixtotal) {
        this.prixtotal = prixtotal;
    }
}
