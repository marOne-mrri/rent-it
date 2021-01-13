package gestionUtilisateurs;

import connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Utilisateur {
    private String nomUtilisateur, nom, prenom, mail, gsm;
    private LocalDate debutConge, finConge;
    private boolean admin;

    public Utilisateur() { admin=false; }

    public Utilisateur(String nU, String n, String p, String m, String g, LocalDate d, LocalDate f) {
        nomUtilisateur=nU;
        nom=n;
        prenom=p;
        mail=m;
        gsm=g;
        debutConge=d;
        finConge=f;
    }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getMail() { return mail; }
    public String getGsm() { return gsm; }
    public LocalDate getDebutConge() { return debutConge; }
    public LocalDate getFinConge() { return finConge; }
    public boolean isAdmin() { return admin; }

    public void setNomUtilisateur(String nU) { this.nomUtilisateur = nU; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setMail(String mail) { this.mail = mail; }
    public void setGsm(String gsm) { this.gsm = gsm; }
    public void setDebutConge(LocalDate debutConge) { this.debutConge = debutConge; }
    public void setFinConge(LocalDate finConge) { this.finConge = finConge; }
    public void setAdmin(boolean admin) { this.admin=admin; }

    void arreterSuspension() {
        String requ="UPDATE utilisateur SET debut_conge=null, fin_conge=null " +
                "WHERE nom_utilisateur=?";
        try {
            PreparedStatement annulSusp=Connexion.connexion.prepareStatement(requ);
            annulSusp.setString(1, nomUtilisateur);
            annulSusp.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    void supprimer() {
        String requ="DELETE FROM utilisateur WHERE nom_utilisateur=?";
        try {
            PreparedStatement suppr= Connexion.connexion.prepareStatement(requ);
            suppr.setString(1, nomUtilisateur);
            suppr.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
