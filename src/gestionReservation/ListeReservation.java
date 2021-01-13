package gestionReservation;

import com.jfoenix.controls.JFXToggleButton;
import connexion.Connexion;
import gestionClients.Client;
import gestionContrats.Contrat;
import gestionVehicules.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import static gestionReservation.ModificationReservation.setFenetreModification;
import static gestionReservation.ModificationReservation.setReservationAModifier;
import static gestionReservation.ajoutReservation.setFenetreAjout;

public class ListeReservation implements Initializable {
    private static ListeReservation controlleurReservation;
    @FXML private TextField code, nom, prenom;
    @FXML private TableView<Reservation> tableReservation;
    @FXML private TableColumn<Reservation, Integer> codeReservation;
    @FXML private TableColumn<Reservation, Date> dateReservation, dateDepart, dateRetour;
    @FXML private TableColumn<Reservation, EtatReservation> etatReservation;
    @FXML private TableColumn<Reservation, String> vehicule;
    @FXML private TableColumn<Client, String> client;
    @FXML private Label status;
    @FXML private JFXToggleButton toutes, valide, nonValide, annule;
    @FXML private Button valider, annuler, modifier;
    private ObservableList<Reservation> listeReservations = FXCollections.observableArrayList();
    enum CritereRecherche {TOUTES, VALIDE, NON_VALIDE, ANNULE}
    private PreparedStatement rechercherReservation;

    static ListeReservation getControlleurReservation() { return controlleurReservation; }

