package gestionParkings;

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

public class DepositionVehicules implements Initializable {
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

    public void deposer() {
        if( parkings.isDisable() ) { // si tous les parkings sont pleins
            return;
        }
        Vehicule vehiculeSelectionne=vehicules.getSelectionModel().getSelectedItem();
        if( vehiculeSelectionne==null ) { return; }
        vehiculeSelectionne.deposerDans( Integer.parseInt(parkings.getValue()) );
        recupererVehicules();// ???? remove
        recupererParkings(); // !!!!
        ListeParkings.getControlleurParking().rechercherParking();//??? only when we close
    }

    // recuperer les vehicules qui sont diponibles et sans parking
    private void recupererVehicules() {
        ObservableList<Vehicule> listeVehicules;
        listeVehicules=Vehicule.recupere(CritereRecherche.DISPONIBLE_SANS_PARKING);
        assert listeVehicules != null;
        if( listeVehicules.isEmpty() ) {
            status.setText("Aucun véhicule disponible et sans parking est trouvé");
        }
        vehicules.setItems(listeVehicules);
    }

    // recuperer les parkings non pleins
    private void recupererParkings() {
        ObservableList<String> listeParkings=Parking.recupererNonPleins();
        if( listeParkings.isEmpty() ) {
            parkingsPlein.setText("pas de parking disponible !");
            this.parkings.setDisable(true);
        }
        parkings.setItems(listeParkings);
        if (!listeParkings.isEmpty()) {
            parkings.setValue(listeParkings.get(0));
        }
    }
}
