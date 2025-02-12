package models ;

public class bonplan {
    private int id_U;
    private int idP;
    private String nomplace;
    private String localisation;
    private String description;
    private String typePlace; // typePlace correspond à l’enum typeC (resto, coworkingspace, cafe, muse)

    public bonplan() {}

    public bonplan(int id_U, int idP, String nomplace, String localisation, String description, String typePlace) {
        this.id_U = id_U;
        this.idP = idP;
        this.nomplace = nomplace;
        this.localisation = localisation;
        this.description = description;
        this.typePlace = typePlace;
    }

    public int getId_U() {
        return id_U;
    }

    public void setId_U(int id_U) {
        this.id_U = id_U;
    }

    public int getIdP() {
        return idP;
    }

    public void setIdP(int idP) {
        this.idP = idP;
    }

    public String getNomplace() {
        return nomplace;
    }

    public void setNomplace(String nomplace) {
        this.nomplace = nomplace;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(String typePlace) {
        this.typePlace = typePlace;
    }

    @Override
    public String toString() {
        return "BonPlan{" +
                "id_U=" + id_U +
                ", idP=" + idP +
                ", nomplace='" + nomplace + '\'' +
                ", localisation='" + localisation + '\'' +
                ", description='" + description + '\'' +
                ", typePlace='" + typePlace + '\'' +
                '}';
    }
}
