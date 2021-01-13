package gestionContrats;

import connexion.Connexion;
import gestionFactures.Facture;
import gestionReservation.Reservation;
import java.sql.*;
import java.time.LocalDate;

public class Contrat {
    private int code;
    private Date dateContrat, dateDepart, dateRetour;
    private String codeReservation;
    private String vehicule; // matricule - marque - type
    private String client; // cin - nom - prenom

    public int getCode() {
        return code;
    }
    public Date getDateContrat() {
        return dateContrat;
    }
    public Date getDateDepart() {
        return dateDepart;
    }
    public Date getDateRetour() {
        return dateRetour;
    }
    public String getCodeReservation() {
        return codeReservation;
    }
    public String getVehicule() {
        return vehicule;
    }
    public String getClient() {
        return client;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public void setDateContrat(Date date_contrat) {
        this.dateContrat = date_contrat;
    }
    public void setDateDepart(Date date_depart) {
        this.dateDepart = date_depart;
    }
    public void setDateRetour(Date date_retour) {
        this.dateRetour = date_retour;
    }
    public void setCodeReservation(String codeReservation) {
        this.codeReservation = codeReservation;
    }
    public void setVehicule(String vehicule) {
        this.vehicule = vehicule;
    }
    public void setClient(String client) {
        this.client = client;
    }

    public static void ajouterContrat(Reservation reservation) {
        String requAjoutContrat="INSERT INTO contrat(date_contrat, date_depart, date_retour, " +
                "reservation_id, vehicule_matricule, client_cin) VALUES(?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ajoutContrat=Connexion.connexion.prepareStatement(requAjoutContrat);
            // ajout automatique du contrat correspandant à cette reservation qui est "valide"
            ajoutContrat.setDate(1, Date.valueOf(LocalDate.now()));
            ajoutContrat.setDate(2, reservation.getDateDepart());
            ajoutContrat.setDate(3, reservation.getDateRetour());
            ajoutContrat.setInt(4, reservation.getCode());
            String matricule=reservation.getVehicule();
            matricule=matricule.substring(0, matricule.indexOf(' '));
            ajoutContrat.setString(5, matricule);
            String cinClient=reservation.getClient();
            cinClient=cinClient.substring(0, cinClient.indexOf(' '));
            ajoutContrat.setString(6, cinClient);
            ajoutContrat.executeUpdate();
            // ajout de la facture associe a ce contrat
            Contrat contrat=new Contrat();
            contrat.setCode( getCodeContrat() );
            contrat.setVehicule( reservation.getVehicule() );
            Facture.ajouterFacture(contrat);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /* pour obtenir le code du contrat recement ajoute */
    public static int getCodeContrat() {
        String sql="SELECT LAST_INSERT_ID()";
        int max=1;
        try {
            Statement statement=Connexion.connexion.createStatement();
            ResultSet resultat=statement.executeQuery(sql);
            resultat.next();
            max=resultat.getInt(1);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return max;
    }
}
