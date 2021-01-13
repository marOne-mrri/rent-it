package gestionContrats;

import connexion.Connexion;
import donneeInvalide.DateInvalide;
import gestionFactures.Facture;
import gestionVehicules.Vehicule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ajoutContrat extends Modif_Ajout implements Initializable {
    private static Stage fenetreAjout;
    @FXML Label cErreur, vErreur;
    private PreparedStatement ajouterContrat;

    static void setFenetreAjout(Stage fA) { fenetreAjout=fA; }

    public ajoutContrat() {
        String requAjoutContrat="INSERT INTO contrat " +
                "(date_contrat, date_depart, date_retour, vehicule_matricule, client_cin) " +
                "VALUES(?, ?, ?, ?, ?)";
        try {
            ajouterContrat= Connexion.connexion.prepareStatement(requAjoutContrat);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateContrat.setText(LocalDate.now().toString());
        recupererClients();
        recupererVehicules();
    }

    public void ajouterContrat() {
        supprErreurs();
        try {
            verifierDates();
            ajouterContrat.setDate(2, Date.valueOf(dateDepart.getValue()));
            ajouterContrat.setDate(3, Date.valueOf(dateRetour.getValue()));

            String client=clients.getValue();
            if( client==null ) {
                cErreur.setText("aucun client n'est séléctionné !");    return;
            }
            ajouterContrat.setString(5, client.substring(0, client.indexOf(' ')) );

            String vehicule=vehicules.getValue();
            if( vehicule==null ) {
                vErreur.setText("aucun véhicule n'est séléctioné !");   return;
            }
            String matricule=vehicule.substring(0, vehicule.indexOf(' '));
            ajouterContrat.setString(4, matricule);
            Vehicule.reserver(matricule);

            ajouterContrat.setDate(1, Date.valueOf(LocalDate.now()));
            ajouterContrat.executeUpdate();
            Facture.ajouterFacture( getContrat() );// ajout de la facture associe à ce contrat
            ListeContrats.getControlleurContrat().rechercherContrats();
            fenetreAjout.close();
        }
        catch (DateInvalide dateInvalide) {
            dateInvalide.getCible().setText(dateInvalide.getMessage());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /* renvoie des informations sur le contrat qu'on ajoute */
    private Contrat getContrat() {
        Contrat contrat=new Contrat();
        contrat.setCode( Contrat.getCodeContrat() );
        contrat.setVehicule(vehicules.getValue());
        return contrat;
    }

    public void annuler() {
        fenetreAjout.close();
    }

    protected void supprErreurs() {
        super.supprErreurs();
        cErreur.setText("");
        vErreur.setText("");
    }
}


