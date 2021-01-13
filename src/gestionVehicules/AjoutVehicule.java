package gestionVehicules;

import connexion.Connexion;
import donneeInvalide.*;
import gestionParkings.Parking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static donneeInvalide.Verification.*;

public class AjoutVehicule extends Modif_Ajout implements Initializable {
    private static Stage fenetreAjout;
    @FXML private TextField matricule;
    @FXML protected ComboBox<String> parking;
    @FXML private Label matErreur;
    private PreparedStatement ajoutVehicule;

    static void setFenetreAjout(Stage fA) { fenetreAjout=fA; }

    public AjoutVehicule() {
        String requAjoutVehicule="INSERT INTO vehicule (matricule, marque, type, carburant, " +
                "kilometrage, date_mise_circulation, prix_location, parking_id) " +
                "VALUES(UPPER(?), UPPER(?), UPPER(?), ?, ?, ?, ?, ?)";
        try {
            ajoutVehicule=Connexion.connexion.prepareStatement(requAjoutVehicule);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Carburant> carburants= FXCollections.observableArrayList();
        carburants.addAll(Carburant.ESSENCE, Carburant.DIESEL);
        carburant.setItems(carburants);
        carburant.setValue(Carburant.DIESEL);
        ObservableList<String> parkings=Parking.recupererNonPleins();
        parkings.add("Sans Parking");
        parking.setItems(parkings);
        parking.setValue("Sans Parking");
    }

    public void ajouterVehicule() {
        supprErreurs();
        try {
            verifierMatricule(matricule.getText());
            ajoutVehicule.setString(1, matricule.getText());
            verifierMarque(marque.getText());
            ajoutVehicule.setString(2, marque.getText());
            verifierTypeVehicule(type.getText());
            ajoutVehicule.setString(3, type.getText());
            if( carburant.getValue()==Carburant.DIESEL ) {
                ajoutVehicule.setString(4, "diesel");
            }
            else {
                ajoutVehicule.setString(4, "essence");
            }
            verifierKilometrage(kilometrage.getText());
            ajoutVehicule.setLong(5, Long.parseLong(kilometrage.getText()));
            verifierPrixLocation(prixLocation.getText());
            ajoutVehicule.setDouble(7, Double.parseDouble(prixLocation.getText()));
            if( dateMiseEnCirculation.getValue()==null ) {
                dErreur.setText("date de mise en circulation doit etre fourni");
                return;
            }
            ajoutVehicule.setDate(6, Date.valueOf(dateMiseEnCirculation.getValue()));

            if( parking.getValue().equals("Sans Parking") ) {
                ajoutVehicule.setInt(8, 0);
            }
            else {
                ajoutVehicule.setInt(8, Integer.parseInt(parking.getValue()));
                Parking.incrementerTaille(Integer.parseInt(parking.getValue()));

            }
            ajoutVehicule.executeUpdate();
            ListeVehicules.getControlleurVehicule().rechercherVehicules();
            fenetreAjout.close();
        }
        catch (MatriculeInvalide matriculeInvalide) {
            matErreur.setText(matriculeInvalide.getMessage());
        }
        catch ( MarqueInvalide marqueInvalide) {
            mErreur.setText(marqueInvalide.getMessage());
        }
        catch (TypeVehiculeInvalide typeVehiculeInvalide) {
            tErreur.setText(typeVehiculeInvalide.getMessage());
        }
        catch (KilometrageInvalide kilometrageInvalide) {
            kErreur.setText(kilometrageInvalide.getMessage());
        }
        catch (PrixLocationInvalide prixLocationInvalide) {
            pErreur.setText(prixLocationInvalide.getMessage());
        }
        catch (SQLException se) {
            /* la seule cas ou on peut avoir erreur SQL est quand on essaye d'ajouter un vehicule avec
                matricule(cle primaire) qui existe deja */
            matErreur.setText("Matricule saisie existe déja");
            se.printStackTrace();
        }
    }

    protected void supprErreurs() {
        matErreur.setText("");
        super.supprErreurs();
    }

    public void annuler() {
        fenetreAjout.close();
    }


}
