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
import java.util.ResourceBundle;
import static gestionReservation.ListeReservation.getControlleurReservation;

public class ModificationReservation extends Modif_Ajout implements Initializable {
    private static Reservation reservationAModifier;
    private static Stage fenetreModification;
    @FXML private Label code;
    private PreparedStatement modifierReservation;

    static void setFenetreModification(Stage fM) { fenetreModification=fM; }
    static void setReservationAModifier(Reservation rM) { reservationAModifier=rM; }

    public ModificationReservation() {
        String requModif="UPDATE reservation SET date_depart=?, date_retour=?, valide=?, " +
                "annule=?, vehicule_matricule=?, client_cin=? " +
                "WHERE id_reservation=?";
        try {
            modifierReservation=Connexion.connexion.prepareStatement(requModif);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<EtatReservation> etats= FXCollections.observableArrayList();
        // affichage des options possible pour une réservation en se basant sur état actuelle
        if( reservationAModifier.getEtat()==EtatReservation.VALIDE ) {
            etats.add(EtatReservation.VALIDE);
        }
        else if( reservationAModifier.getEtat()==EtatReservation.NON_VALIDE ) {
            etats.addAll(EtatReservation.VALIDE, EtatReservation.NON_VALIDE, EtatReservation.ANNULE);
        }
        else { // ANNULE
            etats.add(EtatReservation.ANNULE);
        }
        etat.setItems(etats);
        etat.setValue(reservationAModifier.getEtat());
        code.setText( Integer.toString(reservationAModifier.getCode()) );
        dateReservation.setText(reservationAModifier.getDateReservation().toString());
        dateDepart.setValue( reservationAModifier.getDateDepart().toLocalDate() );
        dateRetour.setValue( reservationAModifier.getDateRetour().toLocalDate() );
        recupererClients();
        clients.setValue(reservationAModifier.getClient());
        recupererVehicules();
        vehicules.setValue(reservationAModifier.getVehicule());
    }

    public void enreigestrerModification() {
        supprErreurs();
        try {
            verifierDates();
            modifierReservation.setDate(1, Date.valueOf(dateDepart.getValue()));
            modifierReservation.setDate(2, Date.valueOf(dateRetour.getValue()));

            String vehicule=vehicules.getValue();
            modifierReservation.setString(5, vehicule.substring(0, vehicule.indexOf(' ')));
            String client=clients.getValue();
            modifierReservation.setString(6, client.substring(0, client.indexOf(' ')) );
            modifierReservation.setInt(7, reservationAModifier.getCode());

            boolean valide=false, annule=false;
            if( reservationAModifier.getEtat()==EtatReservation.NON_VALIDE ) {
                EtatReservation etat = this.etat.getValue();
                if (etat == EtatReservation.VALIDE) {
                    valide = true;
                /* si une reservation est modifier pour etre à l'etat "valide" on va ajouter
                automatiquement contrat associe à cette reservation */
                    Contrat.ajouterContrat( getReservation() );
                } else if (etat == EtatReservation.ANNULE) {
                    annule = true;
                    // marquer la vehicule qui a ete reserve comme disponible maintenant
                    Vehicule.marquerDisponible( vehicule.substring(0, vehicule.indexOf(' ')) );
                }
            }
            else if( reservationAModifier.getEtat()==EtatReservation.VALIDE ) {
                valide=true;
            }
            else { // reservationAModifier.getEtat()==EtatReservation.ANNULE
                annule=true;
            }

            modifierReservation.setBoolean(3, valide);
            modifierReservation.setBoolean(4, annule);
            modifierReservation.executeUpdate();
           getControlleurReservation().rechercherReservations();
           fenetreModification.close();
        }
        catch (DateInvalide dateInvalide) {
            dateInvalide.getCible().setText(dateInvalide.getMessage());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /* -faite pour obtenir l'objet qui represente la réservation en train d'etre modifie.
       -utilise quand une reservation est modifier avec l'etat "valide" pour automatiquement ajoute
        contrat associe à cette reservation, car c'est necessaire de savoir les informations de cette
        reservation afin d'ajouter sa contrat comme: dateDepart, dateRetour, vehicule et client.
     */
    private Reservation getReservation() {
        Reservation reservation=new Reservation();
        reservation.setCode( reservationAModifier.getCode() );
        reservation.setDateDepart(Date.valueOf(dateDepart.getValue()));
        reservation.setDateRetour(Date.valueOf(dateRetour.getValue()));
        reservation.setVehicule(vehicules.getValue());
        reservation.setClient(clients.getValue());
        return reservation;
    }

    public void annuler() {
        fenetreModification.close();
    }
}


