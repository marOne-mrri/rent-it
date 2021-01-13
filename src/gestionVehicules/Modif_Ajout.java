package gestionVehicules;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


class Modif_Ajout {
    @FXML protected TextField marque, type;
    @FXML protected ComboBox<Carburant> carburant;
    @FXML protected TextField kilometrage;
    @FXML protected DatePicker dateMiseEnCirculation;
    @FXML protected TextField prixLocation;
    @FXML protected Label mErreur, tErreur, kErreur, dErreur, pErreur;

    protected void supprErreurs() {
        mErreur.setText("");
        tErreur.setText("");
        kErreur.setText("");
        dErreur.setText("");
        pErreur.setText("");
    }
}
