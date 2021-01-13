package gestionContrats;

import connexion.Connexion;
import donneeInvalide.DateInvalide;
import gestionFactures.Facture;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ModificationContrat extends Modif_Ajout implements Initializable {
    private static Contrat contratAModifier;
    private static Stage fenetreModification;
    @FXML private Label code;
    private PreparedStatement modifierContrat;

    static void setFenetreModification(Stage fM) { fenetreModification=fM; }
    static void setContratAModifier(Contrat cM) { contratAModifier=cM; }

    public ModificationContrat() {
        String requModifContrat="UPDATE contrat SET date_depart=?, date_retour=?, " + "vehicule_matricule=?, client_cin=? WHERE id_contrat=?";
        try {
            modifierContrat= Connexion.connexion.prepareStatement(requModifContrat);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        code.setText( Integer.toString(contratAModifier.getCode()) );
        dateContrat.setText(contratAModifier.getDateContrat().toString());
        dateDepart.setValue( contratAModifier.getDateDepart().toLocalDate() );
        dateRetour.setValue( contratAModifier.getDateRetour().toLocalDate() );
        recupererClients();
        clients.setValue(contratAModifier.getClient());
        recupererVehicules();
        vehicules.setValue(contratAModifier.getVehicule());
    }
    public void enregistrerModification() {
        supprErreurs();
        try {
            verifierDates();
            modifierContrat.setDate(1, Date.valueOf(dateDepart.getValue()));
            modifierContrat.setDate(2, Date.valueOf(dateRetour.getValue()));
            String vehicule=vehicules.getValue();
            modifierContrat.setString(3, vehicule.substring(0, vehicule.indexOf(' ')));
            String client=clients.getValue();
            modifierContrat.setString(4, client.substring(0, client.indexOf(' ')) );
            modifierContrat.setInt(5, contratAModifier.getCode());
            modifierContrat.executeUpdate();
            // modifier le montant de la facture associe a ce contrat
            if( !vehicules.getValue().equals(contratAModifier.getVehicule()) ) {
                Contrat contrat=new Contrat();
                contrat.setCode( contratAModifier.getCode() );
                contrat.setVehicule(vehicule);
                Facture.ModidierMontant(contrat);
            }
            else if( isADateChanged() ) {
                Contrat nouveauContrat=new Contrat();
                nouveauContrat.setDateDepart( Date.valueOf(dateDepart.getValue()) );
                nouveauContrat.setDateRetour( Date.valueOf(dateRetour.getValue()) );
                Facture.moifierMontant(contratAModifier, nouveauContrat);
            }
            ListeContrats.getControlleurContrat().rechercherContrats();
            fenetreModification.close();
        }
        catch (DateInvalide dateInvalide) {
            dateInvalide.getCible().setText(dateInvalide.getMessage());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
    /* pour savoir si dateDepart ou/et dateRetour a/ont ete change, ce changement va affecter
        le montant de la facture associe au contrat qu'on est en train de modifier */
    private boolean isADateChanged() {
        if( !dateDepart.getValue().isEqual(contratAModifier.getDateDepart().toLocalDate()) ) {
            return true;
        }
        return !dateRetour.getValue().isEqual(contratAModifier.getDateRetour().toLocalDate());
    }

    public void annuler() {
        fenetreModification.close();
    }
}






























