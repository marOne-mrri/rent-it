package gestionParkings;

import gestionVehicules.CritereRecherche;
import gestionVehicules.Vehicule;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class VehiculesReserves implements Initializable {
    @FXML private Label status;
    @FXML private TableView<Vehicule> vehicules;
    @FXML private TableColumn<Vehicule, String> matricules, marques, types;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        matricules.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        marques.setCellValueFactory(new PropertyValueFactory<>("marque"));
        types.setCellValueFactory(new PropertyValueFactory<>("type"));
        recupererVehicules();
    }

    public void faireSortir() {
        Vehicule vehiculeSelectionne=vehicules.getSelectionModel().getSelectedItem();
        if( vehiculeSelectionne==null ) { return; }
        vehiculeSelectionne.sortir();
        recupererVehicules();//????
        ListeParkings.getControlleurParking().rechercherParking();//metrre a jour les parkings
    }

    private void recupererVehicules() {
        ObservableList<Vehicule> listeVehicules=Vehicule.recupere(CritereRecherche.RESERVE);
        assert listeVehicules != null;
        if( listeVehicules.isEmpty() ) {
            status.setText("Aucun véhicule reservé et chez l'agence pour le moment");
        }
        vehicules.setItems(listeVehicules);
    }
}
