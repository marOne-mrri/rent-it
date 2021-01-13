package gestionVehicules;

import connexion.Connexion;
import donneeInvalide.KilometrageInvalide;
import donneeInvalide.MarqueInvalide;
import donneeInvalide.PrixLocationInvalide;
import donneeInvalide.TypeVehiculeInvalide;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import static donneeInvalide.Verification.*;

public class ModificationVehicule extends Modif_Ajout implements Initializable {
    private static Vehicule vehiculeAModifier;
    private static Stage fenetreModification;
    @FXML private Label matricule;
    private PreparedStatement modifierVehicule;

    static void setFenetreModification(Stage fM) { fenetreModification=fM; }
    static void setVehiculeAModifier(Vehicule vM) { vehiculeAModifier=vM; }

    public ModificationVehicule() {
        String requModifi="UPDATE vehicule SET marque=?, type=?, carburant=?, kilometrage=?, " +
                "date_mise_circulation=?, prix_location=? WHERE matricule=?";
        try {
            modifierVehicule=Connexion.connexion.prepareStatement(requModifi);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // remplissage des champs de la fenetre de modification par les donnees de la vehicule a modifier
        matricule.setText(vehiculeAModifier.getMatricule());
        marque.setText(vehiculeAModifier.getMarque());
        type.setText(vehiculeAModifier.getType());
        ObservableList<Carburant> carburants=FXCollections.observableArrayList();
        carburants.addAll(Carburant.ESSENCE, Carburant.DIESEL);
        carburant.setItems(carburants);
        carburant.setValue(vehiculeAModifier.getCarburant());
        kilometrage.setText(Long.toString(vehiculeAModifier.getKilometrage()));
        dateMiseEnCirculation.setValue(vehiculeAModifier.getDateMiseEnCirculation().toLocalDate());
        prixLocation.setText(Double.toString(vehiculeAModifier.getPrixLocation()));
    }

    public void enreigestrerModification() {
        supprErreurs();
        try {
            verifierMarque(marque.getText());
            modifierVehicule.setString(1, marque.getText());
            verifierTypeVehicule(type.getText());
            modifierVehicule.setString(2, type.getText());
            if( carburant.getValue()==Carburant.DIESEL ) {
                modifierVehicule.setString(3, "diesel");
            }
            else {
                modifierVehicule.setString(3, "essence");
            }
            verifierKilometrage(kilometrage.getText());
            modifierVehicule.setLong(4, Long.valueOf(kilometrage.getText()) );
            if( dateMiseEnCirculation.getValue()==null ) {
                dErreur.setText("date de mise en circulation doit etre fournie");
                return;
            }
            modifierVehicule.setDate(5, Date.valueOf(dateMiseEnCirculation.getValue()));
            verifierPrixLocation(prixLocation.getText());
            modifierVehicule.setDouble(6, Double.valueOf(prixLocation.getText()));
            modifierVehicule.setString(7, vehiculeAModifier.getMatricule());
            modifierVehicule.executeUpdate();
            ListeVehicules.getControlleurVehicule().rechercherVehicules();
            fenetreModification.close();
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
            se.printStackTrace();
        }
    }

    public void annuler() {
        fenetreModification.close();
    }
}
