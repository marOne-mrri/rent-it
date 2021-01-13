package gestionReservation;

import connexion.Connexion;
import java.sql.*;

public class Reservation {
    private int code;
    private Date dateReservation, dateDepart, dateRetour;
    private EtatReservation etat;
    private String vehicule; // matricule - marque - type
    private String client; // cin - nom - prenom

    public int getCode() { return code; }
    public Date getDateReservation() { return dateReservation; }
    public Date getDateDepart() { return dateDepart; }
    public Date getDateRetour() { return dateRetour; }
    public EtatReservation getEtat() { return etat; }
    public String getVehicule() { return vehicule; }
    public String getClient() { return client; }

    public void setCode(int code) { this.code = code; }
    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }
    public void setDateDepart(Date dateDepart) { this.dateDepart = dateDepart; }
    public void setDateRetour(Date dateRetour) { this.dateRetour = dateRetour; }
    public void setEtat(EtatReservation etat) { this.etat = etat; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    public void setClient(String client) { this.client = client; }

    /* supprimer la reservation de la base de donnees */
    public void supprimer() {
        String requSuppr="DELETE FROM reservation WHERE id_reservation=?";
        try {
            PreparedStatement suppression=Connexion.connexion.prepareStatement(requSuppr);
            suppression.setInt(1, code);
            suppression.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void valider() {
        String requValid="UPDATE reservation SET valide=1 WHERE id_reservation=?";
        try {
            PreparedStatement validation=Connexion.connexion.prepareStatement(requValid);
            validation.setInt(1, code);
            validation.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void annuler() {
        String requAnnul="UPDATE reservation SET annule=1 WHERE id_reservation=?";
        try {
            PreparedStatement annulation=Connexion.connexion.prepareStatement(requAnnul);
            annulation.setInt(1, code);
            annulation.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
