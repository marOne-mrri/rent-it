package gestionReservation;

import connexion.Connexion;
import donneeInvalide.DateInvalide;
import gestionContrats.Contrat;
import gestionVehicules.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import static gestionReservation.ListeReservation.getControlleurReservation;

public class ajoutReservation extends Modif_Ajout implements Initializable {
    private static Stage fenetreAjout;
    @FXML Label cErreur, vErreur;
    private PreparedStatement ajoutReservation;

    static void setFenetreAjout(Stage fA) { fenetreAjout=fA; }

    public ajoutReservation() {
        String requAjoutReservation="INSERT INTO reservation(date_reservation, date_depart, " +
                "date_retour, valide, annule, vehicule_matricule, client_cin) " +
                "VALUES(?, ?, ?, 0, 0, ?, ?)";
        try {
            ajoutReservation= Connexion.connexion.prepareStatement(requAjoutReservation);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateReservation.setText(LocalDate.now().toString());
        recupererClients();
        recupererVehicules();
    }
    public void ajouterReservation() {
        supprErreurs();
        try {
            verifierDates(); // verifier date depart et date de retour
            /* dans la suite on remplit les parametre de la requete d'ajout par les donnees saisis
                dans les champs avec l'affectuation des tests simples pour assurer que certains champs
                ne sont pas vides
             */
            ajoutReservation.setDate(2, Date.valueOf(dateDepart.getValue()));
            ajoutReservation.setDate(3, Date.valueOf(dateRetour.getValue()));

            String client=clients.getValue();
            if( client==null ) {
                cErreur.setText("aucun client n'est séléctionné !");    return;
            }
            ajoutReservation.setString(5, client.substring(0, client.indexOf(' ')) );
            String vehicule=vehicules.getValue();
            if( vehicule==null ) {
                vErreur.setText("aucun véhicule n'est séléctioné !");   return;
            }
            String matricule=vehicule.substring(0, vehicule.indexOf(' '));
            ajoutReservation.setString(4, matricule);
            Vehicule.reserver(matricule); // marquer vehicule reserver comme indisponible

            ajoutReservation.setDate(1, Date.valueOf(LocalDate.now()));
            ajoutReservation.executeUpdate();

            getControlleurReservation().rechercherReservations();
            fenetreAjout.close();
        }
        catch (DateInvalide dateInvalide) {
            dateInvalide.getCible().setText(dateInvalide.getMessage());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    protected void supprErreurs() {
        super.supprErreurs();
        cErreur.setText("");
        vErreur.setText("");
    }

    public void annuler() {
        fenetreAjout.close();
    }
}