    public ListeReservation() {
        controlleurReservation=this;
        String reqRechercheReservation= "SELECT id_reservation, date_reservation, date_depart, " +
                "date_retour, valide, annule, cin, nom, prenom, matricule, marque, type FROM reservation "
                + "INNER JOIN client on cin=client_cin " +
                "INNER JOIN vehicule on matricule=vehicule_matricule " +
                "WHERE valide IN (?, ?) AND annule IN (?, ?) " +
                "AND id_reservation LIKE (?) AND nom LIKE (?) AND prenom LIKE (?) " +
                "ORDER BY date_reservation";
        try {
            rechercherReservation= Connexion.connexion.prepareStatement(reqRechercheReservation);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toutes.setSelected(true);
        valider.setDisable(true);   annuler.setDisable(true);
        codeReservation.setCellValueFactory(new PropertyValueFactory<>("code"));
        dateReservation.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        dateDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        dateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        etatReservation.setCellValueFactory(new PropertyValueFactory<>("etat"));
        vehicule.setCellValueFactory(new PropertyValueFactory<>("vehicule"));
        client.setCellValueFactory(new PropertyValueFactory<>("client"));
        recupererReservations(CritereRecherche.TOUTES);
    }
    /* -recuperer reservation en se basant sur:
        1- etat d'une reservation(toutes, valide, non valide, annule)(parametre 1)
        2- et aussi recherche par codeReservation, nomClient et prenomClient(parametre 2)
     */
    private void recupererReservations(CritereRecherche critere, String...parametres) {
        mettreAjourReservations();
        listeReservations.clear();
        status.setText("");
        try {
            if (critere == CritereRecherche.TOUTES) {
                rechercherReservation.setBoolean(1, false);
                rechercherReservation.setBoolean(2, true);
                rechercherReservation.setBoolean(3, false);
                rechercherReservation.setBoolean(4, true);
            } else if (critere == CritereRecherche.VALIDE) {
                rechercherReservation.setBoolean(1, true);
                rechercherReservation.setBoolean(2, true);
                rechercherReservation.setBoolean(3, false);
                rechercherReservation.setBoolean(4, false);
            } else if (critere == CritereRecherche.NON_VALIDE) {
                rechercherReservation.setBoolean(1, false);
                rechercherReservation.setBoolean(2, false);
                rechercherReservation.setBoolean(3, false);
                rechercherReservation.setBoolean(4, false);
            } else if (critere == CritereRecherche.ANNULE) {
                rechercherReservation.setBoolean(1, false);
                rechercherReservation.setBoolean(2, false);
                rechercherReservation.setBoolean(3, true);
                rechercherReservation.setBoolean(4, true);
            }
            if( parametres.length==3 ) {
                rechercherReservation.setString(5, parametres[0]);
                rechercherReservation.setString(6, parametres[1]);
                rechercherReservation.setString(7, parametres[2]);
            }
            else { // length==0
                rechercherReservation.setString(5, "%");
                rechercherReservation.setString(6, "%");
                rechercherReservation.setString(7, "%");
            }
            ResultSet resultat = rechercherReservation.executeQuery();
            if (resultat.next()) {
                resultat.beforeFirst();
                extraireDonnees(resultat);
                tableReservation.setItems(listeReservations);
            }
            else {
                status.setText("acune réservation trouvé");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
    /* extraire et organiser les donnes recus apres l'execution de la requete de recuperation */
    private void extraireDonnees(ResultSet resultat) {
        try {
            while (resultat.next()) {
                Reservation reservation = new Reservation();
                reservation.setCode(resultat.getInt("id_reservation"));
                reservation.setDateReservation(resultat.getDate("date_reservation"));
                reservation.setDateDepart(resultat.getDate("date_depart"));
                reservation.setDateRetour(resultat.getDate("date_retour"));
                if ( resultat.getBoolean("valide") ) {
                    reservation.setEtat(EtatReservation.VALIDE);
                } else if ( resultat.getBoolean("annule") ) {
                    reservation.setEtat(EtatReservation.ANNULE);
                } else {
                    reservation.setEtat(EtatReservation.NON_VALIDE);
                }
                reservation.setVehicule(resultat.getString("matricule")+" - " +
                        resultat.getString("marque")+" - "+
                        resultat.getString("type"));
                reservation.setClient(resultat.getString("cin")+" - "+
                        resultat.getString("nom")+" - "+
                        resultat.getString("prenom"));
                listeReservations.add(reservation);
            }
        }
        catch(SQLException se){
            se.printStackTrace();
        }
    }
    public void rechercherReservations() {
        String codeReservation=code.getText();
        if( codeReservation.length()==0 )
            codeReservation="%";
        else
            codeReservation="%"+codeReservation+"%";
        String nomClient=nom.getText();
        if( nomClient.length()==0 )
            nomClient="%";
        else
            nomClient="%"+nomClient+"%";
        String prenomClient=prenom.getText();
        if( prenomClient.length()==0 )
            prenomClient="%";
        else
            prenomClient="%"+prenomClient+"%";
        CritereRecherche critereRecherche;
        if( toutes.isSelected() ) {
            critereRecherche=CritereRecherche.TOUTES;
        }
        else if( valide.isSelected() ) {
            critereRecherche=CritereRecherche.VALIDE;
        }
        else if( nonValide.isSelected() ) {
            critereRecherche=CritereRecherche.NON_VALIDE;
        }
        else { // annule is selected
            critereRecherche=CritereRecherche.ANNULE;
        }
        recupererReservations(critereRecherche, codeReservation, nomClient, prenomClient);
    }

    // appele lorsque on clique sur le bouton "afficher toutes"
    public void afficherToutes() {
        // vider les champs de recherche
        code.setText("");   nom.setText("");    prenom.setText("");

        if( toutes.isSelected() ) {
            recupererReservations(CritereRecherche.TOUTES);
        }
        else if( valide.isSelected() ) {
            recupererReservations(CritereRecherche.VALIDE);
        }
        else if( nonValide.isSelected() ) {
            recupererReservations(CritereRecherche.NON_VALIDE);
        }
        else if( annule.isSelected() ) {
            recupererReservations(CritereRecherche.ANNULE);
        }
    }

    // execute quand on selectionne (radio button) toutes
    public void afficherToutesReservations() {
        rechercherReservations();
        valider.setDisable(true);
        annuler.setDisable(true);
        modifier.setDisable(false);
    }

    public void afficherReservationsValides() {
        rechercherReservations();
        modifier.setDisable(false);
        valider.setDisable(true);
        annuler.setDisable(true);
    }

    public void afficherReservationsNonValides() {
        rechercherReservations();
        valider.setDisable(false);
        annuler.setDisable(false);
        modifier.setDisable(false);
    }

    public void afficherReservationAnnules() {
        rechercherReservations();
        valider.setDisable(true);
        annuler.setDisable(true);
        modifier.setDisable(true);
    }

    public void  modifierReservation() throws Exception{
        Reservation reservationSelectionne=tableReservation.getSelectionModel().getSelectedItem();
        if( reservationSelectionne==null ) { return; }
        setReservationAModifier(reservationSelectionne);
        Stage fenetreModification=new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        setFenetreModification(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.setResizable(false);
        fenetreModification.setScene(new Scene(root));
        fenetreModification.show();
    }

    public void supprimerReservation() {
        Reservation reservationSelectionne=tableReservation.getSelectionModel().getSelectedItem();
        if( reservationSelectionne==null ) { return; }
        tableReservation.getItems().remove(reservationSelectionne);
        reservationSelectionne.supprimer();
        if( reservationSelectionne.getEtat()==EtatReservation.NON_VALIDE ) {
            String matricule=reservationSelectionne.getVehicule();
            matricule=matricule.substring(0, matricule.indexOf(' '));
            Vehicule.marquerDisponible(matricule);
        }
    }

    /* valider une reservation et l'ajout automatique de sa contrat par "Contrat.ajoutContrat" */
    public void validerReservation() {
        Reservation reservationSelectionne=tableReservation.getSelectionModel().getSelectedItem();
        if( reservationSelectionne==null ) { return; }
        reservationSelectionne.valider();
        rechercherReservations();//????
        Contrat.ajouterContrat(reservationSelectionne);
    }

    public void annulerReservation() {
        Reservation reservationSelectionne=tableReservation.getSelectionModel().getSelectedItem();
        if( reservationSelectionne==null ) { return; }
        reservationSelectionne.annuler();
        String vehicule=reservationSelectionne.getVehicule();
        // maintenant vehicule est diponible
        Vehicule.marquerDisponible( vehicule.substring(0, vehicule.indexOf(' ')) );
        rechercherReservations();//????
    }

    public void ajouterReservation() throws Exception {
        Stage fenetreAjout=new Stage();
        fenetreAjout.initModality(Modality.APPLICATION_MODAL);
        setFenetreAjout(fenetreAjout);
        Parent root = FXMLLoader.load(getClass().getResource("ajout.fxml"));
        fenetreAjout.setTitle("Rent it");
        fenetreAjout.getIcons().add(new Image("/images/icon.png"));
        fenetreAjout.setScene(new Scene(root));
        fenetreAjout.setResizable(false);
        fenetreAjout.show();
    }

    /* pour annuler automatiquement les reservations non validees
        avant 2 jours de la date de location */
    private void mettreAjourReservations() {
        Vehicule.marquerDisponible();
        String sql = "UPDATE reservation SET annule = 1 WHERE valide = 0 AND DATEDIFF(date_depart, CURDATE()) < 2";
        try {
            Statement mettreAjour=Connexion.connexion.createStatement();
            mettreAjour.executeUpdate(sql);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}