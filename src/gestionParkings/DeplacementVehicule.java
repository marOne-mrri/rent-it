package gestionParkings;

import gestionVehicules.Vehicule;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DeplacementVehicule implements Initializable {
    private static Vehicule vehiculeADeplacer;
    private static Stage fenetreDeplacement;
    @FXML private Label parkingSource;
    @FXML private ComboBox<String> parkingDestination;
    //private ObservableList<String> listeParkings= FXCollections.observableArrayList();

    static void setVehiculeADeplacer(Vehicule v) {
        vehiculeADeplacer=v;
    }
    static void setFenetreDeplacement(Stage fD) {
        fenetreDeplacement=fD;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parkingSource.setText(vehiculeADeplacer.getParking());
        ObservableList<String> listeParkings=Parking.recupererNonPleins();
        listeParkings.add("Sans Parking");
        parkingDestination.setItems(listeParkings);
        if( !vehiculeADeplacer.getParking().equals("Sans Parking") ) {
            parkingDestination.setValue(vehiculeADeplacer.getParking());
        }
        else {
            parkingDestination.setValue("Sans Parking");
        }
    }

    public void enregistrer() {
        int idParkingDestination = 0;
        if( !parkingDestination.getValue().equals("Sans Parking") ) {
            idParkingDestination=Integer.parseInt(parkingDestination.getValue());
        }
        vehiculeADeplacer.deposerDans(idParkingDestination);
        int index=ListeParkings.getControlleurParking().getParkingSlectionner();
        ListeParkings.getControlleurParking().rechercherParking();
        ListeParkings.getControlleurParking().selectionner(index);
        fenetreDeplacement.close();
    }

    public void annuler(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}
