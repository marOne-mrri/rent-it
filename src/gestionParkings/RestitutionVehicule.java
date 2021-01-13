package gestionParkings;

import gestionSanctions.Sanction;
import gestionVehicules.CritereRecherche;
import gestionVehicules.Vehicule;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class RestitutionVehicule implements Initializable {
    @FXML private Label status, parkingsPlein;
    @FXML private TableView<Vehicule> vehicules;
    @FXML private TableColumn<Vehicule, String> matricules, marques, types;
    @FXML private ComboBox<String> parkings; // liste des id des parkings

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        matricules.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        marques.setCellValueFactory(new PropertyValueFactory<>("marque"));
        types.setCellValueFactory(new PropertyValueFactory<>("type"));
        recupererVehicules();
        recupererParkings();
    }

    public void restituer() {
        Vehicule vehiculeSelectionne=vehicules.getSelectionModel().getSelectedItem();
        if( vehiculeSelectionne==null ) { return; }
        if( parkings.getValue().equals("Sans Parking") ) {
            vehiculeSelectionne.deposerDans(0);
        }
        else {
            vehiculeSelectionne.deposerDans(Integer.parseInt(parkings.getValue()));
        }
        Sanction.verifierDateRetour(vehiculeSelectionne);
        recupererVehicules();
        recupererParkings();
        ListeParkings.getControlleurParking().rechercherParking();//???
    }

    // recuperer les vehicules qui sont en location (chez le client)
    private void recupererVehicules() {
        ObservableList<Vehicule> listeVehicules;
        listeVehicules=Vehicule.recupere(CritereRecherche.EN_LOCATION);
        assert listeVehicules != null;
        if( listeVehicules.isEmpty() ) {
            status.setText("Tous les véhicules sont chez l'agence pour le moment");
        }
        vehicules.setItems(listeVehicules);
    }

    private void recupererParkings() {
        ObservableList<String> listeParkings=Parking.recupererNonPleins();
        if( listeParkings.isEmpty() ) {
            parkingsPlein.setText("pas de parking disponible !");
        }
        // parmis les choix possible pour un parking qui siginife en dehors de tout parking
        listeParkings.add("Sans Parking");
        parkings.setItems(listeParkings);
        parkings.setValue(listeParkings.get(0));// choisir premier parking par defaut
    }
}
