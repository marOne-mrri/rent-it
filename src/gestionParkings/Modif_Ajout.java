package gestionParkings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/* classe contient membres et methodes communs entre classe AjoutParking
    et classe ModificationParking
 */
class Modif_Ajout {
    @FXML protected TextField rue, capacite;
    @FXML protected Label rErreur, cErreur;

    protected void supprimerErreurs() {
        rErreur.setText("");
        cErreur.setText("");
    }
}
