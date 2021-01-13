package gestionFactures;

import connexion.Connexion;
import gestionContrats.Contrat;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Period;

public class Facture {
    private int code;
    private Date dateFacture;

    private String client; // cin - nom - prenom
    double montant;

    public int getCode() { return code; }
    public String getClient() { return client; }
    public double getMontant() { return montant; }
    public Date getDateFacture() {
        return dateFacture;
    }
    public void setCode(int code) { this.code = code; }
    public void setDateFacture(Date dateFacture) { this.dateFacture = dateFacture; }
    public void setClient(String client) { this.client = client; }
    public void setMontant(double montant) { this.montant = montant; }

    /* ajouter la facture associe au contrat passe en parametre */
    public static void ajouterFacture(Contrat contrat) {
        String requAjoutFacture = "INSERT INTO facture(contrat_id, montant) VALUES(?, " +
                "(SELECT DATEDIFF(date_retour, date_depart) FROM contrat WHERE id_contrat=?)*" +
                "(SELECT prix_location FROM vehicule WHERE matricule=?))";
        try {
            PreparedStatement ajoutFacture=Connexion.connexion.prepareStatement(requAjoutFacture);
            ajoutFacture.setInt(1, contrat.getCode());
            ajoutFacture.setInt(2, contrat.getCode());
            String matricule=contrat.getVehicule(); // matricule - marque - type
            matricule=matricule.substring(0, matricule.indexOf(' '));
            ajoutFacture.setString(3, matricule);
            ajoutFacture.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /*modifier le montant d'une facture quand on modifie vehicule dans le contrat */
    public static void ModidierMontant(Contrat contrat) {
        String requModifMontant="UPDATE facture " +
                "SET montant=(SELECT date_retour-date_depart from contrat WHERE id_contrat=?)*" +
                "(SELECT prix_location FROM vehicule WHERE matricule=?) " +
                "WHERE contrat_id=?";
        try {
            PreparedStatement modifMontat=Connexion.connexion.prepareStatement(requModifMontant);
            modifMontat.setInt(1, contrat.getCode());
            String matricule=contrat.getVehicule();
            matricule=matricule.substring(0, matricule.indexOf(' '));
            modifMontat.setString(2, matricule);
            modifMontat.setInt(3, contrat.getCode());
            modifMontat.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /*
        Modifier Montant d'une facture si on modifie seulemnt dateDepart ou/et dateRetour
        dans le contrat sans changer vehicule, comme ça si on va changer prixLocation de ce vehicule
        dans le futur les contrats qui ont ete realises avant ce changement vont continuer a travailer
         avec le prix de cette vehicule au moment de realisation du contrat
     */
    public static void moifierMontant(Contrat vieuContrat, Contrat nouveauContrat) {
        String requModifMontant="UPDATE facture SET montant=?*(montant/?) WHERE contrat_id=?";
        try {
            PreparedStatement modifMontant=Connexion.connexion.prepareStatement(requModifMontant);
            int diff= Period.between(nouveauContrat.getDateDepart().toLocalDate(),
                    nouveauContrat.getDateRetour().toLocalDate()).getDays();
            modifMontant.setInt(1, diff);
            diff=Period.between(vieuContrat.getDateDepart().toLocalDate(),
                    vieuContrat.getDateRetour().toLocalDate()).getDays();
            modifMontant.setInt(2, diff);
            modifMontant.setInt(3, vieuContrat.getCode());
            modifMontant.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void supprimer() {
        String requSupprFacture = "DELETE FROM facture WHERE contrat_id=?";
        try {
            PreparedStatement suppfacture = Connexion.connexion.prepareStatement(requSupprFacture);
            suppfacture.setInt(1, this.code);
            suppfacture.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
